package com.rubensworks.minerva.sdk;

public interface ExecutionDataHolder {
	public void onError(Exception e);
	public void onComplete(DataHolder data);
}
