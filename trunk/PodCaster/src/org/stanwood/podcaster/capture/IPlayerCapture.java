package org.stanwood.podcaster.capture;

import java.io.IOException;
import java.text.MessageFormat;

import org.stanwood.podcaster.URLFetcher;
import org.stanwood.podcaster.audio.IAudioFile;
import org.stanwood.podcaster.cliutils.IPlayerDownloader;
import org.stanwood.podcaster.cliutils.MPlayer;
import org.stanwood.podcaster.config.AbstractPodcast;
import org.stanwood.podcaster.config.ConfigReader;
import org.stanwood.podcaster.config.IPlayerPodcast;
import org.stanwood.podcaster.config.StreamPodcast;

public class IPlayerCapture implements ICaptureStream {

	@Override
	public IAudioFile captureLiveAudioStream(ConfigReader config,AbstractPodcast pc) throws CatpureException {
		IPlayerPodcast podcast = (IPlayerPodcast)pc;	
		IPlayerDownloader downloader = new IPlayerDownloader (config);
		return downloader.captureLiveAudioStream(podcast.getEpisodeId());	
	}

}
