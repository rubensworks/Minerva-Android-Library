package com.rubensworks.minerva.sdk.fetch;

import java.io.Serializable;

import com.rubensworks.minerva.sdk.DataHolder;
import com.rubensworks.minerva.sdk.ExecutionDataHolder;
import com.rubensworks.minerva.sdk.Minerva;

public class Fetcher implements Serializable{
	volatile DataHolder coursesData=null;
	private Course[] courses=null;
	
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
}
