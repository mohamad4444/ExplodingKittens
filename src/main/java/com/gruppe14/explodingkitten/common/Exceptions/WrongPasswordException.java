package com.gruppe14.explodingkitten.common.Exceptions;

public class WrongPasswordException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongPasswordException(String errorMessage) {
		super(errorMessage);
	}
}