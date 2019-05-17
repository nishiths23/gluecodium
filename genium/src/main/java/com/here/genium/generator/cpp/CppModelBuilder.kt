/*
 * Copyright (C) 2016-2019 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.genium.generator.cpp

import com.google.common.annotations.VisibleForTesting
import com.here.genium.generator.common.modelbuilder.AbstractLimeBasedModelBuilder
import com.here.genium.common.ModelBuilderContextStack
import com.here.genium.model.common.CommentsPreprocessor
import com.here.genium.model.cpp.CppClass
import com.here.genium.model.cpp.CppConstant
import com.here.genium.model.cpp.CppElement
import com.here.genium.model.cpp.CppEnum
import com.here.genium.model.cpp.CppEnumItem
import com.here.genium.model.cpp.CppField
import com.here.genium.model.cpp.CppInheritance
import com.here.genium.model.cpp.CppMethod
import com.here.genium.model.cpp.CppParameter
import com.here.genium.model.cpp.CppPrimitiveTypeRef
import com.here.genium.model.cpp.CppStruct
import com.here.genium.model.cpp.CppTypeRef
import com.here.genium.model.cpp.CppUsing
import com.here.genium.model.cpp.CppValue
import com.here.genium.model.lime.LimeAttributeType
import com.here.genium.model.lime.LimeBasicType
import com.here.genium.model.lime.LimeConstant
import com.here.genium.model.lime.LimeContainer
import com.here.genium.model.lime.LimeEnumeration
import com.here.genium.model.lime.LimeEnumerator
import com.here.genium.model.lime.LimeField
import com.here.genium.model.lime.LimeMethod
import com.here.genium.model.lime.LimeParameter
import com.here.genium.model.lime.LimeProperty
import com.here.genium.model.lime.LimeStruct
import com.here.genium.model.lime.LimeTypeDef
import com.here.genium.model.lime.LimeTypeHelper
import com.here.genium.model.lime.LimeTypeRef
import com.here.genium.model.lime.LimeValue
import com.here.genium.model.lime.LimeValue.Special.ValueId
import java.util.EnumSet

class CppModelBuilder @VisibleForTesting internal constructor(
    contextStack: ModelBuilderContextStack<CppElement>,
    private val typeMapper: CppTypeMapper,
    private val nameResolver: CppNameResolver
) : AbstractLimeBasedModelBuilder<CppElement>(contextStack) {

    constructor(
        typeMapper: CppTypeMapper,
        nameResolver: CppNameResolver
    ) : this(ModelBuilderContextStack<CppElement>(), typeMapper, nameResolver)

    override fun finishBuilding(limeContainer: LimeContainer) {
        val members: List<CppElement> = getPreviousResults(CppEnum::class.java) +
            getPreviousResults(CppUsing::class.java) +
            getPreviousResults(CppStruct::class.java) +
            getPreviousResults(CppConstant::class.java)

        if (limeContainer.type == LimeContainer.ContainerType.TYPE_COLLECTION) {
            members.forEach { storeResult(it) }
            closeContext()
            return
        }

        val limeParentType = limeContainer.parent
        val inheritances = when {
            limeParentType != null -> {
                val parentType =
                    typeMapper.mapInstanceType(limeParentType.type as LimeContainer, false)
                listOf(CppInheritance(parentType, CppInheritance.Type.Public))
            }
            else -> emptyList()
        }
        val cppClass = CppClass(
            name = nameResolver.getName(limeContainer),
            fullyQualifiedName = nameResolver.getFullyQualifiedName(limeContainer),
            comment = limeContainer.comment,
            isExternal = limeContainer.attributes.have(LimeAttributeType.EXTERNAL_TYPE),
            members = members,
            methods = getPreviousResults(CppMethod::class.java),
            inheritances = inheritances,
            isEquatable = limeContainer.attributes.have(LimeAttributeType.EQUATABLE)
        )

        storeNamedResult(limeContainer, cppClass)
        closeContext()
    }

    override fun finishBuilding(limeMethod: LimeMethod) {
        val specifiers = EnumSet.noneOf(CppMethod.Specifier::class.java)
        val qualifiers = EnumSet.noneOf(CppMethod.Qualifier::class.java)
        if (limeMethod.isStatic || limeMethod.attributes.have(LimeAttributeType.CONSTRUCTOR)) {
            specifiers.add(CppMethod.Specifier.STATIC)
        } else {
            if (limeMethod.attributes.have(LimeAttributeType.CONST)) {
                // const needs to be before "= 0" pure virtual specifier
                qualifiers.add(CppMethod.Qualifier.CONST)
            }
            specifiers.add(CppMethod.Specifier.VIRTUAL)
            qualifiers.add(CppMethod.Qualifier.PURE_VIRTUAL)
        }

        val isNullable = limeMethod.returnType.attributes.have(LimeAttributeType.NULLABLE)
        val isInstance = limeMethod.returnType.typeRef.type is LimeContainer
        var cppReturnType = typeMapper.mapType(limeMethod.returnType.typeRef)
        if (isNullable && !isInstance) {
            cppReturnType = typeMapper.createOptionalType(cppReturnType)
        }
        val errorEnum = getPreviousResultOrNull(CppTypeRef::class.java)
        val returnType = when {
            errorEnum != null && cppReturnType != CppPrimitiveTypeRef.VOID ->
                typeMapper.getReturnWrapperType(
                    cppReturnType,
                    CppTypeMapper.STD_ERROR_CODE_TYPE
                )
            errorEnum != null -> CppTypeMapper.STD_ERROR_CODE_TYPE
            else -> cppReturnType
        }

        val cppMethod = CppMethod(
            nameResolver.getName(limeMethod),
            nameResolver.getFullyQualifiedName(limeMethod),
            limeMethod.comment,
            returnType,
            limeMethod.returnType.comment,
            errorEnum?.fullyQualifiedName,
            isInstance && !isNullable,
            getPreviousResults(CppParameter::class.java),
            specifiers,
            qualifiers
        )

        storeNamedResult(limeMethod, cppMethod)
        closeContext()
    }

    override fun finishBuilding(limeParameter: LimeParameter) {
        val isNullable = limeParameter.attributes.have(LimeAttributeType.NULLABLE)
        val isInstance = limeParameter.typeRef.type is LimeContainer
        var cppTypeRef = getPreviousResult(CppTypeRef::class.java)
        if (isNullable && !isInstance) {
            cppTypeRef = typeMapper.createOptionalType(cppTypeRef)
        }

        val cppParameter = CppParameter(
            name = nameResolver.getName(limeParameter),
            type = cppTypeRef,
            isNotNull = isInstance && !isNullable
        )
        cppParameter.comment = limeParameter.comment

        storeResult(cppParameter)
        closeContext()
    }

    override fun finishBuilding(limeStruct: LimeStruct) {
        val methods = getPreviousResults(CppMethod::class.java).map {
            val specifiers = setOf(CppMethod.Specifier.STATIC) intersect it.specifiers
            val qualifiers = when {
                specifiers.contains(CppMethod.Specifier.STATIC) -> emptySet()
                else -> setOf(CppMethod.Qualifier.CONST)
            }
            it.copy(specifiers, qualifiers)
        }
        val cppStruct = CppStruct(
            nameResolver.getName(limeStruct),
            nameResolver.getFullyQualifiedName(limeStruct),
            limeStruct.comment,
            limeStruct.attributes.have(LimeAttributeType.EXTERNAL_TYPE),
            getPreviousResults(CppField::class.java),
            methods,
            getPreviousResults(CppConstant::class.java),
            limeStruct.attributes.have(LimeAttributeType.EQUATABLE),
            limeStruct.attributes.have(LimeAttributeType.IMMUTABLE)
        )

        storeNamedResult(limeStruct, cppStruct)
        closeContext()
    }

    override fun finishBuilding(limeField: LimeField) {
        var cppTypeRef = getPreviousResult(CppTypeRef::class.java)
        val isNullable = limeField.attributes.have(LimeAttributeType.NULLABLE)
        val isInstance = limeField.typeRef.type is LimeContainer
        if (isNullable && !isInstance) {
            cppTypeRef = typeMapper.createOptionalType(cppTypeRef)
        }

        val allTypes = LimeTypeHelper.getAllFieldTypes(limeField.typeRef.type)
        val hasImmutableType = allTypes.any { it.attributes.have(LimeAttributeType.IMMUTABLE) }

        val cppField = CppField(
            name = nameResolver.getName(limeField),
            fullyQualifiedName = nameResolver.getFullyQualifiedName(limeField),
            type = cppTypeRef,
            initializer = getPreviousResultOrNull(CppValue::class.java),
            isNotNull = isInstance && !isNullable,
            isNullable = isNullable,
            hasImmutableType = hasImmutableType,
            isClassEquatable = isInstance && limeField.typeRef.type.attributes.have(LimeAttributeType.EQUATABLE)
        )
        cppField.comment = limeField.comment

        storeNamedResult(limeField, cppField)
        closeContext()
    }

    override fun finishBuilding(limeTypeDef: LimeTypeDef) {
        val cppUsing = CppUsing(
            nameResolver.getName(limeTypeDef),
            nameResolver.getFullyQualifiedName(limeTypeDef),
            limeTypeDef.comment,
            getPreviousResult(CppTypeRef::class.java)
        )

        storeNamedResult(limeTypeDef, cppUsing)
        closeContext()
    }

    override fun finishBuilding(limeProperty: LimeProperty) {
        var cppTypeRef = getPreviousResult(CppTypeRef::class.java)
        val isNullable = limeProperty.attributes.have(LimeAttributeType.NULLABLE)
        val isInstance = limeProperty.typeRef.type is LimeContainer
        val isNotNull = !isNullable && isInstance
        if (isNullable && !isInstance) {
            cppTypeRef = typeMapper.createOptionalType(cppTypeRef)
        }

        val specifiers = when {
            limeProperty.isStatic -> EnumSet.of(CppMethod.Specifier.STATIC)
            else -> EnumSet.of(CppMethod.Specifier.VIRTUAL)
        }
        val getterQualifiers = when {
            limeProperty.isStatic -> EnumSet.noneOf(CppMethod.Qualifier::class.java)
            else -> EnumSet.of(CppMethod.Qualifier.CONST, CppMethod.Qualifier.PURE_VIRTUAL)
        }

        val getterMethod = CppMethod(
            name = nameResolver.getGetterName(limeProperty),
            fullyQualifiedName = nameResolver.getFullyQualifiedGetterName(limeProperty),
            comment = CommentsPreprocessor.preprocessGetterComment(limeProperty.comment),
            returnType = cppTypeRef,
            isNotNull = isNotNull,
            specifiers = specifiers,
            qualifiers = getterQualifiers
        )
        storeNamedResult(limeProperty, getterMethod)

        if (!limeProperty.isReadonly) {
            val setterParameter = CppParameter("value", cppTypeRef, isNotNull)
            val setterQualifiers = when {
                limeProperty.isStatic -> EnumSet.noneOf(CppMethod.Qualifier::class.java)
                else -> EnumSet.of(CppMethod.Qualifier.PURE_VIRTUAL)
            }
            val setterMethod = CppMethod(
                name = nameResolver.getSetterName(limeProperty),
                fullyQualifiedName = nameResolver.getFullyQualifiedSetterName(limeProperty),
                comment = CommentsPreprocessor.preprocessSetterComment(limeProperty.comment),
                parameters = listOf(setterParameter),
                specifiers = specifiers,
                qualifiers = setterQualifiers
            )
            storeResult(setterMethod)
        }

        closeContext()
    }

    override fun finishBuilding(limeEnumeration: LimeEnumeration) {
        val cppEnum = CppEnum(
            nameResolver.getName(limeEnumeration),
            nameResolver.getFullyQualifiedName(limeEnumeration),
            limeEnumeration.attributes.have(LimeAttributeType.EXTERNAL_TYPE),
            getPreviousResults(CppEnumItem::class.java)
        )
        cppEnum.comment = limeEnumeration.comment

        storeNamedResult(limeEnumeration, cppEnum)
        closeContext()
    }

    override fun finishBuilding(limeEnumerator: LimeEnumerator) {
        val cppEnumItem = CppEnumItem(
            nameResolver.getName(limeEnumerator),
            nameResolver.getFullyQualifiedName(limeEnumerator),
            getPreviousResultOrNull(CppValue::class.java)
        )
        cppEnumItem.comment = limeEnumerator.comment

        storeNamedResult(limeEnumerator, cppEnumItem)
        closeContext()
    }

    override fun finishBuilding(limeConstant: LimeConstant) {
        val cppConstant = CppConstant(
            nameResolver.getName(limeConstant),
            nameResolver.getFullyQualifiedName(limeConstant),
            getPreviousResult(CppTypeRef::class.java),
            getPreviousResult(CppValue::class.java)
        )
        cppConstant.comment = limeConstant.comment

        storeNamedResult(limeConstant, cppConstant)
        closeContext()
    }

    override fun finishBuilding(limeValue: LimeValue) {
        val valueType = limeValue.typeRef.type
        val isFloat = valueType is LimeBasicType && valueType.typeId == LimeBasicType.TypeId.FLOAT
        val cppValue = when (limeValue) {
            is LimeValue.Literal -> {
                val suffix = if (isFloat) "f" else ""
                CppValue(limeValue.value + suffix)
            }
            is LimeValue.Enumerator ->
                CppValue(nameResolver.getFullyQualifiedName(limeValue.valueRef.enumerator))
            is LimeValue.Special -> {
                val signPrefix = if (limeValue.value == ValueId.NEGATIVE_INFINITY) "-" else ""
                val typeString = if (isFloat) "float" else "double"
                val valueString = if (limeValue.value == ValueId.NAN) "quiet_NaN" else "infinity"
                CppValue(
                    "${signPrefix}std::numeric_limits<$typeString>::$valueString()",
                    listOf(CppLibraryIncludes.LIMITS))
            }
            is LimeValue.Null -> {
                val cppType = getPreviousResult(CppTypeRef::class.java)
                CppValue(
                    "${typeMapper.optionalTypeName}<${cppType.name}>()",
                    listOf(CppLibraryIncludes.OPTIONAL)
                )
            }
            is LimeValue.InitializerList -> CppValue("{}")
        }

        storeResult(cppValue)
        closeContext()
    }

    override fun finishBuilding(limeTypeRef: LimeTypeRef) {
        storeResult(typeMapper.mapType(limeTypeRef))
        closeContext()
    }
}