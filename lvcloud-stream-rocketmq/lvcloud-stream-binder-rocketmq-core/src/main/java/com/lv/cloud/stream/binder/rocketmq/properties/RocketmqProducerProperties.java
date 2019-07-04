package com.lv.cloud.stream.binder.rocketmq.properties;

public class RocketmqProducerProperties {
	
	private boolean sync = true;

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}
}
