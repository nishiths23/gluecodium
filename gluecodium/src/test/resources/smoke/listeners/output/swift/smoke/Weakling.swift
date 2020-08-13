//
//
import Foundation
public protocol Weakling : AnyObject {
    var listener: ListenerInterface? { get set }
}
internal func getRef(_ ref: Weakling?, owning: Bool = true) -> RefHolder {
    guard let reference = ref else {
        return RefHolder(0)
    }
    var functions = smoke_Weakling_FunctionTable()
    functions.swift_pointer = Unmanaged<AnyObject>.passRetained(reference).toOpaque()
    functions.release = {swift_class_pointer in
        if let swift_class = swift_class_pointer {
            Unmanaged<AnyObject>.fromOpaque(swift_class).release()
        }
    }
    functions.smoke_Weakling_listener_get = {(swift_class_pointer) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! Weakling
        return copyToCType(swift_class.listener).ref
    }
    functions.smoke_Weakling_listener_set = {(swift_class_pointer, newValue) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! Weakling
        swift_class.listener = ListenerInterface_moveFromCType(newValue)
    }
    let proxy = smoke_Weakling_create_proxy(functions)
    return owning ? RefHolder(ref: proxy, release: smoke_Weakling_release_handle) : RefHolder(proxy)
}
internal func Weakling_copyFromCType(_ handle: _baseRef) -> Weakling {
    if let swift_pointer = smoke_Weakling_get_swift_object_from_cache(handle),
        let re_constructed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeUnretainedValue() as? Weakling {
        return re_constructed
    }
    fatalError("Failed to initialize Swift object")
}
internal func Weakling_moveFromCType(_ handle: _baseRef) -> Weakling {
    if let swift_pointer = smoke_Weakling_get_swift_object_from_cache(handle),
        let re_constructed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeUnretainedValue() as? Weakling {
        smoke_Weakling_release_handle(handle)
        return re_constructed
    }
    fatalError("Failed to initialize Swift object")
}
internal func Weakling_copyFromCType(_ handle: _baseRef) -> Weakling? {
    guard handle != 0 else {
        return nil
    }
    return Weakling_moveFromCType(handle) as Weakling
}
internal func Weakling_moveFromCType(_ handle: _baseRef) -> Weakling? {
    guard handle != 0 else {
        return nil
    }
    return Weakling_moveFromCType(handle) as Weakling
}
internal func copyToCType(_ swiftClass: Weakling) -> RefHolder {
    return getRef(swiftClass, owning: false)
}
internal func moveToCType(_ swiftClass: Weakling) -> RefHolder {
    return getRef(swiftClass, owning: true)
}
internal func copyToCType(_ swiftClass: Weakling?) -> RefHolder {
    return getRef(swiftClass, owning: false)
}
internal func moveToCType(_ swiftClass: Weakling?) -> RefHolder {
    return getRef(swiftClass, owning: true)
}
