package org.stanwood.podcaster.audio;

import java.io.File;
import java.net.URL;

import org.stanwood.podcaster.cliutils.FFMPEGException;
import org.stanwood.podcaster.config.ConfigReader;

/**
 * This interface should be extended by classes used to represent audio files.
 * It is used to write meta data to these files.
 */
public interface IAudioFile {

	/**
	 * Used to get the raw java file object of the audio file
	 * @return The raw file object
	 */
	public File getFile();

	/**
	 * Used to get the format of the file
	 * @return The format of the file
	 */
	public Format getFormat();

	/**
	 * Used to set the title of the file
	 * @param title the title of the file
	 */
	public void setTitle(String title);


	/**
	 * Used to set the artwork of the file
	 * @param artwork The artwork of the file
	 */
	public void setArtwork(URL artwork);

	/**
	 * Used to set the copyright text of the file
	 * @param copyright The copyright text of the file
	 */
	public void setCopyright(String copyright);

	/**
	 * Used to set the artist of the file
	 * @param artist The artist of the file
	 */
	public void setArtist(String artist);

	/**
	 * Used to set the description of the file
	 * @param description The description
	 */
	public void setDescription(String description);

	/**
	 * Used to write the meta data to the file
	 * @throws MetaDataException Thrown if their is a problem writing the meta data
	 */
	public void writeMetaData() throws MetaDataException;

	/**
	 * Gets the length in seconds of the audio
	 * @return The length of the audio
	 * @throws MetaDataException Thrown if their is a problem reading the audio
	 */
	public int getLengthAsSeconds() throws MetaDataException;

	/**
	 * Used to convert a wave file to the a the correct format. The file
	 * is then stored as {@link #getFile()}, leaving the original wav intact.
	 * @param config The application configuration
	 * @param wav The wav file to convert
	 * @throws FFMPEGException Thrown if their is a problem converting the file
	 */
	public void fromWav(ConfigReader config,WavFile wav) throws FFMPEGException;
}
