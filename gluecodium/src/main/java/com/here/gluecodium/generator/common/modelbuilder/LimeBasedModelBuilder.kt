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

package com.here.gluecodium.generator.common.modelbuilder

import com.here.gluecodium.model.lime.LimeConstant
import com.here.gluecodium.model.lime.LimeContainerWithInheritance
import com.here.gluecodium.model.lime.LimeElement
import com.here.gluecodium.model.lime.LimeEnumeration
import com.here.gluecodium.model.lime.LimeEnumerator
import com.here.gluecodium.model.lime.LimeException
import com.here.gluecodium.model.lime.LimeField
import com.here.gluecodium.model.lime.LimeFunction
import com.here.gluecodium.model.lime.LimeLambda
import com.here.gluecodium.model.lime.LimeParameter
import com.here.gluecodium.model.lime.LimeProperty
import com.here.gluecodium.model.lime.LimeStruct
import com.here.gluecodium.model.lime.LimeTypeAlias
import com.here.gluecodium.model.lime.LimeTypeRef
import com.here.gluecodium.model.lime.LimeTypesCollection
import com.here.gluecodium.model.lime.LimeValue

/** An interface for a model builder, used by [LimeTreeWalker].  */
interface LimeBasedModelBuilder {
    fun startBuilding(limeElement: LimeElement)
    fun startBuilding(limeContainer: LimeContainerWithInheritance)
    fun startBuilding(limeStruct: LimeStruct)

    fun finishBuilding(limeContainer: LimeContainerWithInheritance)
    fun finishBuilding(limeTypes: LimeTypesCollection)
    fun finishBuilding(limeTypeRef: LimeTypeRef)
    fun finishBuilding(limeMethod: LimeFunction)
    fun finishBuilding(limeParameter: LimeParameter)
    fun finishBuilding(limeStruct: LimeStruct)
    fun finishBuilding(limeField: LimeField)
    fun finishBuilding(limeTypeDef: LimeTypeAlias)
    fun finishBuilding(limeProperty: LimeProperty)
    fun finishBuilding(limeEnumeration: LimeEnumeration)
    fun finishBuilding(limeEnumerator: LimeEnumerator)
    fun finishBuilding(limeConstant: LimeConstant)
    fun finishBuilding(limeValue: LimeValue)
    fun finishBuilding(limeException: LimeException)
    fun finishBuilding(limeLambda: LimeLambda)
}
