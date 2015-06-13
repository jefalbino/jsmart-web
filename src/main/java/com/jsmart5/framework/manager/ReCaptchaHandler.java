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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

public final class ReCaptchaHandler {
	
	public static final Integer RECAPTCHA_V1 = 1;
	
	public static final Integer RECAPTCHA_V2 = 2;


	public static final String RESPONSE_V1_FIELD_NAME = "recaptcha_response_field";

	public static final String RECAPTCHA_V1_CHALLENGE_URL = "https://www.google.com/recaptcha/api/challenge?k=%s";

	static final String CHALLENGE_V1_FIELD_NAME = "recaptcha_challenge_field";

	static final String RECAPTCHA_V1_VERIFY_URL = "https://www.google.com/recaptcha/api/verify";


	public static final String RESPONSE_V2_FIELD_NAME = "g-recaptcha-response";

	public static final String RECAPTCHA_CHALLENGE_V2_URL = "https://www.google.com/recaptcha/api.js?onload=%s&render=explicit&hl=%s";
	
	static final String RECAPTCHA_V2_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	
	static boolean checkReCaptchaV1(final String secretKey, final String responseField) {
		return checkReCaptcha(secretKey, responseField, RECAPTCHA_V1);
	}
	
	static boolean checkReCaptchaV2(final String secretKey, final String responseField) {
		return checkReCaptcha(secretKey, responseField, RECAPTCHA_V2);
	}
	
	private static boolean checkReCaptcha(final String secretKey, final String responseField, final Integer version) {

		HttpsURLConnection conn = null;
		final HttpServletRequest request = WebContext.getRequest();

		try {
			if (version.equals(RECAPTCHA_V1)) {
				conn = (HttpsURLConnection) new URL(RECAPTCHA_V1_VERIFY_URL).openConnection();
			} else {
				conn = (HttpsURLConnection) new URL(RECAPTCHA_V2_VERIFY_URL).openConnection();
			}

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			StringBuilder params = new StringBuilder();
			params.append("response=").append(URLEncoder.encode(responseField, "UTF-8"))
				.append("&remoteip=").append(request.getRemoteAddr()); 

			if (version.equals(RECAPTCHA_V1)) {
				String challengeField = request.getParameter(CHALLENGE_V1_FIELD_NAME);

				params.append("&privatekey=").append(URLEncoder.encode(secretKey, "UTF-8"))
					.append("&challenge=").append(URLEncoder.encode(challengeField, "UTF-8"));
			} else {
				params.append("&secret=").append(URLEncoder.encode(secretKey, "UTF-8"));
			}

			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(params.toString());
			wr.close();

			if (conn.getResponseCode() != 200) {
				return false;
			}

			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((line = in.readLine()) != null) {
				builder.append(line);
			}
			in.close();

			return builder.toString().contains("true");
			
		} catch (Exception e) {
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private ReCaptchaHandler() {
		// DO NOTHING
	}
}
