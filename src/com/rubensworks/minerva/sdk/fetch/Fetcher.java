package com.rubensworks.minerva.sdk.fetch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.rubensworks.minerva.sdk.DataHolder;
import com.rubensworks.minerva.sdk.Minerva;
import com.rubensworks.minerva.sdk.data.Announcement;
import com.rubensworks.minerva.sdk.data.Course;
import com.rubensworks.minerva.sdk.data.Tools;

public class Fetcher{
	private Executor asyncExec=Executors.newSingleThreadExecutor();
	
	private FetchCourses fetchCourses=new FetchCourses();
	private Map<String, FetchTools> fetchTools=new HashMap<String, FetchTools>();
	private Map<String, FetchAnnouncements> fetchAnnouncements=new HashMap<String, FetchAnnouncements>();
	
	private Course[] courses=null;
	private Map<String, Course> courseMap=null;
	
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
		if(data[0].getData().length==0) {
			minerva.setError();
			return null;
		}
		courses=new Course[data.length];
		for(int i=0;i<courses.length;i++) {
			String cid=data[i].getData("cid").getValue();
			String name=data[i].getData("name").getValue();
			courses[i]=new Course(cid,name);
			fetchTools.put(cid, new FetchTools(cid));
			fetchAnnouncements.put(cid, new FetchAnnouncements(cid));
		}
		makeCourseMap();
		
		return courses;
	}
	
	private void makeCourseMap() {
		courseMap=new HashMap<String, Course>();
		for(int i=0;i<courses.length;i++)
			courseMap.put(courses[i].getCid(),courses[i]);
	}
	
	/**
	 * Tries to get the coursesmap, will be null if it hasn't been fetched first
	 * @return
	 */
	public Map<String, Course> getCoursesMap() {
		return this.courseMap;
	}
	
	/**
	 * Tries to get the courses, will be null if it hasn't been fetched first
	 * @return
	 */
	public Course[] getCourses() {
		return this.courses;
	}
	
	/**
	 * Gets the courses async
	 * @return
	 */
	public void getCoursesAsync(final Minerva minerva, final ExecutionCoursesListener listener) {
		if(courses!=null) {
			listener.onComplete(courses);
		}
		else {
			asyncExec.execute(new Runnable() {

				@Override
				public void run() {
					Course[] fetchedCourses=fetchCourses(minerva);
					if(fetchedCourses==null) listener.onError();
					else listener.onComplete(fetchedCourses);
				}
				
			});
		}
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
		courseMap.get(cid).setTools(tools);
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
		courseMap.get(cid).getTools();
		return null;
	}
	
	/**
	 * Gets the tools async
	 * @return
	 */
	public void getToolsAsync(final Minerva minerva, final ExecutionToolsListener listener, final String cid) {
		Tools theseTools=courseMap.get(cid).getTools();
		if(theseTools!=null) {
			listener.onComplete(theseTools);
		}
		else {
			asyncExec.execute(new Runnable() {

				@Override
				public void run() {
					Tools fetchedTools=fetchTools(minerva,cid);
					if(fetchedTools==null) listener.onError();
					else listener.onComplete(fetchedTools);
				}
				
			});
		}
	}
	
	/**
	 * Fetches the announcement of a course
	 * @param minerva
	 * @param cid
	 * @return
	 */
	public List<Announcement> fetchAnnouncements(Minerva minerva, String cid, int amount) {
		int prev=courseMap.get(cid).getFetchedAnnouncements();
		FetchAnnouncements fetcher=fetchAnnouncements.get(cid);
		if(fetcher==null){
			minerva.setError();
			return null;
		}
		
		fetcher.setPrev(prev);
		fetcher.setAmount(amount);
		DataHolder announcementsData=fetcher.fetch(minerva);
		if(announcementsData==null){
			minerva.setError();
			return null;
		}
		
		List<Announcement> announcements=courseMap.get(cid).getAnnouncements();
		for(int i=0;i<announcementsData.getData()[0].getData().length;i++) {
			announcements.add(new Announcement(announcementsData.getData()[0].getData()[i]));
		}
		
		//courseMap.get(cid).setAnnouncements(announcements);
		courseMap.get(cid).setFetchedAnnouncements(prev+amount);
		courseMap.get(cid).setTotalAnnouncements(Integer.parseInt(announcementsData.getData("pageing").getData("posts").getValue()));
		
		return announcements;
	}
	
	/**
	 * Gets the announcements of a course of a minerva user, will be null if this hasn't been fetched first
	 * @param cid
	 * @return
	 */
	public List<Announcement> getAnnouncements(String cid) {
		if(courses==null)
			return null;
		return courseMap.get(cid).getAnnouncements();
	}
	
	/**
	 * Gets the announcements async
	 * @return
	 */
	public void getAnnouncementsAsync(final Minerva minerva, final ExecutionAnnouncementsListener listener, final String cid, final int amount) {
		List<Announcement> theseAnnouncements=courseMap.get(cid).getAnnouncements();
		if(!theseAnnouncements.isEmpty()) {
			listener.onComplete(theseAnnouncements);
		}
		else {
			asyncExec.execute(new Runnable() {

				@Override
				public void run() {
					List<Announcement> fetchedAnnouncements=fetchAnnouncements(minerva,cid,amount);
					if(fetchedAnnouncements==null) listener.onError();
					else listener.onComplete(fetchedAnnouncements);
				}
				
			});
		}
	}
	
	/**
	 * Resets the cached courses, you'll have to re-fetch yourself!
	 */
	public void resetCourses() {
		this.courses=null;
		this.courseMap=null;
	}
	
	/**
	 * Resets the cached tool contents, you'll have to re-fetch yourself!
	 */
	public void resetTools() {
		for(Course c : courses) {
			c.resetAnnouncements();
		}
	}
}
