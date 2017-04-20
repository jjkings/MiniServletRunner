/*
 * $Id: ServletInputStreamImpl.java,v 1.3 2006/02/01 02:35:17 jun Exp $
 */

package com.dejima.jp.servlet;

import java.io.*;
import javax.servlet.ServletInputStream;

/**
 * The implementation of ServletInputStream.
 * 
 * @version $Revision: 1.3 $
 */
public class ServletInputStreamImpl extends ServletInputStream {
	/**
	 * The input stream from socket.
	 */
	private InputStream in;

	/**
	 * The content length.
	 */
	private int len;

	/**
	 * The size of data which is already read.
	 */
	private int readSize;

	/**
	 * Create new stream.
	 */
	public ServletInputStreamImpl(InputStream in, int len) {
		this.in = in;
		this.len = len;
		this.readSize = 0;
	}

	/**
	 * Reads the next byte of data from the input stream.
	 */
	public int read() throws IOException {
		if (readSize >= len)
			return -1;
		readSize++;
		return in.read();
	}
}
