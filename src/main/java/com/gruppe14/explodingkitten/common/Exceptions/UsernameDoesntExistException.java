package com.gruppe14.explodingkitten.common.Exceptions;

public class UsernameDoesntExistException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UsernameDoesntExistException(String errorMessage) {
		super(errorMessage);
	}
}