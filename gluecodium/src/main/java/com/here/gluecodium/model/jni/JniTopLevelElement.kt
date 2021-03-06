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

package com.here.gluecodium.model.jni

import com.here.gluecodium.generator.jni.JniNameRules
import com.here.gluecodium.model.java.JavaPackage

abstract class JniTopLevelElement(
    val javaName: String,
    val cppFullyQualifiedName: String,
    val javaPackage: JavaPackage,
    @Suppress("unused")
    val externalConverter: String? = null,
    @Suppress("unused")
    val externalConvertedType: String? = null
) : JniElement {
    @Suppress("MemberVisibilityCanBePrivate")
    val fullJavaName = (javaPackage.packageNames + javaName).joinToString("/")
    open val mangledName = JniNameRules.getMangledName(fullJavaName)
    val mangledExternalConverter = externalConverter?.let { JniNameRules.getMangledName(it) }
}
