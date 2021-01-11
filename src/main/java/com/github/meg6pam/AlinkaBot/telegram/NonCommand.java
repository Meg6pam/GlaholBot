package com.github.meg6pam.AlinkaBot.telegram;

public class NonCommand {

    public String execute(Long chatId,  String username, String request) {
        String response;
        //TODO Обрабатываем запрос
        //TODO Логируем событие
        return String.format("Запрос %s получен. Всё работает", request);
    }


}
