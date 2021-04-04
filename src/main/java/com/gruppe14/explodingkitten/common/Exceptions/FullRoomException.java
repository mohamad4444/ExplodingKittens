package com.gruppe14.explodingkitten.common.Exceptions;

public class FullRoomException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FullRoomException(String errorMessage) {
		super(errorMessage);
	}
}