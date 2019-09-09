package org.cmdbuild.bim;

import static java.lang.String.format;

public class BimException extends RuntimeException {

	public BimException(Throwable e) {
		super(e);
	}

	public BimException(String message, Throwable e) {
		super(message, e);
	}

	public BimException(String message) {
		super(message);
	}

	public BimException(Throwable e, String message, Object... params) {
		super(format(message, params), e);
	}

}
