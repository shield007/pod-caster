/*
 *  Copyright (C) 2008  John-Paul.Stanford <dev@stanwood.org.uk>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stanwood.podcaster.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used to swallow the contents of a stream. This is a thread based
 * class, so {@link #start()} should be called to start the thread. {@link #getResult()}
 * can be called to get the contents of the swallowed stream.
 */
public class StreamGobbler extends Thread implements IStreamGobbler {

	private final static Log log = LogFactory.getLog(StreamGobbler.class);

	private InputStream is;
	private StringBuilder result;
	private boolean done;
	private boolean running;

	/**
	 * Creates a instance of the stream gobbler thread
	 * @param is The stream that is to be swallowed
	 * @param name The name of the process
	 */
	public StreamGobbler(InputStream is,String name) {
		this.is = is;
		result = new StringBuilder(""); //$NON-NLS-1$
		this.setName(name);
	}

	/**
	 * This is executed when the thread is started. It will swallow the
	 * input stream.
	 */
	@Override
	public void run() {
		running = true;
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			done = false;

			while (!isDone() && (line = br.readLine()) != null) {
				result.append(line + FileHelper.LS);
			}
		} catch (IOException ioe) {
			log.debug(ioe.getMessage(),ioe);
		}
		finally {
			running = false;
			done = true;
		}
	}

	/**
	 * Used to get the contents of the swallowed stream.
	 * @return The contents of the swallowed stream
	 */
	public String getResult() {
		return result.toString();
	}

	/**
	 * This will return true when the string has been gobbled.
	 * @return True if the stream has been completely gobbled, otherwise false.
	 */
	@Override
	public boolean isDone() {
		return done;
	}

	/**
	 * Used to mark the stream has done and block till it's not running
	 */
	@Override
	public void done() {
		done = true;
		// Block until it finishes
		while (running) {
			Thread.yield();
		}
	}
}