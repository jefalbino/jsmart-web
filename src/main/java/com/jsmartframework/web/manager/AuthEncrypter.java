/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2015, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmartframework.web.manager;

import org.apache.commons.codec.binary.Base64;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

final class AuthEncrypter {

    private static final Logger LOGGER = Logger.getLogger(AuthEncrypter.class.getPackage().getName());

    static final int CYPHER_KEY_LENGTH_MAX = 16;

    private static Cipher encryptCipher;

    private static Cipher decryptCipher;

    private static void createCiphers(String keyValue) throws Exception {
        if (encryptCipher == null && decryptCipher == null && keyValue != null) {
            SecretKey key = new SecretKeySpec(keyValue.getBytes("UTF8"), "AES");

            encryptCipher = Cipher.getInstance("AES");
            decryptCipher = Cipher.getInstance("AES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
        }
    }

    static String encrypt(String key, Object value) {
        try {
            createCiphers(key);
            byte[] encode = encryptCipher.doFinal(value.toString().getBytes("UTF8"));
            return new String(Base64.encodeBase64(encode, true, true)).trim();
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Failed to encrypt value: " + value + " " + ex.getMessage());
        }
        return value.toString();
    }

    static String decrypt(String key, Object value) {
        try {
            createCiphers(key);
            byte[] decoded = Base64.decodeBase64(value.toString());
            return new String(decryptCipher.doFinal(decoded), "UTF8");
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Failed to decrypt value: " + value + " " + ex.getMessage());
        }
        return value.toString();
    }

}