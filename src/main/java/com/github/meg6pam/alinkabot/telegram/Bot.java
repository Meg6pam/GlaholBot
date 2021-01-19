package com.github.meg6pam.alinkabot.telegram;

import com.github.meg6pam.alinkabot.model.Job;
import com.github.meg6pam.alinkabot.telegram.command.service.CancelCommand;
import com.github.meg6pam.alinkabot.telegram.command.service.HelpCommand;
import com.github.meg6pam.alinkabot.telegram.command.service.MailingCommand;
import com.github.meg6pam.alinkabot.telegram.command.service.PushCommand;
import com.github.meg6pam.alinkabot.telegram.command.service.SendCommand;
import com.github.meg6pam.alinkabot.telegram.command.service.StartCommand;
import com.github.meg6pam.alinkabot.telegram.util.DatabaseManager;
import com.github.meg6pam.alinkabot.telegram.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public final class Bot extends TelegramLongPollingCommandBot {

    private static final Set<Job> jobThreads = new HashSet<>();

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
        register(new HelpCommand("help", "Помощь"));
        logger.debug("Команда help создана");
        register(new MailingCommand("mailing", "Рассылка"));
        logger.debug("Команда mailing создана");
        register(new PushCommand("push", "отправить"));
        logger.debug("Команда push создана");
        register(new SendCommand("send", "готово"));
        logger.debug("Команда send создана");
        register(new CancelCommand("cancel", "отмена"));
        logger.debug("Команда cancel создана");
        logger.info("Бот создан!");

        Timer timer = new Timer();
        timer.schedule(new JobFounder(), 0, 60 * 1000);
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

    public void handleJobs() {
        Thread thread = new Thread(() -> {
            while (true) {
                List<Job> completedJobs = new ArrayList<>();
                jobThreads.forEach(job -> {
                    if (job.getFileId() != null && job.getFileId() != 0) {
                        SendPhoto msg = new SendPhoto();
                        msg.setPhoto(new InputFile(String.valueOf(job.getFileId())));
                        msg.setChatId(job.getChatId().toString());
                        try {
                            execute(msg);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (job.getMessage() != null && !job.getMessage().isEmpty()) {
                        SendMessage answer = new SendMessage();
                        answer.setText(job.getMessage());
                        answer.setChatId(job.getChatId().toString());
                        try {
                            execute(answer);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    completedJobs.add(job);
                    DatabaseManager.deleteJob(job.getJobId());
                });
                jobThreads.removeAll(completedJobs);
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
        );

        thread.start();

    }

    private static class JobFounder extends TimerTask {
        @Override
        public void run() {
            List<Job> jobs = DatabaseManager.getAllJobs();
            jobThreads.addAll(jobs);
        }
    }

}
