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
package com.here.android.hello;

import static org.junit.Assert.assertEquals;

import android.os.Build;
import android.support.annotation.NonNull;
import com.example.here.hello.BuildConfig;
import com.here.android.RobolectricApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(
    sdk = Build.VERSION_CODES.M,
    application = RobolectricApplication.class,
    constants = BuildConfig.class)
public final class HelloWorldPlainDataStructuresTest {
  @Test
  public void methodWithNonNestedType_nonNestedPlainDataStructure() {
    HelloWorldPlainDataStructures.SyncResult input = createSyncResult();

    HelloWorldPlainDataStructures.SyncResult result =
        HelloWorldPlainDataStructures.methodWithNonNestedType(input);

    assertEquals(input.lastUpdatedTimeStamp, result.lastUpdatedTimeStamp);
    assertEquals(input.numberOfChanges + 1, result.numberOfChanges);
  }

  @Test
  public void methodWithNonNestedType_nestedPlainDataStructure() {
    HelloWorldPlainDataStructures.IdentifiableSyncResult input =
        new HelloWorldPlainDataStructures.IdentifiableSyncResult(20, createSyncResult());

    HelloWorldPlainDataStructures.IdentifiableSyncResult result =
        HelloWorldPlainDataStructures.methodWithNestedType(input);

    assertEquals(input.syncResult.lastUpdatedTimeStamp, result.syncResult.lastUpdatedTimeStamp);
    assertEquals(input.syncResult.numberOfChanges + 1, result.syncResult.numberOfChanges);
    assertEquals(input.id, result.id);
  }

  @NonNull
  private static HelloWorldPlainDataStructures.SyncResult createSyncResult() {
    return new HelloWorldPlainDataStructures.SyncResult(100L, 5);
  }
}
