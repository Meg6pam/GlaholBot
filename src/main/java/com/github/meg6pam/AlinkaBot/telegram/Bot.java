package com.github.meg6pam.AlinkaBot.telegram;

import com.github.meg6pam.AlinkaBot.telegram.command.service.CancelCommand;
import com.github.meg6pam.AlinkaBot.telegram.command.service.HelpCommand;
import com.github.meg6pam.AlinkaBot.telegram.command.service.MailingCommand;
import com.github.meg6pam.AlinkaBot.telegram.command.service.PushCommand;
import com.github.meg6pam.AlinkaBot.telegram.command.service.SendCommand;
import com.github.meg6pam.AlinkaBot.telegram.command.service.StartCommand;
import com.github.meg6pam.AlinkaBot.telegram.util.DatabaseManager;
import com.github.meg6pam.AlinkaBot.telegram.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public final class Bot extends TelegramLongPollingCommandBot {

    private final NonCommand nonCommand;
    private final AdminCommand adminCommand;
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
        register(new StartCommand("start", "Старт"));
        logger.debug("Команда start создана");
        register(new HelpCommand("help","Помощь"));
        logger.debug("Команда help создана");
        register(new MailingCommand("mailing","Рассылка"));
        logger.debug("Команда mailing создана");
        register(new PushCommand("push","отправить"));
        logger.debug("Команда push создана");
        register(new SendCommand("send", "готово"));
        logger.debug("Команда send создана");
        register(new CancelCommand("cancel","отмена"));
        logger.debug("Команда cancel создана");
        logger.info("Бот создан!");
    }

    @Override
    public String getBotUsername() {
        return this.BOT_USERNAME;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = Utils.getUserName(msg);
        String answer;
        if (DatabaseManager.getUserRole(userName).equals("ADMIN")) {
            answer = adminCommand.handleUpdate(update);
        } else {
            answer = nonCommand.execute(chatId, userName, msg.getText());
        }
            setAnswer(chatId, userName, answer);
    }

    @Override
    public String getBotToken() {
        return this.BOT_TOKEN;
    }

    /**
     * Отправка ответа
     * @param chatId id чата
     * @param userName имя пользователя
     * @param text текст ответа
     */
    private void setAnswer(Long chatId, String userName, String text) {
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
