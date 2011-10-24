package org.stanwood.podcaster.audio;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.stanwood.podcaster.config.ConfigReader;
import org.stanwood.podcaster.logging.LogSetupHelper;
import org.stanwood.podcaster.util.FileHelper;

/**
 * Used to test the Audio conversion operations
 */
public class TestAudioConversion {

	/**
	 * Used to test that a wav file can be converted to a mp3 file
	 * @throws Exception Thrown if their is a problem
	 */
	@Test
	public void testWav2Mp3() throws Exception{
		LogSetupHelper.initLogingInternalConfigFile("debug.log4j.properties");

		File mp3File = File.createTempFile("converted", ".mp3");
		if (!mp3File.delete() && mp3File.exists()) {
			throw new IOException("Unable to delete file: " + mp3File);
		}
		mp3File.deleteOnExit();

		File wavFile = File.createTempFile("source", ".wav");
		if (!wavFile.delete() && wavFile.exists()) {
			throw new IOException("Unable to delete file: " + mp3File);
		}
		FileHelper.copy(TestAudioConversion.class.getResourceAsStream("test.wav"),wavFile);
		wavFile.deleteOnExit();

		ConfigReader config = new ConfigReader(null);
		AudioFileConverter.wav2Format(config,new WavFile(wavFile),Format.MP3, mp3File);

		Assert.assertTrue("Check file can be found",mp3File.exists());
		Assert.assertFalse("Check file can be found",wavFile.exists());
		Assert.assertTrue("Check file contains data",mp3File.length()>0);
	}

	/**
	 * Used to test the audio file length method
	 * @throws Exception Thrown if their is a problem
	 */
	@Test
	public void testLength() throws Exception{
		File mp3File = File.createTempFile("test", ".mp3");
		mp3File.deleteOnExit();

		FileHelper.copy(TestAudioConversion.class.getResourceAsStream("test.mp3"),mp3File);

		MP3File mp3 = new MP3File(mp3File);

		Assert.assertEquals(6,mp3.getLengthAsSeconds());
	}
}
