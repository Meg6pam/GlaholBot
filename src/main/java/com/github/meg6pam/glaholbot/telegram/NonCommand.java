package com.github.meg6pam.glaholbot.telegram;

import com.github.meg6pam.glaholbot.model.MessageTuple;
import com.github.meg6pam.glaholbot.telegram.util.DatabaseManager;

import java.util.Optional;

public class NonCommand {

    public MessageTuple execute(String request) {
        Optional<MessageTuple> tuple = DatabaseManager.getCodeFileAndMessage(request);
        return tuple.orElse(null);
        //TODO Логируем событие
    }


}
