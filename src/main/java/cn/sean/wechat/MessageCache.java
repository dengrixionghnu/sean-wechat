package cn.sean.wechat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: sean  * @Date 2023/7/24 10:41
 */
public class MessageCache {
    private Map<String, StringBuilder> messageMap = new HashMap<>();

    public void addMessage(String userId, String message) {
        StringBuilder builder = messageMap.get(userId);
        if (Objects.isNull(builder)) {
            builder = new StringBuilder();
            messageMap.put(userId, builder);
        }
        builder.append(message + "\n");
    }

    public String getMessage(String userId) {
        StringBuilder builder = messageMap.get(userId);
        if (Objects.isNull(builder)) {
            return "";
        }
        return builder.toString();
    }
}