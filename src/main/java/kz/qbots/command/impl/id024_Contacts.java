package kz.qbots.command.impl;

import kz.qbots.command.Command;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id024_Contacts extends Command {
    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        sendMessage("Contacts",chatId);
        return EXIT;
    }
}
