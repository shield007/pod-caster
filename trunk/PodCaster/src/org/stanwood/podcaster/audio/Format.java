package org.stanwood.podcaster.audio;

/**
 * Used to repesent the format of audio files
 */
public enum Format {

	WAV("WAV",".wav","audio/x-wav",WavFile.class),MP3("MP3",".mp3","audio/mpeg",MP3File.class),MP4("MP4",".mp4","audio/mpeg",MP4File.class),
	OGG("OGG",".ogg","audio/ogg",OggFile.class),FLAC("FLAC",".flac","audio/x-flac",FlacFile.class);

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

	public static Format fromName(String sformat) {
		for (Format f : Format.values()) {
			if (f.getName().toLowerCase().equals(sformat.toLowerCase())) {
				return f;
			}
		}
		return null;
	}
}
