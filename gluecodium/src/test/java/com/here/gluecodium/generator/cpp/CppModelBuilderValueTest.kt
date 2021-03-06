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

package com.here.gluecodium.generator.cpp

import com.here.gluecodium.model.cpp.CppElement
import com.here.gluecodium.model.cpp.CppPrimitiveTypeRef
import com.here.gluecodium.model.cpp.CppValue
import com.here.gluecodium.model.lime.LimeBasicTypeRef
import com.here.gluecodium.model.lime.LimeEnumerator
import com.here.gluecodium.model.lime.LimeLazyEnumeratorRef
import com.here.gluecodium.model.lime.LimePath.Companion.EMPTY_PATH
import com.here.gluecodium.model.lime.LimeValue
import com.here.gluecodium.test.AssertHelpers.assertContains
import com.here.gluecodium.test.MockContextStack
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CppModelBuilderValueTest {
    @MockK private lateinit var typeMapper: CppTypeMapper
    @MockK private lateinit var nameResolver: CppNameResolver
    @MockK private lateinit var includeResolver: CppIncludeResolver

    private val contextStack = MockContextStack<CppElement>()

    private lateinit var modelBuilder: CppModelBuilder

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)

        modelBuilder =
            CppModelBuilder(contextStack, typeMapper, nameResolver, includeResolver, emptyMap())
    }

    @Test
    fun finishBuildingLiteralValue() {
        val limeElement = LimeValue.Literal(LimeBasicTypeRef.DOUBLE, "Foo")

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("Foo", result.name)
    }

    @Test
    fun finishBuildingLiteralValueFloat() {
        val limeElement = LimeValue.Literal(LimeBasicTypeRef.FLOAT, "Foo")

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("Foof", result.name)
    }

    @Test
    fun finishBuildingEnumeratorValue() {
        val limeEnumerator = LimeEnumerator(EMPTY_PATH)
        val limeEnumeratorRef = LimeLazyEnumeratorRef(mapOf("foo" to limeEnumerator), "foo")
        val limeElement = LimeValue.Enumerator(LimeBasicTypeRef.DOUBLE, limeEnumeratorRef)
        every { nameResolver.getFullyQualifiedName(limeEnumerator) } returns "FooBarBaz"

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("FooBarBaz", result.name)
    }

    @Test
    fun finishBuildingLiteralValueFloatNaN() {
        val limeElement = LimeValue.Special.FLOAT_NAN

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("std::numeric_limits<float>::quiet_NaN()", result.name)
        assertContains(CppLibraryIncludes.LIMITS, result.includes)
    }

    @Test
    fun finishBuildingLiteralValueFloatInfinity() {
        val limeElement = LimeValue.Special.FLOAT_INFINITY

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("std::numeric_limits<float>::infinity()", result.name)
        assertContains(CppLibraryIncludes.LIMITS, result.includes)
    }

    @Test
    fun finishBuildingLiteralValueFloatNegativeInfinity() {
        val limeElement = LimeValue.Special.FLOAT_NEGATIVE_INFINITY

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("-std::numeric_limits<float>::infinity()", result.name)
        assertContains(CppLibraryIncludes.LIMITS, result.includes)
    }

    @Test
    fun finishBuildingLiteralValueDoubleNaN() {
        val limeElement = LimeValue.Special.DOUBLE_NAN

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("std::numeric_limits<double>::quiet_NaN()", result.name)
        assertContains(CppLibraryIncludes.LIMITS, result.includes)
    }

    @Test
    fun finishBuildingLiteralValueDoubleInfinity() {
        val limeElement = LimeValue.Special.DOUBLE_INFINITY

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("std::numeric_limits<double>::infinity()", result.name)
        assertContains(CppLibraryIncludes.LIMITS, result.includes)
    }

    @Test
    fun finishBuildingLiteralValueDoubleNegativeInfinity() {
        val limeElement = LimeValue.Special.DOUBLE_NEGATIVE_INFINITY

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("-std::numeric_limits<double>::infinity()", result.name)
        assertContains(CppLibraryIncludes.LIMITS, result.includes)
    }

    @Test
    fun finishBuildingEmptyInitializerListValue() {
        val limeElement = LimeValue.InitializerList(LimeBasicTypeRef.DOUBLE, emptyList())

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("{}", result.name)
    }

    @Test
    fun finishBuildingInitializerListValue() {
        contextStack.injectResult(CppPrimitiveTypeRef.BOOL)
        val limeElement = LimeValue.InitializerList(
            LimeBasicTypeRef.DOUBLE,
            listOf(
                LimeValue.Null(LimeBasicTypeRef.DOUBLE),
                LimeValue.InitializerList(LimeBasicTypeRef.DOUBLE, emptyList())
            )
        )

        modelBuilder.finishBuilding(limeElement)

        val result = modelBuilder.getFinalResult(CppValue::class.java)
        assertEquals("{bool(), {}}", result.name)
    }
}
