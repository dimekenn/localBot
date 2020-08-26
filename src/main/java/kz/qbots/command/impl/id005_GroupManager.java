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
    String devName;

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        groupInit();
        if (chat == null || !chat.isSuperGroupChat())
            return COMEBACK;

        group = GroupCacheService.checkGroup(update, chatId);
        smGroup = factory.getGroupDao().getGroupToId(2);

        if (group == null)
            return COMEBACK;
        if (chatId != smGroup.getChatId()) {
            String[] messageParseDev = getMessageFromGroup();
            if (messageParseDev == null)
                return COMEBACK;
            reportId = Integer.parseInt(messageParseDev[0]);
            report = reportDao.getById(reportId);
            if (report.getChatIdDeveloper() == 0) {
                if (developerDao.isRegistered(updateMessage.getFrom().getId())) {
                    report.setChatIdDeveloper(updateMessage.getFrom().getId());
//                    devName = userDao.getUserByChatId(updateMessage.getFrom().getId()).getFullName();
//                    System.out.println(developerDao.getDevByChatId(updateMessage.getFrom().getId()).getUserName());
                    String devName = developerDao.getDevByChatId(updateMessage.getFrom().getId()).getFullName();
                    reportDao.updateDev(report);
                    sendMessageToDevGroup(devName, reportId);
                    sendMessageToDev(updateMessage.getFrom().getId(), reportId);
                } else {
                    sendMessage("vy ne razrabotchik");
                }
            } else {
                takenReportMessage();
            }
        } else {
            String[] messageParseSM = getMessageFromGroup();
            if (messageParseSM == null)
                return COMEBACK;
            reportId = Integer.parseInt(messageParseSM[0]);
            report = reportDao.getById(reportId);
            if (report.getChatIdManager() == 0) {
                if (salesManagerDao.isRegistered(updateMessage.getFrom().getId())) {
                    report.setChatIdManager(updateMessage.getFrom().getId());
                    reportDao.updateSM(report);
                    //String managerName = userDao.getUserByChatId(updateMessage.getFrom().getId()).getFullName();
                    String managerName = salesManagerDao.getSMByChatId(updateMessage.getFrom().getId()).getFullName();
                    sendMessageToManager(updateMessage.getFrom().getId(), reportId);
                    Long chatIdCustomer = reportDao.getCustomerId(report);
                    if (report.getIdStatus() != 1) {
                        sendMessageToCustomer(chatIdCustomer, reportId, managerName);
                    }
                } else{
                    sendMessage("Vy ne manager!");
                }
            } else {
                takenReportMessage();
            }
        }

        return COMEBACK;
    }

    private void sendMessageToManager(Integer id, int reportId) throws TelegramApiException {
        StringBuilder messageToGroup = new StringBuilder();
        messageToGroup.append("Обращение № " +reportId+ " прикреплено за Вами").append(next);
        messageToGroup.append("Ждите новостей он разработчика!");
        sendMessage(messageToGroup.toString(), id);
    }


    private void takenReportMessage() throws TelegramApiException {
        sendMessage("Эта задачка уже занята!");
    }

    private String[] getMessageFromGroup() {
        if (updateMessageText != null && updateMessageText.contains("#")) {
            if (hasMessageText()) {
                return updateMessageText.split("#");
            } else if (hasPhoto() && update.getMessage().getCaption() != null)
                return update.getMessage().getCaption().split("#");
        }
        return null;
    }

    private void sendMessageToDevGroup(String devName, int reportId) throws TelegramApiException {
        StringBuilder messageToGroup = new StringBuilder();
        messageToGroup.append("Обращение № " +reportId+ " прикреплено за " + devName).append(next);
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
        messageToDev.append("Обращение № " +reportId+ " прекреплен за вами!").append(next);
        messageToDev.append("Просим как можно скорее сделать этот баг!");
        sendMessageWithKeyboard(messageToDev.toString(), keyboardMarkUpDao.select(Const.KEYBOARD_TO_DEV),chatId);
    }

    private void sendMessageToCustomer(long chatIdCustomer, int reportId, String managerName) throws TelegramApiException {
        StringBuilder sb = new StringBuilder();
        sb.append("Обращение № " + reportId).append(next);
        sb.append("Статус: в процессе").append(next);
        sb.append("Ответственный: " + managerName);
        sendMessage(sb.toString(), chatIdCustomer);
    }
}
