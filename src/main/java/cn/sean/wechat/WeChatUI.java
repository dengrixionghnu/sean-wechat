package cn.sean.wechat;

import cn.sean.itchat4j.Wechat;
import cn.sean.itchat4j.api.MessageTools;
import javafx.application.Application;
import javafx.application.Platform;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WeChatUI extends Application {
    private ListView<String> friendListView;
    private TextArea chatArea;
    private TextField messageField;
    private String selectUser;
    private TextField searchField;
    private FilteredList<String> filteredFriendList;

    private List<String> topContact = Arrays.asList("唐智敏","余凤");

    private BlockingQueue<String> requestFocus = new ArrayBlockingQueue<>(100000);

    @Override
    public void start(Stage primaryStage) {
        MessageCache messageCache = new MessageCache();
        FriendRefreshable friendRefreshable = new FriendRefreshable();
        new Wechat(new ReceivedMessageHandler(messageCache, friendRefreshable,requestFocus), "/Users/apple/resources").start();
        friendRefreshable.init();

        List<String> nickNames = new ArrayList<>();
        nickNames.addAll(topContact);
        List<String> temp = friendRefreshable.getNickList();
        Collections.sort(temp);
        nickNames.addAll(temp);

        chatArea = new TextArea();
        chatArea.setEditable(false);
        messageField = new TextField();
        messageField.setPromptText("输入消息...");
        messageField.setOnAction(event -> {
            String userId = friendRefreshable.getUserId(selectUser);
            if (userId != null) {
                sendMessage(chatArea, messageCache, userId);
            }
        });
        friendListView = new ListView<>();
        friendListView.setPrefWidth(200);
        friendListView.getItems().addAll(nickNames);
        friendListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                selectUser = newValue;
                System.out.println("选中好友：" + newValue);
                String userId = friendRefreshable.getUserId(selectUser);
                if (userId != null) {
                    refresh(chatArea, messageCache, selectUser);
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
                    Font font = Font.font("Arial", FontWeight.BOLD, 14);
                    setFont(font);
                }
            }
        });

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
            String userId = friendRefreshable.getUserId(selectUser);
            if (userId != null) {
                sendMessage(chatArea, messageCache, userId);
            }
        });

        Button autoReplayButton = new Button("自动");
        autoReplayButton.setOnAction(event -> {
            String message = messageField.getText();
            if (message != null && message.length() > 0) {
                friendRefreshable.addAutoReplay(selectUser, message);
                messageField.setText("");
            }
        });

        refresh(chatArea, messageCache);
        handlerFocusRequest(primaryStage);
        HBox inputBox = new HBox(messageField, sendButton, autoReplayButton);
        inputBox.setAlignment(Pos.CENTER);
        VBox rightLayout = new VBox(chatArea, inputBox);
        rightLayout.setSpacing(10);
        VBox.setVgrow(chatArea, Priority.ALWAYS);
        Font font = Font.font("Arial", 12);
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

    private void handlerFocusRequest(Stage primaryStage){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        String focus = requestFocus.take();
                        Platform.runLater(() -> {
                            primaryStage.requestFocus();
                        });
                        requestFocus.clear();
                    }catch (Exception e){

                    }
                }
            }
        }).start();

    }

    private void filterFriendList(String keyword) {
        filteredFriendList.setPredicate(friend -> {
            if (keyword == null || keyword.isEmpty()) {
                return true;
            }
            String lowercaseKeyword = keyword.toLowerCase();
            return friend.toLowerCase().contains(lowercaseKeyword);
        });
    }

    private void sendMessage(TextArea area, MessageCache messageCache, String userId) {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            MessageTools.sendMsgById(message, userId);
            String messageMe = "我:" + message;
            messageCache.addMessage(selectUser, messageMe);
            refresh(area, messageCache, selectUser);
            messageField.clear();
        }
    }


    private void refresh(TextArea area, MessageCache messageCache) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (selectUser != null) {
                            refresh(area, messageCache, selectUser);
                        }
                        Thread.sleep(200);
                    } catch (Exception e) {
                    }

                }
            }
        }).start();
    }

    private void refresh(TextArea area, MessageCache messageCache, String userName) {
        String message = messageCache.getMessage(userName);
        area.setText(message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}