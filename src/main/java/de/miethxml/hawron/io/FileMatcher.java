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
package de.miethxml.hawron.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 *
 */
public class FileMatcher {
    public final static String REGEXP_PREFIX = "regexp:";
    public final static String MATCHER_ALL = "*";
    private String[] extensions;
    private Pattern[] patterns;
    private boolean matchesall;

    public FileMatcher(Collection patterns) {
        setPattern(patterns);
    }

    public FileMatcher() {
        extensions = new String[0];
        patterns = new Pattern[0];
    }

    public void setPattern(Collection c) {
        matchesall = false;

        Iterator i = c.iterator();
        ArrayList regexppatterns = new ArrayList();

        while (i.hasNext()) {
            Object obj = i.next();
            String pattern = obj.toString();

            if (pattern.equals(MATCHER_ALL)) {
                matchesall = true;

                //nothing todo now
                return;
            } else if (pattern.startsWith(REGEXP_PREFIX)) {
                regexppatterns.add(pattern.substring(REGEXP_PREFIX.length(),
                        pattern.length()));
                i.remove();
            }
        }

        extensions = new String[c.size()];

        Object[] objects = c.toArray();

        for (int x = 0; x < c.size(); x++) {
            extensions[x] = objects[x].toString();
        }

        patterns = new Pattern[regexppatterns.size()];

        for (int x = 0; x < patterns.length; x++) {
            patterns[x] = Pattern.compile((String) regexppatterns.get(x));
        }
    }

    public boolean matches(String pattern) {
        if (matchesall) {
            return true;
        }

        for (int i = 0; i < extensions.length; i++) {
            if (pattern.endsWith(extensions[i])) {
                return true;
            }
        }

        for (int i = 0; i < patterns.length; i++) {
            Matcher m = patterns[i].matcher(pattern);

            if (m.matches()) {
                return true;
            }
        }

        return false;
    }
}
