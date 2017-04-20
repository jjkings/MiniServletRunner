/*
 * $Id: HttpServletRequestImpl.java,v 1.6 2006/10/18 04:12:02 jun Exp $
 */

package com.dejima.jp.servlet;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.net.*;
import java.security.Principal;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * The implementation of HttpServletRequest.
 * 
 * @version $Revision: 1.6 $
 */
public class HttpServletRequestImpl implements HttpServletRequest {
	/**
	 * The socket the request comes from.
	 */
	private Socket socket;

	/**
	 * The input stream to get request.
	 */
	private ServletInputStream in;

	/**
	 * The method.
	 */
	private String method;

	/**
	 * The request URI
	 */
	private String requestUri;

	/**
	 * The HTTP version.
	 */
	private String protocol;

	/**
	 * The character encoding used in this request.
	 */
	private String encoding;

	/**
	 * Http headers.
	 */
	private Map headers;

	/**
	 * Parameters.
	 */
	private Map parameters;

	/**
	 * Create new request for the specified inbound request.
	 */
	protected HttpServletRequestImpl(Socket socket) throws IOException, InvalidRequestException {
		this.socket = socket;
		InputStream _in = socket.getInputStream();
		parseRequestLine(_in);
		parseHeaders(_in);

		// create input stream
		String contentLengthStr = getHeader("Content-Length");
		int len = contentLengthStr != null ? Integer.parseInt(contentLengthStr) : 0;
		in = new ServletInputStreamImpl(_in, len);
	}

	public Object getAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	public Enumeration getAttributeNames() {
		throw new UnsupportedOperationException();
	}

	public String getCharacterEncoding() {
		return encoding;
	}

	public void setCharacterEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getContentLength() {
		throw new UnsupportedOperationException();
	}

	public String getContentType() {
		throw new UnsupportedOperationException();
	}

	public ServletInputStream getInputStream() throws IOException {
		return in;
	}

	public String getParameter(String name) {
		String[] values = getParameterValues(name);
		return values != null && values.length > 0 ? values[0] : null;
	}

	public Enumeration getParameterNames() {
		return Collections.enumeration(getParameterMap().keySet());
	}

	public String[] getParameterValues(String name) {
		return (String[]) getParameterMap().get(name);
	}

	public String getProtocol() {
		return protocol;
	}

	public String getScheme() {
		throw new UnsupportedOperationException();
	}

	public String getServerName() {
		return socket.getLocalAddress().getHostName();
	}

	public int getServerPort() {
		return socket.getLocalPort();
	}

	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException();
	}

	public String getRemoteAddr() {
		return socket.getInetAddress().getHostAddress();
	}

	public String getRemoteHost() {
		return socket.getInetAddress().getHostName();
	}

	public void setAttribute(String name, Object obj) {
		throw new UnsupportedOperationException();
	}

	public void removeAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	public Locale getLocale() {
		// TODO
		return Locale.getDefault();
	}

	public Enumeration getLocales() {
		throw new UnsupportedOperationException();
	}

	public boolean isSecure() {
		return false;
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	public String getRealPath(String path) {
		throw new UnsupportedOperationException();
	}

	public String getAuthType() {
		throw new UnsupportedOperationException();
	}

	public String getContextPath() {
		throw new UnsupportedOperationException();
	}

	public Cookie[] getCookies() {
		throw new UnsupportedOperationException();
	}

	public long getDateHeader(String name) {
		throw new UnsupportedOperationException();
	}

	public String getHeader(String name) {
		List values = (List) headers.get(name.toLowerCase());
		return (values != null && values.size() > 0) ? (String) values.get(0) : null;
	}

	public Enumeration getHeaderNames() {
		return Collections.enumeration(headers.keySet());
	}

	public Enumeration getHeaders(String name) {
		return Collections.enumeration((List) headers.get(name.toLowerCase()));
	}

	public int getIntHeader(String name) {
		String value = getHeader(name);
		return value != null ? Integer.parseInt(value) : -1;
	}

	public String getMethod() {
		return method;
	}

	public String getPathInfo() {
		int p = requestUri.indexOf('?');
		if (p < 0)
			return requestUri;
		return requestUri.substring(0, p);
	}

	public String getPathTranslated() {
		throw new UnsupportedOperationException();
	}

	public String getQueryString() {
		int p = requestUri.indexOf('?');
		if (p < 0)
			return null;
		else if (p == requestUri.length() - 1)
			return "";
		return requestUri.substring(p + 1);
	}

	public String getRemoteUser() {
		throw new UnsupportedOperationException();
	}

	public String getRequestedSessionId() {
		throw new UnsupportedOperationException();
	}

	public String getRequestURI() {
		return requestUri;
	}

	public HttpSession getSession() {
		throw new UnsupportedOperationException();
	}

	public HttpSession getSession(boolean create) {
		throw new UnsupportedOperationException();
	}

	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException();
	}

	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException();
	}

	public boolean isRequestedSessionIdFromUrl() {
		throw new UnsupportedOperationException();
	}

	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException();
	}

	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException();
	}

	public boolean isUserInRole(String role) {
		throw new UnsupportedOperationException();
	}

	public Map getParameterMap() {
		if (!("GET".equals(method))) {
			// TODO
			throw new UnsupportedOperationException("The parameter is available only for GET method.");
		}
		synchronized (this) {
			if (parameters == null) {
				parameters = parseParameters();
			}
			return parameters;
		}
	}

	public StringBuffer getRequestURL() {
		throw new UnsupportedOperationException();
	}

	public String getServletPath() {
		throw new UnsupportedOperationException();
	}

	//

	/**
	 * Parse Request-Line.
	 */
	private void parseRequestLine(InputStream in) throws IOException, InvalidRequestException {
		int c;
		int oc = -1;
		List tokens = new ArrayList();
		StringBuffer buf = new StringBuffer();
		while ((c = in.read()) >= 0) {
			if (oc == '\r' && c == '\n') {
				break;
			}
			if (c == ' ') {
				tokens.add(buf.toString().trim());
				buf.setLength(0);
			} else {
				buf.append((char) c);
			}
			oc = c;
		}
		if (buf.length() > 0)
			tokens.add(buf.toString().trim());
		if (tokens.size() < 3)
			throw new InvalidRequestException("Insufficient RequestLine: paramnum=" + tokens.size());

		// method, request-uri, version
		method = (String) tokens.get(0);
		requestUri = (String) tokens.get(1);
		protocol = (String) tokens.get(2);
	}

	/**
	 * Parse headers and populates values.
	 */
	private void parseHeaders(InputStream in) throws IOException, InvalidRequestException {
		headers = new HashMap();
		while (parseHeader(in))
			;
	}

	private boolean parseHeader(InputStream in) throws IOException, InvalidRequestException {
		int c;
		int oc = -1;
		String headerName = null;
		StringBuffer buf = new StringBuffer();
		while ((c = in.read()) >= 0) {
			if (oc == '\r' && c == '\n') {
				break;
			}
			if (c == ':' && headerName == null) {
				headerName = buf.toString().trim().toLowerCase();
				buf.setLength(0);
			} else {
				buf.append((char) c);
			}
			oc = c;
		}
		if (headerName == null)
			return false;

		List values = (List) headers.get(headerName);
		if (values == null) {
			values = new ArrayList();
			headers.put(headerName, values);
		}
		String value = buf.toString().trim();
		values.add(value);

		LogManager.debug("(HTTP header) " + headerName + ": " + value);
		return true;
	}

	/**
	 * Parse the request parameter. This method read parameters from
	 * QUERY_STRING.
	 */
	private Map parseParameters() {
		Map parameters = new HashMap();
		String queryString = getQueryString();
		if (queryString != null) {
			int p = 0;
			while (true) {
				int p1 = p;
				p = queryString.indexOf('&', p1);
				if (p < 0)
					p = queryString.length();
				int p2 = queryString.indexOf('=', p1);
				if (p2 < 0)
					p2 = queryString.length();
				String paramName = queryString.substring(p1, p2 < p ? p2 : p);
				p2++;
				String value = null;
				try {
					value = p2 < p ? getParameterValue(queryString, p2, p) : null;
				} catch (IOException ex) {
					LogManager.warn(ex);
				}
				if (value != null) {
					LogManager.debug("(Parameter) " + paramName + " = " + value);
					List values = (List) parameters.get(paramName);
					if (values == null) {
						values = new ArrayList();
						parameters.put(paramName, values);
					}
					values.add(value);
				}

				if (p >= queryString.length() - 1)
					break;
				p++;
			}

			// convert list to string array.
			for (Iterator ite = parameters.entrySet().iterator(); ite.hasNext();) {
				Map.Entry entry = (Map.Entry) ite.next();
				entry.setValue(((List) entry.getValue()).toArray(new String[0]));
			}
		}

		return Collections.unmodifiableMap(parameters);
	}

	/**
	 * Returns the parameter value in the specified query string.
	 */
	private String getParameterValue(String queryString, int start, int end) throws IOException {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int ep = -1;
			for (int p = start; p < end; p++) {
				int b = queryString.charAt(p) & 0xff;
				if (b == '%') {
					ep = p + 1;
				} else if (ep < 0) {
					if (b == '+') {
						buf.write(' ');
					}
					else {
						buf.write(b);
					}
				} else if (p - ep >= 1) {
					buf.write(Integer.parseInt(queryString.substring(ep, p + 1), 16) & 0xff);
					ep = -1;
				}
			}
			buf.close();
			return encoding != null ? new String(buf.toByteArray(), encoding) : new String(buf.toByteArray());
		} catch (NumberFormatException ex) {
			throw new IOException("Invalid URL encoded string: " + queryString.substring(start, end));
		}
	}
}
