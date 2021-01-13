package com.github.meg6pam.alinkabot.telegram.command.service;

import com.github.meg6pam.alinkabot.enums.TaskStatus;
import com.github.meg6pam.alinkabot.model.Task;
import com.github.meg6pam.alinkabot.telegram.util.DatabaseManager;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class PushCommand extends ServiceCommand {
    public PushCommand(String identifier, String description) {
        super(identifier, description);
    }


    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String response;
        if (DatabaseManager.getUserRole(user).equals("ADMIN")) {
            Task task = DatabaseManager.getLastTask(user.getId(), TaskStatus.READY);
            if (task.getId() == null) {
                response = "У тебя нет активных рассылок. Нажми /mailing для создания новой рассылки";
            } else {
                if (task.getMessage() == null && task.getFileId() == null) {
                    response = "Рассылка пустая. Я не могу её отправить. Добавь текст и содержимое";
                } else {
                    sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                            "Сейчас я пришлю тебе что получилось. Если тебе нравится - нажми /send, если нет - " +
                                    "просто пришли новые файлы, или медиаконтент и он заменит существующий. " +
                                    "Если тебе не нравится ничего - введи /delete и начнём с начала!");
                    if (task.getFileId() != null) {
                        DatabaseManager.setTaskStatus(task.getId(), TaskStatus.READY);
                        SendPhoto msg = new SendPhoto();
                        msg.setChatId(chat.getId().toString());
                        msg.setPhoto(new InputFile(task.getFileId()));
                        sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(), msg);
                    }
                    response = task.getMessage();
                }
            }
        } else {
            response = "Неверная команда. Нажми /help для помощи";
        }
        if (response != null && !"".equals(response)) {
            sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(), response);
        }
    }
}
