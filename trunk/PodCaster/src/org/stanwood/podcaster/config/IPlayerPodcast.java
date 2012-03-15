package org.stanwood.podcaster.config;

import org.joda.time.Duration;

/**
 * This class used to to store information about a BBC IPlayer podcast
 */
public class IPlayerPodcast extends AbstractPodcast {

	private String episodeId;
	private Duration captureTime;

	/**
	 * The constructor
	 * @param id The podcast ID within the configuration
	 */
	public IPlayerPodcast(String id) {
		super(id);
	}

	/**
	 * Used to get the episode id that is to be stream
	 * @return the episode id that is to be stream
	 */
	public String getEpisodeId() {
		return episodeId;
	}

	/**
	 * Used to set the episode id that is to be stream
	 * @param episodeId the episode id that is to be stream
	 */
	public void setEpisodeId(String episodeId) {
		this.episodeId = episodeId;
	}

	/**
	 * Used to set the length of time the stream should be captured for
	 * @param time the length of time the stream should be captured for
	 */
	public void setCaptureTime(Duration time) {
		this.captureTime = time;
	}

	/**
	 * Used to get the length of time the stream should be captured for
	 * @return the length of time the stream should be captured for
	 */
	public Duration getCaptureTime() {
		return captureTime;
	}

}
