package org.stanwood.podcaster.cli;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.stanwood.podcaster.Author;
import org.stanwood.podcaster.ProjectDetails;
import org.stanwood.podcaster.config.ConfigException;
import org.stanwood.podcaster.config.ConfigReader;
import org.stanwood.podcaster.logging.LogConfig;
import org.stanwood.podcaster.logging.LogSetupHelper;

/**
 * This class should be extended by classes that have a main method used to lauch them
 * from the command line. It helps with adding command line parameters and add some default
 * CLI options.
 */
public abstract class AbstractLauncher extends BaseLauncher implements ICLICommand {

	private final static Log log = LogFactory.getLog(AbstractLauncher.class);

	private final static String LOG_CONFIG_OPTION = "l"; //$NON-NLS-1$
	private final static String CONFIG_FILE_OPTION = "c"; //$NON-NLS-1$
	private final static String VERSION_OPTION = "v"; //$NON-NLS-1$

	private File configFile = null;
//	private Controller controller;

	private ConfigReader config = null;

	/**
	 * Create a instance of the class
	 * @param name The name of the executable
	 * @param options The options that are to be added to the CLI
	 * @param stdout The standard output stream
	 * @param stderr The standard error stream
	 * @param exitHandler The exit handler
	 */
	public AbstractLauncher(String name,List<Option> options,IExitHandler exitHandler,PrintStream stdout,PrintStream stderr) {
		super(name,stdout,stderr,exitHandler);

		for (Option o : options) {
			addOption(o);
		}

		Option o = new Option(LOG_CONFIG_OPTION,"log_config",true,Messages.getString("AbstractLauncher.LOG_CONFIG_DESCRIPTION")); //$NON-NLS-1$ //$NON-NLS-2$
		o.setArgName("info|debug|file"); //$NON-NLS-1$
		addOption(o);

		o = new Option(CONFIG_FILE_OPTION,"config_file",true,Messages.getString("AbstractLauncher.CONFIG_FILE_DESC")); //$NON-NLS-1$ //$NON-NLS-2$
		o.setArgName("file"); //$NON-NLS-1$
		addOption(o);

		o = new Option(VERSION_OPTION,"version",false,Messages.getString("AbstractLauncher.VERSION_DESC")); //$NON-NLS-1$ //$NON-NLS-2$
		addOption(o);
	}

	/**
	 * This is called to validate the tools CLI options. When this is called,
	 * the default options added by {@link AbstractLauncher} will already have been
	 * validated sucesfully.
	 * @param cmd The command line options
	 * @return True, if the command line options verified successfully, otherwise false
	 */
	protected abstract boolean processOptions(String args[],CommandLine cmd);

	@Override
	protected boolean processOptionsInternal(String args[],CommandLine cmd) {
		String logConfig = null;
		if (cmd.hasOption(LOG_CONFIG_OPTION)) {
			logConfig = cmd.getOptionValue(LOG_CONFIG_OPTION,"INFO"); //$NON-NLS-1$
		}
		if (!initLogging(logConfig)) {
			return false;
		}
		if (cmd.hasOption(CONFIG_FILE_OPTION)) {
			configFile = new File(cmd.getOptionValue(CONFIG_FILE_OPTION));
		}
		if (cmd.hasOption(VERSION_OPTION)) {
			printVersion();
		}
		try {
			processConfig();
		} catch (FileNotFoundException e) {
			fatal(e);
			return false;
		} catch (ConfigException e) {
			fatal(e);
			return false;
		}

		return processOptions(args,cmd);
	}

	private void printVersion() {
		try {
			ProjectDetails details = new ProjectDetails();
			info(details.getVersion().toString()+" "+details.getTitle()); //$NON-NLS-1$
			info(details.getCopyright());
			info(""); //$NON-NLS-1$
			info(Messages.getString("AbstractLauncher.WRITTEN_BY")); //$NON-NLS-1$
			for (Author author : details.getAuthors()) {
				info (author.getName()+" <"+author.getEmail()+"> - " + author.getDescription()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (IOException e) {
			fatal(e);
		}
	}

	private boolean processConfig() throws FileNotFoundException, ConfigException {
		if (config==null) {
			if (configFile==null) {
				configFile = ConfigReader.getDefaultConfigFile();

				if (log.isDebugEnabled()) {
					log.debug("No config file give, so using default location: " + configFile.getAbsolutePath()); //$NON-NLS-1$
				}
			}

			if (!configFile.exists()) {
				fatal("Unable to find config file '" +configFile+"' so using defaults."); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
			else  {
				InputStream is = null;
				try {
					is = new FileInputStream(configFile);
					config = new ConfigReader(is);
					config.parse();
				}
				finally {
					if (is!=null) {
						try {
							is.close();
						} catch (IOException e) {
							warn("Unable to close stream"); //$NON-NLS-1$
						}
					}
				}
			}
		}
		return true;
	}

	private boolean initLogging(String logConfig) {
		if (logConfig!=null) {
			for (LogConfig lc : LogConfig.values()) {
				if (lc.name().equalsIgnoreCase(logConfig)) {
					LogSetupHelper.initLogingInternalConfigFile(lc.getFilename());
					return true;
				}
			}

			File logConfigFile = new File(logConfig);
			if (logConfigFile.exists()) {
				LogSetupHelper.initLogingFromConfigFile(logConfigFile);
			}
			else {
				fatal("Unable to find log configuraion file " + logConfigFile.getAbsolutePath()); //$NON-NLS-1$
				return false;
			}
		}
		else {
			LogSetupHelper.initLogingInternalConfigFile("info.log4j.properties"); //$NON-NLS-1$
		}

		return true;
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
			throw new ParseException("Unable to parse number from " + optionValue); //$NON-NLS-1$
		}
	}

	/**
	 * Get the application configuration
	 * @return the application configuration
	 */
	public ConfigReader getConfig() {
		return config;
	}
}
