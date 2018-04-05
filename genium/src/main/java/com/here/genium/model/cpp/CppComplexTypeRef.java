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

package com.here.ivi.api.model.cpp;

import com.here.ivi.api.model.common.Include;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.Singular;

@EqualsAndHashCode(callSuper = true)
public class CppComplexTypeRef extends CppTypeRef {

  private final boolean refersToEnum;

  @lombok.Builder(builderClassName = "Builder")
  protected CppComplexTypeRef(
      final String fullyQualifiedName,
      @Singular final Collection<Include> includes,
      final boolean refersToEnum) {
    super(fullyQualifiedName, includes);
    this.refersToEnum = refersToEnum;
  }

  @Override
  public boolean refersToEnumType() {
    return refersToEnum;
  }

  @SuppressWarnings("unused")
  public static class Builder {
    private String fullyQualifiedName;

    Builder() {
      this(null);
    }

    public Builder(final String fullyQualifiedName) {
      this.fullyQualifiedName = fullyQualifiedName;
    }
  }
}