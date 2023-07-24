package cn.zhouyafeng.itchat4j;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.core.Core;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;

public class WeChatUI extends Application {
    private ListView<String> friendListView;
    private TextArea chatArea;
    private TextField messageField;
    private String selectUser;

    private TextField searchField; // 新增搜索文本框

    private FilteredList<String> filteredFriendList; // 新增过滤后的好友列表

    @Override
    public void start(Stage primaryStage) {
        MessageHolder messageHolder = new MessageHolder();
        FriendHolder friendHolder = new FriendHolder();
        new Wechat(new WechatMessageHandler(messageHolder,friendHolder), "/Users/apple/resources").start();
        friendHolder.init();
        List<String> nickNames = friendHolder.getNickList();

        Collections.sort(nickNames);         // 创建右侧聊天界面
        chatArea = new TextArea();
        chatArea.setEditable(false);
        messageField = new TextField();
        messageField.setPromptText("输入消息...");
        messageField.setOnAction(event -> {
            String userId = friendHolder.getUserId(selectUser);
            if (userId != null) {
                sendMessage(chatArea, messageHolder, userId);
            }
        });         // 创建左侧好友列表
        friendListView = new ListView<>();
        friendListView.setPrefWidth(200);
        friendListView.getItems().addAll(nickNames);
        friendListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // 选中好友后处理
            if (newValue != null) {
                selectUser = newValue;
                System.out.println("选中好友：" + newValue);
                String userId = friendHolder.getUserId(selectUser);
                if (userId != null) {
                    refresh(chatArea, messageHolder, selectUser);
                }
            }
        });

        friendListView.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    Font font = Font.font("Arial", FontWeight.BOLD, 14); // 指定字体样式
                    setFont(font);
                }
            }
        });
        // 创建搜索文本框
        searchField = new TextField();
        searchField.setPromptText("搜索好友...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterFriendList(newValue);
        });

        // 过滤后的好友列表
        filteredFriendList = new FilteredList<>(friendListView.getItems());
        // 绑定过滤后的列表
        friendListView.setItems(filteredFriendList);

        Button sendButton = new Button("发送");
        sendButton.setOnAction(event -> {
            String userId = friendHolder.getUserId(selectUser);
            if (userId != null) {
                sendMessage(chatArea, messageHolder, userId);
            }
        });

        Button autoReplayButton = new Button("自动");
        autoReplayButton.setOnAction(event -> {
            String message = messageField.getText();
            if(message!=null&&message.length()>0){
                friendHolder.addAutoReplay(selectUser,message);
                messageField.setText("");
            }
        });

        refresh(chatArea, messageHolder);
        HBox inputBox = new HBox(messageField, sendButton,autoReplayButton);
        inputBox.setAlignment(Pos.CENTER);
        VBox rightLayout = new VBox(chatArea, inputBox);
        rightLayout.setSpacing(10);
        VBox.setVgrow(chatArea, Priority.ALWAYS);
        Font font = Font.font("Arial",12);
        chatArea.setFont(font);
        messageField.setFont(font);
        searchField.setFont(font);

        // 创建主界面布局
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setLeft(new VBox(searchField, friendListView));
        root.setCenter(rightLayout);
        // 创建场景并显示窗口
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("聊天");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void filterFriendList(String keyword) {
        filteredFriendList.setPredicate(friend -> {
            if (keyword == null || keyword.isEmpty()) {
                return true; // 没有关键字时显示所有好友
            }
            String lowercaseKeyword = keyword.toLowerCase();
            return friend.toLowerCase().contains(lowercaseKeyword);
        });
    }

    private void sendMessage(TextArea area, MessageHolder messageHolder, String userId) {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            MessageTools.sendMsgById(message, userId);
            String messageMe = "我:" + message;
            messageHolder.addMessage(selectUser, messageMe);
            refresh(area, messageHolder, selectUser);
            messageField.clear();
        }
    }


    private void refresh(TextArea area, MessageHolder messageHolder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if (selectUser != null) {
                            refresh(area,messageHolder,selectUser);
                        }
                        Thread.sleep(200);
                    } catch (Exception e) {
                    }

                }
            }
        }).start();
    }

    private void refresh(TextArea area, MessageHolder messageHolder, String userName) {
        String message = messageHolder.getMessage(userName);
        area.setText(message);
    }     // 其他方法和逻辑可以添加在这里

    public static void main(String[] args) {
        launch(args);
    }
}