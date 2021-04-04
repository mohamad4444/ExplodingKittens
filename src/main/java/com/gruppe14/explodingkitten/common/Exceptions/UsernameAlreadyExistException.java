package com.gruppe14.explodingkitten.common.Exceptions;

public class UsernameAlreadyExistException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UsernameAlreadyExistException(String errorMessage) {
		super(errorMessage);
	}
}