package com.springai.quickstart;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QuickStartApplicationTests {

    @Test
    public  void testQwen(@Autowired DashScopeChatModel dashScopeChatModel) {

        String call = dashScopeChatModel.call("你好你是谁");
        System.out.println(call);

    }


    @Test
    public  void testImage(@Autowired DashScopeImageModel dashScopeImageModel) {
        DashScopeImageOptions buildOptions = DashScopeImageOptions.builder().withModel("wanx2.0-t2i-turbo").build();

        ImageResponse response = dashScopeImageModel.call(new ImagePrompt("生成一个小猫和人在床上嬉戏图片", buildOptions));
        String url = response.getResults().get(0).getOutput().getUrl();
        System.out.println(url);

    }

}
