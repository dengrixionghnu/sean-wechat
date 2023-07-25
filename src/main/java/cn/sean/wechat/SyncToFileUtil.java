package cn.sean.wechat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SyncToFileUtil {
    private StringBuilder stringBuilder;
    private String filePath;
    private Timer timer;

    public SyncToFileUtil(StringBuilder stringBuilder, String filePath) {
        this.stringBuilder = stringBuilder;
        this.filePath = filePath;
        this.timer = new Timer();
    }

    public void startSync() {
        // 使用定时任务每分钟执行一次
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                syncToFile();
            }
        }, 0, 60 * 1000); // 60 * 1000 表示 1 分钟
    }

    private synchronized void syncToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(stringBuilder.toString());
            writer.flush();
            // 清空 StringBuilder
            stringBuilder.setLength(0);
            System.out.println("Synced to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopSync() {
        timer.cancel();
    }
}