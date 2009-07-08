package org.stanwood.podcaster.config;

import java.io.File;

public class Config {

	private static Config instance = null;

	private final static String DEFAULT_FFMPEG_PATH = "/usr/bin/ffmpeg";
	private final static String DEFAULT_MPLAYER_PATH = "/usr/bin/mplayer";

	private File ffmpegPath = new File(DEFAULT_FFMPEG_PATH);
	private File mplayerPath = new File(DEFAULT_MPLAYER_PATH);

	private Config() {

	}

	public static Config getInstance() {
		if (instance==null) {
			instance = new Config();
		}
		return instance;
	}

	public File getFfmpegPath() {
		return ffmpegPath;
	}

	public void setFfmpegPath(File ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}

	public File getMplayerPath() {
		return mplayerPath;
	}

	public void setMplayerPath(File mplayerPath) {
		this.mplayerPath = mplayerPath;
	}
}
