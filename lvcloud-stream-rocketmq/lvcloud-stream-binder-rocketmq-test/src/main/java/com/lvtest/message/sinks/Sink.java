package com.lvtest.message.sinks;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by dengcheng on 2018/5/29.
 */
public interface Sink {
    String ORDER="ORDER";

    @Input(ORDER)
    SubscribableChannel input();
//
//    String ORDER1="ORDER1";
//
//    @Input(ORDER1)
//    SubscribableChannel input2();
}
