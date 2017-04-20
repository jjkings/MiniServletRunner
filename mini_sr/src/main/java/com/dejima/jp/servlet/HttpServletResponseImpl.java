/*
 * $Id: HttpServletResponseImpl.java,v 1.3 2006/10/18 04:12:02 jun Exp $
 */

package com.dejima.jp.servlet;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;
import java.security.Principal;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * The implementation of HttpServletResponse.
 * 
 * @version $Revision: 1.3 $
 */
public class HttpServletResponseImpl implements HttpServletResponse {
	/**
	 * CRLF
	 */
	private static final String CRLF = "\r\n";

	/**
	 * The flag if output is already done.
	 */
	private boolean commited;

	/**
	 * The socket the response comes from.
	 */
	private Socket socket;

	/**
	 * The output stream to get response.
	 */
	private PrintWriter bodyWriter;

	private ServletOutputStream bodyOut;

	private ByteArrayOutputStream bodyBuf;

	/**
	 * Headers.
	 */
	private Map headers;

	/**
	 * The status.
	 */
	private int status;

	/**
	 * Create new response for the specified inbound response.
	 */
	protected HttpServletResponseImpl(Socket socket) throws IOException {
		this.socket = socket;
		bodyBuf = new ByteArrayOutputStream();
		bodyOut = new ServletOutputStreamImpl(bodyBuf);
		headers = new TreeMap();
		reset();
	}
	
	private static SimpleDateFormat dateFormat;
	static {
		dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Forces any content in the buffer to be written to the client.
	 */
	public void flushBuffer() throws IOException {
		if (bodyWriter != null)
			bodyWriter.close();
		else
			bodyOut.close();

		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

		// Status-Line
		out.writeBytes("HTTP/1.0 ");
		out.writeBytes(String.valueOf(status));
		out.writeByte(' ');
		out.writeBytes(getReasonPhrase(status));
		//out.writeBytes("OK");
		out.writeBytes(CRLF);

		// additional header
		if (!headers.containsKey("Date")) {
			headers.put("Date", dateFormat.format(new Date()));
		}
		if (!headers.containsKey("Server")) {
			headers.put("Server", "Mini Servler Runner 1.0");
		}
		
		// headers
		for (Iterator ite = headers.entrySet().iterator(); ite.hasNext();) {
			Map.Entry entry = (Map.Entry) ite.next();
			out.writeBytes(entry.getKey().toString());
			out.writeBytes(": ");
			out.writeBytes(entry.getValue().toString());
			out.writeBytes(CRLF);
		}

		// body
		out.writeBytes(CRLF);
		out.write(bodyBuf.toByteArray(), 0, bodyBuf.size());

		out.flush();

		commited = true;
	}

	/**
	 * Sets the specified response header.
	 */
	private void _setHeader(String name, Object value) {
		headers.put(name, value);
	}

	/**
	 * Returns the value of the specified response header.
	 */
	private Object _getHeader(String name) {
		return headers.get(name);
	}

	public int getBufferSize() {
		return bodyBuf.size();
	}

	public String getCharacterEncoding() {
		MediaType mediaType = (MediaType) _getHeader("Content-Type");
		return mediaType != null ? mediaType.getParameter("charset") : null;
	}

	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	public ServletOutputStream getOutputStream() {
		return bodyOut;
	}

	public PrintWriter getWriter() {
		if (bodyWriter != null)
			return bodyWriter;

		Writer writer = null;
		String encoding = getCharacterEncoding();
		if (encoding != null) {
			try {
				writer = new OutputStreamWriter(bodyOut, encoding);
			} catch (UnsupportedEncodingException ex) {
				LogManager.warn(ex);
			}
		}
		if (writer == null)
			writer = new OutputStreamWriter(bodyOut);
		bodyWriter = new PrintWriter(writer);
		return bodyWriter;
	}

	public boolean isCommitted() {
		return commited;
	}

	public void reset() {
		resetBuffer();
		headers.clear();
		status = SC_OK;
	}

	public void resetBuffer() {
		if (commited)
			throw new IllegalStateException();
		bodyBuf.reset();
	}

	public void setBufferSize(int size) {
		throw new UnsupportedOperationException();
	}

	public void setContentLength(int len) {
		_setHeader("Content-Length", new Integer(len));
	}

	public void setContentType(String type) {
		_setHeader("Content-Type", new MediaType(type));
	}

	public void setLocale(Locale loc) {
		throw new UnsupportedOperationException();
	}

	public void addCookie(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	public void addDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	public void addIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}

	public String encodeRedirectUrl(String url) {
		throw new UnsupportedOperationException();
	}

	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException();
	}

	public String encodeUrl(String url) {
		throw new UnsupportedOperationException();
	}

	public String encodeURL(String url) {
		throw new UnsupportedOperationException();
	}

	public void sendError(int sc) {
		throw new UnsupportedOperationException();
	}

	public void sendError(int sc, String msg) {
		throw new UnsupportedOperationException();
	}

	public void sendRedirect(String location) {
		throw new UnsupportedOperationException();
	}

	public void setDateHeader(String name, long date) {
		_setHeader(name, new Date(date));
	}

	public void setHeader(String name, String value) {
		_setHeader(name, value);
	}

	public void setIntHeader(String name, int value) {
		_setHeader(name, new Integer(value));
	}

	public void setStatus(int sc) {
		this.status = sc;
	}

	public void setStatus(int sc, String sm) {
		setStatus(sc);
	}

	// internal methods
	/**
	 * The map of status code and reason phrase.
	 */
	private static Map reasonPhraseMap;

	/**
	 * Initialize reason phrase map.
	 */
	static {
		reasonPhraseMap = new HashMap();
		reasonPhraseMap.put(new Integer(SC_OK), "OK");
	}

	/**
	 * Returns the Reason-Phrase for the specified Status-Code
	 */
	private String getReasonPhrase(int statusCode) {
		String reasonPhrase = (String) reasonPhraseMap.get(new Integer(statusCode));
		return reasonPhrase != null ? reasonPhrase : "";
	}
}
