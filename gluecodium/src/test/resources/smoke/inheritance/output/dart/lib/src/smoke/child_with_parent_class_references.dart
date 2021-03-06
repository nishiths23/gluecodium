import 'package:library/src/_token_cache.dart' as __lib;
import 'package:library/src/_type_repository.dart' as __lib;
import 'package:library/src/builtin_types__conversion.dart';
import 'package:library/src/smoke/child_class_from_class.dart';
import 'package:library/src/smoke/parent_class.dart';
import 'package:library/src/smoke/parent_with_class_references.dart';
import 'dart:ffi';
import 'package:ffi/ffi.dart';
import 'package:meta/meta.dart';
import 'package:library/src/_library_context.dart' as __lib;
abstract class ChildWithParentClassReferences implements ParentWithClassReferences {
  /// Destroys the underlying native object.
  ///
  /// Call this to free memory when you no longer need this instance.
  /// Note that setting the instance to null will not destroy the underlying native object.
  void release();
}
// ChildWithParentClassReferences "private" section, not exported.
final _smoke_ChildWithParentClassReferences_copy_handle = __lib.nativeLibrary.lookupFunction<
    Pointer<Void> Function(Pointer<Void>),
    Pointer<Void> Function(Pointer<Void>)
  >('library_smoke_ChildWithParentClassReferences_copy_handle');
final _smoke_ChildWithParentClassReferences_release_handle = __lib.nativeLibrary.lookupFunction<
    Void Function(Pointer<Void>),
    void Function(Pointer<Void>)
  >('library_smoke_ChildWithParentClassReferences_release_handle');
final _smoke_ChildWithParentClassReferences_get_raw_pointer = __lib.nativeLibrary.lookupFunction<
      Pointer<Void> Function(Pointer<Void>),
      Pointer<Void> Function(Pointer<Void>)
    >('library_smoke_ChildWithParentClassReferences_get_raw_pointer');
final _smoke_ChildWithParentClassReferences_get_type_id = __lib.nativeLibrary.lookupFunction<
    Pointer<Void> Function(Pointer<Void>),
    Pointer<Void> Function(Pointer<Void>)
  >('library_smoke_ChildWithParentClassReferences_get_type_id');
class ChildWithParentClassReferences$Impl implements ChildWithParentClassReferences {
  @protected
  Pointer<Void> handle;
  ChildWithParentClassReferences$Impl(this.handle);
  @override
  void release() {
    if (handle == null) return;
    __lib.reverseCache.remove(_smoke_ChildWithParentClassReferences_get_raw_pointer(handle));
    _smoke_ChildWithParentClassReferences_release_handle(handle);
    handle = null;
  }
  @override
  ChildClassFromClass classFunction() {
    final _classFunction_ffi = __lib.nativeLibrary.lookupFunction<Pointer<Void> Function(Pointer<Void>, Int32), Pointer<Void> Function(Pointer<Void>, int)>('library_smoke_ParentWithClassReferences_classFunction');
    final _handle = this.handle;
    final __result_handle = _classFunction_ffi(_handle, __lib.LibraryContext.isolateId);
    try {
      return smoke_ChildClassFromClass_fromFfi(__result_handle);
    } finally {
      smoke_ChildClassFromClass_releaseFfiHandle(__result_handle);
    }
  }
  @override
  ParentClass get classProperty {
    final _get_ffi = __lib.nativeLibrary.lookupFunction<Pointer<Void> Function(Pointer<Void>, Int32), Pointer<Void> Function(Pointer<Void>, int)>('library_smoke_ParentWithClassReferences_classProperty_get');
    final _handle = this.handle;
    final __result_handle = _get_ffi(_handle, __lib.LibraryContext.isolateId);
    try {
      return smoke_ParentClass_fromFfi(__result_handle);
    } finally {
      smoke_ParentClass_releaseFfiHandle(__result_handle);
    }
  }
  @override
  set classProperty(ParentClass value) {
    final _set_ffi = __lib.nativeLibrary.lookupFunction<Void Function(Pointer<Void>, Int32, Pointer<Void>), void Function(Pointer<Void>, int, Pointer<Void>)>('library_smoke_ParentWithClassReferences_classProperty_set__ParentClass');
    final _value_handle = smoke_ParentClass_toFfi(value);
    final _handle = this.handle;
    final __result_handle = _set_ffi(_handle, __lib.LibraryContext.isolateId, _value_handle);
    smoke_ParentClass_releaseFfiHandle(_value_handle);
    try {
      return (__result_handle);
    } finally {
      (__result_handle);
    }
  }
}
Pointer<Void> smoke_ChildWithParentClassReferences_toFfi(ChildWithParentClassReferences value) =>
  _smoke_ChildWithParentClassReferences_copy_handle((value as ChildWithParentClassReferences$Impl).handle);
ChildWithParentClassReferences smoke_ChildWithParentClassReferences_fromFfi(Pointer<Void> handle) {
  final raw_handle = _smoke_ChildWithParentClassReferences_get_raw_pointer(handle);
  final instance = __lib.reverseCache[raw_handle] as ChildWithParentClassReferences;
  if (instance != null) return instance;
  final _type_id_handle = _smoke_ChildWithParentClassReferences_get_type_id(handle);
  final factoryConstructor = __lib.typeRepository[String_fromFfi(_type_id_handle)];
  String_releaseFfiHandle(_type_id_handle);
  final _copied_handle = _smoke_ChildWithParentClassReferences_copy_handle(handle);
  final result = factoryConstructor != null
    ? factoryConstructor(_copied_handle)
    : ChildWithParentClassReferences$Impl(_copied_handle);
  __lib.reverseCache[raw_handle] = result;
  return result;
}
void smoke_ChildWithParentClassReferences_releaseFfiHandle(Pointer<Void> handle) =>
  _smoke_ChildWithParentClassReferences_release_handle(handle);
Pointer<Void> smoke_ChildWithParentClassReferences_toFfi_nullable(ChildWithParentClassReferences value) =>
  value != null ? smoke_ChildWithParentClassReferences_toFfi(value) : Pointer<Void>.fromAddress(0);
ChildWithParentClassReferences smoke_ChildWithParentClassReferences_fromFfi_nullable(Pointer<Void> handle) =>
  handle.address != 0 ? smoke_ChildWithParentClassReferences_fromFfi(handle) : null;
void smoke_ChildWithParentClassReferences_releaseFfiHandle_nullable(Pointer<Void> handle) =>
  _smoke_ChildWithParentClassReferences_release_handle(handle);
// End of ChildWithParentClassReferences "private" section.
