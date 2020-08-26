package kz.qbots.command.impl;


import kz.qbots.command.Command;
import kz.qbots.entity.custom.Group;
import kz.qbots.entity.custom.Request;
import kz.qbots.util.Const;
import kz.qbots.util.type.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id021_Request extends Command {
    Request request = new Request();
    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        switch (waitingType){
            case START:
                sendMessage(Const.FULL_NAME,chatId);
                waitingType = WaitingType.SET_FULL_NAME;
                return COMEBACK;
            case SET_FULL_NAME:
                sendMessage(Const.GET_PHONE_NUMBER_FROM_REGISTRATION);
                request.setFullName(updateMessageText);
                waitingType = WaitingType.SET_PHONE_NUMBER;
                return COMEBACK;
            case SET_PHONE_NUMBER:
                if (update.getMessage().hasContact())
                {
                    sendMessage(Const.REQUEST_SAVED,chatId);
                    request.setPhoneNumber(update.getMessage().getContact().getPhoneNumber());
                    request.setChatId(chatId);
                    requestDao.insert(request);
                    sendRequestToGroup();
                }

                return EXIT;
        }

        return EXIT;
    }

    private void sendRequestToGroup() throws TelegramApiException {
        request = requestDao.getById(requestDao.getLastId());
        Group smGroup = factory.getGroupDao().getGroupToId(2);
        StringBuilder sb = new StringBuilder();
        sb.append("Новая заявка №"+request.getId()).append(next);
        sb.append("Имя потенциального клиента: " + request.getFullName()).append(next);
        sb.append("Номер телефона: "+request.getPhoneNumber());
        if (smGroup.getChatId() != 0){
            sendMessage(sb.toString(), smGroup.getChatId());
        }
    }

    public int getMenu() throws TelegramApiException{
        return botUtils.sendMessage(Const.THE_MAIN_MENU,chatId);
    }
}
