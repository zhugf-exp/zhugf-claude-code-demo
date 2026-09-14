package com.springai.chatclientdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.ai.chat.client.ChatClient;

@SpringBootTest
class ChatClientDemoApplicationTests {

    @Test
    public  void testQwen(@Autowired ChatClient.Builder chatClientBuild) {
        ChatClient chatClient = chatClientBuild.build();
        String content = chatClient.prompt().user("现在是北京时间几点").call().content();
        System.out.println(content);

    }



}
