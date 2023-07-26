package cn.sean.wechat;

import cn.sean.itchat4j.MessageTools;
import cn.sean.itchat4j.beans.BaseMsg;
import cn.sean.itchat4j.beans.RecommendInfo;
import cn.sean.itchat4j.service.IMsgHandlerFace;
import cn.sean.itchat4j.utils.enums.MsgTypeEnum;
import cn.sean.itchat4j.utils.tools.DownloadTools;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * @author: sean  * @Date 2023/7/24 10:39
 */
public class ReceivedMessageHandler implements IMsgHandlerFace {
    Logger LOG = Logger.getLogger(ReceivedMessageHandler.class);
    private MessageCache messageCache;
    private FriendRefreshable friendRefreshable;

    private BlockingQueue<String> requestFocus;

    private BlockingQueue<String> txtAreRefresh;

    public ReceivedMessageHandler(MessageCache messageCache, FriendRefreshable friendRefreshable,
                                  BlockingQueue<String> requestFocus,
                                  BlockingQueue<String> txtAreRefresh
    ) {
        this.friendRefreshable = friendRefreshable;
        this.messageCache = messageCache;
        this.requestFocus = requestFocus;
        this.txtAreRefresh = txtAreRefresh;
    }

    @Override
    public String textMsgHandle(BaseMsg msg) {
        String returnValue = null;
        if (!msg.isGroupMsg()) { // 群消息不处理
            String userId = msg.getFromUserName();
            String userName = friendRefreshable.getNick(userId,1);
            boolean isSelfSend = false;
            if (userName == null) {
                String sendId = msg.getToUserName();
                userName = friendRefreshable.getNick(sendId,1);
                isSelfSend=true;
            }
            if (userName == null) {
                return returnValue;
            }
            String text = (isSelfSend?"我":userName) + ":" + msg.getText();
            System.out.println("ReceivedMessageHandler:"+userName+":"+msg.getText());
            messageCache.addMessage(userName, text);
            if (friendRefreshable.contain(userName)) {
                String autoMessage = friendRefreshable.getAutoReplay(userName);
                messageCache.addMessage(userName, "我:" + autoMessage);
                returnValue = autoMessage;
            }
            try{
                txtAreRefresh.put("refersh");
            }catch (Exception e){

            }

            requestFocus();
        }
        return returnValue;
    }

    private void requestFocus(){
        try {
            requestFocus.put("focus");
        } catch (Exception e) {
        }
    }

    @Override
    public String picMsgHandle(BaseMsg msg) {
        String returnValue = null;
        if (!msg.isGroupMsg()) { // 群消息不处理
            String userId = msg.getFromUserName();
            String userName = friendRefreshable.getNick(userId,1);
            boolean isSelfSend = false;
            if (userName == null) {
                String sendId = msg.getToUserName();
                userName = friendRefreshable.getNick(sendId,1);
                isSelfSend=true;
            }
            if (userName == null) {
                return returnValue;
            }
            String text = (isSelfSend?"我":userName) + ":" + "发送了一张图片";
            System.out.println("ReceivedMessageHandler:"+userName+":"+msg.getText());
            messageCache.addMessage(userName, text);
            if (friendRefreshable.contain(userName)) {
                String autoMessage = friendRefreshable.getAutoReplay(userName);
                messageCache.addMessage(userName, "我:" + autoMessage);
                returnValue = autoMessage;
            }
            try{
                txtAreRefresh.put("refersh");
            }catch (Exception e){

            }

            requestFocus();
        }
        return returnValue;
//        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());// 这里使用收到图片的时间作为文件名
//
//        String picPath = "D://itchat4j/pic" + File.separator + fileName + ".jpg"; // 调用此方法来保存图片
//
//        DownloadTools.getDownloadFn(msg, MsgTypeEnum.PIC.getType(), picPath); // 保存图片的路径
    }

    @Override
    public String voiceMsgHandle(BaseMsg msg) {
        String returnValue = null;
        if (!msg.isGroupMsg()) { // 群消息不处理
            String userId = msg.getFromUserName();
            String userName = friendRefreshable.getNick(userId,1);
            boolean isSelfSend = false;
            if (userName == null) {
                String sendId = msg.getToUserName();
                userName = friendRefreshable.getNick(sendId,1);
                isSelfSend=true;
            }
            if (userName == null) {
                return returnValue;
            }
            String text = (isSelfSend?"我":userName) + ":" + "发送了一段语音";
            System.out.println("ReceivedMessageHandler:"+userName+":"+msg.getText());
            messageCache.addMessage(userName, text);
            if (friendRefreshable.contain(userName)) {
                String autoMessage = friendRefreshable.getAutoReplay(userName);
                messageCache.addMessage(userName, "我:" + autoMessage);
                returnValue = autoMessage;
            }
            try{
                txtAreRefresh.put("refersh");
            }catch (Exception e){

            }

            requestFocus();
        }
        return returnValue;
//        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
//        String voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3";
//        DownloadTools.getDownloadFn(msg, MsgTypeEnum.VOICE.getType(), voicePath);
    }

    @Override
    public String viedoMsgHandle(BaseMsg msg) {
        String returnValue = null;
        if (!msg.isGroupMsg()) { // 群消息不处理
            String userId = msg.getFromUserName();
            String userName = friendRefreshable.getNick(userId,1);
            boolean isSelfSend = false;
            if (userName == null) {
                String sendId = msg.getToUserName();
                userName = friendRefreshable.getNick(sendId,1);
                isSelfSend=true;
            }
            if (userName == null) {
                return returnValue;
            }
            String text = (isSelfSend?"我":userName) + ":" + "发送了一段视频";
            System.out.println("ReceivedMessageHandler:"+userName+":"+msg.getText());
            messageCache.addMessage(userName, text);
            if (friendRefreshable.contain(userName)) {
                String autoMessage = friendRefreshable.getAutoReplay(userName);
                messageCache.addMessage(userName, "我:" + autoMessage);
                returnValue = autoMessage;
            }
            try{
                txtAreRefresh.put("refersh");
            }catch (Exception e){

            }

            requestFocus();
        }
        return returnValue;
//        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
//        String viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4";
//        DownloadTools.getDownloadFn(msg, MsgTypeEnum.VIEDO.getType(), viedoPath);
    }

    @Override
    public String nameCardMsgHandle(BaseMsg msg) {
        return  null;
    }

    @Override
    public void sysMsgHandle(BaseMsg msg) { // 收到系统消息
        String text = msg.getContent();
        LOG.info(text);
    }

    @Override
    public String verifyAddFriendMsgHandle(BaseMsg msg) {
        MessageTools.addFriend(msg, true); // 同意好友请求，false为不接受好友请求
        RecommendInfo recommendInfo = msg.getRecommendInfo();
        String nickName = recommendInfo.getNickName();
        String province = recommendInfo.getProvince();
        String city = recommendInfo.getCity();
        String text = "你好，来自" + province + city + "的" + nickName + "， 欢迎添加我为好友！";
        return text;
    }

    @Override
    public String mediaMsgHandle(BaseMsg msg) {
//        String fileName = msg.getFileName();
//        String filePath = "D://itchat4j/file" + File.separator + fileName; // 这里是需要保存收到的文件路径，文件可以是任何格式如PDF，WORD，EXCEL等。
//        DownloadTools.getDownloadFn(msg, MsgTypeEnum.MEDIA.getType(), filePath);
//        return "文件" + fileName + "保存成功";保存成功
        return null;
    }
}