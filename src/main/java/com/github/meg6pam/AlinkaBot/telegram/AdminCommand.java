package com.github.meg6pam.AlinkaBot.telegram;

import com.github.meg6pam.AlinkaBot.enums.TaskStatus;
import com.github.meg6pam.AlinkaBot.telegram.util.DatabaseManager;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;

public class AdminCommand {

    public String handleUpdate(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasPhoto()) {
                Long taskId;
                if ((taskId = DatabaseManager.getLastTaskId().orElse(-1L)) != -1L) {
                    // Array with photos
                    List<PhotoSize> photos = update.getMessage().getPhoto();
                    // Get largest photo's file_id
                    String f_id = photos.stream()
                            .max(Comparator.comparing(PhotoSize::getFileSize))
                            .orElseThrow().getFileId();
                    DatabaseManager.setTaskFileId(taskId, f_id);
                    DatabaseManager.setTaskStatus(taskId, TaskStatus.READY);
                    return "Добавил фотку в рассылку";
                } else {
                    return "Сначала создай новую рассылку";
                }
            }
            if (update.getMessage().hasText()) {
                Long taskId;
                if ((taskId = DatabaseManager.getLastTaskId().orElse(-1L)) != -1L) {
                    DatabaseManager.setTaskMessage(taskId, update.getMessage().getText());
                    DatabaseManager.setTaskStatus(taskId, TaskStatus.READY);
                    return "Добавил текст в рассылку";
                } else {
                    return "Сначала создай новую рассылку";
                }

            }
        }

        //TODO Логируем событие
        return "Запрос получен. Всё работает";
    }

}
