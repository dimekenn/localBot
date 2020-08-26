package kz.qbots.command.impl;

import kz.qbots.command.Command;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id022_AboutUs extends Command {
    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        sendMessage("About US",chatId);
        return EXIT;
    }
}
