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

package com.here.ivi.api.model.cbridge;

import static com.here.ivi.api.generator.cbridge.CBridgeNameRules.BASE_REF_NAME;
import static java.util.Collections.emptyList;

import com.here.ivi.api.generator.cbridge.CBridgeNameRules;
import com.here.ivi.api.model.common.Include;
import java.nio.file.Paths;
import java.util.*;
import lombok.EqualsAndHashCode;

/** Base class for all C types */
@EqualsAndHashCode(callSuper = true)
public class CType extends CElement {
  protected static final String CONST_SPECIFIER = "const";
  public static final Include FIXED_WIDTH_INTEGERS_INCLUDE =
      Include.createSystemInclude("stdint.h");
  public static final Include BOOL_INCLUDE = Include.createSystemInclude("stdbool.h");

  public static final CType VOID = new CType("void");
  public static final CType CHAR = new CType("char");
  public static final CType INT8 = new CType("int8_t", FIXED_WIDTH_INTEGERS_INCLUDE);
  public static final CType UINT8 = new CType("uint8_t", FIXED_WIDTH_INTEGERS_INCLUDE);
  public static final CType INT16 = new CType("int16_t", FIXED_WIDTH_INTEGERS_INCLUDE);
  public static final CType UINT16 = new CType("uint16_t", FIXED_WIDTH_INTEGERS_INCLUDE);
  public static final CType INT32 = new CType("int32_t", FIXED_WIDTH_INTEGERS_INCLUDE);
  public static final CType UINT32 = new CType("uint32_t", FIXED_WIDTH_INTEGERS_INCLUDE);
  public static final CType INT64 = new CType("int64_t", FIXED_WIDTH_INTEGERS_INCLUDE);
  public static final CType UINT64 = new CType("uint64_t", FIXED_WIDTH_INTEGERS_INCLUDE);
  public static final CType BOOL = new CType("bool", BOOL_INCLUDE);
  public static final CType FLOAT = new CType("float");
  public static final CType DOUBLE = new CType("double");
  public static final CType STRING_REF =
      new CType(
          BASE_REF_NAME,
          Include.createInternalInclude(
              Paths.get(CBridgeNameRules.CBRIDGE_PUBLIC, "include", "StringHandle.h").toString()));
  public static final CType BYTE_ARRAY_REF =
      new CType(
          BASE_REF_NAME,
          Include.createInternalInclude(
              Paths.get(CBridgeNameRules.CBRIDGE_PUBLIC, "include", "ByteArrayHandle.h")
                  .toString()));

  public Boolean isConst = false;
  public final Set<Include> includes;

  public CType(final String name) {
    this(name, emptyList());
  }

  public CType(final String name, final Include include) {
    this(name);
    includes.add(include);
  }

  protected CType(final String name, final Collection<Include> includes) {
    super(name);
    this.includes = new LinkedHashSet<>(includes);
  }

  @Override
  public String toString() {
    if (isConst) {
      return CONST_SPECIFIER + " " + name;
    } else {
      return name;
    }
  }
}