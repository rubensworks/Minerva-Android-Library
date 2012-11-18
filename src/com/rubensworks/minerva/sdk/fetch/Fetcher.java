package com.rubensworks.minerva.sdk.fetch;

import java.util.HashMap;
import java.util.Map;

import com.rubensworks.minerva.sdk.DataHolder;
import com.rubensworks.minerva.sdk.ExecutionDataHolder;
import com.rubensworks.minerva.sdk.Minerva;
import com.rubensworks.minerva.sdk.data.Announcement;
import com.rubensworks.minerva.sdk.data.Course;
import com.rubensworks.minerva.sdk.data.Tools;

public class Fetcher{
	private FetchCourses fetchCourses=new FetchCourses();
	private Map<String, FetchTools> fetchTools=new HashMap<String, FetchTools>();
	private Map<String, FetchAnnouncements> fetchAnnouncements=new HashMap<String, FetchAnnouncements>();
	
	private Course[] courses=null;
	
	/**
	 * Fetches the courses of this minerva user, also makes the subfetchers
	 * @param minerva
	 * @return
	 */
	public Course[] fetchCourses(Minerva minerva) {
		DataHolder coursesData=fetchCourses.fetch(minerva);
		if(coursesData==null) {
			minerva.setError();
			return null;
		}
		
		DataHolder[] data=coursesData.getData()[0].getData();
		courses=new Course[data.length];
		for(int i=0;i<data.length;i++) {
			String cid=data[i].getData()[0].getValue();
			String name=data[i].getData()[1].getValue();
			courses[i]=new Course(cid,name);
			fetchTools.put(cid, new FetchTools(cid));
			fetchAnnouncements.put(cid, new FetchAnnouncements(cid));
		}
		
		return courses;
	}
	
	/**
	 * Tries to get the courses, will be null if it hasn't been fetched first
	 * @return
	 */
	public Course[] getCourses() {
		return this.courses;
	}
	
	/**
	 * Fetches the tools of a course of a minerva user
	 * @param minerva
	 * @param cid
	 * @return
	 */
	public Tools fetchTools(Minerva minerva, String cid) {
		FetchTools fetcher=fetchTools.get(cid);
		if(fetcher==null){
			minerva.setError();
			return null;
		}
		
		DataHolder toolsData=fetcher.fetch(minerva);
		if(toolsData==null){
			minerva.setError();
			return null;
		}
		
		Tools tools=new Tools(toolsData);
		for(int i=0;i<courses.length;i++) {
			if(courses[i].getCid().equals(cid))
				courses[i].setTools(tools);
		}
		return tools;
	}
	
	/**
	 * Gets the tools of a course of a minerva user, will be null if this hasn't been fetched first
	 * @param cid
	 * @return
	 */
	public Tools getTools(String cid) {
		if(courses==null)
			return null;
		for(int i=0;i<courses.length;i++) {
			if(courses[i].getCid().equals(cid))
				return courses[i].getTools();
		}
		return null;
	}
	
	/**
	 * Fetches the announcement of a course
	 * @param minerva
	 * @param cid
	 * @return
	 */
	public Announcement[] fetchAnnouncements(Minerva minerva, String cid) {
		FetchAnnouncements fetcher=fetchAnnouncements.get(cid);
		if(fetcher==null){
			minerva.setError();
			return null;
		}
		
		DataHolder announcementsData=fetcher.fetch(minerva);
		if(announcementsData==null){
			minerva.setError();
			return null;
		}
		
		Announcement[] announcements=new Announcement[announcementsData.getData()[0].getData().length];
		for(int i=0;i<announcements.length;i++) {
			announcements[i]=new Announcement(announcementsData.getData()[0].getData()[i]);
		}
		
		for(int i=0;i<courses.length;i++) {
			if(courses[i].getCid().equals(cid))
				courses[i].setAnnouncements(announcements);
		}
		
		return announcements;
	}
	
	/**
	 * Gets the announcements of a course of a minerva user, will be null if this hasn't been fetched first
	 * @param cid
	 * @return
	 */
	public Announcement[] getAnnouncements(String cid) {
		if(courses==null)
			return null;
		for(int i=0;i<courses.length;i++) {
			if(courses[i].getCid().equals(cid))
				return courses[i].getAnnouncements();
		}
		return null;
	}
}
