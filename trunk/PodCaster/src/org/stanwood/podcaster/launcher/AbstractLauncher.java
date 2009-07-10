package org.stanwood.podcaster.launcher;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.stanwood.podcaster.config.Config;
import org.stanwood.podcaster.config.ConfigException;
import org.stanwood.podcaster.logging.LogSetupHelper;

/**
 * This class should be extended by classes that have a main method used to lauch them 
 * from the command line. It helps with adding command line parameters and add some default
 * CLI options.
 */
public abstract class AbstractLauncher {

	
	private IExitHandler exitHandler = null;
	private final static String HELP_OPTION = "h";
	private final static String LOG_CONFIG_OPTION = "l";
	private final static String CONFIG_FILE_OPTION = "c";

	private Options options;
	private String name;

	private File configFile = new File(File.separator+"etc"+File.separator+"podcaster-conf.xml");
	
	/**
	 * Create a instance of the class
	 * @param options The options that are to be added to the CLI
	 * @param exitHandler The exit handler used when exiting
	 */
	public AbstractLauncher(String name,List<Option> options,IExitHandler exitHandler) {
		this.options = new Options();
		this.exitHandler = exitHandler;
		this.name = name;
		this.options.addOption(new Option(HELP_OPTION,"help",false,"Show the help"));
		
		for (Option o : options) {
			this.options.addOption(o);
		}			
		this.options.addOption(new Option(LOG_CONFIG_OPTION,"log_config",true,"The log config mode [<INFO>|<DEBUG>|<log4j config file>]"));
		this.options.addOption(new Option(CONFIG_FILE_OPTION,"config_file",true,"The location of the config file. If not present, attempts to load it from /etc/mediafetcher-conf.xml"));
	}

	/**
	 * This should be called from the main method to launch the tool.
	 * @param args The args passed from the CLI
	 */
	public void launch(String args[]) {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption(HELP_OPTION)) {
				displayHelp();
				doExit(0);
				return;
			} else if (processOptionsInternal(cmd)) {
				if (run()) {
					doExit(0);
					return;
				}
				else {
					doExit(0);
					return;
				}
			} else {
				fatal("Invalid command line parameters");
				return;
			}
		} catch (ParseException e1) {
			fatal(e1.getMessage());
			return;
		}
	}

	/**
	 * This is executed to make the tool perform its function and should be extended.
	 * @return True if executed without problems, otherwise false
	 */
	protected abstract boolean run();

	/**
	 * This is called to validate the tools CLI options. When this is called, 
	 * the default options added by {@link AbstractLauncher} will already have been
	 * validated sucesfully.
	 * @param cmd The command line options
	 * @return True, if the command line options verified successfully, otherwise false
	 */
	protected abstract boolean processOptions(CommandLine cmd);

	private boolean processOptionsInternal(CommandLine cmd) {		
		String logConfig = null;
		if (cmd.hasOption(LOG_CONFIG_OPTION)) {
			logConfig = cmd.getOptionValue(LOG_CONFIG_OPTION);
		}				
		if (!initLogging(logConfig)) {
			return false;
		}
		if (cmd.hasOption(CONFIG_FILE_OPTION)) {
			configFile = new File(cmd.getOptionValue(CONFIG_FILE_OPTION));			
		}
		if (configFile!=null && configFile.exists()) {
			warn("Unable to find config file '" +configFile+"' so using defaults.");		
		}
		else {
			try {
				Config.getInstance().loadConfig(configFile);
			} catch (ConfigException e) {
				warn(e.getMessage());
			}			
		}
		
		return processOptions(cmd);
	}

	private boolean initLogging(String logConfig) {
		if (logConfig!=null) {
			if (logConfig.toLowerCase().equals("info")) {
				LogSetupHelper.initLogingInternalConfigFile("info.log4j.properties");
			}
			else if (logConfig.toLowerCase().equals("debug")) {
				LogSetupHelper.initLogingInternalConfigFile("debug.log4j.properties");
			}
			else {
				File logConfigFile = new File(logConfig);
				if (logConfigFile.exists()) {
					LogSetupHelper.initLogingFromConfigFile(logConfigFile);
				}
				else {
					fatal("Unable to find log configuraion file " + logConfigFile.getAbsolutePath());
					return false;
				}

			}
		}
		else {
			LogSetupHelper.initLogingInternalConfigFile("info.log4j.properties");
		}

		return true;
	}

	/**
	 * This will exit the application
	 * @param code The exit code
	 */
	public void doExit(int code) {
		exitHandler.exit(code);
	}

	/**
	 * Called to issue a warning message
	 * @param msg The message
	 */
	protected void warn(String msg) {
		System.out.println(msg);
	}

	/**
	 * Called to issue a fatal message and exit
	 * @param msg The message
	 */
	protected void fatal(String msg) {
		System.err.println(msg);
		displayHelp();
		doExit(1);
	}

	/**
	 * Called to issue a info message
	 * @param msg The message
	 */
	protected void info(String msg) {
		System.out.println(msg);
	}

	private void displayHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( name, options,true);
	}

	/** 
	 * Called to parse a {@link java.util.Long} option fropm the command line
	 * @param optionValue The command line parameter value
	 * @return The long
	 * @throws ParseException Thrown if it could not be parsed correctly
	 */
	protected long parseLongOption(String optionValue) throws ParseException{
		try {
			return Long.parseLong(optionValue);
		}
		catch (NumberFormatException e) {
			throw new ParseException("Unable to parse number from " + optionValue);
		}
	}
}
