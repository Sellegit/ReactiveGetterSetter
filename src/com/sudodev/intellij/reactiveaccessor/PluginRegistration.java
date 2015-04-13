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


import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.components.ApplicationComponent;

public class PluginRegistration implements ApplicationComponent {
    // Returns the component name (any unique string value).
    public String getComponentName() {
        return "ReactiveAccessor";
    }


    // If you register the MyPluginRegistration class in the <application-components> section of
// the plugin.xml file, this method is called on IDEA start-up.
    public void initComponent() {
        ActionManager am = ActionManager.getInstance();
        AccessorAction action = new AccessorAction();
        // Passes an instance of your custom TextBoxes class to the registerAction method of the ActionManager class.
        am.registerAction("ReactiveAccessor", action);
        // Gets an instance of the WindowMenu action group.
        DefaultActionGroup windowM = (DefaultActionGroup) am.getAction(IdeActions.GROUP_GENERATE);
        // Adds a separator and a new menu command to the WindowMenu group on the main menu.
        windowM.add(action);
    }

    // Disposes system resources.
    public void disposeComponent() {
    }
}
