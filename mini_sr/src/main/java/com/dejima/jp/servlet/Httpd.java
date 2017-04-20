/*
 * $Id: Httpd.java,v 1.9 2006/10/18 04:12:02 jun Exp $
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
 * @version $Revision: 1.9 $
 */
public class Httpd implements Runnable {
	/**
	 * The servlet manager.
	 */
	private ServletManager servletMgr;

	/**
	 * The port number which the httpd listens.
	 */
	private int port;

	/**
	 * The socket to listen http port.
	 */
	private ServerSocket serverSocket;

	/**
	 * Creates new server.
	 */
	public Httpd(int port) {
		this.port = port;
		servletMgr = new ServletManager();
	}

	/**
	 * Returns the instance of the servlet manager.
	 */
	public ServletManager getServletManager() {
		return servletMgr;
	}

	/**
	 * Starts the server as the background thread.
	 */
	public void run() {
		try {
			start();
		} catch (Throwable ex) {
			LogManager.error(ex);
		}
	}

	/**
	 * Start the server.
	 */
	public void start() throws IOException {
		// start server
		try {
			synchronized (this) {
				if (serverSocket == null) {
					serverSocket = new ServerSocket(port);
					LogManager.info("Server is running on port " + port);
				}
			}
			while (serverSocket != null) {
				try {
					new Handler(serverSocket.accept());
				} catch (IOException ex) {
					LogManager.error(ex);
				}
			}
		} finally {
			// destroy servlets
			servletMgr.destroyServlets();
		}
	}

	/**
	 * Stop the server.
	 */
	public synchronized void stop() throws IOException {
		ServerSocket _serverSocket = serverSocket;
		serverSocket = null;
		if (_serverSocket != null)
			_serverSocket.close();
		LogManager.info("Server is stopped");
	}

	/**
	 * The http handler.
	 */
	class Handler extends Thread {
		/**
		 * The socket.
		 */
		Socket socket;

		/**
		 * Creates handler for the inbound request on the specified socket.
		 */
		Handler(Socket socket) {
			this.socket = socket;
			this.start();
		}

		/**
		 * Process request.
		 */
		public void run() {
			try {
				// request
				HttpServletRequestImpl request = null;
				try {
					request = new HttpServletRequestImpl(socket);
				} catch (InvalidRequestException ex) {
					LogManager.warn(ex);
				}

				// response
				HttpServletResponseImpl response = new HttpServletResponseImpl(socket);

				// invoke servlet if the request is valid
				if (request != null) {
					LogManager.accessLog(request.getRemoteHost()
							+ " - - [] \""
							+ request.getMethod()
							+ " "
							+ request.getRequestURI()
							+ " "
							+ request.getProtocol()
							+ "\""
							);
					try {
						Servlet servlet = servletMgr.getServlet(request);
						if (servlet != null)
							servlet.service(request, response);
						else {
							LogManager.accessLog("Servlet not found: " + request.getRequestURI());
							createResponsePage(response, HttpServletResponse.SC_NOT_FOUND, "Servlet not found: "
									+ request.getRequestURI());
						}
					} catch (Exception ex) {
						LogManager.error(ex);
						LogManager.accessLog("Servlet error: " + socket.getInetAddress().toString());
						createResponsePage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								"Internal Server error: " + request.getRequestURI());
					}
				}

				// bad request
				else {
					LogManager.accessLog("Bad request from: " + socket.getInetAddress().toString());
					createResponsePage(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request");
				}

				// flush buffer
				if (!response.isCommitted()) {
					response.flushBuffer();
				}

				socket.close();
			} catch (Throwable ex) {
				LogManager.error(ex);
			}
		}

		/**
		 * Create the response page.
		 */
		private void createResponsePage(HttpServletResponse response, int status, String msg) throws IOException {
			response.setContentType("text/plain");
			response.setStatus(status);
			PrintWriter out = response.getWriter();
			out.println("HTTP Status: " + status);
			out.println(msg);
		}
	}
}
