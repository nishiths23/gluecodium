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

package com.here.ivi.api.platform.android;

import com.here.ivi.api.cli.OptionReader;
import com.here.ivi.api.generator.androidmanifest.AndroidManifestGenerator;
import com.here.ivi.api.generator.jni.JniGenerator;
import com.here.ivi.api.model.java.*;
import java.util.*;

/**
 * Combines generators {@link AndroidManifestGenerator}, {@link JniGenerator} and {@link
 * JavaGenerator} to generate Java code and bindings to BaseAPI layer for Android.
 */
public final class AndroidGeneratorSuite extends JavaGeneratorSuite {

  public static final String GENERATOR_NAME = "android";

  public AndroidGeneratorSuite(final OptionReader.TranspilerOptions transpilerOptions) {
    super(transpilerOptions, true);
  }

  @Override
  public String getName() {
    return "com.here.AndroidGeneratorSuite";
  }

  @Override
  protected String getGeneratorName() {
    return GENERATOR_NAME;
  }
}