package com.lvtest.consumer.sinks;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface Sink {
    String INPUT="input";

    @Input(INPUT)
    SubscribableChannel input();

    String INPUT1="input1";

    @Input(INPUT1)
    SubscribableChannel input2();
}
