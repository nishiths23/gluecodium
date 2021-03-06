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

package com.here.gluecodium.platform.android

import com.here.gluecodium.Gluecodium
import com.here.gluecodium.generator.androidmanifest.AndroidManifestGenerator
import com.here.gluecodium.generator.jni.JniGenerator

/**
 * Combines generators [AndroidManifestGenerator], [JniGenerator], and [JavaGeneratorSuite] to
 * generate Java code and bindings to BaseAPI layer for Android.
 */
class AndroidGeneratorSuite(options: Gluecodium.Options) : JavaGeneratorSuite(options, true) {
    override val generatorName = GENERATOR_NAME

    companion object {
        const val GENERATOR_NAME = "android"
    }
}
