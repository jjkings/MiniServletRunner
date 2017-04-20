/*
 * $Id: ServletManager.java,v 1.7 2006/10/18 04:12:02 jun Exp $
 */

package com.dejima.jp.servlet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * The class to manage servlet in the Httpd.
 * 
 * @version $Revision: 1.7 $
 */
public class ServletManager {
	/**
	 * Installed servlets.
	 */
	private Map servletMap;
	
	/**
	 * Create new manager
	 */
	ServletManager() {
		servletMap = new HashMap();
	}

	/**
	 * Adds a servlet instance with the servlet name. The added servlet is
	 * initialized immediately. The servelet which has same name is replaced by
	 * new one.
	 * 
	 * @exception ServletException
	 *                thrown when the error occurs in initializing servlet.
	 */
	public void addServlet(String name, String path, Map initParams, Servlet servlet) throws ServletException {
		ServletEntry entry = new ServletEntry(name, path, initParams, servlet);
		servletMap.put(name, entry);

		try {
			LogManager.debug("Initialize servlet: name=" + entry.name + "; class=" + entry.servlet.getClass().getName()
					+ "; pathPattern=" + path + "; initParameter=" + entry.initParams);

			// initialize
			ServletConfigImpl config = new ServletConfigImpl(entry.name, entry.initParams);
			entry.servlet.init(config);
		} catch (Throwable ex) {
			LogManager.error("Fail to initialize servlet: " + entry.name, ex);
			throw new ServletException("Fail to initialize servlet: " + entry.name);
		}
	}

	/**
	 * Returns the requested servlet. If no serlet is available for the request,
	 * returns null.
	 */
	synchronized Servlet getServlet(HttpServletRequest request) {
		// find the requested servlet
		for (Iterator ite = servletMap.values().iterator(); ite.hasNext();) {
			ServletEntry entry = (ServletEntry) ite.next();
			if (entry.servlet != null) {
				Matcher matcher = entry.pathPattern.matcher(request.getPathInfo());
				if (matcher.matches()) return entry.servlet;
			}
		}
		return null;
	}

	/**
	 * Destroy all servlets.
	 */
	synchronized void destroyServlets() {
		// initialize servlets
		for (Iterator ite = servletMap.values().iterator(); ite.hasNext();) {
			ServletEntry entry = (ServletEntry) ite.next();
			if (entry.servlet != null)
				entry.servlet.destroy();
			LogManager.info("Servlet is destroyed: " + entry.name);
		}
	}

	/**
	 * Servlet entry.
	 */
	static class ServletEntry {
		String name;

		Pattern pathPattern;

		Map initParams;

		Servlet servlet;

		ServletEntry(String name, String path, Map initParams, Servlet servlet) {
			this.name = name;
			this.pathPattern = Pattern.compile(path);
			this.initParams = initParams;
			this.servlet = servlet;
		}
	}
}
