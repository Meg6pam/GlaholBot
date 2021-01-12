package com.github.meg6pam.AlinkaBot.telegram.command.service;


import com.github.meg6pam.AlinkaBot.telegram.util.DatabaseManager;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CancelCommand extends ServiceCommand {
    public CancelCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager.deleteLastDraftTask(user.getId());
        String response = "Я удалил последний шаблон рассылки. Чтобы создать новую набери /mailing";
        sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(), response);
    }
}
