package kz.qbots.command.impl;

import kz.qbots.command.Command;
import kz.qbots.util.Const;
import kz.qbots.util.type.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id023_Services extends Command {
    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        switch (waitingType){
            case START:
                sendMessage(Const.GET_SERVICES,chatId);
                waitingType = WaitingType.CHOOSE_OPTION;
                return COMEBACK;
            case CHOOSE_OPTION:
                if (isButton(206)){
                    sendMessage("Создание бота!");
                }
                if (isButton(207)){
                    sendMessage("Сопровождение!");
                }
                if (isButton(208)){
                    sendMessage("Обслуживание!");
                }
                return COMEBACK;
        }

        return EXIT;
    }
}
