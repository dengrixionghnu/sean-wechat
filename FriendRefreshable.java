package cn.sean.wechat;

import cn.sean.itchat4j.core.Core;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class FriendRefreshable {

    private BlockingQueue<String> contactRefresh;

    public FriendRefreshable(BlockingQueue<String> contactRefresh) {
        this.contactRefresh = contactRefresh;
    }

    private Map<String, String> autoReplay = new HashMap<>();


    public boolean contain(String userName) {
        return autoReplay.containsKey(userName);
    }

    public void addAutoReplay(String userName, String message) {
        autoReplay.put(userName, message);
    }

    public void removeAutoReplay(String userName) {
        autoReplay.remove(userName);
    }

    public String getAutoReplay(String userName) {
        return autoReplay.get(userName);
    }


    public List<String> getNickList() {
        List<String> result = new ArrayList<>();
        List<JSONObject> contacts = Core.getInstance().getContactList();
        for (JSONObject contact : contacts) {
            result.add(contact.getString("NickName"));
        }
        return result;
    }

    public String getNick(String id,int times) {
        List<JSONObject> contacts = Core.getInstance().getContactList();
        for (JSONObject contact : contacts) {
            if (contact.getString("UserName").equals(id)) {
                return contact.getString("NickName");
            }
        }
        if (times > 0) {
            try {
                contactRefresh.put("refresh");
            } catch (Exception e) {
            }
            try {
                Thread.sleep(1000 * 3);
            } catch (Exception e) {
            }
            return getNick(id, times - 1);
        }
        return null;
    }

    public String getUserId(String name,int times) {
        List<JSONObject> contacts = Core.getInstance().getContactList();
        for (JSONObject contact : contacts) {
            if (contact.getString("NickName").equals(name)) {
                return contact.getString("UserName");
            }
        }
        if (times > 0) {
            try {
                contactRefresh.put("refresh");
            } catch (Exception e) {
            }
            try {
                Thread.sleep(1000 * 3);
            } catch (Exception e) {
            }
            return getUserId(name, times - 1);
        }
        return null;
    }

}
