package com.rubensworks.minerva.sdk.fetch;

import com.rubensworks.minerva.sdk.DataHolder;
import com.rubensworks.minerva.sdk.Minerva;
import com.rubensworks.minerva.sdk.ExecutionDataHolder;

public class FetchCourses extends Fetch{
	
	@Override
	public DataHolder fetch(Minerva minerva) {
		minerva.getCourses(defaultExecutionDataHolder);
		return super.fetch(minerva);
	}

}
