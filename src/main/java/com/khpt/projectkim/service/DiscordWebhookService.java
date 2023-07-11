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

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private final Queue<DiscordWebhook.EmbedObject> embedQueue = new LinkedList<>();
    private final Queue<String> userQueue = new LinkedList<>();


    public void queueLoginLog(String userId, String userName) {
        if (userQueue.contains(userId)) {
            return;
        }

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setTitle("침입 경보")
                .setDescription("")
                .setColor(Color.LIGHT_GRAY)
                .addField("id", userId, true)
                .addField("name", userName, true)
                .setFooter(sdf.format(new Date()), "");

        embedQueue.add(embed);
        userQueue.add(userId);
    }

    public void queueErrorLog(String userId, String message, Throwable e) {
        if (userQueue.contains(userId)) {
            return;
        }

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

        embedQueue.add(embed);
        userQueue.add(userId);
    }

    @Scheduled(fixedRate = 3000)
    public void sendNextWebhook() {
        List<DiscordWebhook.EmbedObject> embedList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            DiscordWebhook.EmbedObject nextEmbed = embedQueue.poll();
            userQueue.poll();
            if (nextEmbed != null) {
                embedList.add(nextEmbed);
            } else {
                break;
            }
        }

        if (!embedList.isEmpty()) {
            sendLoginLogWithDiscordWebhook(embedList);
        }
    }

    @Async("webhookTaskExecutor")
    public void sendLoginLogWithDiscordWebhook(List<DiscordWebhook.EmbedObject> embedList) {
        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
        for (DiscordWebhook.EmbedObject embed : embedList) {
            webhook.addEmbed(embed);
        }
        try {
            webhook.execute();
        } catch (IOException e) {
            log.error("Webhook: send fail");
            e.printStackTrace();
        }
    }

}
