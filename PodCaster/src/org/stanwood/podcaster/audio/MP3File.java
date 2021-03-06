package org.stanwood.podcaster.audio;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.stanwood.podcaster.cliutils.FFMPEG;
import org.stanwood.podcaster.cliutils.FFMPEGException;
import org.stanwood.podcaster.config.ConfigReader;
import org.stanwood.podcaster.util.DownloadedFile;
import org.stanwood.podcaster.util.FileHelper;

/**
 * This class is used to write metadata to MP3 format files
 */
public class MP3File extends AbstractAudioFile {

	private final static Log log = LogFactory.getLog(MP3File.class);

	private String title;
	private URL artworkURL;
	private String copyright;
	private String artist;
	private String description;

	/**
	 * Used to construct a MP3File instance
	 * @param file The file object pointing to the actual file
	 */
	public MP3File(File file) {
		super(file);
	}

	/**
	 * Used to get the format of the file.
	 * @return This will return {@link Format.MP3}
	 */
	@Override
	public Format getFormat() {
		return Format.MP3;
	}

	/**
	 * Used to set the title of the file
	 * @param title the title of the file
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Used to set the copyright text of the file
	 * @param copyright The copyright text of the file
	 */
	@Override
	public void setArtwork(URL artworkURL) {
		this.artworkURL = artworkURL;
	}

	/**
	 * Used to set the copyright text of the file
	 * @param copyright The copyright text of the file
	 */
	@Override
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	/**
	 * Used to set the artist of the file
	 * @param artist The artist of the file
	 */
	@Override
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * Used to set the description of the file
	 * @param description The description
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Used to write the meta data to the file
	 * @throws MetaDataException Thrown if their is a problem writing the meta data
	 */
	@Override
	public void writeMetaData() throws MetaDataException {
		if (artist==null && copyright == null && title == null && description == null && artworkURL == null) {
			return;
		}
		log.info(MessageFormat.format(Messages.getString("MP3File.UnableWriteMetaData"),getFile().getAbsolutePath())); //$NON-NLS-1$
		try {
			org.jaudiotagger.audio.mp3.MP3File mp3 = (org.jaudiotagger.audio.mp3.MP3File)AudioFileIO.read(getFile());
			Tag tag = mp3.getTag();

			if (artist!=null) {
				tag.setField(FieldKey.ARTIST,artist);
			}
			if (copyright!=null) {
				tag.setField(FieldKey.ALBUM, copyright);
			}
			if (title!=null) {
				tag.setField(FieldKey.TITLE,title);
			}
			if (description!=null) {
				tag.setField(FieldKey.COMMENT,description);
			}

			if (artworkURL!=null) {
				ID3v24Tag v2tag = mp3.getID3v2TagAsv24();
				DownloadedFile coverArt = FileHelper.downloadToTempFile(artworkURL);
				RandomAccessFile imageFile = new RandomAccessFile(coverArt.getFile(), "r"); //$NON-NLS-1$
				byte[] imagedata = new byte[(int) imageFile.length()];
				if (imageFile.read(imagedata)!=imagedata.length) {
					throw new MetaDataException(MessageFormat.format(Messages.getString("MP3File.UnableReadCoverArt"),artworkURL.toExternalForm())); //$NON-NLS-1$
				}
				v2tag.addField(v2tag.createArtworkField(imagedata,coverArt.getContentType()));
			}

			tag.setField(FieldKey.YEAR,String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
			mp3.commit();

//			AudioFileIO.write(mp3);
		}
		catch (Exception e) {
			throw new MetaDataException(e.getMessage(),e);
		}
	}

	/**
	 * Used to convert a wave file to a MP3 file. The file
	 * is then stored as {@link #getFile()}, leaving the original wav intact.
	 * @param wav The wav file to convert
	 * @throws FFMPEGException Thrown if their is a problem converting the file
	 */
	@Override
	public void fromWav(ConfigReader config,WavFile wav) throws FFMPEGException {
		FFMPEG ffmpeg = new FFMPEG(config);
		ffmpeg.wav2mp3(wav.getFile(),getFile());
	}
}
