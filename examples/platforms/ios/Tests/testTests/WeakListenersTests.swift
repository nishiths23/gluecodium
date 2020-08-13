// -------------------------------------------------------------------------------------------------
// Copyright (C) 2016-2020 HERE Europe B.V.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// SPDX-License-Identifier: Apache-2.0
// License-Filename: LICENSE
//
// -------------------------------------------------------------------------------------------------

import XCTest
import hello

class WeakListenersTests: XCTestCase {

    static var itHappened = false

    class ListenerImpl: ListenerInterface {
        func notify() { WeakListenersTests.itHappened = true }
    }

    class WeaklingImpl: Weakling {
        weak var listener: ListenerInterface?
    }

    func testWeaklingIsPushed() {
        WeakListenersTests.itHappened = false

        let listener = ListenerImpl()
        let weakling = WeaklingImpl()
        weakling.listener = listener

        WeaklingNotifier.pushNotification(whom: weakling)

        XCTAssertTrue(WeakListenersTests.itHappened)
    }

    func testWeaklingIsIgnored() {
        WeakListenersTests.itHappened = false

        var listener: ListenerInterface? = ListenerImpl()
        let weakling = WeaklingImpl()
        weakling.listener = listener
        listener = nil

        WeaklingNotifier.pushNotification(whom: weakling)

        XCTAssertFalse(WeakListenersTests.itHappened)
    }

    static var allTests = [
        ("testWeaklingIsPushed", testWeaklingIsPushed),
        ("testWeaklingIsIgnored", testWeaklingIsIgnored)
    ]
}
