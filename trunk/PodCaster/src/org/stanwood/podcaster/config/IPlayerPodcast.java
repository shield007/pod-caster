package org.stanwood.podcaster.config;

public class IPlayerPodcast extends AbstractPodcast {

	private String episodeId;
	private long captureTime;
	
	public IPlayerPodcast(String id) {
		super(id);
	}

	public String getEpisodeId() {
		return episodeId;
	}

	public void setEpisodeId(String episodeId) {
		this.episodeId = episodeId;
	}

	public void setCaptureTime(long time) {
		this.captureTime = time;
	}
	
	public long getCaptureTime() {
		return captureTime;
	}
	
}
