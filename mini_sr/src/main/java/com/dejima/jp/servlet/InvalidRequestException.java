/*
 * $Id: InvalidRequestException.java,v 1.2 2006/02/01 02:35:17 jun Exp $
 */

package com.dejima.jp.servlet;

/**
 * The exception to notify the request is invalid.
 * 
 * @version $Revision: 1.2 $
 */
public class InvalidRequestException extends Exception {
	/**
	 * Create new exception which has the specified detail prompt.
	 */
	public InvalidRequestException(String msg) {
		super(msg);
	}
}
