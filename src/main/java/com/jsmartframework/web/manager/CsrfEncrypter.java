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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

final class CsrfEncrypter {

    private static final Logger LOGGER = Logger.getLogger(CsrfEncrypter.class.getPackage().getName());

    static final int CYPHER_KEY_LENGTH = 16;

    private static Cipher getEncryptCipher(String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF8"), "AES");
        Cipher encryptCipher = Cipher.getInstance("AES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return encryptCipher;
    }

    private static Cipher getDecryptCipher(String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF8"), "AES");
        Cipher decryptCipher = Cipher.getInstance("AES");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        return decryptCipher;
    }

    static String encrypt(String key, Object value) {
        if (key != null && value != null) {
            try {
                byte[] encode = getEncryptCipher(key).doFinal(value.toString().getBytes("UTF8"));
                return new String(Base64.encodeBase64(encode, true, true)).trim();
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "Failed to encrypt value: " + value + " " + ex.getMessage());
            }
            return value.toString();
        }
        return null;
    }

    static String decrypt(String key, Object value) {
        if (key != null && value != null) {
            try {
                byte[] decoded = Base64.decodeBase64(value.toString());
                return new String(getDecryptCipher(key).doFinal(decoded), "UTF8");
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "Failed to decrypt value: " + value + " " + ex.getMessage());
            }
            return value.toString();
        }
        return null;
    }

}