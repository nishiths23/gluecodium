//
//
#include "cbridge/include/smoke/cbridge_ListenerInterface.h"
#include "cbridge/include/smoke/cbridge_Weakling.h"
#include "cbridge_internal/include/BaseHandleImpl.h"
#include "cbridge_internal/include/CachedProxyBase.h"
#include "cbridge_internal/include/TypeInitRepository.h"
#include "cbridge_internal/include/WrapperCache.h"
#include "gluecodium/Optional.h"
#include "gluecodium/TypeRepository.h"
#include "smoke/ListenerInterface.h"
#include "smoke/Weakling.h"
#include <memory>
#include <new>
void smoke_Weakling_release_handle(_baseRef handle) {
    delete get_pointer<std::shared_ptr<::smoke::Weakling>>(handle);
}
class smoke_WeaklingProxy : public std::shared_ptr<::smoke::Weakling>::element_type, public CachedProxyBase<smoke_WeaklingProxy> {
public:
    smoke_WeaklingProxy(smoke_Weakling_FunctionTable&& functions)
     : mFunctions(std::move(functions))
    {
    }
    virtual ~smoke_WeaklingProxy() {
        mFunctions.release(mFunctions.swift_pointer);
    }
    smoke_WeaklingProxy(const smoke_WeaklingProxy&) = delete;
    smoke_WeaklingProxy& operator=(const smoke_WeaklingProxy&) = delete;
    ::std::shared_ptr< ::smoke::ListenerInterface > get_listener() const override {
        auto _call_result = mFunctions.smoke_Weakling_listener_get(mFunctions.swift_pointer);
        return Conversion<::std::shared_ptr< ::smoke::ListenerInterface >>::toCppReturn(_call_result);
    }
    void set_listener(const ::std::shared_ptr< ::smoke::ListenerInterface >& newValue) override {
        mFunctions.smoke_Weakling_listener_set(mFunctions.swift_pointer, Conversion<::std::shared_ptr< ::smoke::ListenerInterface >>::toBaseRef(newValue));
    }
private:
    smoke_Weakling_FunctionTable mFunctions;
};
_baseRef smoke_Weakling_create_proxy(smoke_Weakling_FunctionTable functionTable) {
    auto proxy = smoke_WeaklingProxy::get_proxy(std::move(functionTable));
    return proxy ? reinterpret_cast<_baseRef>(new std::shared_ptr<::smoke::Weakling>(proxy)) : 0;
}
const void* smoke_Weakling_get_swift_object_from_cache(_baseRef handle) {
    return handle ? smoke_WeaklingProxy::get_swift_object(get_pointer<std::shared_ptr<::smoke::Weakling>>(handle)->get()) : nullptr;
}
