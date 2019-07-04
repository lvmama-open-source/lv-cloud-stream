package com.lvtest.consumer;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;

@EnableBinding(Processor.class)
public class MessageConsumer {

    private final Log logger = LogFactory.getLog(getClass());

    @StreamListener(Processor.INPUT)
    public void recieve(String data){

        logger.info("Data received from recieve-1..." + data+" | "+Thread.currentThread().getId());
    }

    @StreamListener(Processor.INPUT1)
    @SendTo(Processor.OUTPUT)
    public Object recieve2(Message<?> message){

        logger.info("Data received from recieve-2..." + message.getPayload()+" | "+Thread.currentThread().getId());
        return message;
    }

    @StreamListener("errorChannel")
    public void error(Message<?> message) {
    	System.out.println("Handling ERROR: " + message);
    }

}
