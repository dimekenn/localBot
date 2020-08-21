package kz.qbots.command.impl;

import kz.qbots.command.Command;
import kz.qbots.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id003_ClientShowInfo extends Command {
    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        if (isRegistered()) {
            sendMessageWithAddition();
            deleteMessage(updateMessageId);
            return EXIT;
        } else{
            sendMessage(Const.WRONG_CLIENT);
            return EXIT;
        }

    }
}
