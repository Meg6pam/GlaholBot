package com.github.meg6pam.glaholbot.telegram;

import com.github.meg6pam.glaholbot.model.Job;
import com.github.meg6pam.glaholbot.model.MessageTuple;
import com.github.meg6pam.glaholbot.telegram.command.service.HelpCommand;
import com.github.meg6pam.glaholbot.telegram.command.service.StartCommand;
import com.github.meg6pam.glaholbot.telegram.util.DatabaseManager;
import com.github.meg6pam.glaholbot.telegram.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashSet;
import java.util.Set;


public final class Bot extends TelegramLongPollingCommandBot {

    private static final Set<Job> jobThreads = new HashSet<>();

    private final NonCommand nonCommand;
    private final AdminCommand adminCommand;
    private final SendVideoCommand sendVideoCommand;
    Logger logger = LoggerFactory.getLogger(Bot.class.getName());

    private final String BOT_USERNAME;
    private final String BOT_TOKEN;

    public Bot(String botUsername, String botToken) {
        super();
        this.BOT_USERNAME = botUsername;
        this.BOT_TOKEN = botToken;
        logger.debug("Имя и токен присвоены");
        this.nonCommand = new NonCommand();
        logger.debug("Класс обработки сообщения, не являющегося командой, создан");
        this.adminCommand = new AdminCommand();
        logger.debug("Класс обработки сообщений администратора, не являющегося командой, создан");
        this.sendVideoCommand = new SendVideoCommand();
        logger.debug("Класс отправки видеофайлов, создан");
        register(new StartCommand("start", "Старт"));
        logger.debug("Команда start создана");
        register(new HelpCommand("help", "Помощь"));
        logger.debug("Команда help создана");
        logger.info("Бот создан!");
    }

    @Override
    public String getBotUsername() {
        return this.BOT_USERNAME;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        final Message msg = update.getMessage();
        final Long chatId = msg.getChatId();
        final String userName = Utils.getUserName(msg);
        final String answer = sendVideoCommand.handleUpdate(update);
        setAnswer(chatId, userName, answer);
    }

    @Override
    public String getBotToken() {
        return this.BOT_TOKEN;
    }

    /**
     * Отправка ответа
     *
     * @param chatId   id чата
     * @param userName имя пользователя
     * @param text     текст ответа
     */
    protected void setAnswer(Long chatId, String userName, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            logger.error(String.format("Ошибка %s. Сообщение, не являющееся командой. Пользователь: %s", e.getMessage(),
                    userName));
            e.printStackTrace();
        }
    }
}
