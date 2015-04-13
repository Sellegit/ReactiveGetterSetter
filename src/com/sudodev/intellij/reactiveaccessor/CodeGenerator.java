/*
 * Original AndroidAccessors (https://github.com/jonstaff/AndroidAccessors) Copyright:
 *
 * Copyright 2014 Jonathon Staff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sudodev.intellij.reactiveaccessor;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    private final PsiClass mClass;
    private final List<PsiField> mFields;

    public CodeGenerator(PsiClass psiClass, List<PsiField> fields) {
        mClass = psiClass;
        mFields = fields;
    }

    public void generate() {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(mClass.getProject());

        // TODO: remove old accessors

        List<PsiMethod> methods = new ArrayList<PsiMethod>();

        List<PsiField> fields = new ArrayList<PsiField>();

        for (PsiField field : mFields) {
            PsiMethod getter =
                    elementFactory.createMethodFromText(generateGetterMethod(field), mClass);
            PsiMethod setter =
                    elementFactory.createMethodFromText(generateSetterMethod(field), mClass);
            PsiField staticField = elementFactory.createFieldFromText(generateStaticField(field), mClass);
            methods.add(getter);
            methods.add(setter);
            fields.add(staticField);
        }

        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mClass.getProject());

        for (PsiMethod method : methods) {
            styleManager.shortenClassReferences(mClass.add(method));
        }

        for (PsiField field : fields) {
            styleManager.shortenClassReferences(mClass.add(field));
        }
    }

    public static String getGetterMethodName(PsiField field) {
        return "get" + getUpperPropertyName(field);
    }

    public static String getSetterMethodName(PsiField field) {
        return "set" + getUpperPropertyName(field);
    }

    private static String getUpperPropertyName(PsiField field) {
        StringBuilder sb = new StringBuilder(field.getName());

        // verify that the first character is an 'm' or an 's' and the second is uppercase
        if ((sb.charAt(0) == 'm' || sb.charAt(0) == 's') && sb.charAt(1) < 97) {
            sb.deleteCharAt(0);
        }
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    private static String getLowerPropertyName(PsiField field) {
        StringBuilder sb = new StringBuilder(field.getName());

        // verify that the first character is an 'm' or an 's' and the second is uppercase
        if ((sb.charAt(0) == 'm' || sb.charAt(0) == 's') && sb.charAt(1) < 97) {
            sb.deleteCharAt(0);
        }
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    private static String generateGetterMethod(PsiField field) {
        StringBuilder sb = new StringBuilder("public ");
        sb.append(field.getType().getPresentableText());

        sb.append(" ").append(getGetterMethodName(field)).append("() { return ").append(field.getName())
                .append("; }");
        return sb.toString();
    }

    private static String joinStrings(String[] strings, String joiner) {
        StringBuilder sb = new StringBuilder("");
        boolean isFirst = true;
        for (String s : strings) {
            if (!isFirst) {
                sb.append(joiner);
            }
            sb.append(s);
            isFirst = false;
        }
        return sb.toString();
    }

    public static String getStaticFieldName(PsiField field) {
        String[] strings = getUpperPropertyName(field).split("(?<!^)(?=[A-Z])");
        for (int i = 0; i < strings.length; ++i) {
            strings[i] = strings[i].toUpperCase();
        }
        return joinStrings(strings, "_") + "_PROPERTY";
    }

    private static boolean getNeedsThis(PsiField field) {
        StringBuilder sb = new StringBuilder(field.getName());
        return !((sb.charAt(0) == 'm' || sb.charAt(0) == 's') && sb.charAt(1) < 97);
    }

    private static String generateStaticField(PsiField field) {
        StringBuilder sb = new StringBuilder("public static final String ");
        sb.append(getStaticFieldName(field)).append(" = ").append("\"").append(getUpperPropertyName(field)).append("\"").append(";");
        return sb.toString();
    }

    private static String generateSetterMethod(PsiField field) {
        StringBuilder sb = new StringBuilder("public void set");

        String thisStr = getNeedsThis(field) ? "this." : "";

        String param = getLowerPropertyName(field);

        String paramUpper = getUpperPropertyName(field);

        String staticFieldName = getStaticFieldName(field);

        sb.append(paramUpper).append("(").append(field.getType().getPresentableText()).append(" ")
                .append(param).append(") { ");

//        sb.append("firePropertyChanging(").append(staticFieldName).append(", ")
//                .append(thisStr).append(field.getName()).append(", ").append(param).append(");");

        sb.append("firePropertyChange(").append(staticFieldName).append(", ")
                .append(thisStr).append(field.getName()).append(", ").append(thisStr)
                .append(field.getName()).append(" = ").append(param).append("); }");

        return sb.toString();
    }
}
