package kz.qbots.command.impl;

import kz.qbots.command.Command;
import kz.qbots.entity.standart.Developer;
import kz.qbots.util.Const;
import kz.qbots.util.UpdateUtil;
import kz.qbots.util.type.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id008_RegisterDeveloper extends Command {

    private Developer developer = new Developer();

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        chatId = UpdateUtil.getChatId(update);
        switch (waitingType){
            case START:
                deleteMessage(updateMessageId);
                developer.setChatId(chatId);
                if (developerDao.isRegistered(chatId)){
                    sendMessage(Const.WRONG_DATA_MESSAGE);
                    return EXIT;
                }
                sendMessageWithoutKeyboard(Const.SEND_FULL_NAME);
                waitingType = WaitingType.FULL_NAME;
                return COMEBACK;
            case FULL_NAME:
                deleteMessage(updateMessageId);
                if (update.hasMessage() && updateMessage.hasText() && update.getMessage().getText().length() <= 50){
                    developer.setFullName(update.getMessage().getText());
                    developer.setUserName(update.getMessage().getFrom().getUserName());
                    sendMessage(Const.REGISTRATION_SUCCESS);
                    developerDao.insert(developer);
                } else{
                    sendMessage(Const.WRONG_DATA_MESSAGE);
                }
                return COMEBACK;
        }
        return EXIT;
    }
}
