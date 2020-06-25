// -------------------------------------------------------------------------------------------------
//
//
// -------------------------------------------------------------------------------------------------
#include "include/ExternalTypes.h"
#include <type_traits>
#include <utility>
namespace smoke {
static_assert(
    std::is_same<
        int32_t,
        std::remove_cv<std::remove_reference<decltype(((const external::ClassWithOverloads::StructWithOverloads*)nullptr)->overloadedAccessors())>::type>::type
    >::value,
    "Expected 'int32_t' return type for 'external::ClassWithOverloads::StructWithOverloads::overloadedAccessors'."
);
static_assert(
    std::is_same<
        void,
        decltype(((external::ClassWithOverloads::StructWithOverloads*)nullptr)->overloadedAccessors(*((int32_t*)nullptr)))
    >::value,
    "Expected 'void' return type for 'external::ClassWithOverloads::StructWithOverloads::overloadedAccessors'."
);
static_assert(
    std::is_same<
        ::std::string,
        std::remove_cv<std::remove_reference<decltype(((external::ClassWithOverloads::StructWithOverloads*)nullptr)->overloadedMethod())>::type>::type
    >::value,
    "Expected '::std::string' return type for 'external::ClassWithOverloads::StructWithOverloads::overloadedMethod'."
);
static_assert(
    std::is_same<
        ::std::string,
        std::remove_cv<std::remove_reference<decltype(((external::ClassWithOverloads::StructWithOverloads*)nullptr)->overloadedMethod(
            *((::std::string*)nullptr)))>::type>::type
    >::value,
    "Expected '::std::string' return type for 'external::ClassWithOverloads::StructWithOverloads::overloadedMethod'."
);
static_assert(
    std::is_same<
        ::std::string,
        std::remove_cv<std::remove_reference<decltype(((external::ClassWithOverloads::StructWithOverloads*)nullptr)->overloadedMethod(
            *((::std::string*)nullptr),
            *((bool*)nullptr)))>::type>::type
    >::value,
    "Expected '::std::string' return type for 'external::ClassWithOverloads::StructWithOverloads::overloadedMethod'."
);
}
