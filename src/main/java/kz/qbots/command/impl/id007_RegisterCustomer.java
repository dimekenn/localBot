package kz.qbots.command.impl;

import kz.qbots.command.Command;
import kz.qbots.entity.standart.User;
import kz.qbots.util.Const;
import kz.qbots.util.UpdateUtil;
import kz.qbots.util.type.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id007_RegisterCustomer extends Command {

    private User user = new User();

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        chatId = UpdateUtil.getChatId(update);
        switch (waitingType){
            case START:
                deleteMessage(updateMessageId);
                user.setChatId(chatId);
                if (userDao.isRegistered(chatId)){
                    sendMessage(Const.WRONG_DATA_MESSAGE);
                    return EXIT;
                }
                sendMessageWithoutKeyboard(Const.SEND_FULL_NAME);
                waitingType = WaitingType.FULL_NAME;
                return COMEBACK;
            case FULL_NAME:
                deleteMessage(updateMessageId);
                if (update.hasMessage() && updateMessage.hasText() && update.getMessage().getText().length() <= 50){
                    user.setFullName(update.getMessage().getText());
                    user.setUserName(updateMessage.getFrom().getUserName());
                    sendMessage(Const.SEND_CONTACT_NUMBER);
                    waitingType = WaitingType.END;
                } else{
                    sendMessage(Const.WRONG_DATA_MESSAGE);
                }
                return COMEBACK;
            case END:
                if (update.getMessage().hasContact()) {
                    user.setPhone(update.getMessage().getContact().getPhoneNumber());
                    userDao.insert(user);
                    sendMessage(Const.REGISTRATION_SUCCESS);
                }else{
                    sendMessage(Const.WRONG_DATA_MESSAGE);
                }
                return COMEBACK;
        }
        return EXIT;
    }
}
