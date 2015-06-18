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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.HttpConstraintElement;
import javax.servlet.HttpMethodConstraintElement;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.ServletSecurity.EmptyRoleSemantic;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;

import com.jsmart5.framework.config.*;
import org.springframework.web.context.ContextLoader;

import com.jsmart5.framework.annotation.SmartFilter;
import com.jsmart5.framework.annotation.SmartServlet;

import static com.jsmart5.framework.config.Config.*;
import static com.jsmart5.framework.manager.BeanHandler.*;
import static com.jsmart5.framework.util.WebImage.*;
import static com.jsmart5.framework.util.WebText.*;

@WebListener
public final class ContainerListener implements ServletContextListener {

	private static final List<String> METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE");

	private static final ContextLoader CONTEXT_LOADER = new ContextLoader();

	@Override
	@SuppressWarnings("unchecked")
	public void contextInitialized(ServletContextEvent sce)  {
		try {
			ServletContext servletContext = sce.getServletContext();

			CONFIG.init(servletContext);
	        if (CONFIG.getContent() == null) {
	        	throw new RuntimeException("Configuration file " + Constants.WEB_CONFIG_XML + " was not found in WEB-INF resources folder!");
	        }

	        // Configure the necessary parameters in the Servlet context to get Spring to configure the application without needing an XML file
	        servletContext.setInitParameter("contextClass", "org.springframework.web.context.support.AnnotationConfigWebApplicationContext");

	        String contextConfigLocation = "com.jsmart5.framework.manager";
	        if (CONFIG.getContent().getPackageScan() != null) {
	        	contextConfigLocation += "," + CONFIG.getContent().getPackageScan();
	        }

	        // Tell Spring where to scan for annotations
	        servletContext.setInitParameter("contextConfigLocation", contextConfigLocation);

	        CONTEXT_LOADER.initWebApplicationContext(servletContext);

	        IMAGES.init(servletContext);
	        TEXTS.init(CONFIG.getContent().getMessageFiles(), CONFIG.getContent().getDefaultLocale());
	        HANDLER.init(servletContext);

	        // WebServlet -> @MultipartConfig @WebServlet(name = "WebServlet", displayName = "WebServlet", loadOnStartup = 1)
	        Servlet webServlet = servletContext.createServlet((Class<? extends Servlet>) Class.forName("com.jsmart5.framework.manager.WebServlet"));
	        ServletRegistration.Dynamic servletReg = (ServletRegistration.Dynamic) servletContext.addServlet("WebServlet", webServlet);
	        servletReg.setAsyncSupported(true);
	        servletReg.setLoadOnStartup(1);

	        // WebServlet Initial Parameters
	        InitParam[] initParams = CONFIG.getContent().getInitParams();
	        if (initParams != null) {
	        	for (InitParam initParam : initParams) {
	        		servletReg.setInitParameter(initParam.getName(), initParam.getValue());
	        	}
	        }

	        // MultiPart to allow file upload on WebServlet 
	        MultipartConfigElement multipartElement = getServletMultipartElement();
	        if (multipartElement != null) {
	        	servletReg.setMultipartConfig(multipartElement);
	        }

	        // Security constraint to WebServlet
	        ServletSecurityElement servletSecurityElement = getServletSecurityElement(servletContext);
	        if (servletSecurityElement != null) {
	        	servletReg.setServletSecurity(servletSecurityElement);
	        }

	        // TODO: Fix problem related to authentication by container to use SSL dynamically (Maybe create more than one servlet for secure and non-secure patterns)
	        // Check also the use of request.login(user, pswd)
	        // Check the HttpServletRequest.BASIC_AUTH, CLIENT_CERT_AUTH, FORM_AUTH, DIGEST_AUTH
	        // servletReg.setRunAsRole("admin");
	        // servletContext.declareRoles("admin");

	        // WebServlet URL mapping
	        String[] servletMapping = getServletMapping();
	        servletReg.addMapping(servletMapping);

	        // ErrorFilter -> @WebFilter(urlPatterns = {"/*"})
	        Filter errorFilter = servletContext.createFilter((Class<? extends Filter>) Class.forName("com.jsmart5.framework.filter.ErrorFilter"));
	        FilterRegistration.Dynamic errorFilterReg = (FilterRegistration.Dynamic) servletContext.addFilter("ErrorFilter", errorFilter);

	        errorFilterReg.setAsyncSupported(true);
	        errorFilterReg.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD,
                    DispatcherType.ERROR, DispatcherType.INCLUDE), true, "/*");

	        // EncodeFilter -> @WebFilter(urlPatterns = {"/*"})
	        Filter encodeFilter = servletContext.createFilter((Class<? extends Filter>) Class.forName("com.jsmart5.framework.filter.EncodeFilter"));
	        FilterRegistration.Dynamic encodeFilterReg = (FilterRegistration.Dynamic) servletContext.addFilter("EncodeFilter", encodeFilter);

	        encodeFilterReg.setAsyncSupported(true);
	        encodeFilterReg.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR, 
	        		DispatcherType.INCLUDE), true, "/*");

	        // CacheFilter -> @WebFilter(urlPatterns = {"/*"})
	        Filter cacheFilter = servletContext.createFilter((Class<? extends Filter>) Class.forName("com.jsmart5.framework.filter.CacheFilter"));
	        FilterRegistration.Dynamic cacheFilterReg = (FilterRegistration.Dynamic) servletContext.addFilter("CacheFilter", cacheFilter);

	        cacheFilterReg.setAsyncSupported(true);
	        cacheFilterReg.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR, 
	        		DispatcherType.INCLUDE), true, "/*");

	        // Add custom filters defined by client
	        for (String filterName : sortCustomFilters()) {
	        	Filter customFilter = servletContext.createFilter((Class<? extends Filter>) HANDLER.smartFilters.get(filterName));
	        	HANDLER.executeInjection(customFilter);

	        	SmartFilter smartFilter = customFilter.getClass().getAnnotation(SmartFilter.class);
     	        FilterRegistration.Dynamic customFilterReg = (FilterRegistration.Dynamic) servletContext.addFilter(filterName, customFilter);

     	        if (smartFilter.initParams() != null) {
     	        	for (WebInitParam initParam : smartFilter.initParams()) {
     	        		customFilterReg.setInitParameter(initParam.name(), initParam.value());
     	        	}
     	        }

     	        customFilterReg.setAsyncSupported(smartFilter.asyncSupported());
     	        customFilterReg.addMappingForUrlPatterns(EnumSet.copyOf(Arrays.asList(smartFilter.dispatcherTypes())), true,
                        smartFilter.urlPatterns());
	        }

	        // WebFilter -> @WebFilter(servletNames = {"WebServlet"})
	        Filter webFilter = servletContext.createFilter((Class<? extends Filter>) Class.forName("com.jsmart5.framework.manager.WebFilter"));
	        FilterRegistration.Dynamic webFilterReg = (FilterRegistration.Dynamic) servletContext.addFilter("WebFilter", webFilter);

	        webFilterReg.setAsyncSupported(true);
	        webFilterReg.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ERROR, 
	        		DispatcherType.INCLUDE), true, "WebServlet");

	        // OutputFilter -> @WebFilter(servletNames = {"WebServlet"})
	        Filter outputFilter = servletContext.createFilter((Class<? extends Filter>) Class.forName("com.jsmart5.framework.filter.OutputFilter"));
	        FilterRegistration.Dynamic outputFilterReg = (FilterRegistration.Dynamic) servletContext.addFilter("OutputFilter", outputFilter);

	        outputFilterReg.setAsyncSupported(true);
	        outputFilterReg.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ERROR, 
	        		DispatcherType.INCLUDE), true, "WebServlet");

            // AsyncFilter -> @WebFilter(servletNames = {"WebServlet"})
            // Filter used case AsyncContext is dispatched internally by AsyncBean implementation
            Filter asyncFilter = servletContext.createFilter((Class<? extends Filter>) Class.forName("com.jsmart5.framework.manager.AsyncFilter"));
            FilterRegistration.Dynamic asyncFilterReg = (FilterRegistration.Dynamic) servletContext.addFilter("AsyncFilter", asyncFilter);

            asyncFilterReg.setAsyncSupported(true);
            asyncFilterReg.addMappingForServletNames(EnumSet.of(DispatcherType.ASYNC), true, "WebServlet");

	        // SessionControl - > @WebListener
	        EventListener smartSessionListener = servletContext.createListener((Class<? extends EventListener>) Class.forName("com.jsmart5.framework.manager.SessionControl"));
	        servletContext.addListener(smartSessionListener);

	        // Custom SmartServlet -> Custom Servlets created by application
	        for (String servletName : HANDLER.smartServlets.keySet()) {
	        	Servlet customServlet = servletContext.createServlet((Class<? extends Servlet>) HANDLER.smartServlets.get(servletName));
	        	HANDLER.executeInjection(customServlet);

	        	SmartServlet smartServlet = customServlet.getClass().getAnnotation(SmartServlet.class);
	        	ServletRegistration.Dynamic customReg = (ServletRegistration.Dynamic) servletContext.addServlet(servletName, customServlet);

	        	customReg.setLoadOnStartup(smartServlet.loadOnStartup());
	        	customReg.setAsyncSupported(smartServlet.asyncSupported());

	        	WebInitParam[] customInitParams = smartServlet.initParams();
	        	if (customInitParams != null) {
	        		for (WebInitParam customInitParam : customInitParams) {
	        			customReg.setInitParameter(customInitParam.name(), customInitParam.value());
	        		}
	        	}

		        // Add mapping url for custom servlet
		        customReg.addMapping(smartServlet.urlPatterns());

		        if (customServlet.getClass().isAnnotationPresent(MultipartConfig.class)) {
		        	customReg.setMultipartConfig(new MultipartConfigElement(customServlet.getClass().getAnnotation(MultipartConfig.class)));
		        }
	        }

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		HANDLER.destroy(sce.getServletContext());
		CONTEXT_LOADER.closeWebApplicationContext(sce.getServletContext());
	}

	private List<String> sortCustomFilters() {
		// Sort the custom filter by the order specified
        List<String> customFilters = new ArrayList<String>(HANDLER.smartFilters.keySet());
        Collections.sort(customFilters, new Comparator<String>() {

			@Override
			public int compare(String filterNameOne, String filterNameTwo) {
				SmartFilter smartFilterOne = HANDLER.smartFilters.get(filterNameOne).getAnnotation(SmartFilter.class);
				SmartFilter smartFilterTwo = HANDLER.smartFilters.get(filterNameTwo).getAnnotation(SmartFilter.class);
				return Integer.compare(smartFilterOne.order(), smartFilterTwo.order());
			}
        });

        return customFilters;
	}

	private MultipartConfigElement getServletMultipartElement() {
		UploadConfig uploadConfig = null;
		MultipartConfigElement multipartElement = new MultipartConfigElement("");

        if ((uploadConfig = CONFIG.getContent().getUploadConfig()) != null) {
        	multipartElement = new MultipartConfigElement(uploadConfig.getLocation(), uploadConfig.getMaxFileSize(), uploadConfig.getMaxRequestSize(), uploadConfig.getFileSizeThreshold());
        }

        return multipartElement;
	}

	private ServletSecurityElement getServletSecurityElement(ServletContext servletContext) {
		SecureMethod[] secureMethods = CONFIG.getContent().getSecureMethods();

        if (secureMethods != null && secureMethods.length > 0) {

        	HttpConstraintElement constraint = new HttpConstraintElement();

        	SecureMethod allMethods = CONFIG.getContent().getSecureMethod("*");
        	Set<HttpMethodConstraintElement> methodConstraints = new HashSet<HttpMethodConstraintElement>();

        	if (allMethods != null) {
        		for (String method : METHODS) {
        			HttpConstraintElement constraintElement = getHttpConstraintElement(allMethods);
        			if (constraintElement != null) {
        				methodConstraints.add(new HttpMethodConstraintElement(method, constraintElement));
        			}
        		}

        	} else {
        		for (SecureMethod secureMethod : secureMethods) {
        			HttpConstraintElement constraintElement = getHttpConstraintElement(secureMethod);
        			if (constraintElement != null) {

        				if (secureMethod.getMethod() == null || !METHODS.contains(secureMethod.getMethod().toUpperCase())) {
        					throw new RuntimeException("Method name declared in [secure-method] tag is unsupported! Supported values are HTTP methods.");
        				}
        				methodConstraints.add(new HttpMethodConstraintElement(secureMethod.getMethod().toUpperCase(), constraintElement));
        			}
        		}
        	}

            return new ServletSecurityElement(constraint, methodConstraints);
        }

        return null;
	}

	private HttpConstraintElement getHttpConstraintElement(SecureMethod secureMethod) {
		HttpConstraintElement constraintElement = null;

		if (secureMethod.getEmptyRole() != null && secureMethod.getTransport() != null) {

			EmptyRoleSemantic emptyRole = getEmptyRoleSemantic(secureMethod.getEmptyRole());

			TransportGuarantee transport = getTransportGuarantee(secureMethod.getTransport());

			if (transport == null || emptyRole == null) {
				throw new RuntimeException("Invalid transport or emptyRole attribute for [secure-method] tag! Values allowed are [confidential, none].");
			}
			constraintElement = new HttpConstraintElement(emptyRole, transport, secureMethod.getRoles() != null ? secureMethod.getRoles() : new String[]{});

		} else if (secureMethod.getTransport() != null) {

			TransportGuarantee transport = getTransportGuarantee(secureMethod.getTransport());

			if (transport == null) {
				throw new RuntimeException("Invalid transport attribute for [secure-method] tag! Values allowed are [confidential, none].");
			}
			constraintElement = new HttpConstraintElement(transport, secureMethod.getRoles() != null ? secureMethod.getRoles() : new String[]{});

		} else if (secureMethod.getEmptyRole() != null) {

			EmptyRoleSemantic emptyRole = getEmptyRoleSemantic(secureMethod.getEmptyRole());

			if (emptyRole == null) {
				throw new RuntimeException("Invalid emptyRole attribute for [secure-method] tag! Values allowed are [deny, permit].");
			}
			constraintElement = new HttpConstraintElement(emptyRole);
		}

		return constraintElement;
	}

	private TransportGuarantee getTransportGuarantee(String transport) {
		return transport.equalsIgnoreCase("confidential") ? TransportGuarantee.CONFIDENTIAL : transport.equalsIgnoreCase("none") ? TransportGuarantee.NONE : null;
	}

	private EmptyRoleSemantic getEmptyRoleSemantic(String emptyRole) {
		return emptyRole.equalsIgnoreCase("deny") ? EmptyRoleSemantic.DENY : emptyRole.equalsIgnoreCase("permit") ? EmptyRoleSemantic.PERMIT : null;
	}

	private String[] getServletMapping() {
		List<String> mapping = new ArrayList<String>();

		if (CONFIG.getContent().getUrlPatterns() == null) {
        	throw new RuntimeException("None [url-pattern] tags were found in configuration file " + Constants.WEB_CONFIG_XML
                    + " for url mapping! At lease one URL pattern must be informed.");
        }

    	for (UrlPattern urlPattern : CONFIG.getContent().getUrlPatterns()) {
    		mapping.add(urlPattern.getUrl());
    	}

        CONFIG.addMappedUrls(mapping);

        return mapping.toArray(new String[mapping.size()]);
	}

}
