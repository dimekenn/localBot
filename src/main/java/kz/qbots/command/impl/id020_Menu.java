package kz.qbots.command.impl;

import kz.qbots.command.Command;
import kz.qbots.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id020_Menu extends Command {

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        getMenu();
        return EXIT;
    }

    public int getMenu() throws TelegramApiException{
        return botUtils.sendMessage(Const.THE_MAIN_MENU,chatId);
    }
}
