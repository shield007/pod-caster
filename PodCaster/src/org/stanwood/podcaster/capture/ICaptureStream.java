package org.stanwood.podcaster.capture;

import org.stanwood.podcaster.StreamReference;
import org.stanwood.podcaster.audio.IAudioFile;

public interface ICaptureStream {

	public IAudioFile captureLiveAudioStream(StreamReference stream,long time) throws  CatpureException;
}
