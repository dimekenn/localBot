package kz.qbots.command.impl;


import kz.qbots.command.Command;
import kz.qbots.entity.standart.Language;
import kz.qbots.service.LanguageService;
import kz.qbots.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id002_SelectLang extends Command {

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        chosenLanguage();
        welcome();
        return EXIT;
    }


    private void chosenLanguage() {
        if (isButton(Const.RU_LANGUAGE)) {
            LanguageService.setLanguage(chatId, Language.ru);
        }
        if (isButton(Const.KZ_LANGUAGE)) {
            LanguageService.setLanguage(chatId, Language.kz);
        }
        if (isButton(Const.EN_LANGUAGE)) {
            LanguageService.setLanguage(chatId, Language.en);
        }
    }

    private int welcome() throws TelegramApiException{
        return botUtils.sendMessage(Const.WELCOME_TEXT_WHEN_START,chatId);
    }

}
