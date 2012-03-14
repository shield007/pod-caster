package org.stanwood.podcaster.audio;

import java.io.File;

import org.junit.Test;
import org.stanwood.podcaster.util.FileHelper;

/**
 * Used to test the class {@link MP3File}
 */
@SuppressWarnings("nls")
public class TestMP3File {

	/**
	 * Used to test that metadata is written
	 * @throws Exception Thrown if their is a problem
	 */
	@Test
	public void testWriteMetaData() throws Exception {
		File tmpMp3 = FileHelper.createTempFile("test", ".mp3");
		try {
			if (tmpMp3.exists()) {
				FileHelper.delete(tmpMp3);
			}
			FileHelper.copy(TestMP3File.class.getResourceAsStream("test.mp3"),tmpMp3 );

			MP3File mp3file = new MP3File(tmpMp3);
			mp3file.setArtist("Test");
			mp3file.setCopyright("Blah");
			mp3file.setDescription("Blah Blah Blah");
			mp3file.setTitle("A title");
			mp3file.writeMetaData();
		}
		finally {
			if (tmpMp3.exists()) {
				FileHelper.delete(tmpMp3);
			}
		}
	}
}
