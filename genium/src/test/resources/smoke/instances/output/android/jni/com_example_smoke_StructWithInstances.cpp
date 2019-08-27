/*
 *
 * Automatically generated. Do not modify. Your changes will be lost.
 */
#include "smoke/StructWithInstances.h"
#include "CppProxyBase.h"
#include "FieldAccessMethods.h"
#include "JniBase.h"
#include "JniCppConversionUtils.h"
#include "com_example_smoke_StructWithInstances.h"
#include "ArrayConversionUtils.h"
#include "EnumConversion.h"
#include "EnumSetConversion.h"
#include "InstanceConversion.h"
#include "StructConversion.h"
#include "ProxyConversion.h"
#include "JniReference.h"
extern "C" {
jobject
Java_com_example_smoke_StructWithInstances_useSimpleClass(JNIEnv* _jenv, jobject _jinstance, jobject jinput)
{
    ::std::shared_ptr< ::smoke::SimpleClass > input = ::genium::jni::convert_from_jni(_jenv,
            ::genium::jni::make_non_releasing_ref(jinput),
            (::std::shared_ptr< ::smoke::SimpleClass >*)nullptr);
    auto _ninstance = ::genium::jni::convert_from_jni(_jenv,
        ::genium::jni::make_non_releasing_ref(_jinstance),
        (::smoke::StructWithInstances*)nullptr);
    auto result = _ninstance.use_simple_class(input);
    return ::genium::jni::convert_to_jni(_jenv, result).release();
}
jobject
Java_com_example_smoke_StructWithInstances_useSimpleInterface(JNIEnv* _jenv, jobject _jinstance, jobject jinput)
{
    ::std::shared_ptr< ::smoke::SimpleInterface > input = ::genium::jni::convert_from_jni(_jenv,
            ::genium::jni::make_non_releasing_ref(jinput),
            (::std::shared_ptr< ::smoke::SimpleInterface >*)nullptr);
    auto _ninstance = ::genium::jni::convert_from_jni(_jenv,
        ::genium::jni::make_non_releasing_ref(_jinstance),
        (::smoke::StructWithInstances*)nullptr);
    auto result = _ninstance.use_simple_interface(input);
    return ::genium::jni::convert_to_jni(_jenv, result).release();
}
}