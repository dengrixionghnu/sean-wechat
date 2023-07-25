package cn.sean.wechat;

import cn.sean.itchat4j.core.Core;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendRefreshable {
    private Map<String,String> nickMap;
    private Map<String,String> userIdMap;


    private Map<String,String> autoReplay = new HashMap<>();


    public boolean contain(String userName){
        return autoReplay.containsKey(userName);
    }

    public void addAutoReplay(String userName,String message){
        autoReplay.put(userName,message);
    }

    public String getAutoReplay(String userName){
        return autoReplay.get(userName);
    }

    public void init(){
        refresh();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10*1000);
                    refresh();
                }catch (Exception e){

                }
            }
        });
    }

    public List<String> getNickList(){
        if(nickMap!=null){
            return new ArrayList<>(nickMap.keySet());
        }
        return new ArrayList<>();
    }

    public String getNick(String id){
        if(userIdMap==null){
            return null;
        }
        return userIdMap.get(id);
    }

    public String getUserId(String name){
        if(nickMap==null){
            return null;
        }
        return nickMap.get(name);
    }


    private void refresh(){
        nickMap = new HashMap<>();
        userIdMap = new HashMap<>();
        List<JSONObject> contacts = Core.getInstance().getContactList();
        for (JSONObject contact : contacts) {
            nickMap.put(contact.getString("NickName"), contact.getString("UserName"));
            userIdMap.put(contact.getString("UserName"), contact.getString("NickName"));
        }
    }
}
