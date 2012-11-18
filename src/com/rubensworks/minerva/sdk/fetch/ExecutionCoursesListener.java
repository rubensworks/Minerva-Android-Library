package com.rubensworks.minerva.sdk.fetch;

import com.rubensworks.minerva.sdk.data.Course;

public interface ExecutionCoursesListener {
	public void onError();
	public void onComplete(Course[] courses);
}
