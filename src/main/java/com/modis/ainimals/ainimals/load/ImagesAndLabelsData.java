package com.modis.ainimals.ainimals.load;

public class ImagesAndLabelsData {
	private long id;
	private String content;

	public ImagesAndLabelsData(long id, String content) {
		this.id = id;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
}
