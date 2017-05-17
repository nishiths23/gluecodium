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

package com.here.ivi.api.model.cppmodel;

import com.here.ivi.api.model.*;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CppIncludeResolver {

  static Logger logger = java.util.logging.Logger.getLogger(CppIncludeResolver.class.getName());

  private FrancaModel<?, ?> rootModel;
  private String outputFile;

  public CppIncludeResolver(FrancaModel<?, ?> rootModel, String outputFile) {
    this.rootModel = rootModel;
    this.outputFile = outputFile;
  }

  public void resolveLazyIncludes(CppElement root) {
    root.streamRecursive()
        .filter(p -> p instanceof CppElementWithIncludes)
        .map(CppElementWithIncludes.class::cast)
        .forEach(this::resolveLazyIncludes);
  }

  private void resolveLazyIncludes(CppElementWithIncludes type) {
    type.includes =
        type.includes
            .stream()
            .map(
                i -> {
                  if (i instanceof Includes.LazyInternalInclude) {

                    Includes.LazyInternalInclude li = (Includes.LazyInternalInclude) i;

                    Optional<? extends FrancaElement<?>> externalDefinitionOpt =
                        rootModel.find(li.model, li.tc);
                    if (!externalDefinitionOpt.isPresent()) {
                      logger.severe("Could not resolve type collection include " + li);
                      return null;
                    }

                    FrancaElement<?> externalDefinition = externalDefinitionOpt.get();
                    String includeName = li.nameRules.getHeaderPath(externalDefinition);

                    // no self includes needed
                    if (includeName.equals(outputFile)) {
                      return null;
                    }

                    // TODO think about relative include path resolution and stuff
                    return new Includes.InternalPublicInclude(includeName);
                  }

                  return i;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
  }
}
