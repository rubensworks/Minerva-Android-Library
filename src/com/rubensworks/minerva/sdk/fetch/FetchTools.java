package com.rubensworks.minerva.sdk.fetch;

import com.rubensworks.minerva.sdk.DataHolder;
import com.rubensworks.minerva.sdk.Minerva;

public class FetchTools extends Fetch{
	private String cid;
	
	public FetchTools(String cid) {
		this.cid=cid;
	}

	@Override
	public DataHolder fetch(Minerva minerva) {
		minerva.getTools(defaultExecutionDataHolder,cid);
		return super.fetch(minerva);
	}
}
