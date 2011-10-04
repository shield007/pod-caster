package org.stanwood.podcaster.capture;

import org.stanwood.podcaster.logging.StanwoodException;

public class CaptureException extends StanwoodException {

	public CaptureException() {
		super();
	}

	public CaptureException(String message, Throwable cause) {
		super(message, cause);
	}

	public CaptureException(String message) {
		super(message);
	}

	public CaptureException(Throwable cause) {
		super(cause);
	}

}
