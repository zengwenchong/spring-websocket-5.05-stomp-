package com.dougong.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.dougong.socket.Greeting;
import com.dougong.socket.HelloMessage;
import com.dougong.vo.ChatObject;

@Controller
public class GreetingController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting demo(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + message.getName() + "!");
    }

    @MessageMapping("/hellos")
    @SendTo("/topics/greetings")
    public ChatObject greeting(ChatObject message) throws Exception {
        return message;
    }
}
