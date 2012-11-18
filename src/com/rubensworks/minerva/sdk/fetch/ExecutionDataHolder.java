package com.rubensworks.minerva.sdk.fetch;

import java.io.Serializable;

import com.rubensworks.minerva.sdk.DataHolder;

public interface ExecutionDataHolder extends Serializable{
	public void onError(Exception e);
	public void onComplete(DataHolder data);
}
