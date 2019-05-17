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

package com.here.genium.validator

import com.here.genium.franca.FrancaTypeHelper
import com.here.genium.franca.FrancaDeploymentModel
import org.franca.core.franca.FStructType
import org.franca.core.franca.FTypeCollection

/** Validates that Franca struct inheritance is not used. */
class StructInheritanceValidatorPredicate : ValidatorPredicate<FStructType> {

    override val elementClass = FStructType::class.java

    override fun validate(
        deploymentModel: FrancaDeploymentModel,
        francaElement: FStructType
    ) = if (francaElement.base != null) {
        String.format(
            INHERITANCE_MESSAGE,
            francaElement.name,
            FrancaTypeHelper.getFullName(francaElement.eContainer() as FTypeCollection)
        )
    } else {
        null
    }

    companion object {
        private const val INHERITANCE_MESSAGE =
            "Struct Inheritance is not supported: struct '%s' in type collection '%s'."
    }
}