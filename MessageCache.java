package cn.sean.wechat;

import java.util.*;

/**
 * @author: sean  * @Date 2023/7/24 10:41
 */
public class MessageCache {
    private Map<String, StringBuilder> messageMap = new HashMap<>();

    private Map<String, StringBuilder> autoSyncMessage = new HashMap<>();

    private Map<String,SyncToFileUtil> autoSyncMap = new HashMap<>();

    private List<String> autoSyncNames = Arrays.asList("唐智敏");


    public void addMessage(String userId, String message) {
        StringBuilder builder = messageMap.get(userId);
        if (Objects.isNull(builder)) {
            builder = new StringBuilder();
            messageMap.put(userId, builder);
            if(autoSyncNames.contains(userId)){
                StringBuilder autoMessageBuilder = null;
                if(!autoSyncMessage.containsKey(userId)){
                    autoMessageBuilder = new StringBuilder();
                    autoSyncMessage.put(userId,autoMessageBuilder);
                    autoMessageBuilder.append(message + "\n");
                    SyncToFileUtil sync = new SyncToFileUtil(autoMessageBuilder, "D:\\wechat\\message/" + userId + ".txt");
                    sync.startSync();
                    autoSyncMap.put(userId,sync);
                }
            }
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