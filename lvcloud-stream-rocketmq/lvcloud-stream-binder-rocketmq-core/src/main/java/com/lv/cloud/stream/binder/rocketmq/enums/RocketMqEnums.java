package com.lv.cloud.stream.binder.rocketmq.enums;

public class RocketMqEnums {
	
	public static enum HANLDER_STRATEGY {
		/**
		 * 	AMQP协议,不做转发
		 */
		DEFAULT,
		/**
		 * 广播协议
		 */
	    BROADCAST,
	    /**
	     * Http协议
	     */
	    HTTP;
	}
}
