/*
 * $Id: SimpleHttpdLauncher.java,v 1.2 2006/02/01 02:35:17 jun Exp $
 */

package com.dejima.jp.servlet;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * The simple httpd implementation.
 * 
 * @version $Revision: 1.2 $
 */
public class SimpleHttpdLauncher {
	/**
	 * The default port.
	 */
	private static final int DEFAULT_PORT = 8000;

	/**
	 * The property header.
	 */
	private static final String PROP_HEADER = "minisr.servlet.";

	/**
	 * Start server.
	 */
	public static final void main(String[] args) {
		try {
			// create hddpd instance
			int port = DEFAULT_PORT;
			Properties serverProps = System.getProperties();
			String portStr = serverProps.getProperty("minisr.httpd.port");
			if (portStr != null) {
				try {
					port = Integer.parseInt(portStr);
				} catch (NumberFormatException ex) {
					LogManager.warn("Use default port because a valid port is not specified.");
				}
			}
			Httpd httpd = new Httpd(port);

			// regist servlets
			ServletManager servletMgr = httpd.getServletManager();
			int l = PROP_HEADER.length();
			for (Iterator ite = serverProps.entrySet().iterator(); ite.hasNext();) {
				Map.Entry propEntry = (Map.Entry) ite.next();
				String propName = (String) propEntry.getKey();
				String propValue = (String) propEntry.getValue();
				if (propName.startsWith(PROP_HEADER) && propName.length() > PROP_HEADER.length()
						&& propName.indexOf('.', PROP_HEADER.length()) < 0) {

					String servletName = propName.substring(PROP_HEADER.length());
					String servletClassName = propValue;
					String servletPath = null;
					Map servletInitParams = new HashMap();

					String servPropPrefix = PROP_HEADER + servletName + ".";
					for (Iterator servPropIte = serverProps.entrySet().iterator(); servPropIte.hasNext();) {
						Map.Entry servPropEntry = (Map.Entry) servPropIte.next();
						String servPropName = (String) servPropEntry.getKey();
						String servPropValue = (String) servPropEntry.getValue();
						if (!(servPropName.startsWith(servPropPrefix) && servPropName.length() > servPropPrefix
								.length()))
							continue;
						String attrName = servPropName.substring(servPropPrefix.length());

						// path
						if (attrName.equals("path")) {
							servletPath = servPropValue;
						}

						// init parameters
						else if (attrName.startsWith("initParam.") && attrName.length() > 10) {
							String initParamName = attrName.substring(10);
							servletInitParams.put(initParamName, servPropValue);
						}
					}
					// create servlet instance and regist it.
					Class servletClass = Class.forName(servletClassName);
					Servlet servlet = (Servlet) servletClass.newInstance();
					servletMgr.addServlet(servletName, servletPath, servletInitParams, servlet);
				}
			}

			// start server
			Thread thread = new Thread(httpd);
			thread.start();
		} catch (Throwable ex) {
			LogManager.error(ex);
		}
	}
}
