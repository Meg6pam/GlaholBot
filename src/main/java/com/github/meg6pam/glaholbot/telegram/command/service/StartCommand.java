package com.github.meg6pam.glaholbot.telegram.command.service;

import com.github.meg6pam.glaholbot.telegram.util.Utils;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Команда "Старт"
 */
public class StartCommand extends ServiceCommand {

    public StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);
        //обращаемся к методу суперкласса для отправки пользователю ответа
        sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Круто! Ты зарегистрировался на марафон-практикум Кристины Эйприл!\n" +
                        "\n" +
                        "Впереди тебя ждёт 3 живых вебинара 19, 20 и 21 января \uD83D\uDD25\n" +
                        "\n" +
                        "Переходи по ссылке и подписывайся на канал. Там будет основная движуха! \n" +
                        "\n" +
                        "\uD83D\uDC49 @chabrechnaya_1\n" +
                        "\n" +
                        "\uD83C\uDF7F Бот — для ссылок на вебинары\n" +
                        "\uD83D\uDCFAКанал — расписание марафона и полезные материалы.\n" +
                        "\n" +
                        "Обязательно, подпишись \uD83D\uDC49 @chabrechnaya_1");
    }
}