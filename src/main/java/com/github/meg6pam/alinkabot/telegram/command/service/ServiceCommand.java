package com.github.meg6pam.alinkabot.telegram.command.service;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class ServiceCommand extends BotCommand {
    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public ServiceCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    public void sendResponse(AbsSender absSender, Long chatId, String commandName, String username, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(message);
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            //TODO добавить логгер
            e.printStackTrace();
        }
    }

    public void sendResponse(AbsSender absSender, Long chatId, String commandName, String username, SendPhoto message) {
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            //TODO добавить логгер
            e.printStackTrace();
        }
    }

    public void sendResponse(AbsSender absSender, Long chatId, String commandName, String username, SendDocument message) {
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            //TODO добавить логгер
            e.printStackTrace();
        }
    }
}
