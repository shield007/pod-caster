package org.stanwood.podcaster.audio;

/**
 * Used to represent the format of audio files
 */
public enum Format {

	/** A .wav file format */
	WAV("WAV",".wav","audio/x-wav",WavFile.class),
	/** A .mp3 file format */
	MP3("MP3",".mp3","audio/mpeg",MP3File.class),
	/** A .mp4 file format */
	MP4("MP4",".mp4","audio/mpeg",MP4File.class),
	/** A .ogg file format */
	OGG("OGG",".ogg","audio/ogg",OggFile.class),
	/** A .flac file format */
	FLAC("FLAC",".flac","audio/x-flac",FlacFile.class);

	private String name;
	private String extension;
	private String contentType;
	private Class<? extends IAudioFile> audioFileClass;

	private Format(String name,String extension,String contentType,Class<? extends IAudioFile> audioFileClass) {
		this.name = name;
		this.extension = extension;
		this.contentType = contentType;
		this.audioFileClass = audioFileClass;
	}

	/**
	 * Used to get a name for the format that can be displayed
	 * @return The format name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Used to get a file extension including the . character that can be added
	 * to file names.
	 * @return The format file extension.
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * The Internet content type of the file
	 * @return Internet content type of the file
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * The audio file class that handles the format
	 * @return The audio file class
	 */
	public Class<? extends IAudioFile>getAudioFileClass() {
		return audioFileClass;
	}

	/**
	 * Used to get the format from a format name
	 * @param sformat The format name
	 * @return The format, or null if it can't be found
	 */
	public static Format fromName(String sformat) {
		for (Format f : Format.values()) {
			if (f.getName().toLowerCase().equals(sformat.toLowerCase())) {
				return f;
			}
		}
		return null;
	}
}
