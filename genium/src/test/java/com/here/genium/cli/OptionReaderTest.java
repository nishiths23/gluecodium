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

package com.here.ivi.api.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.here.ivi.api.cli.OptionReader.TranspilerOptions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class OptionReaderTest {
  private static final String[] TEST_INPUT_SINGLE_FOLDER = {"dirA"};
  private static final String[] TEST_INPUT_TWO_FOLDERS = {"dirA", "dirB"};
  private static final String TEST_OUTPUT = "./outputFile";
  private static final String TEST_GENERATORS = "java,cpp";
  private static final String TEST_JAVA_PACKAGE_LIST = "some_package";
  private static final String TEST_ADDITIONAL_ANDROID_MANIFEST =
      "path/to/additional/AndroidManifest.xml";

  private final OptionReader optionReader = new OptionReader();

  @Rule public final ExpectedException expectedException = ExpectedException.none();

  @Test
  public void listGeneratorsOptionIsRecognised() throws OptionReaderException {
    // Arrange, Act
    TranspilerOptions transpilerOptions = optionReader.read(new String[] {"-listGenerators"});

    // Assert
    assertNull(transpilerOptions);
  }

  @Test
  public void helpOptionIsRecognised() throws OptionReaderException {
    // Arrange, Act
    TranspilerOptions transpilerOptions = optionReader.read(new String[] {"-help"});

    // Assert
    assertNull(transpilerOptions);
  }

  @Test
  public void listGeneratorsOptionCreatesOutput() throws OptionReaderException, IOException {
    // Arrange
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    System.setOut(new PrintStream(bo));

    // Act
    optionReader.read(new String[] {"-listGenerators"});
    bo.flush();
    String consoleOutput = new String(bo.toByteArray());

    // Assert
    assertTrue(consoleOutput.contains("Found generator android"));
  }

  @Test
  public void helpOptionCreatesOutput() throws OptionReaderException, IOException {
    // Arrange
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    System.setOut(new PrintStream(bo));

    // Act
    optionReader.read(new String[] {"-help"});
    bo.flush();
    String consoleOutput = new String(bo.toByteArray());

    // Assert
    assertTrue(consoleOutput.contains("Transpiler - Generate APIs for franca files"));
  }

  @Test
  public void inputOptionSingleFolder() throws OptionReaderException {
    // Arrange, Act
    TranspilerOptions options =
        optionReader.read(new String[] {"-input", TEST_INPUT_SINGLE_FOLDER[0]});

    // Assert
    assertNotNull(options.getInputDirs());
    assertEquals(1, options.getInputDirs().length);
    assertEquals(TEST_INPUT_SINGLE_FOLDER[0], options.getInputDirs()[0]);
  }

  @Test
  public void inputOptionTwoFolders() throws OptionReaderException {
    // Arrange, Act
    TranspilerOptions options =
        optionReader.read(
            new String[] {
              "-input", TEST_INPUT_TWO_FOLDERS[0],
              "-input", TEST_INPUT_TWO_FOLDERS[1]
            });

    // Assert
    assertNotNull(options.getInputDirs());
    assertEquals(2, options.getInputDirs().length);
    assertEquals(TEST_INPUT_TWO_FOLDERS[0], options.getInputDirs()[0]);
    assertEquals(TEST_INPUT_TWO_FOLDERS[1], options.getInputDirs()[1]);
  }

  @Test
  public void outputOptionIsRecognised() throws OptionReaderException {
    // Arrange
    String[] toRead = prepareToRead("-output", TEST_OUTPUT);

    // Act
    TranspilerOptions transpilerOptions = optionReader.read(toRead);

    // Assert
    assertEquals(TEST_OUTPUT, transpilerOptions.getOutputDir());
  }

  @Test
  public void generatorsOptionIsRecognised() throws OptionReaderException {
    // Arrange
    String[] toRead = prepareToRead("-generators", TEST_GENERATORS);

    // Act
    TranspilerOptions transpilerOptions = optionReader.read(toRead);

    // Assert
    Set<String> generators = transpilerOptions.getGenerators();
    assertTrue(generators.contains("cpp"));
    assertTrue(generators.contains("java"));
  }

  @Test
  public void javapackageOptionIsRecognised() throws OptionReaderException {
    // Arrange
    String[] toRead = prepareToRead("-javapackage", TEST_JAVA_PACKAGE_LIST);

    // Act
    TranspilerOptions transpilerOptions = optionReader.read(toRead);

    // Assert
    assertEquals(Arrays.asList(TEST_JAVA_PACKAGE_LIST), transpilerOptions.getJavaPackageList());
  }

  @Test
  public void unknownOptionIsNotRecognised() throws OptionReaderException {
    // Arrange
    String[] toRead = prepareToRead("-someUnknownOption", "");

    // Act, Assert
    expectedException.expect(OptionReaderException.class);
    optionReader.read(toRead);
  }

  @Test
  public void androidMergeManifestPathIsRecognised() throws OptionReaderException {
    String[] toRead = prepareToRead("-androidMergeManifest", TEST_ADDITIONAL_ANDROID_MANIFEST);
    TranspilerOptions transpilerOptions = optionReader.read(toRead);
    assertEquals(TEST_ADDITIONAL_ANDROID_MANIFEST, transpilerOptions.getAndroidMergeManifestPath());
  }

  private String[] prepareToRead(String optionName, String optionValue) {
    return new String[] {"-input", TEST_INPUT_SINGLE_FOLDER[0], optionName, optionValue};
  }
}