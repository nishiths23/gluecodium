// -------------------------------------------------------------------------------------------------
// Copyright (c) 2016-2019 HERE Europe B.V.
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

import Foundation

extension String {
    internal func convertToCType() -> _baseRef {
        let result = std_string_create_handle(self)
        precondition(result != 0, "Out of memory")
        return result
    }
}

internal func copyFromCType(_ handle: _baseRef) -> String {
    return String(data: Data(bytes: std_string_data_get(handle),
                  count: Int(std_string_size_get(handle))), encoding: .utf8)!
}

internal func moveFromCType(_ handle: _baseRef) -> String {
    defer {
        std_string_release_handle(handle)
    }
    return copyFromCType(handle)
}

internal func copyFromCType(_ handle: _baseRef) -> Data {
    return Data(bytes: byteArray_data_get(handle), count: Int(byteArray_size_get(handle)))
}

internal func moveFromCType(_ handle: _baseRef) -> Data {
    defer {
        byteArray_release_handle(handle)
    }
    return copyFromCType(handle)
}

// catch primitive types
internal func copyFromCType<T>(_ primitive: T) -> T {
    return primitive
}

// catch primitive types
internal func moveFromCType<T>(_ primitive: T) -> T {
    return primitive
}