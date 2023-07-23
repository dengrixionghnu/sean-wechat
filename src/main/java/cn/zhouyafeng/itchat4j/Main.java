package cn.zhouyafeng.itchat4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String,String> autoMap = new HashMap<>();
        autoMap.put("余凤","我收到你的消息了,这个是我通过ai答复你的");
        autoMap.put("唐智敏","宝贝，我收到你的消息了，等我一下,ai替我处理的哦");
        //new Wechat(new SimpleDemo(autoMap),"/Users/apple/resources").start();
    }
}
