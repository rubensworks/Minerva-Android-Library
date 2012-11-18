package com.rubensworks.minerva.sdk.fetch;

import com.rubensworks.minerva.sdk.DataHolder;

public class Announcement {
	private String id;
	private String title;
	private boolean mailed;
	private String visibility;
	private String created;
	private String content;
	//also get pageing
	public Announcement(DataHolder data) {
		this.id=data.getData()[0].getValue();
		this.title=data.getData()[1].getValue();
		this.mailed="1".equals(data.getData()[2].getValue());
		this.visibility=data.getData()[3].getValue();
		this.created=data.getData()[4].getValue();
		this.content=data.getData()[5].getValue();
	}
	
	public Announcement(String id, String title, boolean mailed, String visibility, String created, String content) {
		this.id=id;
		this.title=title;
		this.mailed=mailed;
		this.visibility=visibility;
		this.created=created;
		this.content=content;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public boolean isMailed() {
		return this.mailed;
	}
	
	public String getVisibility() {
		return this.visibility;
	}
	
	public String getCreated() {
		return this.created;
	}
	
	public String getContent() {
		return this.content;
	}
	
	@Override
	public String toString() {
		return "A:id:"+this.id+";title:"+this.title;
	}
}
