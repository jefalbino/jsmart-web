/*
 * JSmart5 - Java Web Development Framework
 * Copyright (c) 2014, Jeferson Albino da Silva, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmart5.framework.manager;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.jsmart5.framework.config.Config;

final class TagEncrypter {

	private static final String KEY_VALUE = "Aq0Sw9De8Fr7GtH6";

	private static final Logger LOGGER = Logger.getLogger(TagEncrypter.class.getPackage().getName());

	private static Cipher encryptCipher;

	private static Cipher decryptCipher;

	private static boolean encryptTagEnabled = Config.CONFIG.getContent().isEncryptTags();

	static {
		try {
			SecretKey key = new SecretKeySpec(KEY_VALUE.getBytes("UTF8"), "AES");

			encryptCipher = Cipher.getInstance("AES");
			decryptCipher = Cipher.getInstance("AES");

			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception ex) {
			LOGGER.log(Level.INFO, "Failure to generate key and cipher data to encrypt/decrypt tag process: " + ex.getMessage());
		}
	}

	static String complexEncrypt(String prefix, String value) {
		try {
			if (encryptTagEnabled) {
	            byte[] encode = encryptCipher.doFinal(value.getBytes("UTF8"));
	            return prefix + new String(Base64.encodeBase64(encode));
			}
        } catch (Exception ex) {
        	LOGGER.log(Level.INFO, "Failure to encrypt tag: " + value + " " + ex.getMessage());
        }
		return prefix + value;
	}

	static String complexDecrypt(String prefix, String value) {
		try {
			if (encryptTagEnabled) {
				byte[] decoded = Base64.decodeBase64(value.replaceFirst(prefix, ""));
	        	return new String(decryptCipher.doFinal(decoded), "UTF8");
			}
		} catch (Exception ex) {
			LOGGER.log(Level.INFO, "Failure to decrypt tag: " + value + " " + ex.getMessage());
        }
		return value.replaceFirst(prefix, "");
	}

}