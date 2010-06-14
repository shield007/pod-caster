package org.stanwood.podcaster.audio;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Used to convert audio files from one format to another
 */
public class AudioFileConverter {
	
	/**
	 * This will convert a {@link Format.WAV} file into a different format. The original will
	 * be deleted and the new one returned with the correct extension.
	 * @param wav The wav file to convert
	 * @param format The format to convert it to
	 * @param targetFile The filename of the file that we are converting to
	 * @return The newly converted wav file
	 * @throws AudioConvertException Thrown if their is a problem converting the file
	 */
	public static IAudioFile wav2Format(WavFile wav,Format format,File targetFile) throws AudioConvertException {
		IAudioFile audioFile = null;
		if (format == Format.WAV) {
			audioFile = wav;
		}
		else {						
			try {
				Constructor<? extends IAudioFile> con = format.getAudioFileClass().getConstructor(File.class);
				audioFile = con.newInstance(targetFile);				
			} catch (SecurityException e) {
				throw new AudioConvertException("Unable to create format handler class",e);
			} catch (NoSuchMethodException e) {
				throw new AudioConvertException("Unable to create format handler class",e);
			} catch (IllegalArgumentException e) {
				throw new AudioConvertException("Unable to create format handler class",e);
			} catch (InstantiationException e) {
				throw new AudioConvertException("Unable to create format handler class",e);
			} catch (IllegalAccessException e) {
				throw new AudioConvertException("Unable to create format handler class",e);
			} catch (InvocationTargetException e) {
				throw new AudioConvertException("Unable to create format handler class",e);
			}
			audioFile.fromWav(wav);
		}

		if (!audioFile.getFile().equals(wav.getFile())) {								
			if (!wav.getFile().delete() && wav.getFile().exists()) {
				throw new AudioConvertException("Unable to delete old wav file " + wav.getFile());
			}
		}
		return audioFile;
	}
}