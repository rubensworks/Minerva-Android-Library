package com.rubensworks.minerva.sdk;

import java.io.Serializable;

public interface ExecutionDataHolder extends Serializable{
	public void onError(Exception e);
	public void onComplete(DataHolder data);
}
