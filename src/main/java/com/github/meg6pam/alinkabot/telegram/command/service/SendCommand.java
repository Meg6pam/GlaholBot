package com.github.meg6pam.alinkabot.telegram.command.service;

import com.github.meg6pam.alinkabot.enums.TaskStatus;
import com.github.meg6pam.alinkabot.model.Task;
import com.github.meg6pam.alinkabot.telegram.util.DatabaseManager;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class SendCommand extends ServiceCommand {

    public SendCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Task task = DatabaseManager.getLastTask(user.getId(), TaskStatus.READY);
        DatabaseManager.addTaskToAllUsers(task.getId());
        sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(), "Рассылка отправлена! Чтобы начать новую рассылку нажми /mailing");
    }
}
