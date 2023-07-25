package cn.sean.itchat4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sean.itchat4j.core.MsgCenter;
import cn.sean.itchat4j.service.IMsgHandlerFace;

import java.util.concurrent.BlockingQueue;

public class Wechat {
	private static final Logger LOG = LoggerFactory.getLogger(Wechat.class);
	private IMsgHandlerFace msgHandler;

	public Wechat(IMsgHandlerFace msgHandler, String qrPath, BlockingQueue<String> contactRefresh) {
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
		this.msgHandler = msgHandler;

		// 登陆
		LoginController login = new LoginController(contactRefresh);
		login.login(qrPath);
	}

	public void start() {
		LOG.info("+++++++++++++++++++开始消息处理+++++++++++++++++++++");
		new Thread(new Runnable() {
			@Override
			public void run() {
				MsgCenter.handleMsg(msgHandler);
			}
		}).start();
	}

}
