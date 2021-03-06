import 'package:library/src/_token_cache.dart' as __lib;
import 'package:library/src/builtin_types__conversion.dart';
import 'dart:ffi';
import 'package:ffi/ffi.dart';
import 'package:meta/meta.dart';
import 'package:library/src/_library_context.dart' as __lib;
abstract class ExternalClass {
  /// Destroys the underlying native object.
  ///
  /// Call this to free memory when you no longer need this instance.
  /// Note that setting the instance to null will not destroy the underlying native object.
  void release();
  someMethod(int someParameter);
  String get someProperty;
}
enum ExternalClass_SomeEnum {
    someValue
}
// ExternalClass_SomeEnum "private" section, not exported.
int smoke_ExternalClass_SomeEnum_toFfi(ExternalClass_SomeEnum value) {
  switch (value) {
  case ExternalClass_SomeEnum.someValue:
    return 0;
  break;
  default:
    throw StateError("Invalid enum value $value for ExternalClass_SomeEnum enum.");
  }
}
ExternalClass_SomeEnum smoke_ExternalClass_SomeEnum_fromFfi(int handle) {
  switch (handle) {
  case 0:
    return ExternalClass_SomeEnum.someValue;
  break;
  default:
    throw StateError("Invalid numeric value $handle for ExternalClass_SomeEnum enum.");
  }
}
void smoke_ExternalClass_SomeEnum_releaseFfiHandle(int handle) {}
final _smoke_ExternalClass_SomeEnum_create_handle_nullable = __lib.nativeLibrary.lookupFunction<
    Pointer<Void> Function(Uint32),
    Pointer<Void> Function(int)
  >('library_smoke_ExternalClass_SomeEnum_create_handle_nullable');
final _smoke_ExternalClass_SomeEnum_release_handle_nullable = __lib.nativeLibrary.lookupFunction<
    Void Function(Pointer<Void>),
    void Function(Pointer<Void>)
  >('library_smoke_ExternalClass_SomeEnum_release_handle_nullable');
final _smoke_ExternalClass_SomeEnum_get_value_nullable = __lib.nativeLibrary.lookupFunction<
    Uint32 Function(Pointer<Void>),
    int Function(Pointer<Void>)
  >('library_smoke_ExternalClass_SomeEnum_get_value_nullable');
Pointer<Void> smoke_ExternalClass_SomeEnum_toFfi_nullable(ExternalClass_SomeEnum value) {
  if (value == null) return Pointer<Void>.fromAddress(0);
  final _handle = smoke_ExternalClass_SomeEnum_toFfi(value);
  final result = _smoke_ExternalClass_SomeEnum_create_handle_nullable(_handle);
  smoke_ExternalClass_SomeEnum_releaseFfiHandle(_handle);
  return result;
}
ExternalClass_SomeEnum smoke_ExternalClass_SomeEnum_fromFfi_nullable(Pointer<Void> handle) {
  if (handle.address == 0) return null;
  final _handle = _smoke_ExternalClass_SomeEnum_get_value_nullable(handle);
  final result = smoke_ExternalClass_SomeEnum_fromFfi(_handle);
  smoke_ExternalClass_SomeEnum_releaseFfiHandle(_handle);
  return result;
}
void smoke_ExternalClass_SomeEnum_releaseFfiHandle_nullable(Pointer<Void> handle) =>
  _smoke_ExternalClass_SomeEnum_release_handle_nullable(handle);
// End of ExternalClass_SomeEnum "private" section.
class ExternalClass_SomeStruct {
  String someField;
  ExternalClass_SomeStruct(this.someField);
}
// ExternalClass_SomeStruct "private" section, not exported.
final _smoke_ExternalClass_SomeStruct_create_handle = __lib.nativeLibrary.lookupFunction<
    Pointer<Void> Function(Pointer<Void>),
    Pointer<Void> Function(Pointer<Void>)
  >('library_smoke_ExternalClass_SomeStruct_create_handle');
final _smoke_ExternalClass_SomeStruct_release_handle = __lib.nativeLibrary.lookupFunction<
    Void Function(Pointer<Void>),
    void Function(Pointer<Void>)
  >('library_smoke_ExternalClass_SomeStruct_release_handle');
final _smoke_ExternalClass_SomeStruct_get_field_someField = __lib.nativeLibrary.lookupFunction<
    Pointer<Void> Function(Pointer<Void>),
    Pointer<Void> Function(Pointer<Void>)
  >('library_smoke_ExternalClass_SomeStruct_get_field_someField');
Pointer<Void> smoke_ExternalClass_SomeStruct_toFfi(ExternalClass_SomeStruct value) {
  final _someField_handle = String_toFfi(value.someField);
  final _result = _smoke_ExternalClass_SomeStruct_create_handle(_someField_handle);
  String_releaseFfiHandle(_someField_handle);
  return _result;
}
ExternalClass_SomeStruct smoke_ExternalClass_SomeStruct_fromFfi(Pointer<Void> handle) {
  final _someField_handle = _smoke_ExternalClass_SomeStruct_get_field_someField(handle);
  try {
    return ExternalClass_SomeStruct(
      String_fromFfi(_someField_handle)
    );
  } finally {
    String_releaseFfiHandle(_someField_handle);
  }
}
void smoke_ExternalClass_SomeStruct_releaseFfiHandle(Pointer<Void> handle) => _smoke_ExternalClass_SomeStruct_release_handle(handle);
// Nullable ExternalClass_SomeStruct
final _smoke_ExternalClass_SomeStruct_create_handle_nullable = __lib.nativeLibrary.lookupFunction<
    Pointer<Void> Function(Pointer<Void>),
    Pointer<Void> Function(Pointer<Void>)
  >('library_smoke_ExternalClass_SomeStruct_create_handle_nullable');
final _smoke_ExternalClass_SomeStruct_release_handle_nullable = __lib.nativeLibrary.lookupFunction<
    Void Function(Pointer<Void>),
    void Function(Pointer<Void>)
  >('library_smoke_ExternalClass_SomeStruct_release_handle_nullable');
final _smoke_ExternalClass_SomeStruct_get_value_nullable = __lib.nativeLibrary.lookupFunction<
    Pointer<Void> Function(Pointer<Void>),
    Pointer<Void> Function(Pointer<Void>)
  >('library_smoke_ExternalClass_SomeStruct_get_value_nullable');
Pointer<Void> smoke_ExternalClass_SomeStruct_toFfi_nullable(ExternalClass_SomeStruct value) {
  if (value == null) return Pointer<Void>.fromAddress(0);
  final _handle = smoke_ExternalClass_SomeStruct_toFfi(value);
  final result = _smoke_ExternalClass_SomeStruct_create_handle_nullable(_handle);
  smoke_ExternalClass_SomeStruct_releaseFfiHandle(_handle);
  return result;
}
ExternalClass_SomeStruct smoke_ExternalClass_SomeStruct_fromFfi_nullable(Pointer<Void> handle) {
  if (handle.address == 0) return null;
  final _handle = _smoke_ExternalClass_SomeStruct_get_value_nullable(handle);
  final result = smoke_ExternalClass_SomeStruct_fromFfi(_handle);
  smoke_ExternalClass_SomeStruct_releaseFfiHandle(_handle);
  return result;
}
void smoke_ExternalClass_SomeStruct_releaseFfiHandle_nullable(Pointer<Void> handle) =>
  _smoke_ExternalClass_SomeStruct_release_handle_nullable(handle);
// End of ExternalClass_SomeStruct "private" section.
// ExternalClass "private" section, not exported.
final _smoke_ExternalClass_copy_handle = __lib.nativeLibrary.lookupFunction<
    Pointer<Void> Function(Pointer<Void>),
    Pointer<Void> Function(Pointer<Void>)
  >('library_smoke_ExternalClass_copy_handle');
final _smoke_ExternalClass_release_handle = __lib.nativeLibrary.lookupFunction<
    Void Function(Pointer<Void>),
    void Function(Pointer<Void>)
  >('library_smoke_ExternalClass_release_handle');
final _smoke_ExternalClass_get_raw_pointer = __lib.nativeLibrary.lookupFunction<
      Pointer<Void> Function(Pointer<Void>),
      Pointer<Void> Function(Pointer<Void>)
    >('library_smoke_ExternalClass_get_raw_pointer');
class ExternalClass$Impl implements ExternalClass {
  @protected
  Pointer<Void> handle;
  ExternalClass$Impl(this.handle);
  @override
  void release() {
    if (handle == null) return;
    __lib.reverseCache.remove(_smoke_ExternalClass_get_raw_pointer(handle));
    _smoke_ExternalClass_release_handle(handle);
    handle = null;
  }
  @override
  someMethod(int someParameter) {
    final _someMethod_ffi = __lib.nativeLibrary.lookupFunction<Void Function(Pointer<Void>, Int32, Int8), void Function(Pointer<Void>, int, int)>('library_smoke_ExternalClass_someMethod__Byte');
    final _someParameter_handle = (someParameter);
    final _handle = this.handle;
    final __result_handle = _someMethod_ffi(_handle, __lib.LibraryContext.isolateId, _someParameter_handle);
    (_someParameter_handle);
    try {
      return (__result_handle);
    } finally {
      (__result_handle);
    }
  }
  @override
  String get someProperty {
    final _get_ffi = __lib.nativeLibrary.lookupFunction<Pointer<Void> Function(Pointer<Void>, Int32), Pointer<Void> Function(Pointer<Void>, int)>('library_smoke_ExternalClass_someProperty_get');
    final _handle = this.handle;
    final __result_handle = _get_ffi(_handle, __lib.LibraryContext.isolateId);
    try {
      return String_fromFfi(__result_handle);
    } finally {
      String_releaseFfiHandle(__result_handle);
    }
  }
}
Pointer<Void> smoke_ExternalClass_toFfi(ExternalClass value) =>
  _smoke_ExternalClass_copy_handle((value as ExternalClass$Impl).handle);
ExternalClass smoke_ExternalClass_fromFfi(Pointer<Void> handle) {
  final raw_handle = _smoke_ExternalClass_get_raw_pointer(handle);
  final instance = __lib.reverseCache[raw_handle] as ExternalClass;
  if (instance != null) return instance;
  final _copied_handle = _smoke_ExternalClass_copy_handle(handle);
  final result = ExternalClass$Impl(_copied_handle);
  __lib.reverseCache[raw_handle] = result;
  return result;
}
void smoke_ExternalClass_releaseFfiHandle(Pointer<Void> handle) =>
  _smoke_ExternalClass_release_handle(handle);
Pointer<Void> smoke_ExternalClass_toFfi_nullable(ExternalClass value) =>
  value != null ? smoke_ExternalClass_toFfi(value) : Pointer<Void>.fromAddress(0);
ExternalClass smoke_ExternalClass_fromFfi_nullable(Pointer<Void> handle) =>
  handle.address != 0 ? smoke_ExternalClass_fromFfi(handle) : null;
void smoke_ExternalClass_releaseFfiHandle_nullable(Pointer<Void> handle) =>
  _smoke_ExternalClass_release_handle(handle);
// End of ExternalClass "private" section.
