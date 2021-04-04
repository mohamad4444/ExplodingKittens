package com.gruppe14.explodingkitten.common.Exceptions;

public class RoomAlreadyExistException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoomAlreadyExistException(String errorMessage) {
		super(errorMessage);
	}
}