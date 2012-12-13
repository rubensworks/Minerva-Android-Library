package com.rubensworks.minerva.sdk.fetch;

import com.rubensworks.minerva.sdk.DataHolder;
import com.rubensworks.minerva.sdk.Minerva;

public class FetchAnnouncements extends Fetch{
	private String cid;
	private int prev;
	private int amount;
	
	public FetchAnnouncements(String cid) {
		this.cid=cid;
	}
	
	public void setPrev(int prev) {
		this.prev=prev;
	}
	
	public void setAmount(int amount) {
		this.amount=amount;
	}

	@Override
	public DataHolder fetch(Minerva minerva) {
		this.reset();
		minerva.getAnnouncements(defaultExecutionDataHolder, cid, prev, amount);
		return super.fetch(minerva);
	}
}
