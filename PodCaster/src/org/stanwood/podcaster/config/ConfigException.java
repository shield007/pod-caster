package org.stanwood.podcaster.config;

/**
 * This exception is thrown when their is a problem related to the configuration file.
 */
public class ConfigException extends Exception {

	private static final long serialVersionUID = -4700676846904304971L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
     * The cause is not initialised, and may subsequently be initialised by a
     * call to {@link #initCause}.
     */
	public ConfigException() {

	}

	/**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialised, and may subsequently be initialised by
     * a call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
	public ConfigException(String message) {
		super(message);

	}

   /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public ConfigException(Throwable cause) {
		super(cause);

	}

	/**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public ConfigException(String message, Throwable cause) {
		super(message, cause);

	}
}