//
//
// Automatically generated. Do not modify. Your changes will be lost.
#include "cbridge/include/smoke/cbridge_PointerEquatableInterface.h"
#include "cbridge_internal/include/BaseHandleImpl.h"
#include "genium/Optional.h"
#include "smoke/PointerEquatableInterface.h"
#include <memory>
#include <new>
void smoke_PointerEquatableInterface_release_handle(_baseRef handle) {
    delete get_pointer<std::shared_ptr<::smoke::PointerEquatableInterface>>(handle);
}
_baseRef smoke_PointerEquatableInterface_copy_handle(_baseRef handle) {
    return handle
        ? reinterpret_cast<_baseRef>(checked_pointer_copy(*get_pointer<std::shared_ptr<::smoke::PointerEquatableInterface>>(handle)))
        : 0;
}
bool smoke_PointerEquatableInterface_equal(_baseRef lhs, _baseRef rhs) {
    return *get_pointer<std::shared_ptr<::smoke::PointerEquatableInterface>>(lhs) == *get_pointer<std::shared_ptr<::smoke::PointerEquatableInterface>>(rhs);
}