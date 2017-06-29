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

package com.here.ivi.api.generator.common.jni.templates;

import static org.junit.Assert.assertEquals;

import com.here.ivi.api.model.cppmodel.CppPrimitiveType;
import com.here.ivi.api.model.cppmodel.CppType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class JniPrimitiveTypeConversionTemplateTest {
  @Test
  public void generate() {
    // Arrange
    CppType cppType = new CppPrimitiveType(CppPrimitiveType.Type.UINT64);
    String baseName = "parameterName";
    String expected = "uint64_t n" + baseName + " = static_cast<uint64_t>(j" + baseName + ");\n";

    // Act
    String result = JniPrimitiveTypeConversionTemplate.generate(cppType, baseName).toString();

    // Assert
    assertEquals(expected, result);
  }
}
