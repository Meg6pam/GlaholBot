package com.github.meg6pam.alinkabot.telegram.command.service;

import com.github.meg6pam.alinkabot.telegram.util.DatabaseManager;
import com.github.meg6pam.alinkabot.telegram.util.Utils;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Команда "Старт"
 */
public class StartCommand extends ServiceCommand {

    public StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);
        //обращаемся к методу суперкласса для отправки пользователю ответа
        DatabaseManager.addUser(user, chat.getId());
        sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Давайте начнём! Если Вам нужна помощь, нажмите /help");
    }
}