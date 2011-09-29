package org.stanwood.podcaster.config;

import java.net.URL;

public class StreamPodcast extends AbstractPodcast {

	private URL streamURL;
	private long captureTime;

	public StreamPodcast(String id) {
		super(id);
	}

	public void setCaptureTime(long time) {
		this.captureTime = time;
	}

	public void setStreamURL(URL url) {
		this.streamURL = url;
	}
	
	public URL getStreamURL() {
		return streamURL;
	}

	public long getCaptureTime() {
		return captureTime;
	}
}
