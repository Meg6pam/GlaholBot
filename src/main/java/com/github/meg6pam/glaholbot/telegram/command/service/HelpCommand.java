package com.github.meg6pam.alinkabot.telegram.command.service;

import com.github.meg6pam.alinkabot.telegram.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Команда "Помощь"
 */
public class HelpCommand extends ServiceCommand {

    public HelpCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);
        sendResponse(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Если у тебя возникнут сложности или вопросы по марафону, пиши в службу заботы  \uD83E\uDD70  @a_peshkina");
    }
}