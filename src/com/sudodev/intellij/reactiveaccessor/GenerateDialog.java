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

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import org.jetbrains.annotations.Nullable;
import org.mozilla.javascript.optimizer.Codegen;

import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class GenerateDialog extends DialogWrapper {

    private final LabeledComponent<JPanel> myComponent;
    private CollectionListModel<PsiField> myFields;

    protected GenerateDialog(PsiClass psiClass) {
        super(psiClass.getProject());
        setTitle("Select Fields to Generate Reactive Accessors");

        PsiField[] allFields = psiClass.getFields();
        PsiField[] fields = new PsiField[allFields.length];

        int i = 0;

        for (PsiField field : allFields) {
            String getterName = CodeGenerator.getGetterMethodName(field);
            String setterName = CodeGenerator.getSetterMethodName(field);
            String staticFieldName = CodeGenerator.getStaticFieldName(field);
            if (!hasMethod(psiClass, getterName) && !hasMethod(psiClass, setterName) && !hasStaticField(psiClass, staticFieldName)) {
                if (!field.hasModifierProperty(PsiModifier.STATIC)) {
                    fields[i++] = field;
                }
            }
        }

        fields = Arrays.copyOfRange(fields, 0, i);

        myFields = new CollectionListModel<PsiField>(fields);

        JBList fieldList = new JBList(myFields);
        fieldList.setCellRenderer(new DefaultPsiElementCellRenderer());
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fieldList);
        decorator.disableAddAction();
        JPanel panel = decorator.createPanel();

        myComponent = LabeledComponent.create(panel, "Fields for which to generate reactive getters and setters");

        init();
    }

    private boolean hasMethod(PsiClass psiClass, String methodName) {
        for (PsiMethod method : psiClass.getMethods()) {
            if (method.getName().equals(methodName) && !method.hasModifierProperty(PsiModifier.STATIC)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasStaticField(PsiClass psiClass, String fieldName) {
        for (PsiField field: psiClass.getAllFields()) {
            if (field.getName().equals(fieldName) && field.hasModifierProperty(PsiModifier.STATIC)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myComponent;
    }

    public List<PsiField> getSelectedFields() {
        return myFields.getItems();
    }
}
