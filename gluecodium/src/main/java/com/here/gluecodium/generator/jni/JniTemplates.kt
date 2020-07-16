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

package com.here.gluecodium.generator.jni

import com.here.gluecodium.generator.common.GeneratedFile
import com.here.gluecodium.generator.common.templates.TemplateEngine
import com.here.gluecodium.generator.cpp.Cpp2IncludeResolver
import com.here.gluecodium.generator.cpp.Cpp2NameResolver
import com.here.gluecodium.generator.cpp.CppFullNameResolver
import com.here.gluecodium.generator.cpp.CppNameResolver
import com.here.gluecodium.generator.cpp.CppNameRules
import com.here.gluecodium.generator.java.JavaNameRules
import com.here.gluecodium.model.common.Include
import com.here.gluecodium.model.lime.LimeContainer
import com.here.gluecodium.model.lime.LimeContainerWithInheritance
import com.here.gluecodium.model.lime.LimeElement
import com.here.gluecodium.model.lime.LimeEnumeration
import com.here.gluecodium.model.lime.LimeInterface
import com.here.gluecodium.model.lime.LimeLambda
import com.here.gluecodium.model.lime.LimeNamedElement
import com.here.gluecodium.model.lime.LimeStruct
import com.here.gluecodium.model.lime.LimeTypeHelper
import com.here.gluecodium.model.lime.LimeTypesCollection

internal class JniTemplates(
    limeReferenceMap: Map<String, LimeElement>,
    javaNameRules: JavaNameRules,
    private val basePackages: List<String>,
    private val internalPackages: List<String>,
    private val internalNamespace: List<String>,
    cppNameRules: CppNameRules,
    rootNamespace: List<String>,
    generatorName: String
) {
    private val jniNameResolver = JniNameResolver(limeReferenceMap, basePackages, javaNameRules)
    private val fileNameRules = JniFileNameRules(generatorName, basePackages, jniNameResolver)
    private val cachingNameResolver = CppNameResolver(rootNamespace, limeReferenceMap, cppNameRules)
    private val nameResolvers = mapOf(
        "" to jniNameResolver,
        "signature" to JniTypeSignatureNameResolver(jniNameResolver),
        "mangled" to JniMangledNameResolver(jniNameResolver),
        "C++" to Cpp2NameResolver(limeReferenceMap, internalNamespace, cachingNameResolver),
        "C++ FQN" to CppFullNameResolver(cachingNameResolver)
    )
    private val predicates = JniGeneratorPredicates(limeReferenceMap, javaNameRules).predicates

    private val jniIncludeResolver = JniIncludeResolver(fileNameRules)
    private val cppIncludeResolver = Cpp2IncludeResolver(limeReferenceMap, cppNameRules, internalNamespace)

    fun generateFiles(limeElement: LimeNamedElement): List<GeneratedFile> {
        if (limeElement !is LimeContainer) {
            return emptyList()
        }

        return limeElement.structs.filter { it.functions.isNotEmpty() }.flatMap { generateFilesForElement(it) } +
             if (limeElement !is LimeTypesCollection) generateFilesForElement(limeElement) else emptyList()
    }

    private fun generateFilesForElement(limeElement: LimeContainer): List<GeneratedFile> {
        val containerData = mutableMapOf("container" to limeElement, "internalNamespace" to internalNamespace)
        val fileName = fileNameRules.getElementFileName(limeElement)

        val headerFile = GeneratedFile(
            TemplateEngine.render("jni/Header", containerData, nameResolvers, predicates),
            fileNameRules.getHeaderFilePath(fileName)
        )

        val implIncludes = when (limeElement) {
            is LimeStruct -> jniIncludeResolver.collectFunctionImplementationIncludes(limeElement)
            else -> jniIncludeResolver.collectImplementationIncludes(limeElement)
        } + Include.createInternalInclude("$fileName.h")
        containerData["includes"] = implIncludes.distinct().sorted()

        val implFile = GeneratedFile(
            TemplateEngine.render("jni/Implementation", containerData, nameResolvers, predicates),
            fileNameRules.getImplementationFilePath(fileName)
        )

        return listOf(headerFile, implFile)
    }

    fun generateConversionFiles(limeElements: List<LimeNamedElement>): List<GeneratedFile> {
        val allTypes = limeElements.flatMap { LimeTypeHelper.getAllTypes(it) }
        val lambdas = allTypes.filterIsInstance<LimeLambda>()
        return allTypes.filterIsInstance<LimeStruct>().flatMap { generateStructConversionFiles(it) } +
            allTypes.filterIsInstance<LimeEnumeration>().flatMap { generateEnumConversionFiles(it) } +
            (lambdas + allTypes.filterIsInstance<LimeContainerWithInheritance>()).flatMap {
                generateInstanceConversionFiles(it)
            } + (lambdas + allTypes.filterIsInstance<LimeInterface>()).flatMap { generateCppProxyFiles(it) }
    }

    fun generateConversionUtilsHeaderFile(fileName: String) =
        GeneratedFile(
            TemplateEngine.render(
                "jni/utils/${fileName}Header",
                mapOf("internalNamespace" to internalNamespace)
            ),
            fileNameRules.getHeaderFilePath(fileName),
            GeneratedFile.SourceSet.COMMON
        )

    fun generateConversionUtilsImplementationFile(fileName: String) =
        GeneratedFile(
            TemplateEngine.render(
                "jni/utils/${fileName}Implementation",
                mapOf(
                    "internalNamespace" to internalNamespace,
                    "internalPackages" to basePackages + internalPackages
                )
            ),
            fileNameRules.getImplementationFilePath(fileName),
            GeneratedFile.SourceSet.COMMON
        )

    private fun generateStructConversionFiles(limeStruct: LimeStruct): List<GeneratedFile> {
        val mustacheData = mutableMapOf(
            "struct" to limeStruct,
            "includes" to cppIncludeResolver.resolveIncludes(limeStruct).distinct().sorted(),
            "internalNamespace" to internalNamespace
        )

        val fileName = fileNameRules.getConversionFileName(limeStruct)
        val headerFile = GeneratedFile(
            TemplateEngine.render("jni/StructConversionHeader", mustacheData, nameResolvers),
            fileNameRules.getHeaderFilePath(fileName)
        )

        val includes = jniIncludeResolver.collectConversionImplementationIncludes(limeStruct) +
                Include.createInternalInclude("$fileName.h")
        mustacheData["includes"] = includes.distinct().sorted()
        val implFile = GeneratedFile(
            TemplateEngine.render("jni/StructConversionImplementation", mustacheData, nameResolvers, predicates),
            fileNameRules.getImplementationFilePath(fileName)
        )

        return listOf(headerFile, implFile)
    }

    private fun generateEnumConversionFiles(limeEnumeration: LimeEnumeration): List<GeneratedFile> {
        val mustacheData = mutableMapOf(
            "enum" to limeEnumeration,
            "includes" to cppIncludeResolver.resolveIncludes(limeEnumeration).distinct().sorted(),
            "internalNamespace" to internalNamespace
        )

        val fileName = fileNameRules.getConversionFileName(limeEnumeration)
        val headerFile = GeneratedFile(
            TemplateEngine.render("jni/EnumConversionHeader", mustacheData, nameResolvers),
            fileNameRules.getHeaderFilePath(fileName)
        )

        mustacheData["includes"] = listOf(Include.createInternalInclude("$fileName.h"))
        val implFile = GeneratedFile(
            TemplateEngine.render("jni/EnumConversionImplementation", mustacheData, nameResolvers),
            fileNameRules.getImplementationFilePath(fileName)
        )

        return listOf(headerFile, implFile)
    }

    private fun generateInstanceConversionFiles(limeElement: LimeNamedElement): List<GeneratedFile> {
        val mustacheData = mutableMapOf(
            "model" to limeElement,
            "includes" to cppIncludeResolver.resolveIncludes(limeElement).distinct().sorted(),
            "basePackages" to basePackages,
            "internalPackages" to basePackages + internalPackages,
            "internalNamespace" to internalNamespace
        )

        val fileName = fileNameRules.getConversionFileName(limeElement)
        val headerFile = GeneratedFile(
            TemplateEngine.render("jni/InstanceConversionHeader", mustacheData, nameResolvers),
            fileNameRules.getHeaderFilePath(fileName)
        )

        val includes = jniIncludeResolver.collectImplementationIncludes(limeElement) +
            Include.createInternalInclude("$fileName.h") +
            if (limeElement is LimeInterface || limeElement is LimeLambda) {
                val proxyFileName = fileNameRules.getElementFileName(limeElement) + "ImplCppProxy"
                listOf(Include.createInternalInclude("$proxyFileName.h"))
            } else emptyList()

        mustacheData["includes"] = includes.distinct().sorted()
        val implFile = GeneratedFile(
            TemplateEngine.render("jni/InstanceConversionImplementation", mustacheData, nameResolvers, predicates),
            fileNameRules.getImplementationFilePath(fileName)
        )

        return listOf(headerFile, implFile)
    }

    private fun generateCppProxyFiles(limeElement: LimeNamedElement): List<GeneratedFile> {
        val mustacheData = mutableMapOf(
            "container" to limeElement,
            "includes" to cppIncludeResolver.resolveIncludes(limeElement).distinct().sorted(),
            "internalNamespace" to internalNamespace
        )

        val fileName = fileNameRules.getElementFileName(limeElement) + "ImplCppProxy"
        val headerFile = GeneratedFile(
            TemplateEngine.render("jni/CppProxyHeader", mustacheData, nameResolvers),
            fileNameRules.getHeaderFilePath(fileName)
        )

        val includes = jniIncludeResolver.collectImplementationIncludes(limeElement) +
                Include.createInternalInclude("$fileName.h")

        mustacheData["includes"] = includes.distinct().sorted()
        val implFile = GeneratedFile(
            TemplateEngine.render("jni/CppProxyImplementation", mustacheData, nameResolvers, predicates),
            fileNameRules.getImplementationFilePath(fileName)
        )

        return listOf(headerFile, implFile)
    }
}
