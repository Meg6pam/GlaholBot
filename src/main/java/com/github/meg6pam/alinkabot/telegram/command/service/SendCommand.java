package com.github.meg6pam.alinkabot.telegram.command.service;

import com.github.meg6pam.alinkabot.enums.TaskStatus;
import com.github.meg6pam.alinkabot.model.Task;
import com.github.meg6pam.alinkabot.telegram.util.DatabaseManager;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

public class SendCommand extends ServiceCommand {

    public SendCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Task task = DatabaseManager.getLastTask(user.getId(), TaskStatus.READY);
        List<Long> allChatIds = DatabaseManager.getAllChatIds();
        if (task.getFileId() != null) {
            SendPhoto msg = new SendPhoto();
            msg.setPhoto(new InputFile(task.getFileId()));
            allChatIds.forEach(chatId -> {
                msg.setChatId(chatId.toString());
                sendResponse(absSender, chatId, this.getCommandIdentifier(), user.getUserName(), msg);
            });
        }
        if (task.getMessage() != null && !"".equals(task.getMessage())) {
            allChatIds.forEach(chatId -> sendResponse(absSender, chatId, this.getCommandIdentifier(), user.getUserName(), task.getMessage()));
        }
        DatabaseManager.setTaskStatus(task.getId(), TaskStatus.SENT);
        sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(), "Рассылка отправлена! Чтобы начать новую рассылку нажми /mailing");
    }
}
