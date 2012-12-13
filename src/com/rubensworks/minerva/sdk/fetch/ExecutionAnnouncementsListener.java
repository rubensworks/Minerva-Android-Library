package com.rubensworks.minerva.sdk.fetch;

import java.util.List;

import com.rubensworks.minerva.sdk.data.Announcement;



public interface ExecutionAnnouncementsListener {
	public void onError();
	public void onComplete(List<Announcement> announcements);
}
