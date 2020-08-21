package kz.qbots.config;


import kz.qbots.dao.DaoFactory;
import kz.qbots.dao.implement.PropertiesDao;
import kz.qbots.util.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

public class Bot extends TelegramLongPollingBot {
    private String tokenBot;
    private String nameBot;
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private UpdateHandler updateHandler = new UpdateHandler();
    private DaoFactory daoFactory    = DaoFactory.getInstance();
    private PropertiesDao propertiesDao = daoFactory.getPropertiesDao();


    @Override
    public void onUpdateReceived(Update update) {
        logger.debug("------ get UPDATE: " + getBotUsername());
        updateHandler.handle(update, this);
        logger.debug("------ UPDATE processed success");
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        if (method instanceof SendMessage) {
            ((SendMessage) method).disableWebPagePreview();
        }
        return super.execute(method);
    }

    @Override
    public String getBotUsername() {
        if (nameBot == null || nameBot.isEmpty()) nameBot = propertiesDao.getPropertiesValue(Const.BOT_NAME);
        return nameBot;
    }

    @Override
    public String getBotToken() {
        if (tokenBot == null || tokenBot.isEmpty())
            tokenBot = propertiesDao.getPropertiesValue(Const.BOT_TOKEN);
        return tokenBot;
//      return "1018614657:AAFSXmF1acRFpyo6TkOpnsPdwLkwNc71838";
    }
}
