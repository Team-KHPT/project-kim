package com.khpt.projectkim.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class DiscordWebhookService {

    @Value("${discord.webhook.root.url}")
    private String webhookRootUrl;
    @Value("${discord.webhook.login.url}")
    private String webhookLoginUrl;
    @Value("${discord.webhook.error.url}")
    private String webhookErrorUrl;
    @Value("${discord.webhook.chat.url}")
    private String webhookChatUrl;
    @Value("${discord.webhook.result.url}")
    private String webhookResultUrl;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private final Queue<DiscordWebhook.EmbedObject> loginLogQueue = new LinkedList<>();
    private final Queue<DiscordWebhook.EmbedObject> errorLogQueue = new LinkedList<>();
    private final Queue<DiscordWebhook.EmbedObject> chatLogQueue = new LinkedList<>();
    private final Queue<DiscordWebhook.EmbedObject> resultLogQueue = new LinkedList<>();
    private final Queue<DiscordWebhook.EmbedObject> allLogQueue = new LinkedList<>();


    public void queueLoginLog(String userId, String userName) {
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setTitle("침입 경보")
                .setDescription("")
                .setColor(Color.LIGHT_GRAY)
                .addField("id", userId, true)
                .addField("name", userName, true)
                .setFooter(sdf.format(new Date()), "");

        loginLogQueue.add(embed);
    }

    public void queueErrorLog(String userId, String message, Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        String error = e.toString();
        log.info("err: {}", e.toString());
        String trace = sw.toString();
        log.info("trace: {}", trace);

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setTitle("김비서가 왜 그럴까")
                .setDescription("")
                .setColor(Color.LIGHT_GRAY)
                .addField("id", userId, true)
                .addField("message", message, false)
                .addField("error", error, false)
                .setFooter(sdf.format(new Date()), "");

        errorLogQueue.add(embed);
    }

    public void queueStatusLog(String message) {
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setTitle("김비서 상태")
                .setColor(Color.LIGHT_GRAY)
                .addField("message", message, false)
                .setFooter(sdf.format(new Date()), "");

        errorLogQueue.add(embed);
    }

    public void queueChatLog(String userId, String userName, String message) {
        if (message.length() > 300) {
            message = message.substring(0, 300);
            message += "...";
        }

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setTitle("채팅 입력 발생")
                .setColor(Color.LIGHT_GRAY)
                .addField("id", userId, true)
                .addField("name", userName, true)
                .addField("message", message, false)
                .setFooter(sdf.format(new Date()), "");

        chatLogQueue.add(embed);
    }

    public void queueResultLog(String userId, String userName, String message) {
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setTitle("분석결과 출력")
                .setColor(Color.LIGHT_GRAY)
                .addField("id", userId, true)
                .addField("name", userName, true)
                .addField("message", message, false)
                .setFooter(sdf.format(new Date()), "");

        resultLogQueue.add(embed);
    }

    private void queueAllLog(DiscordWebhook.EmbedObject embed) {
        allLogQueue.add(embed);
    }

    @Scheduled(fixedRate = 5000)
    public void sendNextWebhook() {
        final int MAX_EMBED_CNT = 10;

        List<DiscordWebhook.EmbedObject> embedList1 = new ArrayList<>();
        for (int i = 0; i < MAX_EMBED_CNT; i++) {
            DiscordWebhook.EmbedObject nextEmbed = loginLogQueue.poll();
            if (nextEmbed != null) {
                embedList1.add(nextEmbed);
                queueAllLog(nextEmbed);
            } else {
                break;
            }
        }
        if (!embedList1.isEmpty()) {
            sendLogWithDiscordWebhook(webhookLoginUrl, embedList1);
        }

        List<DiscordWebhook.EmbedObject> embedList2 = new ArrayList<>();
        for (int i = 0; i < MAX_EMBED_CNT; i++) {
            DiscordWebhook.EmbedObject nextEmbed = errorLogQueue.poll();
            if (nextEmbed != null) {
                embedList2.add(nextEmbed);
                queueAllLog(nextEmbed);
            } else {
                break;
            }
        }
        if (!embedList2.isEmpty()) {
            sendLogWithDiscordWebhook(webhookErrorUrl, embedList2);
        }

        List<DiscordWebhook.EmbedObject> embedList3 = new ArrayList<>();
        for (int i = 0; i < MAX_EMBED_CNT; i++) {
            DiscordWebhook.EmbedObject nextEmbed = chatLogQueue.poll();
            if (nextEmbed != null) {
                embedList3.add(nextEmbed);
                queueAllLog(nextEmbed);
            } else {
                break;
            }
        }
        if (!embedList3.isEmpty()) {
            sendLogWithDiscordWebhook(webhookChatUrl, embedList3);
        }

        List<DiscordWebhook.EmbedObject> embedList4 = new ArrayList<>();
        for (int i = 0; i < MAX_EMBED_CNT; i++) {
            DiscordWebhook.EmbedObject nextEmbed = resultLogQueue.poll();
            if (nextEmbed != null) {
                embedList4.add(nextEmbed);
                queueAllLog(nextEmbed);
            } else {
                break;
            }
        }
        if (!embedList4.isEmpty()) {
            sendLogWithDiscordWebhook(webhookResultUrl, embedList4);
        }


        List<DiscordWebhook.EmbedObject> embedList5 = new ArrayList<>();
        for (int i = 0; i < MAX_EMBED_CNT; i++) {
            DiscordWebhook.EmbedObject nextEmbed = allLogQueue.poll();
            if (nextEmbed != null) {
                embedList5.add(nextEmbed);
            } else {
                break;
            }
        }
        if (!embedList5.isEmpty()) {
            sendLogWithDiscordWebhook(webhookRootUrl, embedList5);
        }
    }

    @Async("webhookTaskExecutor")
    public void sendLogWithDiscordWebhook(String webhookUrl, List<DiscordWebhook.EmbedObject> embedList) {
        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
        for (DiscordWebhook.EmbedObject embed : embedList) {
            webhook.addEmbed(embed);
        }
        try {
            webhook.execute();
        } catch (IOException e) {
            log.error("Webhook: send fail");
            queueStatusLog("Webhook: Error sending webhook");
            e.printStackTrace();
        }
    }

}
