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

package com.jsmart5.framework.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "jsmart5")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class SmartConfigContent {

	private SmartUrlPattern[] urlPatterns;

	private SmartSecureMethod[] secureMethods;

	private int sessionTimeout;

	private String[] messageFiles;

	private String defaultLocale;

	private SmartUploadConfig uploadConfig;

	private SmartErrorPage[] errorPages;

	private SmartInitParam[] initParams;

	private SmartCachePattern[] cachePatterns;

	private SmartContentEncode contentEncode;

	private boolean escapeRequest = true;

	private boolean encryptTags = true;

	private boolean printHtml = false;

	private SmartHtmlCompress compressHtml;

	private String smartScan;

	private String ejbLookup = "global";

	private List<String> secureUrls;

	private List<String> nonSecureUrls;

	private List<String> nonSecureUrlsOnly;

	SmartConfigContent() {
		secureUrls = new ArrayList<String>();
		nonSecureUrls = new ArrayList<String>();
		nonSecureUrlsOnly = new ArrayList<String>();
	}

	@XmlElement(name = "url-pattern")
	@XmlElementWrapper(name = "url-patterns")
	public SmartUrlPattern[] getUrlPatterns() {
		return urlPatterns;
	}

	public SmartUrlPattern getUrlPattern(String url) {
		if (url != null && !url.trim().isEmpty() && urlPatterns != null) {
			for (SmartUrlPattern urlPattern : urlPatterns) {
				if (urlPattern.getUrl().equals(url)) {
					return urlPattern;
				}
			}
		}
		return null;
	}

	public void setUrlPatterns(SmartUrlPattern[] urlPatterns) {
		if (urlPatterns != null && urlPatterns.length > 0) {
			this.urlPatterns = urlPatterns;

			for (SmartUrlPattern urlPattern : urlPatterns) {

				if (!urlPattern.isLoggedAccess()) {
					nonSecureUrlsOnly.add(urlPattern.getUrl());
				} else {
					if (urlPattern.getAccess() == null) {
						nonSecureUrls.add(urlPattern.getUrl());
					} else {
						secureUrls.add(urlPattern.getUrl());
					}
				}
			}
		}
	}

	public List<String> getSecureUrls() {
		return secureUrls;
	}

	public boolean containsSecureUrl(String url) {
		if (url != null && !url.trim().isEmpty() && secureUrls != null) {
			return secureUrls.contains(url);
		}
		return false;
	}

	public boolean containsNonSecureUrlOnly(String url) {
		if (url != null && !url.trim().isEmpty() && nonSecureUrlsOnly != null) {
			return nonSecureUrlsOnly.contains(url);
		}
		return false;
	}

	@XmlElement(name = "secure-method")
	@XmlElementWrapper(name = "secure-methods")
	public SmartSecureMethod[] getSecureMethods() {
		return secureMethods;
	}

	public SmartSecureMethod getSecureMethod(String method) {
		if (secureMethods != null) {
			for (SmartSecureMethod secMethod : secureMethods) {
				if (secMethod.getMethod().equals(method)) {
					return secMethod;
				}
			}
		}
		return null;
	}

	public void setSecureMethods(SmartSecureMethod[] secureMethods) {
		this.secureMethods = secureMethods;
	}

	@XmlElement(name = "session-timeout")
	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		if (sessionTimeout >= 0) {
			this.sessionTimeout = sessionTimeout;
		}
	}

	@XmlElement(name = "message-file")
	@XmlElementWrapper(name = "message-files")
	public String[] getMessageFiles() {
		return messageFiles;
	}

	public void setMessageFiles(String[] messageFiles) {
		this.messageFiles = messageFiles;
	}

	@XmlElement(name = "default-locale")
	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		if (defaultLocale != null && !defaultLocale.trim().isEmpty()) {
			this.defaultLocale = defaultLocale;
		}
	}

	@XmlElement(name = "upload-config")
	public SmartUploadConfig getUploadConfig() {
		return uploadConfig;
	}

	public void setUploadConfig(SmartUploadConfig uploadConfig) {
		this.uploadConfig = uploadConfig;
	}

	@XmlElement(name = "error-page")
	@XmlElementWrapper(name = "error-pages")
	public SmartErrorPage[] getErrorPages() {
		return errorPages;
	}

	public SmartErrorPage getErrorPage(Integer code) {
		if (errorPages != null) {
			for (SmartErrorPage errorPage : errorPages) {
				if (errorPage.getCode().equals(code)) {
					return errorPage;
				}
			}
		}
		return null;
	}

	public void setErrorPages(SmartErrorPage[] errorPages) {
		if (errorPages != null && errorPages.length > 0) {
			this.errorPages = errorPages;
		}
	}

	@XmlElement(name = "init-param")
	@XmlElementWrapper(name = "init-params")
	public SmartInitParam[] getInitParams() {
		return initParams;
	}

	public void setInitParams(SmartInitParam[] initParams) {
		if (initParams != null && initParams.length > 0) {
			this.initParams = initParams;
		}
	}

	@XmlElement(name = "escape-request")
	public boolean isEscapeRequest() {
		return escapeRequest;
	}

	public void setEscapeRequest(boolean escapeRequest) {
		this.escapeRequest = escapeRequest;
	}

	@XmlElement(name = "encrypt-tags")
	public boolean isEncryptTags() {
		return encryptTags;
	}

	public void setEncryptTags(boolean encryptTags) {
		this.encryptTags = encryptTags;
	}

	@XmlElement(name = "print-html")
	public boolean isPrintHtml() {
		return printHtml;
	}

	public void setPrintHtml(boolean printHtml) {
		this.printHtml = printHtml;
	}

	@XmlElement(name = "compress-html")
	public SmartHtmlCompress getCompressHtml() {
		if (compressHtml == null) {
			compressHtml = new SmartHtmlCompress();
		}
		return compressHtml;
	}

	public void setCompressHtml(SmartHtmlCompress compressHtml) {
		this.compressHtml = compressHtml;
	}

	@XmlElement(name = "smart-scan")
	public String getSmartScan() {
		return smartScan;
	}

	public void setSmartScan(String smartScan) {
		if (smartScan != null && !smartScan.trim().isEmpty()) {
			this.smartScan = smartScan;
		}
	}

	@XmlElement(name = "ejb-lookup")
	public String getEjbLookup() {
		return ejbLookup;
	}

	public void setEjbLookup(String ejbLookup) {
		if (ejbLookup != null && !ejbLookup.trim().isEmpty()) {
			this.ejbLookup = ejbLookup;
		}
	}

	@XmlElement(name = "cache-pattern")
	@XmlElementWrapper(name = "cache-patterns")
	public SmartCachePattern[] getCachePatterns() {
		return cachePatterns;
	}

	public SmartCachePattern getCachePattern(String file) {
		if (file != null && !file.trim().isEmpty() && cachePatterns != null) {
			for (SmartCachePattern cachePattern : cachePatterns) {
				if (cachePattern.isEndedIn(file)) {
					return cachePattern;
				}
			}
		}
		return null;
	}

	public void setCachePatterns(SmartCachePattern[] cachePatterns) {
		if (cachePatterns != null && cachePatterns.length > 0) {
			this.cachePatterns = cachePatterns;
		}
	}

	@XmlElement(name = "content-encode")
	public SmartContentEncode getContentEncode() {
		return contentEncode;
	}

	public void setContentEncode(SmartContentEncode contentEncode) {
		this.contentEncode = contentEncode;
	}

}
