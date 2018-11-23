//
//
// Automatically generated. Do not modify. Your changes will be lost.

import Foundation

internal func getRef(_ ref: InterfaceWithStruct?, owning: Bool = true) -> RefHolder {
    guard let reference = ref else {
        return RefHolder(0)
    }
    if let instanceReference = reference as? NativeBase {
        let handle_copy = smoke_InterfaceWithStruct_copy_handle(instanceReference.c_handle)
        return owning
            ? RefHolder(ref: handle_copy, release: smoke_InterfaceWithStruct_release_handle)
            : RefHolder(handle_copy)
    }
    var functions = smoke_InterfaceWithStruct_FunctionTable()
    functions.swift_pointer = Unmanaged<AnyObject>.passRetained(reference).toOpaque()
    functions.release = {swift_class_pointer in
        if let swift_class = swift_class_pointer {
            Unmanaged<AnyObject>.fromOpaque(swift_class).release()
        }
    }
    functions.smoke_InterfaceWithStruct_innerStructMethod = {(swift_class_pointer, inputStruct) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! InterfaceWithStruct
        defer {
            smoke_InterfaceWithStruct_InnerStruct_release_handle(inputStruct)
        }
        return swift_class.innerStructMethod(inputStruct: InnerStruct(cInnerStruct: inputStruct)).convertToCType()
    }
    let proxy = smoke_InterfaceWithStruct_create_proxy(functions)
    return owning ? RefHolder(ref: proxy, release: smoke_InterfaceWithStruct_release_handle) : RefHolder(proxy)
}

public protocol InterfaceWithStruct : AnyObject {

    func innerStructMethod(inputStruct: InnerStruct) -> InnerStruct
}

internal class _InterfaceWithStruct: InterfaceWithStruct {

    let c_instance : _baseRef

    init?(cInterfaceWithStruct: _baseRef) {
        guard cInterfaceWithStruct != 0 else {
            return nil
        }
        c_instance = cInterfaceWithStruct
    }

    deinit {
        smoke_InterfaceWithStruct_release_handle(c_instance)
    }
    public func innerStructMethod(inputStruct: InnerStruct) -> InnerStruct {
        let inputStruct_handle = inputStruct.convertToCType()
        defer {
            smoke_InterfaceWithStruct_InnerStruct_release_handle(inputStruct_handle)
        }
        let cResult = smoke_InterfaceWithStruct_innerStructMethod(c_instance, inputStruct_handle)
        defer {
            smoke_InterfaceWithStruct_InnerStruct_release_handle(cResult)
        }
        return InnerStruct(cInnerStruct: cResult)
    }

}

extension _InterfaceWithStruct: NativeBase {
    var c_handle: _baseRef { return c_instance }
}

public struct InnerStruct {
    public var value: Int8

    public init(value: Int8) {
        self.value = value
    }

    internal init(cInnerStruct: _baseRef) {
        value = smoke_InterfaceWithStruct_InnerStruct_value_get(cInnerStruct)
    }

    internal func convertToCType() -> _baseRef {
        let value_handle = value
        return smoke_InterfaceWithStruct_InnerStruct_create_handle(value_handle)
    }
}
