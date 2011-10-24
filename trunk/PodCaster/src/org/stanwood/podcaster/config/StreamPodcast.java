package org.stanwood.podcaster.config;

import java.net.URL;

/**
 * Used to store info about a podcast that is been streamed from a URL
 */
public class StreamPodcast extends AbstractPodcast {

	private URL streamURL;
	private long captureTime;

	/**
	 * The constructor
	 * @param id The podcast ID within the configuration
	 */
	public StreamPodcast(String id) {
		super(id);
	}

	/**
	 * Used to set the URL the stream should be streamed from
	 * @param url the URL the stream should be streamed from
	 */
	public void setStreamURL(URL url) {
		this.streamURL = url;
	}

	/**
	 * Used to get the URL the stream should be streamed from
	 * @return the URL the stream should be streamed from
	 */
	public URL getStreamURL() {
		return streamURL;
	}

	/**
	 * Used to set the length of time the stream should be captured for
	 * @param time the length of time the stream should be captured for
	 */
	public void setCaptureTime(long time) {
		this.captureTime = time;
	}

	/**
	 * Used to get the length of time the stream should be captured for
	 * @return the length of time the stream should be captured for
	 */
	public long getCaptureTime() {
		return captureTime;
	}
}
