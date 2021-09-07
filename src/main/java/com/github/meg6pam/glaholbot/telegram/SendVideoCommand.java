package com.github.meg6pam.glaholbot.telegram;

import com.github.meg6pam.glaholbot.youtube.YoutubeAdapter;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoSnippet;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.Video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class SendVideoCommand {
    private static final YouTube youTube = YoutubeAdapter.getService();
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    public String handleUpdate(Update update) {
        if (update.hasMessage()) {
            if (!update.getMessage().hasVideo()) {
                return ("Отправь мне видео!");
            }
            final Video video = update.getMessage().getVideo();
            final User user = update.getChatMember().getFrom();
            final String username = user.getUserName() == null? user.getFirstName() + " " + user.getLastName():user.getUserName();
            update.getChatMember().getChat();
            final File file = saveVideo(video, username);
            try {
                sendVideo(file);
                file.delete();
            } catch (IOException e) {
                return "Ошибка при обработке видео";
            }
            return "Начинаю выкладывать видео";
        }

        //TODO Логируем событие
        return "Неизвестный запрос";
    }

    private File saveVideo(Video video, String username) {
        final String fileURL ="https://api.telegram.org/file/bot" + BOT_TOKEN + "/" + video.getFileId();
        final String tempFileName = username + "_" + LocalDateTime.now() + video.getFileName().split("\\.")[1];
        final String wgetFile = String.format("sh wget -0 /tmp/%s %s", tempFileName, fileURL);
        try {
            final Process exec = Runtime.getRuntime().exec(wgetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File("/tmp/" + tempFileName);
    }

    private void sendVideo(File file) throws IOException {
        final String filename = file.getName();
        final VideoSnippet snippet = new VideoSnippet();
        snippet.setCategoryId("22");
        snippet.setTitle(filename);
        final com.google.api.services.youtube.model.Video youtubeVideo = new com.google.api.services.youtube.model.Video();
        youtubeVideo.setSnippet(snippet);
        final YouTube.Videos.Insert insert = youTube.videos().insert("0", youtubeVideo);
        insert.executeAndDownloadTo(new FileOutputStream(file));

    }
}
