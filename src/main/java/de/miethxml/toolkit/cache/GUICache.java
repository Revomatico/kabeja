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
package de.miethxml.toolkit.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 *
 *
 *
 *
 *
 */
public class GUICache {
    //generate on startup
    static GUICache instance = new GUICache();
    HashMap elements;

    /**
     *
     *
     *
     */
    private GUICache() {
        super();

        elements = new HashMap();
    }

    public static GUICache getInstance() {
        return instance;
    }

    public void addComponent(
        String key,
        Object obj) {
        elements.put(key, obj);
    }

    public boolean cached(String key) {
        return elements.containsKey(key);
    }

    public Object getComponent(String key) {
        return elements.get(key);
    }

    public void emptyCache() {
        Set e = elements.keySet();

        Iterator i = e.iterator();

        while (i.hasNext()) {
            String key = (String) i.next();

            Object obj = elements.get(key);

            if (obj instanceof Cacheable) {
                Cacheable c = (Cacheable) obj;

                c.destroy();
            }
        }

        elements.clear();
    }
}
