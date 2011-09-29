package org.stanwood.podcaster.config;

public class IPlayerPodcast extends AbstractPodcast {

	private String episodeId;
	
	public IPlayerPodcast(String id) {
		super(id);
	}

	public String getEpisodeId() {
		return episodeId;
	}

	public void setEpisodeId(String episodeId) {
		this.episodeId = episodeId;
	}

	
}
