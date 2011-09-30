package org.stanwood.podcaster.capture;

import org.stanwood.podcaster.StreamReference;
import org.stanwood.podcaster.audio.IAudioFile;
import org.stanwood.podcaster.config.AbstractPodcast;
import org.stanwood.podcaster.config.ConfigReader;

public interface ICaptureStream {

	public IAudioFile captureLiveAudioStream(ConfigReader configReader,AbstractPodcast podcast) throws  CatpureException;
}
