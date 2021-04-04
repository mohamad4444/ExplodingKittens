package com.gruppe14.explodingkitten.common.Exceptions;

public class RoomDoesntExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public RoomDoesntExistException(String errorMessage) {
		super(errorMessage);
	}
}