/*
   Copyright 2005 Simon Mieth

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package de.miethxml.toolkit.wizard;

import java.util.Hashtable;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class WizardConfiguration {
    private Hashtable attribute = new Hashtable();
    private Hashtable configurations = new Hashtable();
    private String name = "";

    public WizardConfiguration() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasValue(String value) {
        return attribute.containsKey(value);
    }

    public Object getValue(String key) {
        return attribute.get(key);
    }

    public Object setValue(
        String key,
        Object value) {
        return attribute.put(key, value);
    }

    public boolean hasChild(String value) {
        return configurations.containsKey(value);
    }

    public WizardConfiguration getChild(String key) {
        return (WizardConfiguration) attribute.get(key);
    }

    public Object addChild(WizardConfiguration child) {
        return attribute.put(child.getName(), child);
    }
}
