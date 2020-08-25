package kz.qbots.command.impl;

import kz.qbots.command.Command;
import kz.qbots.entity.custom.Group;
import kz.qbots.entity.standart.Report;
import kz.qbots.entity.standart.ReportArchive;
import kz.qbots.entity.standart.User;
import kz.qbots.util.Const;
import kz.qbots.util.type.WaitingType;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class id004_ReportBug extends Command {

    Report report = new Report();

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        switch (waitingType){
            case START:
                deleteMessage(updateMessageId);
                sendMessage(Const.REPORT_TEXT_SEND);
                report.setChat_id(chatId);
                User user = userDao.getUserByChatId(chatId);
                report.setNameOfClient(user.getFullName());
                waitingType = WaitingType.SEND_REPORT_TEXT;
                return COMEBACK;
            case SEND_REPORT_TEXT:
                if (update.hasMessage() && updateMessage.hasText() && update.getMessage().getText().length() <= 50){
                    report.setText(updateMessageText);
                    sendMessage(Const.REPORT_AUDIO_VIDEO_SEND);
                    waitingType = WaitingType.SEND_REPORT_PHOTO_VIDEO;
                    return COMEBACK;
                }
                if (updateMessage.hasAudio()){
                    report.setAudio(updateMessage.getAudio().getFileId());
                    sendMessage(Const.REPORT_AUDIO_VIDEO_SEND);
                    waitingType = WaitingType.SEND_REPORT_PHOTO_VIDEO;
                    return COMEBACK;
                }
            case SEND_REPORT_PHOTO_VIDEO:
                if (updateMessage.hasPhoto()){
                    report.setPhoto(updateMessagePhoto);
                    report.setDate(new Date());
                    sendMessageWithKeyboard(getText(Const.REPORT_AUDIO_VIDEO_SUBMITTED), Const.MAIN_MENU_CLIENT);
                    reportDao.insert(report);
                    sendReport();
                    return COMEBACK;
                }
                if (updateMessage.hasVideo()){
                    report.setVideo(updateMessage.getVideo().getFileId());
                    report.setDate(new Date());
                    sendMessageWithKeyboard(getText(Const.REPORT_AUDIO_VIDEO_SUBMITTED), Const.MAIN_MENU_CLIENT);
                    reportDao.insert(report);
                    sendReport();
                    return COMEBACK;
                }
        }
        return EXIT;
    }

    private void sendReport() throws TelegramApiException {
        reportDao.updateStatus(reportDao.getLastId(), 3);
        report = reportDao.getById(reportDao.getLastId());
        Group group = factory.getGroupDao().getGroupToId(1);
        Group smGroup = factory.getGroupDao().getGroupToId(2);
        List<ReportArchive> archiveList = reportArchiveDao.getReportList(report.getId());
        StringBuilder sb = new StringBuilder();
            if (archiveList.size() != 0) {
                for (ReportArchive reports : archiveList) {
                    sb.append(reports.getText()).append(next);
                }
            }
            sb.append("Новое обращение №: " + report.getId()).append(next);
            sb.append("Text obrashenie: ").append(report.getText()).append(next);
            sb.append("Status obrasheine: " + report.getIdStatus());

        if (group.getId() != 0) {
            sendMessage(sb.toString(), group.getChatId());
        }
        if (smGroup.getChatId() != 0){
            sendMessage(sb.toString(), smGroup.getChatId());
        }
        if (report.getPhoto() !=null){
            bot.execute(new SendPhoto().setPhoto(report.getPhoto()).setChatId(group.getChatId()));
            bot.execute(new SendPhoto().setPhoto(report.getPhoto()).setChatId(smGroup.getChatId()));
        }
        sendMessage(getText(10));
    }
}
