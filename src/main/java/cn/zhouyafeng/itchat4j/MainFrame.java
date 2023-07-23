package cn.zhouyafeng.itchat4j;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.core.Core;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private JComboBox<String> contactComboBox;
    private JTextField messageTextField;

    private JTextField autoMessageTextField;

    private JButton autoButton;
    private JButton sendButton;
    private JTextArea messageArea;
    private JTextField searchTextField;


    public MainFrame(Map<String,String> contactMap,ContactContent content,
                     Map<String,String> userMap,Map<String,String> autoMessage) {
        // 设置窗口标题
        setTitle("发送消息");

        // 创建联系人下拉选择框
        contactComboBox = new JComboBox<>();
        contactMap.forEach((k,v)->{
            contactComboBox.addItem(k);
        });

        searchTextField = new JTextField(15);


        // 创建消息输入框
        messageTextField = new JTextField(50);

        autoMessageTextField = new JTextField(50);

        // 创建发送按钮
        sendButton = new JButton("发送");

        autoButton = new JButton("设置自动回复");
        // 添加发送按钮的点击事件监听器
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String contact = (String) contactComboBox.getSelectedItem();
                String message = messageTextField.getText();
                MessageTools.sendMsgById(message,contactMap.get(contact));


                messageArea.append("向 " + contact + " 发送消息：" + message + "\n");
                // 这里可以添加你发送消息的逻辑

                // 清空消息输入框
                messageTextField.setText("");
            }
        });

        autoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String contact = (String) contactComboBox.getSelectedItem();
                String message = autoMessageTextField.getText();
                String nick =   userMap.get(contactMap.get(contact));
                autoMessage.put(nick,message);
                autoMessageTextField.setText("");
            }
        });

        searchTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchTextField.getText();

                // 进行联系人搜索并更新下拉选择框中的选项
                updateContacts(searchTerm,contactMap);
            }
        });

        // 创建消息显示文本框
        messageArea = new JTextArea(10, 30);
        content.setMessageArea(messageArea);
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        // 创建主面板，并设置布局
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(contactComboBox, BorderLayout.WEST);
        northPanel.add(searchTextField, BorderLayout.EAST);


        JPanel centerPanel1 = new JPanel(new BorderLayout());
        centerPanel1.add(autoMessageTextField, BorderLayout.WEST);
        centerPanel1.add(autoButton, BorderLayout.EAST);

        JPanel centerPanel2 = new JPanel(new BorderLayout());
        centerPanel2.add(messageTextField, BorderLayout.WEST);
        centerPanel2.add(sendButton, BorderLayout.EAST);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(centerPanel1,BorderLayout.NORTH);
        centerPanel.add(centerPanel2,BorderLayout.SOUTH);


        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

// 将主面板添加到窗口的内容面板
        setContentPane(mainPanel);

// 设置窗口大小并可见
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void updateContacts(String searchTerm,Map<String,String> contactMap) {
        // 这里可以加入实际的联系人搜索逻辑
        // 更新下拉选择框中的选项
        contactComboBox.removeAllItems();

        contactMap.forEach((k, v) -> {
            if (k.contains(searchTerm)) {
                contactComboBox.addItem(k);
            }
            if (searchTerm == null || searchTerm.length() == 0) {
                contactComboBox.addItem(k);
            }

        });

    }


    public static void main(String[] args) {

        Map<String,String> autoMap = new HashMap<>();
        ContactContent content = new ContactContent();

        new Wechat(new SimpleDemo(autoMap,content),"/Users/apple/resources").start();

        List<JSONObject> contacts = Core.getInstance().getContactList();
        Map<String,String> contactNickNameMap = new HashMap<>();
        Map<String,String> userIdNameMap = new HashMap<>();


        for(JSONObject contact:contacts){
            contactNickNameMap.put(contact.getString("NickName"),
                    contact.getString("UserName"));
            userIdNameMap.put(contact.getString("UserName"),contact.getString("NickName"));
        }


        // 创建主界面实例
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame(contactNickNameMap,content,userIdNameMap,autoMap);
            }
        });
    }
}