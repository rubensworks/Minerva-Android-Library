package com.rubensworks.minerva.sdk.fetch;

import java.io.Serializable;

public class Course{
	private String name;
	private String cid;
	private Tools tools;
	private Announcement[] announcements;
	
	public Course(String cid, String name) {
		this.cid=cid;
		this.name=name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getCid() {
		return this.cid;
	}
	
	public Tools getTools() {
		return this.tools;
	}
	
	public Announcement[] getAnnouncements() {
		return this.announcements;
	}
	
	public void setTools(Tools tools) {
		System.out.println("SET:"+tools);
		this.tools=tools;
	}
	
	public void setAnnouncements(Announcement[] announcements) {
		System.out.println("SET:"+announcements);
		this.announcements=announcements;
	}
}
