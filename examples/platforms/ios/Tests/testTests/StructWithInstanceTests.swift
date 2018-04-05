// -------------------------------------------------------------------------------------------------
// Copyright (c) 2016-2018 HERE Europe B.V.
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

class StructWithInstanceTests: XCTestCase {

    func testAssignInstanceToStruct() {
        let simpleOne = hello.InstanceInStruct.SelfHolder(mySelf: hello.InstanceInStruct.create())
        simpleOne.mySelf?.setStringValue(stringValue: "Hello")

        XCTAssertEqual(simpleOne.mySelf?.getStringValue(), "Hello")
    }

    func testCopyInstanceInStruct() {
        let a = hello.InstanceInStruct.SelfHolder(mySelf: hello.InstanceInStruct.create())
        do {
           let b = a
           b.mySelf?.setStringValue(stringValue: "Hello")
        }
        XCTAssertEqual(a.mySelf?.getStringValue(), "Hello")
    }

    func testCopyInstanceInStructFromMethod() {
        let a = hello.InstanceInStruct.createInStruct()!
        do {
           let b = a
           b.mySelf?.setStringValue(stringValue: "Hello")
        }
        XCTAssertEqual(a.mySelf?.getStringValue(), "Hello")
    }

    func testNullInstanceInStruct() {
        let emptyStruct = hello.InstanceInStruct.createNullInStruct()
        XCTAssertNotNil(emptyStruct)
        XCTAssertNil(emptyStruct?.mySelf)
    }

    func testInstanceInNotNullStruct() {
        let structNotNull = hello.InstanceInStruct.createInNotNullStruct()
        XCTAssertNotNil(structNotNull)
        XCTAssertNotNil(structNotNull!.mySelf)
    }

    func testNullInstanceInNotNullStruct() {
        let emptyStruct = hello.InstanceInStruct.createInEmptyNotNullStruct()
        XCTAssertNil(emptyStruct)
    }

    static var allTests = [
        ("testAssignInstanceToStruct", testAssignInstanceToStruct),
        ("testCopyInstanceInStruct", testCopyInstanceInStruct),
        ("testCopyInstanceInStructFromMethod", testCopyInstanceInStructFromMethod),
        ("testNullInstanceInStruct", testNullInstanceInStruct),
        ("testInstanceInNotNullStruct", testInstanceInNotNullStruct),
        ("testNullInstanceInNotNullStruct", testNullInstanceInNotNullStruct)
    ]
}