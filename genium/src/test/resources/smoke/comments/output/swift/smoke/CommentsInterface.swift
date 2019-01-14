//
//
// Automatically generated. Do not modify. Your changes will be lost.
import Foundation
internal func getRef(_ ref: CommentsInterface?, owning: Bool = true) -> RefHolder {
    guard let reference = ref else {
        return RefHolder(0)
    }
    if let instanceReference = reference as? NativeBase {
        let handle_copy = smoke_CommentsInterface_copy_handle(instanceReference.c_handle)
        return owning
            ? RefHolder(ref: handle_copy, release: smoke_CommentsInterface_release_handle)
            : RefHolder(handle_copy)
    }
    var functions = smoke_CommentsInterface_FunctionTable()
    functions.swift_pointer = Unmanaged<AnyObject>.passRetained(reference).toOpaque()
    functions.release = {swift_class_pointer in
        if let swift_class = swift_class_pointer {
            Unmanaged<AnyObject>.fromOpaque(swift_class).release()
        }
    }
    functions.smoke_CommentsInterface_someMethodWithAllComments = {(swift_class_pointer, input) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        return swift_class.someMethodWithAllComments(input: moveFromCType(input))
    }
    functions.smoke_CommentsInterface_someMethodWithInputComments = {(swift_class_pointer, input) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        return swift_class.someMethodWithInputComments(input: moveFromCType(input))
    }
    functions.smoke_CommentsInterface_someMethodWithOutputComments = {(swift_class_pointer, input) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        return swift_class.someMethodWithOutputComments(input: moveFromCType(input))
    }
    functions.smoke_CommentsInterface_someMethodWithNoComments = {(swift_class_pointer, input) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        return swift_class.someMethodWithNoComments(input: moveFromCType(input))
    }
    functions.smoke_CommentsInterface_someMethodWithoutReturnTypeWithAllComments = {(swift_class_pointer, input) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        swift_class.someMethodWithoutReturnTypeWithAllComments(input: moveFromCType(input))
    }
    functions.smoke_CommentsInterface_someMethodWithoutReturnTypeWithNoComments = {(swift_class_pointer, input) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        swift_class.someMethodWithoutReturnTypeWithNoComments(input: moveFromCType(input))
    }
    functions.smoke_CommentsInterface_someMethodWithoutInputParametersWithAllComments = {(swift_class_pointer) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        return swift_class.someMethodWithoutInputParametersWithAllComments()
    }
    functions.smoke_CommentsInterface_someMethodWithoutInputParametersWithNoComments = {(swift_class_pointer) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        return swift_class.someMethodWithoutInputParametersWithNoComments()
    }
    functions.smoke_CommentsInterface_someMethodWithNothing = {(swift_class_pointer) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        swift_class.someMethodWithNothing()
    }
    functions.smoke_CommentsInterface_someMethodWithoutReturnTypeOrInputParameters = {(swift_class_pointer) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        swift_class.someMethodWithoutReturnTypeOrInputParameters()
    }
    functions.smoke_CommentsInterface_someAttribute_get = {(swift_class_pointer) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        return swift_class.someAttribute
    }
    functions.smoke_CommentsInterface_someAttribute_set = {(swift_class_pointer, newValue) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! CommentsInterface
        swift_class.someAttribute = moveFromCType(newValue)
    }
    let proxy = smoke_CommentsInterface_create_proxy(functions)
    return owning ? RefHolder(ref: proxy, release: smoke_CommentsInterface_release_handle) : RefHolder(proxy)
}
/// This is some very useful interface.
public protocol CommentsInterface : AnyObject {
    /// This is some very useful typedef.
    typealias Usefulness = Bool
    /// This is some very useful map.
    typealias SomeMap = [String: CommentsInterface.Usefulness]
    /// This is some very useful attribute.
    var someAttribute: CommentsInterface.Usefulness { get set }
    /// This is some very useful method that measures the usefulness of its input.
    /// - Parameter input: Very useful input parameter
    /// - Returns: Usefulness of the input
    func someMethodWithAllComments(input: String) -> CommentsInterface.Usefulness
    /// This is some very useful method that measures the usefulness of its input.
    /// - Parameter input: Very useful input parameter
    func someMethodWithInputComments(input: String) -> CommentsInterface.Usefulness
    /// This is some very useful method that measures the usefulness of its input.
    /// - Returns: Usefulness of the input
    func someMethodWithOutputComments(input: String) -> CommentsInterface.Usefulness
    /// This is some very useful method that measures the usefulness of its input.
    func someMethodWithNoComments(input: String) -> CommentsInterface.Usefulness
    /// This is some very useful method that does not measure the usefulness of its input.
    /// - Parameter input: Very useful input parameter
    func someMethodWithoutReturnTypeWithAllComments(input: String) -> Void
    /// This is some very useful method that does not measure the usefulness of its input.
    func someMethodWithoutReturnTypeWithNoComments(input: String) -> Void
    /// This is some very useful method that measures the usefulness of something.
    /// - Returns: Usefulness of the input
    func someMethodWithoutInputParametersWithAllComments() -> CommentsInterface.Usefulness
    /// This is some very useful method that measures the usefulness of something.
    func someMethodWithoutInputParametersWithNoComments() -> CommentsInterface.Usefulness
    func someMethodWithNothing() -> Void
    /// This is some very useful method that does nothing.
    func someMethodWithoutReturnTypeOrInputParameters() -> Void
}
internal class _CommentsInterface: CommentsInterface {
    /// This is some very useful constant.
    public static let veryUseful: CommentsInterface.Usefulness = true
    /// This is some very useful attribute.
    var someAttribute: CommentsInterface.Usefulness {
        get {
            return moveFromCType(smoke_CommentsInterface_someAttribute_get(c_instance))
        }
        set {
            return moveFromCType(smoke_CommentsInterface_someAttribute_set(c_instance, newValue))
        }
    }
    let c_instance : _baseRef
    init(cCommentsInterface: _baseRef) {
        guard cCommentsInterface != 0 else {
            fatalError("Nullptr value is not supported for initializers")
        }
        c_instance = cCommentsInterface
    }
    deinit {
        smoke_CommentsInterface_release_handle(c_instance)
    }
    /// This is some very useful method that measures the usefulness of its input.
    /// - Parameter input: Very useful input parameter
    /// - Returns: Usefulness of the input
    public func someMethodWithAllComments(input: String) -> CommentsInterface.Usefulness {
        return moveFromCType(smoke_CommentsInterface_someMethodWithAllComments(c_instance, input))
    }
    /// This is some very useful method that measures the usefulness of its input.
    /// - Parameter input: Very useful input parameter
    public func someMethodWithInputComments(input: String) -> CommentsInterface.Usefulness {
        return moveFromCType(smoke_CommentsInterface_someMethodWithInputComments(c_instance, input))
    }
    /// This is some very useful method that measures the usefulness of its input.
    /// - Returns: Usefulness of the input
    public func someMethodWithOutputComments(input: String) -> CommentsInterface.Usefulness {
        return moveFromCType(smoke_CommentsInterface_someMethodWithOutputComments(c_instance, input))
    }
    /// This is some very useful method that measures the usefulness of its input.
    public func someMethodWithNoComments(input: String) -> CommentsInterface.Usefulness {
        return moveFromCType(smoke_CommentsInterface_someMethodWithNoComments(c_instance, input))
    }
    /// This is some very useful method that does not measure the usefulness of its input.
    /// - Parameter input: Very useful input parameter
    public func someMethodWithoutReturnTypeWithAllComments(input: String) -> Void {
        return moveFromCType(smoke_CommentsInterface_someMethodWithoutReturnTypeWithAllComments(c_instance, input))
    }
    /// This is some very useful method that does not measure the usefulness of its input.
    public func someMethodWithoutReturnTypeWithNoComments(input: String) -> Void {
        return moveFromCType(smoke_CommentsInterface_someMethodWithoutReturnTypeWithNoComments(c_instance, input))
    }
    /// This is some very useful method that measures the usefulness of something.
    /// - Returns: Usefulness of the input
    public func someMethodWithoutInputParametersWithAllComments() -> CommentsInterface.Usefulness {
        return moveFromCType(smoke_CommentsInterface_someMethodWithoutInputParametersWithAllComments(c_instance))
    }
    /// This is some very useful method that measures the usefulness of something.
    public func someMethodWithoutInputParametersWithNoComments() -> CommentsInterface.Usefulness {
        return moveFromCType(smoke_CommentsInterface_someMethodWithoutInputParametersWithNoComments(c_instance))
    }
    public func someMethodWithNothing() -> Void {
        return moveFromCType(smoke_CommentsInterface_someMethodWithNothing(c_instance))
    }
    /// This is some very useful method that does nothing.
    public func someMethodWithoutReturnTypeOrInputParameters() -> Void {
        return moveFromCType(smoke_CommentsInterface_someMethodWithoutReturnTypeOrInputParameters(c_instance))
    }
}
extension _CommentsInterface: NativeBase {
    var c_handle: _baseRef { return c_instance }
}
internal func CommentsInterfacecopyFromCType(_ handle: _baseRef) -> CommentsInterface {
    if let swift_pointer = smoke_CommentsInterface_get_swift_object_from_cache(handle),
        let re_constructed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeUnretainedValue() as? CommentsInterface {
        smoke_CommentsInterface_release_handle(handle)
        return re_constructed
    }
    return _CommentsInterface(cCommentsInterface: handle)
}
internal func CommentsInterfacemoveFromCType(_ handle: _baseRef) -> CommentsInterface {
    return CommentsInterfacecopyFromCType(handle)
}
internal func CommentsInterfacecopyFromCType(_ handle: _baseRef) -> CommentsInterface? {
    guard handle != 0 else {
        return nil
    }
    return CommentsInterfacemoveFromCType(handle) as CommentsInterface
}
internal func CommentsInterfacemoveFromCType(_ handle: _baseRef) -> CommentsInterface? {
    return CommentsInterfacecopyFromCType(handle)
}
/// This is some very useful enum.
public enum SomeEnum : UInt32 {
    /// Not quite useful
    case useless
    /// Somewhat useful
    case useful
}
internal func copyFromCType(_ cValue: UInt32) -> SomeEnum {
    return SomeEnum(rawValue: cValue)!
}
internal func moveFromCType(_ cValue: UInt32) -> SomeEnum {
    return copyFromCType(cValue)
}
/// This is some very useful struct.
public struct SomeStruct {
    /// How useful this struct is
    public var someField: CommentsInterface.Usefulness
    public init(someField: CommentsInterface.Usefulness) {
        self.someField = someField
    }
    internal init(cHandle: _baseRef) {
        someField = smoke_CommentsInterface_SomeStruct_someField_get(cHandle)
    }
    internal func convertToCType() -> _baseRef {
        let someField_handle = someField
        return smoke_CommentsInterface_SomeStruct_create_handle(someField_handle)
    }
}
internal func copyFromCType(_ handle: _baseRef) -> SomeStruct {
    return SomeStruct(cHandle: handle)
}
internal func moveFromCType(_ handle: _baseRef) -> SomeStruct {
    defer {
        smoke_CommentsInterface_SomeStruct_release_handle(handle)
    }
    return copyFromCType(handle)
}
func convertCommentsInterface_SomeMapToCType(_ swiftDict: CommentsInterface.SomeMap) -> _baseRef {
    let c_handle = smoke_CommentsInterface_SomeMap_create_handle()
    for (swift_key, swift_value) in swiftDict {
        let c_key = swift_key.convertToCType()
        defer {
            std_string_release_handle(c_key)
        }
        let c_value = swift_value
        smoke_CommentsInterface_SomeMap_put(c_handle, c_key, c_value)
    }
    return c_handle
}
func convertCommentsInterface_SomeMapFromCType(_ c_handle: _baseRef) -> CommentsInterface.SomeMap {
    var swiftDict: CommentsInterface.SomeMap = [:]
    let iterator_handle = smoke_CommentsInterface_SomeMap_iterator(c_handle)
    while smoke_CommentsInterface_SomeMap_iterator_is_valid(c_handle, iterator_handle) {
        let c_key = smoke_CommentsInterface_SomeMap_iterator_key(iterator_handle)
        let swift_key: String = moveFromCType(c_key)
        let c_value = smoke_CommentsInterface_SomeMap_iterator_value(iterator_handle)
        let swift_value: Bool = moveFromCType(c_value)
        swiftDict[swift_key] = swift_value
        smoke_CommentsInterface_SomeMap_iterator_increment(iterator_handle)
    }
    smoke_CommentsInterface_SomeMap_iterator_release_handle(iterator_handle)
    return swiftDict
}