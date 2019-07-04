package com.lvtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("event")
@EnableBinding
public class MessageController {

    @Autowired
    private BinderAwareChannelResolver resolver;


    @RequestMapping(path = "/{dest}", method = RequestMethod.POST, consumes = "*/*")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void handleRequest(@PathVariable("dest") String dest,@RequestBody String body, 
    		@RequestHeader(HttpHeaders.CONTENT_TYPE) Object contentType,@RequestHeader Map header) {
        sendMessage(body,dest, contentType,header);
    }

    private void sendMessage(Object body, String dest, Object contentType,Map header) {
        resolver.resolveDestination(dest).send(MessageBuilder.createMessage(body,new MessageHeaders(header)));
    }


    @RequestMapping("/home")
    @ResponseBody
    String home() {
        return "home";
    }

    @RequestMapping("/pub/{event}")
    @ResponseBody
    String pub(){
        return  "ok";
    }



}
