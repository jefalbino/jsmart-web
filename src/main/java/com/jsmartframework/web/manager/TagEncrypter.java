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

final class TagEncrypter {

    private static final String KEY_VALUE = "Aq0Sw9De8Fr7GtH6";

    private static final Logger LOGGER = Logger.getLogger(TagEncrypter.class.getPackage().getName());

    private static Cipher encryptCipher;

    private static Cipher decryptCipher;

    static {
        try {
            SecretKey key = new SecretKeySpec(KEY_VALUE.getBytes("UTF8"), "AES");

            encryptCipher = Cipher.getInstance("AES");
            decryptCipher = Cipher.getInstance("AES");

            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Failed to generate key and cipher to encrypt/decrypt "
                                    + "tag process: " + ex.getMessage());
        }
    }

    static String encrypt(String value) {
        try {
            byte[] encode = encryptCipher.doFinal(value.getBytes("UTF8"));
            return new String(Base64.encodeBase64(encode, true, true)).trim();
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Failed to encrypt tag: " + value + " " + ex.getMessage());
        }
        return value;
    }

    static String decrypt(String value) {
        try {
            byte[] decoded = Base64.decodeBase64(value);
            return new String(decryptCipher.doFinal(decoded), "UTF8");
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Failed to decrypt tag: " + value + " " + ex.getMessage());
        }
        return value;
    }

}