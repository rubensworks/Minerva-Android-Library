package com.rubensworks.minerva.sdk.fetch;

import com.rubensworks.minerva.sdk.DataHolder;
import com.rubensworks.minerva.sdk.Minerva;

public class FetchCourses extends Fetch{
	
	@Override
	public DataHolder fetch(Minerva minerva) {
		this.reset();
		minerva.getCourses(defaultExecutionDataHolder);
		return super.fetch(minerva);
	}

}
