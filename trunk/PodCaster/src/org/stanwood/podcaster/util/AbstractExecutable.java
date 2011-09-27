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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.stanwood.podcaster.config.ConfigReader;


/**
 * This should be extended by classes that wrap around executables. It has methods
 * to execute the executable and get the resulting error and output streams.
 *
 */
public class AbstractExecutable {

	private final static Log log = LogFactory.getLog(AbstractExecutable.class);

	private StreamGobbler errorGobbler;
	private StreamGobbler outputGobbler;
	private Process proc;

	private ConfigReader config;

	public AbstractExecutable(ConfigReader config) {
		this.config = config;
	}
	
	public ConfigReader getConfig() {
		return config;
	}
	
	/**
	 * Execute the command with a list of arguments. The this argument should be the application.
	 * @param args The arguments. The first is the application been executed.
	 * @return The exit code of the application that is executed.
	 * @throws IOException Thrown if their is a IO related problem.
	 * @throws InterruptedException Thrown if the thread is interrupted.
	 */
	protected int execute(List<String> args) throws IOException, InterruptedException {		
		List<String> newArgs = new ArrayList<String>();
		if (System.getProperty("os.name").toLowerCase().equals("Windows 95")) {
			newArgs.add("command.com");
			newArgs.add("/C");
		}
		else if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			newArgs.add("cmd.exe");
			newArgs.add("/C");
		}
		newArgs.addAll(args);

		if (log.isDebugEnabled()) {
			StringBuilder debugOutput = new StringBuilder();
			for (String arg : newArgs) {
				debugOutput.append(arg);
				debugOutput.append(" ");
			}
			log.debug(debugOutput.toString());
		}

		ProcessBuilder pb = new ProcessBuilder(newArgs);
		proc = pb.start();

		errorGobbler = new StreamGobbler(proc.getErrorStream(),"stderr reader");
		outputGobbler = new StreamGobbler(proc.getInputStream(),"stdout reader");

        outputGobbler.start();
        errorGobbler.start();        
		int exitCode =  proc.waitFor();		
		while (!errorGobbler.isDone() || !outputGobbler.isDone()) { }
		
		errorGobbler.done();
		outputGobbler.done();

		proc.getErrorStream().close();
		proc.getInputStream().close();
		proc.getOutputStream().close();
		return exitCode;
	}

	/**
	 * Used to kill the running executable
	 */
	public void kill()
	{
		proc.destroy();
		errorGobbler.done();
		outputGobbler.done();
	}

	/**
	 * Returns the output stream generated by executing the application.
	 * @return The output stream
	 */
	public String getOutputStream() {
		return outputGobbler.getResult();
	}

	/**
	 * Returns the error stream generated by executing the application.
	 * @return The error stream
	 */
	public String getErrorStream() {
		return errorGobbler.getResult();
	}

}