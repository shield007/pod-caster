package org.stanwood.podcaster.cliutils;

import org.stanwood.podcaster.StreamReference;
import org.stanwood.podcaster.audio.IAudioFile;

public interface ICaptureStream {

	public IAudioFile captureLiveAudioStream(StreamReference stream,long time) throws  CatpureException;
}
