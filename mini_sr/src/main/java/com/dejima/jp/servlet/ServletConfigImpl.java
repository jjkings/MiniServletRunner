/*
 * $Id: ServletConfigImpl.java,v 1.4 2006/02/01 02:35:17 jun Exp $
 */

package com.dejima.jp.servlet;

import java.net.URL;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

import javax.servlet.*;

/**
 * The implementation of ServletConfig and ServletContext.
 * 
 * @version $Revision: 1.4 $
 */
public class ServletConfigImpl implements ServletConfig, ServletContext {
	/**
	 * Logging facilities.
	 */
	protected static final Logger servletLog = Logger.getLogger("servlet");

	/**
	 * The name of servlet.
	 */
	private String name;

	/**
	 * Initialize parameters.
	 */
	private Map initParams;

	/**
	 * Create new configration.
	 */
	public ServletConfigImpl(String name, Map initParams) {
		this.name = name;
		this.initParams = initParams;
	}

	// methods of ServletConfig
	public String getInitParameter(String name) {
		return initParams != null ? (String) initParams.get(name) : null;
	}

	public Enumeration getInitParameterNames() {
		return initParams != null ? Collections.enumeration(initParams.keySet()) : Collections
				.enumeration(Collections.EMPTY_SET);
	}

	public ServletContext getServletContext() {
		return this;
	}

	public String getServletName() {
		return name;
	}

	// method of ServletContext
	public Object getAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	public Enumeration getAttributeNames() {
		throw new UnsupportedOperationException();
	}

	public ServletContext getContext(String uripath) {
		throw new UnsupportedOperationException();
	}

	public int getMajorVersion() {
		throw new UnsupportedOperationException();
	}

	public String getMimeType(String file) {
		throw new UnsupportedOperationException();
	}

	public int getMinorVersion() {
		throw new UnsupportedOperationException();
	}

	public RequestDispatcher getNamedDispatcher(String name) {
		throw new UnsupportedOperationException();
	}

	public String getRealPath(String path) {
		throw new UnsupportedOperationException();
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	public URL getResource(String path) {
		throw new UnsupportedOperationException();
	}

	public InputStream getResourceAsStream(String path) {
		throw new UnsupportedOperationException();
	}

	public Set getResourcePaths(String path) {
		throw new UnsupportedOperationException();
	}

	public String getServerInfo() {
		throw new UnsupportedOperationException();
	}

	public Servlet getServlet(String name) {
		throw new UnsupportedOperationException();
	}

	public String getServletContextName() {
		throw new UnsupportedOperationException();
	}

	public Enumeration getServletNames() {
		throw new UnsupportedOperationException();
	}

	public Enumeration getServlets() {
		throw new UnsupportedOperationException();
	}

	public void log(Exception exception, String msg) {
		log(msg, exception);
	}

	public void log(String msg) {
		servletLog.info(msg);
	}

	public void log(String message, Throwable throwable) {
		servletLog.warning(message);
		servletLog.throwing(name, "", throwable);
	}

	public void removeAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	public void setAttribute(String name, Object object) {
		throw new UnsupportedOperationException();
	}
}
