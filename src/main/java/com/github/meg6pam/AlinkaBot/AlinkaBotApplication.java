package com.github.meg6pam.AlinkaBot;

import com.github.meg6pam.AlinkaBot.telegram.Bot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class AlinkaBotApplication {
    private final static String BOT_USRENAME = System.getenv("BOT_USRENAME");
    private final static String BOT_TOKEN = System.getenv("BOT_TOKEN");
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(BOT_USRENAME, BOT_TOKEN));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}