/*
 * Copyright (C) 2017 HERE Global B.V. and its affiliate(s). All rights reserved.
 *
 * This software, including documentation, is protected by copyright controlled by
 * HERE Global B.V. All rights are reserved. Copying, including reproducing, storing,
 * adapting or translating, any or all of this material requires the prior written
 * consent of HERE Global B.V. This material also contains confidential information,
 * which may not be disclosed to others without prior written consent of HERE Global B.V.
 *
 */

package com.here.ivi.api.generator.common.jni.templates

import com.here.ivi.api.generator.common.java.templates.JavaCopyrightHeaderTemplate
import com.here.ivi.api.generator.common.jni.JniModelBuilder
import com.here.ivi.api.generator.common.jni.JniTypeNameMapper
import com.here.ivi.api.model.common.Includes
import com.here.ivi.api.model.cppmodel.CppClass
import com.here.ivi.api.model.cppmodel.CppMethod
import com.here.ivi.api.model.javamodel.JavaClass
import com.here.ivi.api.model.javamodel.JavaMethod
import com.here.ivi.api.generator.common.TemplateEngine
import java.util.List
import static com.here.ivi.api.generator.common.jni.JniNameRules.getParameterName
import com.here.ivi.api.generator.common.jni.JniToCppTypeConversionTemplateDelegator
import java.util.LinkedList
import java.util.HashMap
import com.here.ivi.api.generator.common.jni.CppToJniConversionTemplateDelegator
import com.here.ivi.api.generator.common.jni.JniModel

public class JniImplementationTemplate {
  def static generate(JniModel jniModel, List<Includes.InternalPublicInclude> includes) {
    if (jniModel === null || includes === null || includes.isEmpty()) {
      return ""
    }
    return
     '''
      «JavaCopyrightHeaderTemplate.generate()»

      «FOR include : includes»
      #include "«include.file»"
      «ENDFOR»

      extern "C" {

      «FOR jniMethod : jniModel.methods»
      «JniTypeNameMapper.map(jniMethod.javaReturnType)»
      «JniFunctionSignatureTemplate.generate(jniMethod)»
      {
        «FOR parameter : jniMethod.parameters»
          «parameter.cppType» «parameter.baseName» = «JniToCppTypeConversionTemplateDelegator.generate(parameter.javaType, getParameterName(parameter.baseName))»;
        «ENDFOR»
        «jniMethod.cppReturnType» result = «TemplateEngine.render("cpp/CppMethodCall", jniMethod)»
        return «CppToJniConversionTemplateDelegator.generate("result", jniMethod.javaReturnType).toString()»;
      }

      «ENDFOR»
      }
      '''
  }
}
