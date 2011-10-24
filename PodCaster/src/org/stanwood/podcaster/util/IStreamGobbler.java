package org.stanwood.podcaster.util;

/**
 * Stream gobbler interface
 */
public interface IStreamGobbler {

	/**
	 * Used to mark the stream has done and block till it's not running
	 */
	public void done();

	/**
	 * This will return true when the string has been gobbled.
	 * @return True if the stream has been completely gobbled, otherwise false.
	 */
	public boolean isDone();

	/**
	 * Causes this thread to begin execution
	 */
	public void start();
}
