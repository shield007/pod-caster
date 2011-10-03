package org.stanwood.podcaster.capture;

import org.stanwood.podcaster.audio.IAudioFile;
import org.stanwood.podcaster.cliutils.GetIPlayer;
import org.stanwood.podcaster.config.AbstractPodcast;
import org.stanwood.podcaster.config.ConfigReader;
import org.stanwood.podcaster.config.IPlayerPodcast;

public class IPlayerCapture implements ICaptureStream {

	@Override
	public IAudioFile captureLiveAudioStream(ConfigReader config,AbstractPodcast pc) throws CatpureException {
		IPlayerPodcast podcast = (IPlayerPodcast)pc;	
		GetIPlayer downloader = new GetIPlayer (config);
		return downloader.captureLiveAudioStream(podcast.getEpisodeId(),podcast.getCaptureTime());	
	}

}
