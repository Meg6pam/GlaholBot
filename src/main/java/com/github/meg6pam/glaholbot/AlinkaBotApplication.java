package com.github.meg6pam.alinkabot;

import com.github.meg6pam.alinkabot.telegram.Bot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class AlinkaBotApplication {
    private final static String BOT_USERNAME = System.getenv("BOT_USERNAME");
    private final static String BOT_TOKEN = System.getenv("BOT_TOKEN");
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot(BOT_USERNAME, BOT_TOKEN);
            botsApi.registerBot(bot);
            bot.handleJobs();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}