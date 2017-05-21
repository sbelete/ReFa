package edu.brown.cs.sbelete.dataminer;

public class HTTPException extends Exception {
	// Parameterless Constructor
	public HTTPException() {
	}

	// Constructor that accepts a message
	public HTTPException(String message) {
		super(message);
	}
}
