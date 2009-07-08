package org.stanwood.podcaster.audio;

/**
 * Used to repesent the format of audio files
 */
public enum Format {

	WAV("WAV",".wav"),MP3("MP3",".mp3"),MP4("MP4",".mp4");

	private String name;
	private String extension;
	
	private Format(String name,String extension) {
		this.name = name;
		this.extension = extension;
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

}
