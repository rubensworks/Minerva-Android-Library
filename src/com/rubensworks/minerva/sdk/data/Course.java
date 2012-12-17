package com.rubensworks.minerva.sdk.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable{
	private String name;
	private String cid;
	private Tools tools;
	private List<Announcement> announcements=new ArrayList<Announcement>();
	private int fetchedAnnouncements=0;
	private int totalAnnouncements=0;
	
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
	
	public void resetAnnouncements() {
		this.announcements=new ArrayList<Announcement>();
		this.setTotalAnnouncements(0);
		this.setFetchedAnnouncements(0);
	}
	
	public List<Announcement> getAnnouncements() {
		return this.announcements;
	}
	
	public void setTools(Tools tools) {
		this.tools=tools;
	}
	
	public void setAnnouncements(List<Announcement> announcements) {
		this.announcements=announcements;
	}
	
	public int getFetchedAnnouncements() {
		return this.fetchedAnnouncements;
	}
	
	public int getTotalAnnouncements() {
		return this.totalAnnouncements;
	}
	
	public void setFetchedAnnouncements(int fetchedAnnouncements) {
		this.fetchedAnnouncements=fetchedAnnouncements;
	}
	
	public void setTotalAnnouncements(int totalAnnouncements) {
		this.totalAnnouncements=totalAnnouncements;
	}
}
