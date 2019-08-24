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
package de.miethxml;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 *
 *
 */
public class HawronTestSuite extends TestSuite {
    /**
     *
     */
    public HawronTestSuite() {
        super();
    }

    /**
     * @param arg0
     */
    public HawronTestSuite(Class arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public HawronTestSuite(String arg0) {
        super(arg0);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("HawronTestSuite");
        suite.addTestSuite(de.miethxml.security.test.PasswordManagerTest.class);
        suite.addTestSuite(de.miethxml.io.test.UtilitiesTest.class);

        return suite;
    }
}
