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

import java.util.stream.Stream;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class CppParameter extends CppTypedElement {

  public final boolean isOutput;

  public CppParameter(final String name, final CppTypeRef type) {
    this(name, type, false);
  }

  public CppParameter(final String name, final CppTypeRef type, final boolean isOutput) {
    super(name, type);
    this.isOutput = isOutput;
  }

  @Override
  public Stream<? extends CppElement> stream() {
    return Stream.of(type);
  }
}