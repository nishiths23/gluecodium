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

package com.here.ivi.api.generator.swift

import com.here.ivi.api.generator.common.GeneratorSuite
import com.here.ivi.api.loader.FrancaModelLoader
import com.here.ivi.api.loader.baseapi.BaseApiSpecAccessorFactory
import com.here.ivi.api.model.franca.FrancaModel
import com.here.ivi.api.model.franca.ModelHelper
import com.here.ivi.api.validator.common.ResourceValidator
import java.io.File
import java.util.Objects
import static java.util.stream.Collectors.toList
import navigation.BaseApiSpec;
import static java.util.stream.Stream.concat
import com.here.ivi.api.generator.cbridge.CBridgeNameRules
import com.here.ivi.api.generator.cbridge.CBridgeGenerator

final class SwiftGeneratorSuite implements GeneratorSuite {
    // TODO: APIGEN-173 - Deal with multiple deployment specificiations and use different accessor factory
    val specAccessorFactory = new BaseApiSpecAccessorFactory
    File[] currentFiles
    FrancaModel<BaseApiSpec.InterfacePropertyAccessor,BaseApiSpec.TypeCollectionPropertyAccessor> model
    FrancaModelLoader<BaseApiSpec.InterfacePropertyAccessor,BaseApiSpec.TypeCollectionPropertyAccessor> modelLoader

    new () {
        modelLoader = new FrancaModelLoader(specAccessorFactory)
    }

    new (FrancaModelLoader<BaseApiSpec.InterfacePropertyAccessor,
        BaseApiSpec.TypeCollectionPropertyAccessor> modelLoader) {
        this.modelLoader = modelLoader;
    }

    override generate() {
        val swiftNameRules = new SwiftNameRules
        val cBridgeNameRules = new CBridgeNameRules
        val includeResolver = new SwiftIncludeResolver

        // TODO: APIGEN-108 Add all other possible generators and call them here

        val swiftGenerator = new SwiftGenerator(swiftNameRules, includeResolver)
        val cBrigdeGenerator = new CBridgeGenerator(cBridgeNameRules)

        val swiftStream = model.getInterfaces().stream().map([swiftGenerator.generate(it)]).flatMap([stream])
        val cBridgeStream = model.getInterfaces().stream().map([cBrigdeGenerator.generate(it)]).flatMap([stream])

        return concat(swiftStream, cBridgeStream).filter([Objects.nonNull(it)]).collect(toList)
    }

    override getSpecPath() {
        return specAccessorFactory.getSpecPath
    }

    override getName() {
        return "com.here.SwiftGenerator"
    }

    override validate() {
        val resourceSet = modelLoader.getResourceSetProvider.get
        return ResourceValidator.validate(resourceSet, currentFiles)
    }

    override buildModel(String inputPath) {
        ModelHelper.getFdeplInjector.injectMembers(modelLoader)
        currentFiles = FrancaModelLoader.listFilesRecursively(new File(inputPath))
        model = modelLoader.load(specAccessorFactory.getSpecPath, currentFiles)
    }
}
