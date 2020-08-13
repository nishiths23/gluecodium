/*
 * Copyright (C) 2016-2020 HERE Europe B.V.
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

package com.here.gluecodium.generator.swift

import com.here.gluecodium.common.LimeLogger
import com.here.gluecodium.model.lime.LimeAttributeType.SWIFT
import com.here.gluecodium.model.lime.LimeAttributeValueType.WEAK
import com.here.gluecodium.model.lime.LimeElement
import com.here.gluecodium.model.lime.LimeInterface
import com.here.gluecodium.model.lime.LimeNamedElement
import com.here.gluecodium.model.lime.LimeProperty

/**
 * Validate properties marked as "weak". Gluecodium supports Swift weak properties only for interfaces.
 */
internal class SwiftWeakPropertiesValidator(
    private val limeReferenceMap: Map<String, LimeElement>,
    private val logger: LimeLogger
) {

    fun validate(limeElements: List<LimeNamedElement>): Boolean {
        val validationResults = limeElements
            .filterIsInstance<LimeProperty>()
            .map { validateProperty(it) }

        return !validationResults.contains(false)
    }

    private fun validateProperty(limeProperty: LimeProperty): Boolean {
        if (!limeProperty.attributes.have(SWIFT, WEAK)) return true

        val parentInterface = limeReferenceMap[limeProperty.path.parent.toString()] as? LimeInterface
        return when {
            parentInterface == null -> {
                logger.error(limeProperty, "@Swift(Weak) can only apply to a property in an interface.")
                false
            }
            !limeProperty.typeRef.isNullable -> {
                logger.error(limeProperty, "@Swift(Weak) can only apply to a property with a nullable type.")
                false
            }
            else -> true
        }
    }
}
