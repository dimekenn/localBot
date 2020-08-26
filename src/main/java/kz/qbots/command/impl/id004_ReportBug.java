package kz.qbots.command.impl;

import kz.qbots.command.Command;
import kz.qbots.entity.custom.Group;
import kz.qbots.entity.standart.Report;
import kz.qbots.entity.standart.ReportArchive;
import kz.qbots.entity.standart.User;
import kz.qbots.util.Const;
import kz.qbots.util.type.WaitingType;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
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
                sendMessageWithoutKeyboard(Const.REPORT_TEXT_SEND);
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
                } else if (updateMessage.hasVoice()){
                    report.setAudio(updateMessage.getVoice().getFileId());
                    sendMessage(Const.REPORT_AUDIO_VIDEO_SEND);
                    waitingType = WaitingType.SEND_REPORT_PHOTO_VIDEO;
                    return COMEBACK;
                } else {
                    sendMessage(Const.WRONG_DATA_MESSAGE);
                    sendMessageWithoutKeyboard(Const.REPORT_TEXT_SEND);
                }
            case SEND_REPORT_PHOTO_VIDEO:
                if (updateMessage.hasPhoto()){
                    report.setPhoto(updateMessagePhoto);
                    report.setDate(new Date());
                    sendMessageWithKeyboard(getText(Const.REPORT_AUDIO_VIDEO_SUBMITTED), Const.MAIN_MENU_CLIENT);
                    reportDao.insert(report);
                    sendReport();
                    return COMEBACK;
                }else if (updateMessage.hasVideo()){
                    report.setVideo(updateMessage.getVideo().getFileId());
                    report.setDate(new Date());
                    sendMessageWithKeyboard(getText(Const.REPORT_AUDIO_VIDEO_SUBMITTED), Const.MAIN_MENU_CLIENT);
                    reportDao.insert(report);
                    sendReport();
                    return COMEBACK;
                } else{
                    sendMessage(Const.WRONG_DATA_MESSAGE);
                    sendMessage(Const.REPORT_AUDIO_VIDEO_SEND);
                }
        }
        return EXIT;
    }

    private void sendReport() throws TelegramApiException {
        reportDao.updateStatus(reportDao.getLastId(), 3);
        report = reportDao.getById(reportDao.getLastId());
        Group group = factory.getGroupDao().getGroupToId(1);
        Group smGroup = factory.getGroupDao().getGroupToId(2);
        //List<ReportArchive> archiveList = reportArchiveDao.getReportList(report.getId());
        StringBuilder sb = new StringBuilder();
//            if (archiveList.size() != 0) {
//                for (ReportArchive reports : archiveList) {
//                    sb.append(reports.getText()).append(next);
//                }
//            }
            sb.append("Новое обращение №: " + report.getId()).append(next);
            if (!reportDao.hasAudio(report)) {
                sb.append("Текст: ").append(report.getText()).append(next);
            } else {
                bot.execute(new SendVoice().setVoice(report.getAudio()).setChatId(group.getChatId()));
                bot.execute(new SendVoice().setVoice(report.getAudio()).setChatId(smGroup.getChatId()));
            }

            sb.append("Статус: На рассмотрении");

        if (group.getId() != 0) {
            sendMessage(sb.toString(), group.getChatId());
        }
        if (smGroup.getChatId() != 0){
            sendMessage(sb.toString(), smGroup.getChatId());
        }
        if (report.getPhoto() !=null){
            bot.execute(new SendPhoto().setPhoto(report.getPhoto()).setChatId(group.getChatId()));
            bot.execute(new SendPhoto().setPhoto(report.getPhoto()).setChatId(smGroup.getChatId()));
        } else {
            bot.execute(new SendVideo().setVideo(report.getVideo()).setChatId(group.getChatId()));
            bot.execute(new SendVideo().setVideo(report.getVideo()).setChatId(smGroup.getChatId()));
        }
        sendMessage(getText(10));
    }
}
