package kz.qbots.command.impl;

import kz.qbots.command.Command;
import kz.qbots.entity.custom.Group;
import kz.qbots.entity.standart.Report;
import kz.qbots.service.GroupCacheService;
import kz.qbots.util.Const;
import kz.qbots.util.UpdateUtil;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id005_GroupManager extends Command {

    private Chat chat;
    private Report report;
    private Group group;
    private Group smGroup;
    private int reportId;

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        groupInit();
        if (chat == null || !chat.isSuperGroupChat())
            return COMEBACK;
        group = GroupCacheService.checkGroup(update, chatId);
        if (group == null)
            return COMEBACK;
        String[] messageParse = getMessageFromDev();
        if (messageParse == null)
            return COMEBACK;
        reportId = Integer.parseInt(messageParse[0]);
        report = reportDao.getById(reportId);
        if (report.getChatIdDeveloper() == 0) {
            report.setChatIdDeveloper(updateMessage.getFrom().getId());
            reportDao.updateDev(report);
            String devName = userDao.getUserByChatId(updateMessage.getFrom().getId()).getFullName();
            sendMessageToGroup(devName, reportId);
            sendMessageToDev(updateMessage.getFrom().getId(), reportId);
        }else{
            takenReportMessage();
        }
        return COMEBACK;
    }

    private void takenReportMessage() throws TelegramApiException {
        sendMessage("Эта задачка уже занята!");
    }

    private String[] getMessageFromDev() {
        if (updateMessageText != null && updateMessageText.contains("#")) {
            if (hasMessageText()) {
                return updateMessageText.split("#");
            } else if (hasPhoto() && update.getMessage().getCaption() != null)
                return update.getMessage().getCaption().split("#");
        }
        return null;
    }

    private void sendMessageToGroup(String devName, int reportId) throws TelegramApiException {
        StringBuilder messageToGroup = new StringBuilder();
        messageToGroup.append("Обращение №" +reportId+ " прикреплено за " + devName).append(next);
        messageToGroup.append("Ждите новостей он менеждера!");
        sendMessage(messageToGroup.toString());
    }

    private void groupInit() {
        chat = UpdateUtil.getChat(update);
        updateMessage = UpdateUtil.getMessage(update);
        if (updateMessage != null) {
            updateMessageId = updateMessage.getMessageId();
            if (update.hasCallbackQuery()) {
                updateMessageText = update.getCallbackQuery().getData();
            } else if (update.hasMessage()) {
                if (updateMessage.hasText()) {
                    updateMessageText = updateMessage.getText();
                } else if (updateMessage.getCaption() != null) updateMessageText = update.getMessage().getCaption();
            } else {
                updateMessageText = null;
            }
        }
    }

    private void sendMessageToDev(long chatId, int reportId) throws TelegramApiException {
        StringBuilder messageToDev = new StringBuilder();
        messageToDev.append("Обращение № " +reportId+ "прекреплен за вами!").append(next);
        messageToDev.append("Просим как можно скорее сделать этот баг!");
        sendMessageWithKeyboard(messageToDev.toString(), keyboardMarkUpDao.select(Const.KEYBOARD_TO_DEV),chatId);
    }
}
