package org.stanwood.podcaster.audio;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.stanwood.podcaster.cliutils.FFMPEG;
import org.stanwood.podcaster.cliutils.FFMPEGException;
import org.stanwood.podcaster.config.ConfigReader;

/**
 * This class is used to write metadata to Flac format files
 */
public class FlacFile extends AbstractAudioFile {

	private final static Log log = LogFactory.getLog(FlacFile.class);

	private String title;
	private URL artworkURL;
	private String copyright;
	private String artist;
	private String description;

	/**
	 * Used to construct a {@link FlacFile} instance
	 * @param file The file object pointing to the actual file
	 */
	public FlacFile(File file) {
		super(file);
	}

	/**
	 * Used to get the format of the file.
	 * @return This will return {@link Format.FLAC}
	 */
	@Override
	public Format getFormat() {
		return Format.FLAC;
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
		log.info(MessageFormat.format(Messages.getString("FlacFile.UnableWriteMetadata"),getFile().getAbsolutePath())); //$NON-NLS-1$
		try {
			AudioFile flac = AudioFileIO.read(getFile());
			Tag tag = flac.getTag();

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

//			if (artworkURL!=null) {
//				DownloadedFile coverArt = FileHelper.downloadToTempFile(artworkURL);
//				RandomAccessFile imageFile = new RandomAccessFile(coverArt.getFile(), "r");
//				byte[] imagedata = new byte[(int) imageFile.length()];
//				if (imageFile.read(imagedata)!=imagedata.length) {
//					throw new MetaDataException("Unable to read cover art " + artworkURL.toExternalForm());
//				}
//				tag.addField(((FlacTag)tag).createArtworkField(imagedata));
//			}

			tag.setField(FieldKey.YEAR,String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
			flac.commit();
		}
		catch (Exception e) {
			throw new MetaDataException(e.getMessage(),e);
		}
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
	 * Used to set the artwork of the file
	 * @param artwork The artwork of the file
	 */
	@Override
	public void setArtwork(URL artwork) {
		this.artworkURL = artwork;
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
	 * Used to convert a wave file to a MP4 file. The file
	 * is then stored as {@link #getFile()}, leaving the original wav intact.
	 * @param wav The wav file to convert
	 * @throws FFMPEGException Thrown if their is a problem converting the file
	 */
	@Override
	public void fromWav(ConfigReader config,WavFile wav) throws FFMPEGException {
		FFMPEG ffmpeg = new FFMPEG(config);
		ffmpeg.wav2flac(wav.getFile(),getFile());
	}
}
