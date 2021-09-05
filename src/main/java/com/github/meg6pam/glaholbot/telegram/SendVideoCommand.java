package com.github.meg6pam.glaholbot.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

public class SendVideoCommand {

    public String handleUpdate(Update update) {
        if (update.hasMessage()) {
            if (!update.getMessage().hasVideo()) {
                return ("Отправь мне видео!");
            }
            return "Начинаю выкладывать видео";
        }

        //TODO Логируем событие
        return "Неизвестный запрос";
    }
}
