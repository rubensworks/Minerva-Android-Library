package com.rubensworks.minerva.sdk.fetch;

import com.rubensworks.minerva.sdk.data.Tools;

public interface ExecutionToolsListener {
	public void onError();
	public void onComplete(Tools tools);
}
