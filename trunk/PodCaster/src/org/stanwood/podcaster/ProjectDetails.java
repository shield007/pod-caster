package org.stanwood.podcaster;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.stanwood.podcaster.util.FileHelper;
import org.stanwood.podcaster.util.Version;

/**
 * Used to get the information about the project
 */
public class ProjectDetails {

	private static final String APP_URL = "http://http://code.google.com/p/pod-caster/"; //$NON-NLS-1$

	private String versionFileContents;

	/**
	 * The constructor
	 * @throws IOException Thrown if thier is a problem reading the version informatiom
	 */
	public ProjectDetails() throws IOException {
		versionFileContents = FileHelper.readFileContents(ProjectDetails.class.getResourceAsStream("VERSION")).trim(); //$NON-NLS-1$
	}

	/**
	 * Returns the version of the project
	 * @return The version of the project
	 */
	public Version getVersion() {
		return new Version(versionFileContents.substring(versionFileContents.lastIndexOf(' ')+1));
	}

	/**
	 * Returns the title of the project
	 * @return the title of the project
	 */
	public String getTitle()  {
		return versionFileContents.substring(0,versionFileContents.lastIndexOf(' '));
	}

	/**
	 * Used to get the copyright message
	 * @return the copyright message
	 */
	public String getCopyright() {
		StringBuilder result = new StringBuilder();
		result.append(Messages.getString("ProjectDetails.Copyright1")+FileHelper.LS); //$NON-NLS-1$
		result.append(Messages.getString("ProjectDetails.Copyright2")+FileHelper.LS); //$NON-NLS-1$
		result.append(Messages.getString("ProjectDetails.Copyright3")+FileHelper.LS); //$NON-NLS-1$
		result.append("There is NO WARRANTY"); //$NON-NLS-1$
		return result.toString();
	}

	/**
	 * Get a list of authors
	 * @return The authors
	 */
	public List<Author> getAuthors() {
		List<Author>authors = new ArrayList<Author>();
		authors.add(new Author("John-Paul Stanford","dev@stanwood.org.uk",Messages.getString("ProjectDetails.Title"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return authors;
	}

	/**
	 * Used to get the application URL
	 * @return The URL of the applications website
	 */
	public String getProjectURL() {
		return APP_URL;
	}

	/**
	 * Used to get the project description
	 * @return The project description
	 */
	public String getDescription() {
		return MessageFormat.format(Messages.getString("ProjectDetails.AppDesc"),getTitle()); //$NON-NLS-1$
	}
}
