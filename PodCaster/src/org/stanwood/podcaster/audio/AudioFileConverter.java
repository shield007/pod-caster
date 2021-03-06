package org.stanwood.podcaster.audio;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.stanwood.podcaster.config.ConfigReader;

/**
 * Used to convert audio files from one format to another
 */
public class AudioFileConverter {

	/**
	 * This will convert a {@link Format.WAV} file into a different format. The original will
	 * be deleted and the new one returned with the correct extension.
	 * @param config The configuration of the application
	 * @param wav The wav file to convert
	 * @param format The format to convert it to
	 * @param targetFile The filename of the file that we are converting to
	 * @return The newly converted wav file
	 * @throws AudioConvertException Thrown if their is a problem converting the file
	 */
	public static IAudioFile wav2Format(ConfigReader config,WavFile wav,Format format,File targetFile) throws AudioConvertException {
		IAudioFile audioFile = null;
		if (format == Format.WAV) {
			audioFile = wav;
		}
		else {
			try {
				Constructor<? extends IAudioFile> con = format.getAudioFileClass().getConstructor(File.class);
				audioFile = con.newInstance(targetFile);
			} catch (SecurityException e) {
				throw new AudioConvertException(Messages.getString("AudioFileConverter.UnableCreateFormatHandler"),e); //$NON-NLS-1$
			} catch (NoSuchMethodException e) {
				throw new AudioConvertException(Messages.getString("AudioFileConverter.UnableCreateFormatHandler"),e); //$NON-NLS-1$
			} catch (IllegalArgumentException e) {
				throw new AudioConvertException(Messages.getString("AudioFileConverter.UnableCreateFormatHandler"),e); //$NON-NLS-1$
			} catch (InstantiationException e) {
				throw new AudioConvertException(Messages.getString("AudioFileConverter.UnableCreateFormatHandler"),e); //$NON-NLS-1$
			} catch (IllegalAccessException e) {
				throw new AudioConvertException(Messages.getString("AudioFileConverter.UnableCreateFormatHandler"),e); //$NON-NLS-1$
			} catch (InvocationTargetException e) {
				throw new AudioConvertException(Messages.getString("AudioFileConverter.UnableCreateFormatHandler"),e); //$NON-NLS-1$
			}
			audioFile.fromWav(config,wav);
		}

		if (!audioFile.getFile().equals(wav.getFile())) {
			if (!wav.getFile().delete() && wav.getFile().exists()) {
				throw new AudioConvertException(MessageFormat.format(Messages.getString("AudioFileConverter.UnableDeleteWav"),wav.getFile())); //$NON-NLS-1$
			}
		}
		return audioFile;
	}

	/**
	 * This will convert a audio file into a different format. The original will
	 * be deleted and the new one returned with the correct extension.
	 * @param config The configuration of the application
	 * @param audioFile The audio file
	 * @param format The format to convert it to
	 * @param outputFile The filename of the file that we are converting to
	 * @return The newly converted wav file
	 * @throws AudioConvertException Thrown if their is a problem converting the file or their is a unsupported conversion type
	 */
	public static IAudioFile convertAudio(ConfigReader config,IAudioFile audioFile, Format format,File outputFile) throws AudioConvertException {
		if (audioFile.getFormat().equals(format)) {
			return audioFile;
		}
		else if (audioFile.getFormat().equals(Format.WAV)) {
			return wav2Format(config,(WavFile)audioFile, format, outputFile);
		}
		else {
			throw new AudioConvertException(MessageFormat.format(Messages.getString("AudioFileConverter.UnspportedConversion"),audioFile.getFormat(),format)); //$NON-NLS-1$
		}
	}
}
