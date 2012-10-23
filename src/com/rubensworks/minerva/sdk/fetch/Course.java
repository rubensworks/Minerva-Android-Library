package com.rubensworks.minerva.sdk.fetch;

import java.io.Serializable;

public class Course{
	private String name;
	private String cid;
	private Tools tools;
	
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
	
	public void setTools(Tools tools) {
		this.tools=tools;
	}
}
