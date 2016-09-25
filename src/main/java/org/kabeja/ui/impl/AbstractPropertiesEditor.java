/*
   Copyright 2008 Simon Mieth

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
package org.kabeja.ui.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kabeja.ui.PropertiesEditor;
import org.kabeja.ui.PropertiesListener;


public abstract class AbstractPropertiesEditor implements PropertiesEditor {
    protected ArrayList<PropertiesListener> listeners = new ArrayList<PropertiesListener>();
    protected Map<String, Object> properties = new HashMap<String, Object>();

    @Override
    public void addPropertiesListener(PropertiesListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public void removePropertiesListener(PropertiesListener listener) {
        this.listeners.remove(listeners);
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    protected void firePropertiesChangedEvent() {
        @SuppressWarnings("unchecked")
        Iterator<PropertiesListener> i = ((ArrayList<PropertiesListener>) this.listeners.clone()).iterator();

        while (i.hasNext()) {
            PropertiesListener l = i.next();
            l.propertiesChanged(this.properties);
        }
    }
}
