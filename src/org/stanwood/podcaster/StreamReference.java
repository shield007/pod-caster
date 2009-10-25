package org.stanwood.podcaster;

/**
 * Used to store stream information
 */
public class StreamReference {

	private String url;
	private boolean playlist;	
	
	/**
	 * Used to create a instance of the class
	 * @param url The stream url
	 * @param playlist True if this is a playlist url
	 */
	public StreamReference(String url, boolean playlist) {
		super();
		this.url = url;
		this.playlist = playlist;
	}
	
	/**
	 * Used to get the url
	 * @return The url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Used to findout if this is a playlist
	 * @return True if this is a playlist, otherwise false
	 */
	public boolean isPlaylist() {
		return playlist;
	}
	
	
	
}
