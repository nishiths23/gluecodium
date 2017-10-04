/*
 * Copyright (C) 2017 HERE Global B.V. and its affiliate(s). All rights reserved.
 *
 * This software, including documentation, is protected by copyright controlled by
 * HERE Global B.V. All rights are reserved. Copying, including reproducing, storing,
 * adapting or translating, any or all of this material requires the prior written
 * consent of HERE Global B.V. This material also contains confidential information,
 * which may not be disclosed to others without prior written consent of HERE Global B.V.
 *
 */

package com.here.ivi.api.generator.baseapi;

import com.google.common.annotations.VisibleForTesting;
import com.here.ivi.api.common.CollectionsHelper;
import com.here.ivi.api.generator.common.AbstractModelBuilder;
import com.here.ivi.api.generator.common.ModelBuilderContextStack;
import com.here.ivi.api.generator.cpp.CppDefaultInitializer;
import com.here.ivi.api.generator.cpp.CppNameRules;
import com.here.ivi.api.generator.cpp.CppTypeMapper;
import com.here.ivi.api.generator.cpp.CppValueMapper;
import com.here.ivi.api.model.cppmodel.*;
import com.here.ivi.api.model.franca.FrancaElement;
import com.here.ivi.api.model.rules.InstanceRules;
import java.util.*;
import java.util.stream.Collectors;
import org.franca.core.franca.*;

public class CppModelBuilder extends AbstractModelBuilder<CppElement> {

  private final FrancaElement rootModel;
  private final CppTypeMapper typeMapper;
  private final CppValueMapper valueMapper;

  @VisibleForTesting
  CppModelBuilder(
      final ModelBuilderContextStack<CppElement> contextStack,
      final FrancaElement rootModel,
      final CppTypeMapper typeMapper,
      final CppValueMapper valueMapper) {
    super(contextStack);
    this.rootModel = rootModel;
    this.typeMapper = typeMapper;
    this.valueMapper = valueMapper;
  }

  public CppModelBuilder(final FrancaElement rootModel, final CppIncludeResolver includeResolver) {
    this(
        new ModelBuilderContextStack<>(),
        rootModel,
        new CppTypeMapper(includeResolver),
        new CppValueMapper(includeResolver));
  }

  @Override
  public void finishBuilding(FInterface francaInterface) {

    String className = CppNameRules.getClassName(francaInterface.getName());

    List<CppElement> previousResults = getCurrentContext().previousResults;

    CppClass cppClass = new CppClass(className);
    cppClass.comment = CppCommentParser.parse(francaInterface).getMainBodyText();

    cppClass.methods.addAll(CollectionsHelper.getAllOfType(previousResults, CppMethod.class));
    cppClass.enums.addAll(CollectionsHelper.getAllOfType(previousResults, CppEnum.class));
    cppClass.usings.addAll(CollectionsHelper.getAllOfType(previousResults, CppUsing.class));
    cppClass.structs.addAll(CollectionsHelper.getAllOfType(previousResults, CppStruct.class));

    storeResult(cppClass);
    closeContext();
  }

  @Override
  public void finishBuilding(FMethod francaMethod) {

    List<CppParameter> outputParameters =
        CollectionsHelper.getStreamOfType(getCurrentContext().previousResults, CppParameter.class)
            .filter(parameter -> parameter.isOutput)
            .collect(Collectors.toList());
    CppMethodMapper.ReturnTypeData returnTypeData =
        CppMethodMapper.mapMethodReturnType(typeMapper, francaMethod, outputParameters);
    CppMethod cppMethod = buildCppMethod(francaMethod, returnTypeData.type, returnTypeData.comment);

    storeResult(cppMethod);
    closeContext();
  }

  @Override
  public void finishBuildingInputArgument(FArgument francaArgument) {

    CppTypeRef cppTypeRef =
        CollectionsHelper.getFirstOfType(getCurrentContext().previousResults, CppTypeRef.class);
    CppParameter cppParameter = new CppParameter(francaArgument.getName(), cppTypeRef);

    storeResult(cppParameter);
    closeContext();
  }

  @Override
  public void finishBuildingOutputArgument(FArgument francaArgument) {

    CppTypeRef cppTypeRef =
        CollectionsHelper.getFirstOfType(getCurrentContext().previousResults, CppTypeRef.class);
    CppParameter cppParameter = new CppParameter(francaArgument.getName(), cppTypeRef, true);

    storeResult(cppParameter);
    closeContext();
  }

  @Override
  public void finishBuilding(FTypeCollection francaTypeCollection) {

    for (CppElement cppElement : getCurrentContext().previousResults) {
      if (cppElement instanceof CppEnum
          || cppElement instanceof CppConstant
          || cppElement instanceof CppUsing
          || cppElement instanceof CppStruct) {
        storeResult(cppElement);
      }
    }

    closeContext();
  }

  @Override
  public void finishBuilding(FConstantDef francaConstant) {

    CppTypeRef cppTypeRef =
        CollectionsHelper.getFirstOfType(getCurrentContext().previousResults, CppTypeRef.class);
    CppValue value = valueMapper.map(cppTypeRef, francaConstant.getRhs());

    String name = CppNameRules.getConstantName(francaConstant.getName());
    String fullyQualifiedName = CppNameRules.getConstantFullyQualifiedName(francaConstant);
    CppConstant cppConstant = new CppConstant(name, fullyQualifiedName, cppTypeRef, value);
    cppConstant.comment = CppCommentParser.parse(francaConstant).getMainBodyText();

    storeResult(cppConstant);
    closeContext();
  }

  @Override
  public void finishBuilding(FField francaField) {

    CppTypeRef cppTypeRef =
        CollectionsHelper.getFirstOfType(getCurrentContext().previousResults, CppTypeRef.class);
    String fieldName = CppNameRules.getFieldName(francaField.getName());
    CppValue cppValue = CppDefaultInitializer.map(francaField);

    CppField cppField = new CppField(cppTypeRef, fieldName, cppValue);
    cppField.comment = CppCommentParser.parse(francaField).getMainBodyText();

    storeResult(cppField);
    closeContext();
  }

  @Override
  public void finishBuilding(FStructType francaStructType) {

    CppStruct cppStruct = buildCompoundType(francaStructType, false);
    if (francaStructType.getBase() != null) {
      CppTypeRef parentTypeRef = typeMapper.mapStruct(francaStructType.getBase());
      cppStruct.inheritances.add(new CppInheritance(parentTypeRef, CppInheritance.Type.Public));
    }

    storeResult(cppStruct);
    closeContext();
  }

  @Override
  public void finishBuilding(FTypeDef francaTypeDef) {

    if (!InstanceRules.isInstanceId(francaTypeDef)) {
      String typeDefName = CppNameRules.getTypedefName(francaTypeDef.getName());
      String fullyQualifiedName = CppNameRules.getFullyQualifiedName(francaTypeDef);
      CppTypeRef cppTypeRef =
          CollectionsHelper.getFirstOfType(getCurrentContext().previousResults, CppTypeRef.class);

      CppUsing cppUsing = new CppUsing(typeDefName, fullyQualifiedName, cppTypeRef);
      cppUsing.comment = CppCommentParser.parse(francaTypeDef).getMainBodyText();

      storeResult(cppUsing);
    }

    closeContext();
  }

  @Override
  public void finishBuilding(FArrayType francaArrayType) {

    String name = CppNameRules.getTypedefName(francaArrayType.getName());
    CppTypeRef elementType =
        CollectionsHelper.getFirstOfType(getCurrentContext().previousResults, CppTypeRef.class);
    CppTypeRef targetType =
        CppTemplateTypeRef.create(CppTemplateTypeRef.TemplateClass.VECTOR, elementType);
    CppUsing cppUsing = new CppUsing(name, targetType);
    cppUsing.comment = CppCommentParser.parse(francaArrayType).getMainBodyText();

    storeResult(cppUsing);
    closeContext();
  }

  @Override
  public void finishBuilding(FMapType francaMapType) {

    String name = CppNameRules.getTypedefName(francaMapType.getName());
    List<CppTypeRef> typeRefs =
        CollectionsHelper.getAllOfType(getCurrentContext().previousResults, CppTypeRef.class);
    CppTypeRef targetType = CppTypeMapper.wrapMap(typeRefs.get(0), typeRefs.get(1));
    CppUsing cppUsing = new CppUsing(name, targetType);
    cppUsing.comment = CppCommentParser.parse(francaMapType).getMainBodyText();

    storeResult(cppUsing);
    closeContext();
  }

  @Override
  public void finishBuilding(FEnumerationType francaEnumerationType) {

    String enumName = CppNameRules.getEnumName(francaEnumerationType.getName());
    String fullyQualifiedName = CppNameRules.getFullyQualifiedName(francaEnumerationType);

    CppEnum cppEnum = CppEnum.createScoped(enumName, fullyQualifiedName);
    cppEnum.comment = CppCommentParser.parse(francaEnumerationType).getMainBodyText();
    cppEnum.items =
        CollectionsHelper.getAllOfType(getCurrentContext().previousResults, CppEnumItem.class);

    storeResult(cppEnum);
    closeContext();
  }

  @Override
  public void finishBuilding(FEnumerator francaEnumerator) {

    String enumItemName = CppNameRules.getEnumEntryName(francaEnumerator.getName());
    CppValue cppValue =
        CollectionsHelper.getFirstOfType(getCurrentContext().previousResults, CppValue.class);
    CppEnumItem cppEnumItem = new CppEnumItem(enumItemName, cppValue);
    cppEnumItem.comment = CppCommentParser.parse(francaEnumerator).getMainBodyText();

    storeResult(cppEnumItem);
    closeContext();
  }

  @Override
  public void finishBuilding(FExpression francaExpression) {

    storeResult(CppValueMapper.map(francaExpression));
    closeContext();
  }

  @Override
  public void finishBuilding(FTypeRef francaTypeRef) {

    CppTypeRef cppTypeRef = typeMapper.map(francaTypeRef);
    if (francaTypeRef.eContainer() instanceof FTypedElement
        && ((FTypedElement) francaTypeRef.eContainer()).isArray()) {
      cppTypeRef = CppTemplateTypeRef.create(CppTemplateTypeRef.TemplateClass.VECTOR, cppTypeRef);
    }

    storeResult(cppTypeRef);
    closeContext();
  }

  @Override
  public void finishBuilding(FUnionType francaUnionType) {

    storeResult(buildCompoundType(francaUnionType, true));
    closeContext();
  }

  @Override
  public void finishBuilding(FAttribute francaAttribute) {

    CppTypeRef cppTypeRef =
        CollectionsHelper.getFirstOfType(getCurrentContext().previousResults, CppTypeRef.class);

    String getterName = CppNameRules.getGetterName(francaAttribute.getName());
    CppMethod getterMethod = buildAccessorMethod(getterName, cppTypeRef);
    storeResult(getterMethod);

    if (!francaAttribute.isReadonly()) {
      String setterName = CppNameRules.getSetterName(francaAttribute.getName());
      CppMethod setterMethod = buildAccessorMethod(setterName, CppPrimitiveTypeRef.VOID);
      setterMethod.parameters.add(new CppParameter("value", cppTypeRef));
      storeResult(setterMethod);
    }

    closeContext();
  }

  private CppMethod buildAccessorMethod(String getterName, CppTypeRef cppTypeRef) {

    CppMethod getterMethod = new CppMethod.Builder(getterName).returnType(cppTypeRef).build();
    getterMethod.specifiers.add(CppMethod.Specifier.VIRTUAL);
    getterMethod.qualifiers.add(CppMethod.Qualifier.PURE_VIRTUAL);

    return getterMethod;
  }

  private CppMethod buildCppMethod(
      FMethod francaMethod, CppTypeRef returnType, String returnTypeComment) {

    String methodName = CppNameRules.getMethodName(francaMethod.getName());
    CppMethod.Builder builder = new CppMethod.Builder(methodName).returnType(returnType);

    if (rootModel.isStatic(francaMethod)) {
      builder.specifier(CppMethod.Specifier.STATIC);
    } else {
      if (rootModel.isConst(francaMethod)) {
        // const needs to be before "= 0" pure virtual specifier
        builder.qualifier(CppMethod.Qualifier.CONST);
      }
      builder.specifier(CppMethod.Specifier.VIRTUAL);
      builder.qualifier(CppMethod.Qualifier.PURE_VIRTUAL);
    }

    StringBuilder methodCommentBuilder =
        new StringBuilder(CppCommentParser.parse(francaMethod).getMainBodyText());
    if (!returnTypeComment.isEmpty()) {
      methodCommentBuilder.append(
          CppCommentParser.FORMATTER.formatTag("@return", returnTypeComment));
    }
    builder.comment(methodCommentBuilder.toString());

    CollectionsHelper.getStreamOfType(getCurrentContext().previousResults, CppParameter.class)
        .filter(parameter -> !parameter.isOutput)
        .forEach(builder::parameter);

    return builder.build();
  }

  private CppStruct buildCompoundType(
      final FCompoundType francaCompoundType, final boolean isUnion) {

    String name = CppNameRules.getStructName(francaCompoundType.getName());
    String fullyQualifiedName = CppNameRules.getFullyQualifiedName(francaCompoundType);
    CppStruct cppStruct =
        isUnion
            ? new CppTaggedUnion(name, fullyQualifiedName)
            : new CppStruct(name, fullyQualifiedName);
    cppStruct.comment = CppCommentParser.parse(francaCompoundType).getMainBodyText();

    List<CppField> elements =
        CollectionsHelper.getAllOfType(getCurrentContext().previousResults, CppField.class);

    cppStruct.fields.addAll(elements);
    return cppStruct;
  }
}
