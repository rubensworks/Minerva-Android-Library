package com.rubensworks.minerva.sdk.fetch;

import java.io.Serializable;

public class Course implements Serializable{
	private String name;
	private String cid;
	
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
}
