//
//
import Foundation
@objc
public protocol ObjcInterface : AnyObject {
}
internal class _ObjcInterface: ObjcInterface {
    let c_instance : _baseRef
    init(cObjcInterface: _baseRef) {
        guard cObjcInterface != 0 else {
            fatalError("Nullptr value is not supported for initializers")
        }
        c_instance = cObjcInterface
    }
    deinit {
        smoke_ObjcInterface_remove_swift_object_from_wrapper_cache(c_instance)
        smoke_ObjcInterface_release_handle(c_instance)
    }
}
@_cdecl("_CBridgeInitsmoke_ObjcInterface")
internal func _CBridgeInitsmoke_ObjcInterface(handle: _baseRef) -> UnsafeMutableRawPointer {
    let reference = _ObjcInterface(cObjcInterface: handle)
    return Unmanaged<AnyObject>.passRetained(reference).toOpaque()
}
internal func getRef(_ ref: ObjcInterface?, owning: Bool = true) -> RefHolder {
    guard let reference = ref else {
        return RefHolder(0)
    }
    if let instanceReference = reference as? NativeBase {
        let handle_copy = smoke_ObjcInterface_copy_handle(instanceReference.c_handle)
        return owning
            ? RefHolder(ref: handle_copy, release: smoke_ObjcInterface_release_handle)
            : RefHolder(handle_copy)
    }
    var functions = smoke_ObjcInterface_FunctionTable()
    functions.swift_pointer = Unmanaged<AnyObject>.passRetained(reference).toOpaque()
    functions.release = {swift_class_pointer in
        if let swift_class = swift_class_pointer {
            Unmanaged<AnyObject>.fromOpaque(swift_class).release()
        }
    }
    let proxy = smoke_ObjcInterface_create_proxy(functions)
    return owning ? RefHolder(ref: proxy, release: smoke_ObjcInterface_release_handle) : RefHolder(proxy)
}
extension _ObjcInterface: NativeBase {
    var c_handle: _baseRef { return c_instance }
}
internal func ObjcInterface_copyFromCType(_ handle: _baseRef) -> ObjcInterface {
    if let swift_pointer = smoke_ObjcInterface_get_swift_object_from_cache(handle),
        let re_constructed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeUnretainedValue() as? ObjcInterface {
        return re_constructed
    }
    if let swift_pointer = smoke_ObjcInterface_get_swift_object_from_wrapper_cache(handle),
        let re_constructed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeUnretainedValue() as? ObjcInterface {
        return re_constructed
    }
    if let swift_pointer = smoke_ObjcInterface_get_typed(smoke_ObjcInterface_copy_handle(handle)),
        let typed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeRetainedValue() as? ObjcInterface {
        smoke_ObjcInterface_cache_swift_object_wrapper(handle, swift_pointer)
        return typed
    }
    fatalError("Failed to initialize Swift object")
}
internal func ObjcInterface_moveFromCType(_ handle: _baseRef) -> ObjcInterface {
    if let swift_pointer = smoke_ObjcInterface_get_swift_object_from_cache(handle),
        let re_constructed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeUnretainedValue() as? ObjcInterface {
        smoke_ObjcInterface_release_handle(handle)
        return re_constructed
    }
    if let swift_pointer = smoke_ObjcInterface_get_swift_object_from_wrapper_cache(handle),
        let re_constructed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeUnretainedValue() as? ObjcInterface {
        smoke_ObjcInterface_release_handle(handle)
        return re_constructed
    }
    if let swift_pointer = smoke_ObjcInterface_get_typed(handle),
        let typed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeRetainedValue() as? ObjcInterface {
        smoke_ObjcInterface_cache_swift_object_wrapper(handle, swift_pointer)
        return typed
    }
    fatalError("Failed to initialize Swift object")
}
internal func ObjcInterface_copyFromCType(_ handle: _baseRef) -> ObjcInterface? {
    guard handle != 0 else {
        return nil
    }
    return ObjcInterface_moveFromCType(handle) as ObjcInterface
}
internal func ObjcInterface_moveFromCType(_ handle: _baseRef) -> ObjcInterface? {
    guard handle != 0 else {
        return nil
    }
    return ObjcInterface_moveFromCType(handle) as ObjcInterface
}
internal func copyToCType(_ swiftClass: ObjcInterface) -> RefHolder {
    return getRef(swiftClass, owning: false)
}
internal func moveToCType(_ swiftClass: ObjcInterface) -> RefHolder {
    return getRef(swiftClass, owning: true)
}
internal func copyToCType(_ swiftClass: ObjcInterface?) -> RefHolder {
    return getRef(swiftClass, owning: false)
}
internal func moveToCType(_ swiftClass: ObjcInterface?) -> RefHolder {
    return getRef(swiftClass, owning: true)
}
