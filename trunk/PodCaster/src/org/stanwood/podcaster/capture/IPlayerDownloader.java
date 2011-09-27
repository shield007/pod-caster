package org.stanwood.podcaster.capture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.stanwood.podcaster.StreamReference;
import org.stanwood.podcaster.audio.IAudioFile;
import org.stanwood.podcaster.audio.WavFile;
import org.stanwood.podcaster.capture.stream.MPlayerException;
import org.stanwood.podcaster.config.Config;
import org.stanwood.podcaster.util.AbstractExecutable;

public class IPlayerDownloader extends AbstractExecutable implements ICaptureStream {

	private final static Log log = LogFactory.getLog(IPlayerDownloader.class);
	
	@Override
	public IAudioFile captureLiveAudioStream(StreamReference stream, long time) throws CatpureException {
		File wavOutputFile;
		try {
			wavOutputFile = File.createTempFile("captured", ".wav");
		} catch (IOException e) {
			throw new MPlayerException("Unable to create temp file",e);
		}
				
		log.info("Capturing audio from stream: " + stream.getUrl() + " to " + wavOutputFile.getAbsolutePath());
		List<String> args = new ArrayList<String>();
		args.add(Config.getInstance().getIPlayerDl().getAbsolutePath());		
		args.add("-f");
		args.add(wavOutputFile.getAbsolutePath());		
		args.add(stream.getUrl());
		try {
			execute(args);
		} catch (IOException e) {
			throw new CatpureException("Unable to execute iplayer-dl",e);
		} catch (InterruptedException e) {
			throw new CatpureException("Unable to execute iplayer-dl",e);
		}
		if (wavOutputFile.length()==0) {
			throw new MPlayerException("Unable to caputre audio, the caputred audio file is empty. Try increasing the caputre time.");
		}
		WavFile result = new WavFile(wavOutputFile);
		return result;		
	}

}
