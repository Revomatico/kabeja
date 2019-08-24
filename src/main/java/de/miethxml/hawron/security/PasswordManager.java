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
package de.miethxml.hawron.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import de.miethxml.hawron.net.PublishTarget;


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
public final class PasswordManager {
    private static PasswordManager instance = new PasswordManager();
    private Key key;
    private String algorithm = "DES";

    /**
     *
     *
     *
     */
    private PasswordManager() {
        super();

        this.key = loadKey();
    }

    public static void setPasswordManager(Object obj) {
        if (obj.getClass().getName().equals("de.miethxml.hawron.net.PublishTarget")) {
            PublishTarget target = (PublishTarget) obj;
            target.setPasswordManager(instance);
        }
    }

    public String encrypt(String string) {
        try {
            Cipher c = Cipher.getInstance(algorithm);

            c.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypt = c.doFinal(string.getBytes());

            string = "";

            return new sun.misc.BASE64Encoder().encode(encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public String decrypt(String string) {
        try {
            Cipher c = Cipher.getInstance(algorithm);

            c.init(Cipher.DECRYPT_MODE, key);

            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(string);

            byte[] encrypt = c.doFinal(dec);

            string = "";

            return new String(encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private void init() {
    }

    private Key getKey() {
        return key;
    }

    private Key generateKey() {
        try {
            key = KeyGenerator.getInstance(algorithm).generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return key;
    }

    private Key loadKey() {
        try {
            FileInputStream in = new FileInputStream("key.sec");

            byte[] data = new byte[100];

            int i = 0;

            int count = -1;

            while ((count = in.available()) > 0) {
                in.read(data, i, count);

                i += count;
            }

            in.close();

            key = new SecretKeySpec(data, 0, i, algorithm);
        } catch (IOException ioe) {
            key = generateKey();

            storeKey(key);
        }

        return key;
    }

    private void storeKey(Key key) {
        try {
            FileOutputStream out = new FileOutputStream("key.sec");

            out.write(key.getEncoded());

            out.close();
        } catch (IOException ieo) {
        }
    }
}
