package org.stanwood.podcaster.audio;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

/**
 * A base class for audio files
 */
public abstract class AbstractAudioFile implements IAudioFile {

	private File file;

	/**
	 * The constructor
	 * @param file The audio file
	 */
	public AbstractAudioFile(File file){
		this.file = file;
	}

	/**
	 * Used to get the raw java file object of the audio file
	 * @return The raw file object
	 */
	@Override
	public File getFile() {
		return file;
	}

	/**
	 * Used to set the title of the file. This will always throw  {@link UnsupportedOperationException}
	 * and should be reimplemented by extending classes.
	 * @param title the title of the file
	 */
	@Override
	public void setTitle(String title) {
		throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("AbstractAudioFile.UnableSetMetadata"),getFormat().getName())); //$NON-NLS-1$
	}

	/**
	 * Used to set the artwork of the file. This will always throw  {@link UnsupportedOperationException}
	 * and should be reimplemented by extending classes.
	 * @param artwork The artwork of the file
	 */
	@Override
	public void setArtwork(URL artwork) {
		throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("AbstractAudioFile.UnableSetMetadata"),getFormat().getName())); //$NON-NLS-1$
	}

	/**
	 * Used to set the copyright text of the file. This will always throw  {@link UnsupportedOperationException}
	 * and should be reimplemented by extending classes.
	 * @param copyright The copyright text of the file
	 */
	@Override
	public void setCopyright(String copyright) {
		throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("AbstractAudioFile.UnableSetMetadata"),getFormat().getName())); //$NON-NLS-1$
	}

	/**
	 * Used to set the artist of the file. This will always throw  {@link UnsupportedOperationException}
	 * and should be reimplemented by extending classes.
	 * @param artist The artist of the file
	 */
	@Override
	public void setArtist(String artist) {
		throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("AbstractAudioFile.UnableSetMetadata"),getFormat().getName())); //$NON-NLS-1$
	}

	/**
	 * Used to set the description of the file. This will always throw  {@link UnsupportedOperationException}
	 * and should be reimplemented by extending classes.
	 * @param description The description
	 */
	@Override
	public void setDescription(String description)
	{
		throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("AbstractAudioFile.UnableSetMetadata"),getFormat().getName())); //$NON-NLS-1$
	}

	/**
	 * Used to write the meta data to the file. This will always throw  {@link UnsupportedOperationException}
	 * and should be reimplemented by extending classes.
	 * @throws MetaDataException Thrown if their is a problem writing the meta data
	 */
	@Override
	public void writeMetaData() throws MetaDataException {
		throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("AbstractAudioFile.UnableSetMetadata"),getFormat().getName())); //$NON-NLS-1$
	}

	/**
	 * Gets the length in seconds of the audio
	 * @return The length of the audio
	 * @throws MetaDataException Thrown if their is a problem reading the audio
	 */
	@Override
	public int getLengthAsSeconds() throws MetaDataException {
		try {
			AudioFile mp4 = AudioFileIO.read(getFile());
			return mp4.getAudioHeader().getTrackLength();
		}
		catch (Exception e) {
			throw new MetaDataException(e.getMessage(),e);
		}
	}
}
