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
package de.miethxml.toolkit.application;

import java.util.Hashtable;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 *
 */
public class CLIParser {
    /**
     *
     */
    public CLIParser() {
        super();
    }

    public static Hashtable parseParameters(String[] args) {
        Hashtable params = new Hashtable();

        if (args.length > 1) {
            for (int i = 0; (i < args.length) && ((i + 1) < args.length);
                    i += 2) {
                String key = null;

                if (args[i].startsWith("--")) {
                    params.put(args[i].substring(2, args[i].length()),
                        args[i + 1]);
                } else if (args[i].startsWith("-")) {
                    params.put(args[i].substring(1, args[i].length()),
                        args[i + 1]);
                }
            }
        }

        return params;
    }
}
