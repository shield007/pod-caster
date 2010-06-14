package org.stanwood.podcaster.audio;

import java.io.File;

import org.stanwood.podcaster.cliutils.FFMPEGException;

/**
 * This class is used to represent WAV Files, as metadata can't be stored in these files,
 * the meta data methods will return a @{link {@link UnsupportedOperationException} 
 */
public class WavFile extends AbstractAudioFile {

	/**
	 * Used to construct a WavFile instance
	 * @param file The file object pointing to the actual file
	 */
	public WavFile(File file) {
		super(file);
	}

	/**
	 * Used to get the format of the file
	 * @return The format of the file
	 */
	@Override
	public Format getFormat() {
		return Format.WAV;
	}
	
	/**
	 * This does nothing as the file is all ready a wav
	 * @param wav the wav file
	 */
	@Override
	public void fromWav(WavFile wav)  {
	}

	
	
}
