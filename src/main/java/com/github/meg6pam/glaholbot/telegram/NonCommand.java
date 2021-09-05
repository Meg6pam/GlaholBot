package com.github.meg6pam.alinkabot.telegram;

import com.github.meg6pam.alinkabot.model.MessageTuple;
import com.github.meg6pam.alinkabot.telegram.util.DatabaseManager;

import java.util.Optional;

public class NonCommand {

    public MessageTuple execute(String request) {
        Optional<MessageTuple> tuple = DatabaseManager.getCodeFileAndMessage(request);
        return tuple.orElse(null);
        //TODO Логируем событие
    }


}
