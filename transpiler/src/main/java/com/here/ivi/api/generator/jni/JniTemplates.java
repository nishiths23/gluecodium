/*
 * Copyright (c) 2016-2018 HERE Europe B.V.
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

package com.here.ivi.api.generator.jni;

import com.google.common.annotations.VisibleForTesting;
import com.here.ivi.api.generator.common.GeneratedFile;
import com.here.ivi.api.generator.common.TemplateEngine;
import com.here.ivi.api.generator.cpp.CppLibraryIncludes;
import com.here.ivi.api.model.common.Include;
import com.here.ivi.api.model.jni.JniContainer;
import com.here.ivi.api.platform.android.AndroidGeneratorSuite;
import java.util.*;
import java.util.stream.Collectors;

public final class JniTemplates {

  @VisibleForTesting public static final String INCLUDES_NAME = "includes";
  @VisibleForTesting public static final String MODELS_NAME = "models";
  private static final String BASE_PACKAGES_NAME = "basePackages";
  private static final String CONTAINER_NAME = "container";
  private static final String INTERNAL_NAMESPACE_NAME = "internalNamespace";

  private static final String JNI_UTILS_TEMPLATE_PREFIX = "jni/utils/";
  private static final String HEADER_TEMPLATE_SUFFIX = "Header";
  private static final String IMPL_TEMPLATE_SUFFIX = "Implementation";

  private final List<String> basePackages;
  private final String internalNamespace;

  public JniTemplates(final List<String> basePackages, final String internalNamespace) {
    this.basePackages = basePackages;
    this.internalNamespace = internalNamespace;
  }

  public List<GeneratedFile> generateFiles(final JniContainer jniContainer) {

    List<GeneratedFile> results = new LinkedList<>();
    if (jniContainer == null) {
      return results;
    }

    Map<String, Object> containerData = new HashMap<>();
    containerData.put(CONTAINER_NAME, jniContainer);
    containerData.put(INTERNAL_NAMESPACE_NAME, internalNamespace);

    String fileName = JniNameRules.getJniClassFileName(jniContainer);
    results.add(
        generateFile("jni/Header", containerData, JniNameRules.getHeaderFilePath(fileName)));
    results.add(
        generateFile(
            "jni/Implementation", containerData, JniNameRules.getImplementationFilePath(fileName)));

    return results;
  }

  public List<GeneratedFile> generateConversionFiles(final List<JniContainer> jniContainers) {
    List<GeneratedFile> results = new LinkedList<>();
    if (jniContainers == null || jniContainers.isEmpty()) {
      return results;
    }

    addStructConversionFiles(jniContainers, results);
    addInstanceConversionFiles(jniContainers, results);
    addEnumConversionFiles(jniContainers, results);
    addCppProxyFiles(jniContainers, results);

    return results;
  }

  public GeneratedFile generateConversionUtilsHeaderFile(final String fileName) {
    return generateFile(
        JNI_UTILS_TEMPLATE_PREFIX + fileName + HEADER_TEMPLATE_SUFFIX,
        internalNamespace,
        JniNameRules.getHeaderFilePath(fileName));
  }

  public GeneratedFile generateConversionUtilsImplementationFile(final String fileName) {
    return generateFile(
        JNI_UTILS_TEMPLATE_PREFIX + fileName + IMPL_TEMPLATE_SUFFIX,
        internalNamespace,
        JniNameRules.getImplementationFilePath(fileName));
  }

  private void addStructConversionFiles(
      List<JniContainer> jniContainers, List<GeneratedFile> results) {

    final Set<Include> includes = new LinkedHashSet<>();
    jniContainers.forEach(model -> includes.addAll(model.includes));

    Map<String, Object> mustacheData = new HashMap<>();
    mustacheData.put(INCLUDES_NAME, includes);
    mustacheData.put(MODELS_NAME, jniContainers);
    mustacheData.put(INTERNAL_NAMESPACE_NAME, internalNamespace);

    results.add(
        generateFile(
            "jni/StructConversionHeader",
            mustacheData,
            JniNameRules.getHeaderFilePath(JniNameRules.JNI_STRUCT_CONVERSION_NAME)));

    mustacheData.put(
        INCLUDES_NAME,
        Arrays.asList(
            Include.createInternalInclude(
                JniNameRules.getHeaderFileName(JniNameRules.JNI_STRUCT_CONVERSION_NAME)),
            CppLibraryIncludes.INT_TYPES,
            CppLibraryIncludes.VECTOR,
            Include.createInternalInclude(
                JniNameRules.getHeaderFileName(AndroidGeneratorSuite.FIELD_ACCESS_UTILS)),
            Include.createInternalInclude(
                JniNameRules.getHeaderFileName(JniNameRules.JNI_ENUM_CONVERSION_NAME))));

    results.add(
        generateFile(
            "jni/StructConversionImplementation",
            mustacheData,
            JniNameRules.getImplementationFilePath(JniNameRules.JNI_STRUCT_CONVERSION_NAME)));
  }

  private void addEnumConversionFiles(
      List<JniContainer> jniContainers, List<GeneratedFile> results) {
    final Set<Include> includes = new LinkedHashSet<>();
    jniContainers.forEach(model -> includes.addAll(model.includes));

    Map<String, Object> mustacheData = new HashMap<>();
    mustacheData.put(INCLUDES_NAME, includes);
    mustacheData.put(MODELS_NAME, jniContainers);
    mustacheData.put(INTERNAL_NAMESPACE_NAME, internalNamespace);

    results.add(
        generateFile(
            "jni/EnumConversionHeader",
            mustacheData,
            JniNameRules.getHeaderFilePath(JniNameRules.JNI_ENUM_CONVERSION_NAME)));

    mustacheData.put(
        INCLUDES_NAME,
        Collections.singletonList(
            Include.createInternalInclude(
                JniNameRules.getHeaderFileName(JniNameRules.JNI_ENUM_CONVERSION_NAME))));

    results.add(
        generateFile(
            "jni/EnumConversionImplementation",
            mustacheData,
            JniNameRules.getImplementationFilePath(JniNameRules.JNI_ENUM_CONVERSION_NAME)));
  }

  private void addInstanceConversionFiles(
      List<JniContainer> jniContainers, List<GeneratedFile> results) {

    List<JniContainer> instanceContainers =
        jniContainers
            .stream()
            .filter(container -> container.isFrancaInterface)
            .collect(Collectors.toList());

    Map<String, Object> instanceData = new HashMap<>();
    final Set<Include> instanceIncludes = new LinkedHashSet<>();
    instanceIncludes.add(CppLibraryIncludes.MEMORY);
    instanceIncludes.add(CppLibraryIncludes.NEW);
    instanceContainers.forEach(container -> instanceIncludes.addAll(container.includes));
    instanceData.put(INCLUDES_NAME, instanceIncludes);
    instanceData.put(MODELS_NAME, instanceContainers);
    instanceData.put(BASE_PACKAGES_NAME, basePackages);
    instanceData.put(INTERNAL_NAMESPACE_NAME, internalNamespace);

    results.add(
        generateFile(
            "jni/InstanceConversionHeader",
            instanceData,
            JniNameRules.getHeaderFilePath(JniNameRules.JNI_INSTANCE_CONVERSION_NAME)));

    instanceIncludes.add(
        Include.createInternalInclude(
            JniNameRules.getHeaderFileName(JniNameRules.JNI_INSTANCE_CONVERSION_NAME)));

    results.add(
        generateFile(
            "jni/InstanceConversionImplementation",
            instanceData,
            JniNameRules.getImplementationFilePath(JniNameRules.JNI_INSTANCE_CONVERSION_NAME)));
  }

  private void addCppProxyFiles(
      final List<JniContainer> jniContainers, final List<GeneratedFile> results) {

    List<JniContainer> listeners =
        jniContainers.stream().filter(JniTemplates::isListener).collect(Collectors.toList());

    List<Include> proxyIncludes = new LinkedList<>();

    for (JniContainer jniContainer : listeners) {

      Map<String, Object> containerData = new HashMap<>();
      containerData.put(CONTAINER_NAME, jniContainer);
      containerData.put(INTERNAL_NAMESPACE_NAME, internalNamespace);

      String fileName =
          JniNameRules.getJniClassFileName(jniContainer) + JniNameRules.JNI_CPP_PROXY_SUFFIX;
      results.add(
          generateFile(
              "jni/CppProxyHeader", containerData, JniNameRules.getHeaderFilePath(fileName)));

      Include headerInclude =
          Include.createInternalInclude(JniNameRules.getHeaderFileName(fileName));

      proxyIncludes.add(headerInclude);

      containerData.put("headerInclude", headerInclude);

      results.add(
          generateFile(
              "jni/CppProxyImplementation",
              containerData,
              JniNameRules.getImplementationFilePath(fileName)));
    }

    Map<String, Object> mustacheData = new HashMap<>();
    mustacheData.put(INCLUDES_NAME, proxyIncludes);
    mustacheData.put(MODELS_NAME, listeners);
    mustacheData.put(INTERNAL_NAMESPACE_NAME, internalNamespace);

    results.add(
        generateFile(
            "jni/ProxyGeneratorHeader",
            mustacheData,
            JniNameRules.getHeaderFilePath(JniNameRules.JNI_PROXY_CONVERSION_NAME)));
  }

  private static boolean isListener(final JniContainer jniContainer) {

    return jniContainer.isInterface
        && jniContainer.methods.stream().allMatch(method -> method.returnType == null);
  }

  private static GeneratedFile generateFile(
      final String templateName, final Object data, final String fileName) {
    return new GeneratedFile(TemplateEngine.render(templateName, data), fileName);
  }
}
