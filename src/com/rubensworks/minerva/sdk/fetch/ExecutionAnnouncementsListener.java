package com.rubensworks.minerva.sdk.fetch;

import com.rubensworks.minerva.sdk.data.Announcement;


public interface ExecutionAnnouncementsListener {
	public void onError();
	public void onComplete(Announcement[] announcements);
}
