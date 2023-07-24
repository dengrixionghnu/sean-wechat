package cn.zhouyafeng.itchat4j;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.beans.RecommendInfo;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import cn.zhouyafeng.itchat4j.utils.tools.DownloadTools;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: sean  * @Date 2023/7/24 10:39
 */
public class WechatMessageHandler implements IMsgHandlerFace {
    Logger LOG = Logger.getLogger(SimpleDemo.class);
    private MessageHolder messageHolder;
    private Map<String, String> userIdNickNameMap;

    public WechatMessageHandler(MessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }

    private void setUserIdNickNameMap() {
        userIdNickNameMap = new HashMap<>();
        List<JSONObject> contacts = Core.getInstance().getContactList();
        for (JSONObject contact : contacts) {
            userIdNickNameMap.put(contact.getString("UserName"), contact.getString("NickName"));
        }
    }

    @Override
    public String textMsgHandle(BaseMsg msg) {         // String docFilePath = "D:/itchat4j/pic/1.jpg"; // 这里是需要发送的文件的路径
        if (!msg.isGroupMsg()) { // 群消息不处理
            if (Objects.isNull(userIdNickNameMap) || userIdNickNameMap.isEmpty()) {
                setUserIdNickNameMap();
            }
            String userId = msg.getFromUserName();
            String userName = userIdNickNameMap.get(userId);
            String text = userName + ":" + msg.getText();
            messageHolder.addMessage(userId, text);
        }
        return null;
    }

    @Override
    public String picMsgHandle(BaseMsg msg) {
        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());// 这里使用收到图片的时间作为文件名

        String picPath = "D://itchat4j/pic" + File.separator + fileName + ".jpg"; // 调用此方法来保存图片

        DownloadTools.getDownloadFn(msg, MsgTypeEnum.PIC.getType(), picPath); // 保存图片的路径
        return "图片保存成功";
    }

    @Override
    public String voiceMsgHandle(BaseMsg msg) {
        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3";
        DownloadTools.getDownloadFn(msg, MsgTypeEnum.VOICE.getType(), voicePath);
        return "声音保存成功";
    }

    @Override
    public String viedoMsgHandle(BaseMsg msg) {
        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4";
        DownloadTools.getDownloadFn(msg, MsgTypeEnum.VIEDO.getType(), viedoPath);
        return "视频保存成功";
    }

    @Override
    public String nameCardMsgHandle(BaseMsg msg) {
        return "收到名片消息";
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
        String fileName = msg.getFileName();
        String filePath = "D://itchat4j/file" + File.separator + fileName; // 这里是需要保存收到的文件路径，文件可以是任何格式如PDF，WORD，EXCEL等。
        DownloadTools.getDownloadFn(msg, MsgTypeEnum.MEDIA.getType(), filePath);
        return "文件" + fileName + "保存成功";
    }
}