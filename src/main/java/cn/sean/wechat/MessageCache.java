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



    public void init(){
        for(String name:autoSyncNames){
            StringBuilder autoMessageBuilder = new StringBuilder();
            SyncToFileUtil sync = new SyncToFileUtil(autoMessageBuilder, "/Users/apple/resources/wechat/" + name + ".txt");
            sync.startSync();
            autoSyncMessage.put(name,autoMessageBuilder);
            autoSyncMap.put(name,sync);
        }
    }

    public void addMessage(String userId, String message) {
        StringBuilder builder = messageMap.get(userId);
        if (Objects.isNull(builder)) {
            builder = new StringBuilder();
            messageMap.put(userId, builder);
            if(autoSyncNames.contains(userId)){
                autoSyncMessage.get(userId).append(message + "\n");
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