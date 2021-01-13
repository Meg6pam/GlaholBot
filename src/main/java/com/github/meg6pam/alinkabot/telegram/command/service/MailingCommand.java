package com.github.meg6pam.alinkabot.telegram.command.service;


import com.github.meg6pam.alinkabot.telegram.util.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class MailingCommand extends ServiceCommand {

    private final static Logger logger = LoggerFactory.getLogger(HelpCommand.class);

    public MailingCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if (DatabaseManager.getUncompletedTasks(user) != null) {
            sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                    "У тебя есть незавершённая рассылка! Введи /cancel чтобы удалить её, или /push чтобы отправить");
        } else {
            DatabaseManager.newTask("SIMPLE", user.getId());
            sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                    "Я создал пустую рассылку для тебя. Пришли мне текст и файлы, которые я должен разослать. Когда закончишь - введи /push");
        }
    }
}
