package com.lvtest.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/bus")
@EnableBinding
public class MessageController {

    @Autowired
    @Qualifier("sourceChannel")
    private MessageChannel localChannel;


    @Autowired
    private BinderAwareChannelResolver resolver;


    @RequestMapping(path = "/{dest}", method = RequestMethod.POST, consumes = "*/*")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void handleRequest(@PathVariable("dest") String dest,@RequestBody String body, @RequestHeader(HttpHeaders.CONTENT_TYPE) Object contentType,@RequestHeader Map header) {
        sendMessage(body,dest, contentType,header);
    }

    private void sendMessage(Object body, String dest, Object contentType,Map header) {
        resolver.resolveDestination(dest).send(MessageBuilder.createMessage(body,new MessageHeaders(header)));
    }


    @RequestMapping(path = "/dynamic", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void handleRequest(@RequestBody String body, @RequestHeader Map header, @RequestHeader(HttpHeaders.CONTENT_TYPE) Object contentType) {
        sendMessageDynamic(body, header);
    }

    private void sendMessageDynamic(Object body, Map header) {
        localChannel.send(MessageBuilder.createMessage(body,
                new MessageHeaders(header)));

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
