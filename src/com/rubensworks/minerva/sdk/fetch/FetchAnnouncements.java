package com.rubensworks.minerva.sdk.fetch;

import com.rubensworks.minerva.sdk.DataHolder;
import com.rubensworks.minerva.sdk.Minerva;

public class FetchAnnouncements extends Fetch{
	private String cid;
	
	public FetchAnnouncements(String cid) {
		this.cid=cid;
	}

	@Override
	public DataHolder fetch(Minerva minerva) {
		minerva.getAnnouncements(defaultExecutionDataHolder,cid);
		return super.fetch(minerva);
	}
}
