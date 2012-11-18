package com.rubensworks.minerva.sdk.data;

import com.rubensworks.minerva.sdk.DataHolder;

public class Tools {
	private String[] tools;
	private boolean[] enabled;
	
	public Tools(DataHolder data) {		
		DataHolder[] toolsData=data.getData()[0].getData();
		enabled=new boolean[toolsData.length];
		tools=new String[toolsData.length];
		for(int i=0;i<toolsData.length;i++) {
			tools[i]=toolsData[i].getData()[0].getValue();
			enabled[i]="1".equals(toolsData[i].getData()[1].getValue());
		}
		
	}
	
	public boolean isEnabled(String name) {
		for(int i=0;i<tools.length;i++) {
			if(tools[i].equals(name))
				return enabled[i];
		}
		return false;
	}
	
	@Override
	public String toString() {
		String s="";
		for(int i=0;i<tools.length;i++) {
			s+=tools[i]+":"+enabled[i]+";";
		}
		return s;
	}
}
