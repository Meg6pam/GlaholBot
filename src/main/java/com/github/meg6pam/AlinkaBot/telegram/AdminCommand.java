package com.github.meg6pam.AlinkaBot.telegram;

import com.github.meg6pam.AlinkaBot.telegram.util.DatabaseManager;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;

public class AdminCommand {
    public String handleUpdate(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasPhoto()) {
                Integer taskId;
                if ((taskId = DatabaseManager.getLastTask().orElse(-1)) != -1) {
                    // Array with photos
                    List<PhotoSize> photos = update.getMessage().getPhoto();
                    // Get largest photo's file_id
                    String f_id = photos.stream()
                            .max(Comparator.comparing(PhotoSize::getFileSize))
                            .orElseThrow().getFileId();
                    DatabaseManager.setTaskFileId(taskId, f_id);
                    return "Добавил фотку в рассылку";
                } else {
                    return "Сначала создай новую рассылку";
                }
            }
            if (update.getMessage().hasText()) {
                Integer taskId;
                if ((taskId = DatabaseManager.getLastTask().orElse(-1)) != -1) {
                    DatabaseManager.setTaskMessage(taskId, update.getMessage().getText());
                    return "Добавил текст в рассылку";
                } else {
                    return "Сначала создай новую рассылку";
                }

            }
        }

        //TODO Обрабатываем запрос
        //TODO Логируем событие
        return "Запрос получен. Всё работает";
    }
}
