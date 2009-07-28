package org.stanwood.podcaster.audio;

/**
 * Used to repesent the format of audio files
 */
public enum Format {

	WAV("WAV",".wav","audio/x-wav"),MP3("MP3",".mp3","audio/mpeg"),MP4("MP4",".mp4","audio/mpeg");

	private String name;
	private String extension;
	private String contentType;
	
	private Format(String name,String extension,String contentType) {
		this.name = name;
		this.extension = extension;
		this.contentType = contentType;
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

	public String getContentType() {
		return contentType;
	}
}
