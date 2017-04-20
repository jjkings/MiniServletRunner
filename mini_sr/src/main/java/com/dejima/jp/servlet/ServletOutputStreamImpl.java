/*
 * $Id: ServletOutputStreamImpl.java,v 1.2 2006/02/01 02:35:17 jun Exp $
 */

package com.dejima.jp.servlet;

import java.io.*;
import javax.servlet.ServletOutputStream;

/**
 * The implementation of ServletOutputStream.
 * 
 * @version $Revision: 1.2 $
 */
public class ServletOutputStreamImpl extends ServletOutputStream {
	private OutputStream out;

	/**
	 * Create new stream.
	 */
	public ServletOutputStreamImpl(OutputStream out) {
		this.out = out;
	}

	/**
	 * Reads the next byte of data from the output stream.
	 */
	public void write(int b) throws IOException {
		out.write(b);
	}
}
