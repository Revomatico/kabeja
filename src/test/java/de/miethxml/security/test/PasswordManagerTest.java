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
package de.miethxml.security.test;

import junit.framework.TestCase;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 *
 *
 */
public class PasswordManagerTest extends TestCase {
    public PasswordManagerTest(String arg0) {
        super(arg0);
    }

    public void testCrypt() {
        //		PasswordManager pm = PasswordManager.getInstance();
        //
        //		String source = "Hello World,crypt me!";
        //
        //		String crypt = pm.encrypt(source);
        //
        //		String result = pm.decrypt(crypt);
        //
        //		assertEquals(source, result);
    }

    public void testLoadSaveCrypt() {
        //		PasswordManager pm = PasswordManager.getInstance();
        //
        //		String source = "Hello World,crypt me!";
        //
        //		String crypt = pm.encrypt(source);
        //
        //		try {
        //
        //			PrintWriter out = new PrintWriter(new FileOutputStream("test.txt"));
        //
        //			out.write(crypt);
        //
        //			out.flush();
        //
        //			out.close();
        //
        //		} catch (IOException ioe) {
        //
        //			ioe.printStackTrace();
        //
        //		}
        //
        //		crypt = "";
        //
        //		try {
        //
        //			BufferedReader in = new BufferedReader(new FileReader("test.txt"));
        //
        //			crypt = in.readLine();
        //
        //			in.close();
        //
        //		} catch (IOException ioe) {
        //
        //			ioe.printStackTrace();
        //
        //		}
        //
        //		String result = pm.decrypt(crypt);
        //
        //		assertEquals(source, result);
    }
}
