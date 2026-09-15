package com.springai.moreplatformandmodel;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@RestController
public class MorePlatformAndModelController {

    HashMap<String , ChatModel> platforms = new HashMap<>();

    public MorePlatformAndModelController(
            DashScopeChatModel dashScopeChatModel,
            DeepSeekChatModel deepSeekChatModel
    ){
        platforms.put("dashScope", dashScopeChatModel);
        platforms.put("deepSeek", deepSeekChatModel);
    }

    @RequestMapping(value = "chat", produces = "text/stream;charset=UTF-8")
    public Flux<String> chat(String message, MorePlatformAndModelOptions options){
        String platform = options.getPlatform();

        ChatModel chatModel = platforms.get(platform);

        ChatClient.Builder builder = ChatClient.builder(chatModel);

        ChatClient chatClient = builder.defaultOptions(
                ChatOptions.builder()
                        .temperature(options.getTemperature())
                        .model(options.getModel())
                        .build()
        ).build();

        Flux<String> content = chatClient.prompt().user(message).stream().content();

        return content;
    }
}
