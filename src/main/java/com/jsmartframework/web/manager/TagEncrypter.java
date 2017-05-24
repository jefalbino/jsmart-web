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

import static com.jsmartframework.web.config.Config.CONFIG;
import static com.jsmartframework.web.config.Constants.REQUEST_TAG_ENCRYPT_CIPHER;
import static com.jsmartframework.web.config.Constants.REQUEST_TAG_DECRYPT_CIPHER;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

final class TagEncrypter {

    private static final Logger LOGGER = Logger.getLogger(TagEncrypter.class.getPackage().getName());

    private static final String DEFAULT_KEY = "Aq0Sw9De8Fr7GtH6";

    private static final int CYPHER_KEY_LENGTH = 16;

    private static SecretKey secretKey;

    static void init() {
        try {
            String customKey = CONFIG.getContent().getTagSecretKey();
            if (StringUtils.isNotBlank(customKey)) {

                if (customKey.length() != CYPHER_KEY_LENGTH) {
                    throw new RuntimeException("Custom tag-secret-key must have its value " +
                                               "with [" + CYPHER_KEY_LENGTH + "] characters");
                }
                secretKey = new SecretKeySpec(customKey.getBytes("UTF8"), "AES");
            } else {
                secretKey = new SecretKeySpec(DEFAULT_KEY.getBytes("UTF8"), "AES");
            }
        } catch (UnsupportedEncodingException ex) {
            LOGGER.log(Level.INFO, "Failed to generate key secret for tag: " + ex.getMessage());
        }
    }

    private static Cipher getEncryptCipher(HttpServletRequest request) throws Exception {
        Cipher encryptCipher = (Cipher) request.getAttribute(REQUEST_TAG_ENCRYPT_CIPHER);
        if (encryptCipher == null) {
            encryptCipher = Cipher.getInstance("AES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            request.setAttribute(REQUEST_TAG_ENCRYPT_CIPHER, encryptCipher);
        }
        return encryptCipher;
    }

    private static Cipher getDecryptCipher(HttpServletRequest request) throws Exception {
        Cipher decryptCipher = (Cipher) request.getAttribute(REQUEST_TAG_DECRYPT_CIPHER);
        if (decryptCipher == null) {
            decryptCipher = Cipher.getInstance("AES");
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
            request.setAttribute(REQUEST_TAG_DECRYPT_CIPHER, decryptCipher);
        }
        return decryptCipher;
    }

    static String encrypt(HttpServletRequest request, String value) {
        if (value != null) {
            try {
                byte[] encode = getEncryptCipher(request).doFinal(value.getBytes("UTF8"));
                return new String(Base64.encodeBase64(encode, true, true)).trim();
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "Failed to encrypt tag [" + value + "]: " + ex.getMessage());
            }
        }
        return value;
    }

    static String decrypt(HttpServletRequest request, String value) {
        if (value != null) {
            try {
                byte[] decoded = Base64.decodeBase64(value);
                return new String(getDecryptCipher(request).doFinal(decoded), "UTF8");
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "Failed to decrypt tag [" + value + "]: " + ex.getMessage());
            }
        }
        return value;
    }

}