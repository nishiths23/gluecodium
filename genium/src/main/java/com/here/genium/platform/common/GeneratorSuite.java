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

package com.here.ivi.api.platform.common;

import com.here.ivi.api.cli.OptionReader;
import com.here.ivi.api.cli.TranspilerExecutionException;
import com.here.ivi.api.generator.common.GeneratedFile;
import com.here.ivi.api.model.franca.FrancaDeploymentModel;
import com.here.ivi.api.platform.android.AndroidGeneratorSuite;
import com.here.ivi.api.platform.android.JavaGeneratorSuite;
import com.here.ivi.api.platform.baseapi.BaseApiGeneratorSuite;
import com.here.ivi.api.platform.swift.SwiftGeneratorSuite;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import org.apache.commons.io.IOUtils;
import org.franca.core.franca.FTypeCollection;

/** The base interface for all the generators. */
public abstract class GeneratorSuite {

  /** @return the human readable name of this generator */
  public abstract String getName();

  /**
   * Triggers the generation. The model is assumed to be valid.
   *
   * @param deploymentModel model containing deployment specification definitions
   * @param typeCollections type collections and interfaces (interfaces are type collection as well)
   * @return a list of generated files with their relative destination paths
   */
  public abstract List<GeneratedFile> generate(
      FrancaDeploymentModel deploymentModel, List<FTypeCollection> typeCollections);

  public static String getSpecPath() {
    return "classpath:/TranspilerExtensions.fdepl";
  }

  /** Creates a new instance of a generator suite by its short identifier */
  public static GeneratorSuite instantiateByShortName(
      String shortName, OptionReader.TranspilerOptions options) {

    switch (shortName) {
      case AndroidGeneratorSuite.GENERATOR_NAME:
        return new AndroidGeneratorSuite(options);
      case JavaGeneratorSuite.GENERATOR_NAME:
        return new JavaGeneratorSuite(options);
      case BaseApiGeneratorSuite.GENERATOR_NAME:
        return new BaseApiGeneratorSuite(options);
      case SwiftGeneratorSuite.GENERATOR_NAME:
        return new SwiftGeneratorSuite(options);
    }

    return null;
  }

  /** @return all available generators */
  public static Set<String> generatorShortNames() {
    return new HashSet<>(
        Arrays.asList(
            AndroidGeneratorSuite.GENERATOR_NAME,
            JavaGeneratorSuite.GENERATOR_NAME,
            BaseApiGeneratorSuite.GENERATOR_NAME,
            SwiftGeneratorSuite.GENERATOR_NAME));
  }

  public static GeneratedFile copyTarget(String fileName, String targetDir) {
    InputStream stream = GeneratorSuite.class.getClassLoader().getResourceAsStream(fileName);

    if (stream != null) {
      try {
        String content = IOUtils.toString(stream, Charset.defaultCharset());
        return new GeneratedFile(
            content, targetDir.isEmpty() ? fileName : targetDir + File.separator + fileName);
      } catch (IOException e) {
        throw new TranspilerExecutionException("Copying resource file failed with error:", e);
      }
    }

    throw new TranspilerExecutionException(String.format("Failed loading resource %s.", fileName));
  }
}