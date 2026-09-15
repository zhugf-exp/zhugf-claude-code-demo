package com.springai.chatclientdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;

@SpringBootTest
class ChatClientDemoApplicationTests {

    @Test
    public  void testQwen(@Autowired ChatClient.Builder chatClientBuild) {
        ChatClient chatClient = chatClientBuild.build();
        String content = chatClient.prompt().user("现在是北京时间几点").call().content();
        System.out.println(content);

    }


    @Test
    public  void testStreamQwen(@Autowired ChatClient.Builder chatClientBuild) {
        ChatClient chatClient = chatClientBuild.build();
        Flux<String> content = chatClient.prompt().user("你是千问哪个版本")
                .stream()
                .content();
        content.toIterable().forEach(s -> System.out.println(s));

    }
}
