package kz.qbots.command.impl;

import kz.qbots.command.Command;
import kz.qbots.entity.standart.Report;
import kz.qbots.util.type.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id006_SendMessageToCustomer extends Command {

    private Report report;
    private StringBuilder sb = new StringBuilder();

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        switch (waitingType){
            case START:
                if (isButton(11)){
                    String text = update.getCallbackQuery().getMessage().getText();
                    int id = Integer.parseInt(text.split(next)[0].replaceAll("[^0-9]", ""));
                    report = reportDao.getById(id);
                    sb.append("Ваше обращение № "+id+" выполнено!").append(next);
                    sb.append("Статус: Выполнено!");

                    if (report.getIdStatus()!=1) {
                        sendMessage(sb.toString(), reportDao.getCustomerId(report));
                    }
                    //report.setIdStatus(1);
                    reportDao.updateStatus(id, 1);
                }
        }
        return EXIT;
    }
}
