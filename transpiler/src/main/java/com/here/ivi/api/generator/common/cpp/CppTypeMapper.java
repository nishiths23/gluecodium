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

package com.here.ivi.api.generator.common.cpp;

import com.google.common.collect.Sets;
import com.here.ivi.api.TranspilerExecutionException;
import com.here.ivi.api.model.common.Includes;
import com.here.ivi.api.model.cppmodel.CppElements;
import com.here.ivi.api.model.cppmodel.CppType;
import com.here.ivi.api.model.franca.DefinedBy;
import com.here.ivi.api.model.franca.FrancaElement;
import com.here.ivi.api.model.rules.InstanceRules;
import java.util.HashSet;
import java.util.Set;
import navigation.BaseApiSpec;
import org.franca.core.franca.*;

public class CppTypeMapper {

  public static CppType map(
      FrancaElement<? extends BaseApiSpec.InterfacePropertyAccessor> rootModel,
      FArgument argument) {
    CppType type = CppTypeMapper.map(rootModel, argument.getType());

    if (argument.isArray()) {
      type =
          CppTypeMapper.wrapArrayType(
              DefinedBy.createFromFrancaElement(rootModel),
              type,
              CppTypeMapper.ArrayMode.map(rootModel, argument));
    }

    return type;
  }

  public static CppType map(
      FrancaElement<? extends BaseApiSpec.InterfacePropertyAccessor> rootModel,
      FAttribute attribute) {
    CppType type = CppTypeMapper.map(rootModel, attribute.getType());

    if (attribute.isArray()) {
      type =
          CppTypeMapper.wrapArrayType(
              DefinedBy.createFromFrancaElement(rootModel),
              type,
              CppTypeMapper.ArrayMode.map(rootModel, attribute));
    }

    return type;
  }

  public static CppType map(FrancaElement<?> rootModel, FConstantDef constant) {
    CppType type = CppTypeMapper.map(rootModel, constant.getType());

    if (constant.isArray()) {
      // FIXME impossible to define if a constant is a set or not inside Franca, always putting as vector
      // should have CppTypeMapper.ArrayMode.map(rootModel, constant)
      type =
          CppTypeMapper.wrapArrayType(
              DefinedBy.createFromFrancaElement(rootModel), type, ArrayMode.STD_VECTOR);
    }

    return type;
  }

  public static CppType map(FrancaElement<?> rootModel, FField field) {
    CppType type = CppTypeMapper.map(rootModel, field.getType());

    if (field.isArray()) {
      type =
          CppTypeMapper.wrapArrayType(
              DefinedBy.createFromFrancaElement(rootModel),
              type,
              CppTypeMapper.ArrayMode.map(rootModel, field));
    }

    return type;
  }

  public static CppType map(FrancaElement<?> rootModel, FTypeRef type) {

    if (type.getDerived() != null) {
      return mapDerived(rootModel, type);
    }

    if (type.getPredefined() != FBasicTypeId.UNDEFINED) {
      return mapPredefined(type);
    }

    return new CppType();
  }

  private static CppType mapDerived(FrancaElement<?> rootModel, FTypeRef type) {
    FType derived = type.getDerived();

    // types without a parent are not valid
    if (derived.eContainer() == null) {
      return reportInvalidType(rootModel, type);
    }

    if (derived instanceof FTypeDef) {
      return mapTypeDef(rootModel, (FTypeDef) derived);
    }
    if (derived instanceof FArrayType) {
      return mapArray(rootModel, (FArrayType) derived);
    }
    if (derived instanceof FMapType) {
      return mapMap(rootModel, (FMapType) derived);
    }
    if (derived instanceof FStructType) {
      return mapStruct(rootModel, (FStructType) derived);
    }
    if (derived instanceof FEnumerationType) {
      return mapEnum(rootModel, (FEnumerationType) derived);
    }

    return new CppType(null, "UNMAPPED DERIVED", CppElements.TypeInfo.Invalid);
  }

  private static CppType reportInvalidType(FrancaElement<?> rootModel, FTypeRef type) {
    DefinedBy definer = DefinedBy.createFromFModelElement(type);
    String name = "unknown";
    String typeDesc = "derived type";

    if (type.eContainer() instanceof FTypeDef) {
      name = ((FTypeDef) type.eContainer()).getName();
      typeDesc = "type reference";
    } else if (type.eContainer() instanceof FArgument) {
      FArgument arg = (FArgument) type.eContainer();
      name = arg.getName();

      // look at method name as well
      if (arg.eContainer() instanceof FMethod) {
        name = ((FMethod) arg.eContainer()).getName() + "::" + name;
      } else if (arg.eContainer() instanceof FBroadcast) {
        name = ((FMethod) arg.eContainer()).getName() + "::" + name;
      }

      typeDesc = "argument";
    } else if (type.eContainer() instanceof FField) {
      name = ((FField) type.eContainer()).getName();
      typeDesc = "field";
    } else if (type.eContainer() instanceof FAttribute) {
      name = ((FAttribute) type.eContainer()).getName();
      typeDesc = "attribute";
    }

    String formatMessage =
        "Failed resolving %s for '%s' in %s (indicates wrong typedef or missing include). "
            + "Type included in %s.";
    throw new TranspilerExecutionException(
        String.format(formatMessage, typeDesc, name, definer, rootModel));
  }

  private static CppType mapTypeDef(FrancaElement<?> rootModel, FTypeDef typedef) {
    DefinedBy typeRefDefiner = DefinedBy.createFromFModelElement(typedef);

    if (typedef.getActualType() == null) {
      return new CppType(typeRefDefiner, "NO ACTUAL TYPE FOUND", CppElements.TypeInfo.Invalid);
    } else if (InstanceRules.isInstanceId(typedef)) {
      Includes.Include include =
          new Includes.LazyInternalInclude(typeRefDefiner, Includes.InternalType.Interface);

      // each Instance type is defined directly in the Interface that is refers to, this is already
      // resolved in the typeRefDefiner, and named as the interface

      String name = CppNameRules.getClassName(typeRefDefiner.type.getName());
      String namespacedName = CppNamespaceUtils.getCppTypename(rootModel, typeRefDefiner, name);

      return new CppType(
          typeRefDefiner, namespacedName, CppElements.TypeInfo.InterfaceInstance, include);
    } else {
      CppType actual = map(rootModel, typedef.getActualType());
      DefinedBy actualTypeDefiner = actual.definedIn;

      // lookup where type came from, setup includes
      Includes.Include include = new Includes.LazyInternalInclude(actualTypeDefiner);

      String namespacedName = CppNamespaceUtils.getCppTypename(rootModel, typedef);
      // actually use the typedef in this case, not the underlying type
      return new CppType(typeRefDefiner, namespacedName, actual.info, include);
    }
  }

  private static CppType mapArray(FrancaElement<?> rootModel, FArrayType array) {
    DefinedBy arrayDefiner = DefinedBy.createFromFModelElement(array);

    String typeName = array.getName(); // use name defined for array
    if (typeName != null) {
      // lookup where array typedef came from, setup includes
      Set<Includes.Include> includes =
          Sets.newHashSet(new Includes.LazyInternalInclude(arrayDefiner));

      typeName = CppNamespaceUtils.getCppTypename(rootModel, array);

      return new CppType(arrayDefiner, typeName, CppElements.TypeInfo.Complex, includes);
    }

    FTypeRef elementType = array.getElementType();
    CppType actual = map(rootModel, elementType);

    // if no name is given, fallback to underlying type
    return wrapArrayType(arrayDefiner, actual, ArrayMode.map(rootModel, array));
  }

  public static CppType defineArray(FrancaElement<?> rootModel, FArrayType array) {
    DefinedBy arrayDefiner = DefinedBy.createFromFModelElement(array);
    FTypeRef elementType = array.getElementType();
    CppType actual = map(rootModel, elementType);

    return wrapArrayType(arrayDefiner, actual, ArrayMode.map(rootModel, array));
  }

  private enum ArrayMode {
    STD_VECTOR,
    STD_SET;

    public static ArrayMode map(FrancaElement<?> rootModel, FArrayType array) {
      return rootModel.getPropertyAccessor().getIsSet(array) ? STD_SET : STD_VECTOR;
    }

    public static ArrayMode map(FrancaElement<?> rootModel, FField field) {
      return rootModel.getPropertyAccessor().getIsSet(field) ? STD_SET : STD_VECTOR;
    }

    public static ArrayMode map(
        FrancaElement<? extends BaseApiSpec.InterfacePropertyAccessor> rootModel,
        FArgument argument) {
      return rootModel.getPropertyAccessor().getIsSet(argument) ? STD_SET : STD_VECTOR;
    }

    public static ArrayMode map(
        FrancaElement<? extends BaseApiSpec.InterfacePropertyAccessor> rootModel,
        FAttribute argument) {
      return rootModel.getPropertyAccessor().getIsSet(argument) ? STD_SET : STD_VECTOR;
    }
  }

  private static CppType wrapArrayType(DefinedBy definedIn, CppType elementType, ArrayMode mode) {
    CppType result;
    switch (mode) {
      case STD_VECTOR:
        result = wrapVector(elementType);
        break;
      case STD_SET:
        result = wrapSet(elementType);
        break;
      default:
        return null;
    }
    result.definedIn = definedIn;
    return result;
  }

  private static CppType mapMap(FrancaElement<?> rootModel, FMapType map) {
    DefinedBy mapDefiner = DefinedBy.createFromFModelElement(map);

    if (map.getKeyType() == null || map.getValueType() == null) {
      return new CppType(mapDefiner, "NO KEY OR VALUE TYPE FOUND", CppElements.TypeInfo.Invalid);
    } else {

      String typeName = map.getName(); // use name defined for map
      if (typeName != null) {
        // lookup where map typedef came from, setup includes
        Set<Includes.Include> includes =
            Sets.newHashSet(new Includes.LazyInternalInclude(mapDefiner));
        typeName = CppNamespaceUtils.getCppTypename(rootModel, map);

        return new CppType(mapDefiner, typeName, CppElements.TypeInfo.Complex, includes);
      }

      CppType key = map(rootModel, map.getKeyType());
      CppType value = map(rootModel, map.getValueType());

      // if no names are given, fallback to underlying type
      return wrapMapType(mapDefiner, key, value);
    }
  }

  public static CppType mapStruct(FrancaElement<?> rootModel, FStructType struct) {
    DefinedBy structDefiner = DefinedBy.createFromFModelElement(struct);

    if (struct.getElements().isEmpty()) {
      return new CppType(structDefiner, "EMPTY STRUCT", CppElements.TypeInfo.Invalid);
    } else {
      Includes.Include include = new Includes.LazyInternalInclude(structDefiner);
      String typeName = CppNamespaceUtils.getCppTypename(rootModel, struct);

      return new CppType(structDefiner, typeName, CppElements.TypeInfo.Complex, include);
    }
  }

  public static CppType mapEnum(FrancaElement<?> rootModel, FEnumerationType enumeration) {
    DefinedBy enumDefiner = DefinedBy.createFromFModelElement(enumeration);

    if (enumeration.getEnumerators().isEmpty()) {
      return new CppType(enumDefiner, "EMPTY ENUM", CppElements.TypeInfo.Invalid);
    } else {
      Includes.Include include = new Includes.LazyInternalInclude(enumDefiner);
      String typeName = CppNamespaceUtils.getCppTypename(rootModel, enumeration);

      return new CppType(enumDefiner, typeName, CppElements.TypeInfo.BuiltIn, include);
    }
  }

  public static CppType wrapMapType(DefinedBy mapDefiner, CppType key, CppType value) {
    // lookup where map types came from, setup includes
    Includes.Include keyInclude = new Includes.LazyInternalInclude(key.definedIn);
    Includes.Include valueInclude = new Includes.LazyInternalInclude(value.definedIn);

    String mapType = "std::map< " + key.name + ", " + value.name + " >";

    // include key type, value type and the map
    Set<Includes.Include> includes = new HashSet<>(key.includes);
    includes.add(keyInclude);
    includes.add(valueInclude);
    includes.add(CppLibraryIncludes.MAP);

    return new CppType(mapDefiner, mapType, CppElements.TypeInfo.Complex, includes);
  }

  private static CppType wrapStdTemplateType(
      CppType content, String templateName, Includes.Include libraryInclude) {
    // lookup where content type came from, setup includes
    Includes.Include typeInclude = new Includes.LazyInternalInclude(content.definedIn);

    String mapType = "std::" + templateName + "< " + content.name + " >";

    Set<Includes.Include> includes = new HashSet<>(content.includes);
    includes.add(typeInclude);
    includes.add(libraryInclude);

    return new CppType(content.definedIn, mapType, CppElements.TypeInfo.Complex, includes);
  }

  public static CppType wrapUniquePtr(CppType content) {
    return wrapStdTemplateType(content, "unique_ptr", CppLibraryIncludes.MEMORY);
  }

  public static CppType wrapSharedPtr(CppType content) {
    return wrapStdTemplateType(content, "shared_ptr", CppLibraryIncludes.MEMORY);
  }

  public static CppType wrapWeakPtr(CppType content) {
    return wrapStdTemplateType(content, "weak_ptr", CppLibraryIncludes.MEMORY);
  }

  public static CppType wrapVector(CppType content) {
    return wrapStdTemplateType(content, "vector", CppLibraryIncludes.VECTOR);
  }

  public static CppType wrapSet(CppType content) {
    return wrapStdTemplateType(content, "set", CppLibraryIncludes.SET);
  }

  public static String mapPredefinedToTypeName(final FBasicTypeId basicTypeId) {
    switch (basicTypeId.getValue()) {
      case FBasicTypeId.BOOLEAN_VALUE:
        return "bool";
      case FBasicTypeId.FLOAT_VALUE:
        return "float";
      case FBasicTypeId.DOUBLE_VALUE:
        return "double";
      case FBasicTypeId.INT8_VALUE:
        return "int8_t";
      case FBasicTypeId.INT16_VALUE:
        return "int16_t";
      case FBasicTypeId.INT32_VALUE:
        return "int32_t";
      case FBasicTypeId.INT64_VALUE:
        return "int64_t";
      case FBasicTypeId.UINT8_VALUE:
        return "uint8_t";
      case FBasicTypeId.UINT16_VALUE:
        return "uint16_t";
      case FBasicTypeId.UINT32_VALUE:
        return "uint32_t";
      case FBasicTypeId.UINT64_VALUE:
        return "uint64_t";
      case FBasicTypeId.STRING_VALUE:
        return "std::string";
      case FBasicTypeId.BYTE_BUFFER_VALUE:
        return "std::vector< uint8_t >";
      default:
        return "UNMAPPED PREDEFINED [" + basicTypeId.getName() + "]";
    }
  }

  private static CppType mapPredefined(final FTypeRef type) {
    DefinedBy definer = DefinedBy.createFromFModelElement(type);

    switch (type.getPredefined().getValue()) {
      case FBasicTypeId.BOOLEAN_VALUE:
      case FBasicTypeId.FLOAT_VALUE:
      case FBasicTypeId.DOUBLE_VALUE:
        return new CppType(
            definer, mapPredefinedToTypeName(type.getPredefined()), CppElements.TypeInfo.BuiltIn);
      case FBasicTypeId.INT8_VALUE:
      case FBasicTypeId.INT16_VALUE:
      case FBasicTypeId.INT32_VALUE:
      case FBasicTypeId.INT64_VALUE:
      case FBasicTypeId.UINT8_VALUE:
      case FBasicTypeId.UINT16_VALUE:
      case FBasicTypeId.UINT32_VALUE:
      case FBasicTypeId.UINT64_VALUE:
        return new CppType(
            definer,
            mapPredefinedToTypeName(type.getPredefined()),
            CppElements.TypeInfo.BuiltIn,
            CppLibraryIncludes.INT_TYPES);
      case FBasicTypeId.STRING_VALUE:
        return new CppType(
            definer,
            mapPredefinedToTypeName(type.getPredefined()),
            CppElements.TypeInfo.Complex,
            CppLibraryIncludes.STRING);
      case FBasicTypeId.BYTE_BUFFER_VALUE:
        return new CppType(
            definer,
            mapPredefinedToTypeName(type.getPredefined()),
            CppElements.TypeInfo.Complex,
            CppLibraryIncludes.VECTOR);
      default:
        return new CppType(
            definer, mapPredefinedToTypeName(type.getPredefined()), CppElements.TypeInfo.Invalid);
    }
  }
}
