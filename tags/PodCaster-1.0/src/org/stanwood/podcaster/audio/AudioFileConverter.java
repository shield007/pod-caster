package org.stanwood.podcaster.audio;

import java.io.File;

import org.stanwood.podcaster.cliutils.FFMPEG;

/**
 * Used to convert audio files from one format to another
 */
public class AudioFileConverter {

	private static MP3File wav2mp3(WavFile wav,File targetFile) throws AudioConvertException {
		FFMPEG ffmpeg = new FFMPEG();
		ffmpeg.wav2mp3(wav.getFile(),targetFile);
		MP3File mp3File = new MP3File(targetFile);
		return mp3File;
	}

	private static MP4File wav2mp4(WavFile wav,File targetFile) throws AudioConvertException {
		FFMPEG ffmpeg = new FFMPEG();
		ffmpeg.wav2mp4(wav.getFile(),targetFile);
		MP4File mp4File = new MP4File(targetFile);
		return mp4File;
	}

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
		if (format == Format.MP3) {
			audioFile = AudioFileConverter.wav2mp3(wav,targetFile);
		}
		else if (format == Format.MP4){
			audioFile = AudioFileConverter.wav2mp4(wav,targetFile);
		}
		else {
			audioFile = wav;
		}

		if (!audioFile.getFile().equals(wav.getFile())) {								
			if (!wav.getFile().delete() && wav.getFile().exists()) {
				throw new AudioConvertException("Unable to delete old wav file " + wav.getFile());
			}
		}
		return audioFile;
	}
}
