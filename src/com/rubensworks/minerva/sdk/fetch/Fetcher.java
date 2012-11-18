package com.rubensworks.minerva.sdk.fetch;

import java.io.Serializable;

import com.rubensworks.minerva.sdk.DataHolder;
import com.rubensworks.minerva.sdk.ExecutionDataHolder;
import com.rubensworks.minerva.sdk.Minerva;

public class Fetcher{
	volatile DataHolder coursesData=null;
	volatile DataHolder toolsData=null;
	volatile DataHolder announcementsData=null;
	private Course[] courses=null;
	
	public boolean dataHolderSleeper(Minerva minerva, DataHolder data) {
		while(data==null) {
			if(minerva.isError())
				return false;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		
		if(data.getData()==null 
				|| data.getData().length==0 
				|| data.getData()[0].getData()==null 
				|| data.getData()[0].getData().length==0
				)
			return false;
		
		return true;
	}
	
	public Course[] fetchCourses(Minerva minerva) {
		minerva.getCourses(new ExecutionDataHolder() {

			@Override
			public void onError(Exception e) {
				System.out.println("error");
			}

			@Override
			public void onComplete(DataHolder data) {
				coursesData=data;
			}
			
		});
		
		while(coursesData==null) {
			if(minerva.isError())
				return null;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return null;
			}
		}
		
		if(coursesData.getData()==null || coursesData.getData().length==0 || coursesData.getData()[0].getData()==null || coursesData.getData()[0].getData().length==0)
			return null;
		DataHolder[] data=coursesData.getData()[0].getData();
		courses=new Course[data.length];
		for(int i=0;i<data.length;i++) {
			courses[i]=new Course(data[i].getData()[0].getValue(),data[i].getData()[1].getValue());
		}
		
		return courses;
	}
	
	public Course[] getCourses() {
		return this.courses;
	}
	
	public Tools fetchTools(Minerva minerva, String cid) {
		toolsData=null;
		minerva.getTools(new ExecutionDataHolder() {

			@Override
			public void onError(Exception e) {
				System.out.println("error");
			}

			@Override
			public void onComplete(DataHolder data) {
				toolsData=data;
			}
			
		},cid);
		
		while(toolsData==null) {
			if(minerva.isError())
				return null;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				minerva.setError();
				return null;
			}
		}
		System.out.println("CONTINUE");
		if(toolsData.getData()==null || toolsData.getData().length==0 || toolsData.getData()[0].getData()==null || toolsData.getData()[0].getData().length==0) {
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
	
	public Tools getTools(String cid) {
		if(courses==null)
			return null;
		for(int i=0;i<courses.length;i++) {
			if(courses[i].getCid().equals(cid))
				return courses[i].getTools();
		}
		return null;
	}
	
	public Announcement[] fetchAnnouncements(Minerva minerva, String cid) {
		announcementsData=null;
		minerva.getAnnouncements(new ExecutionDataHolder() {

			@Override
			public void onError(Exception e) {
				System.out.println("error");
			}

			@Override
			public void onComplete(DataHolder data) {
				announcementsData=data;
			}
			
		},cid);
		
		while(announcementsData==null) {
			if(minerva.isError())
				return null;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return null;
			}
		}
		
		if(announcementsData.getData()==null || announcementsData.getData().length==0 || announcementsData.getData()[0].getData()==null || announcementsData.getData()[0].getData().length==0)
			return null;
		
		//Tools tools=new Tools(toolsData);
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
}
