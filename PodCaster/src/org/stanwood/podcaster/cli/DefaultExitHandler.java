package org.stanwood.podcaster.cli;

/**
 * A default exit handler that calls {@link System#exit(int)}
 */
public class DefaultExitHandler implements IExitHandler {

	/**
	 * This is called when the application wants to exit. It calls {@link System#exit(int)}.
	 * @param exitCode The exit code it's exiting with.
	 */
	@Override
	public void exit(int exitCode) {
		System.exit(exitCode);
	}
}
