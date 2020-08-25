package kz.qbots.config;


import kz.qbots.command.Command;
import kz.qbots.command.impl.id005_GroupManager;
import kz.qbots.dao.DaoFactory;
import kz.qbots.dao.implement.ButtonDao;
import kz.qbots.dao.implement.MessageDao;
import kz.qbots.entity.standart.Language;
import kz.qbots.entity.standart.Message;
import kz.qbots.exeption.CommandNotFoundException;
import kz.qbots.service.CommandService;
import kz.qbots.service.LanguageService;
import kz.qbots.util.Const;
import kz.qbots.util.DateUtil;
import kz.qbots.util.SetDeleteMessages;
import kz.qbots.util.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

@Slf4j
public class Conversation {

    private CommandService commandService = new CommandService();
    private Command command;
    private Long chatId;
    private static long currentChatId;
    private DaoFactory factory = DaoFactory.getFactory();
    private MessageDao messageDao;
    private ButtonDao buttonDao;

    public static long getCurrentChatId() {
        return currentChatId;
    }

    public void handleUpdate(Update update, DefaultAbsSender bot) throws SQLException, TelegramApiException {
        printUpdate(update);
        chatId = UpdateUtil.getChatId(update);
        currentChatId = chatId;
        messageDao = factory.getMessageDao();
        checkLang(chatId);
        try {
            if (chatId < 0) {
                command = new id005_GroupManager();
                commandExecute(update, bot);
                return;
            }
            if (chatId > 0) {
                checkLanguage(chatId);
            }
        } catch (NullPointerException | IOException e) {
            log.error("Ошибка в groupManager", e);
        }

        try {
            command = commandService.getCommand(update);
            if (command != null) {
                SetDeleteMessages.deleteKeyboard(chatId, bot);
                SetDeleteMessages.deleteMessage(chatId, bot);
            }
        } catch (CommandNotFoundException e) {
            if (chatId < 0) {
                return;
            }
            if (command == null) {
                SetDeleteMessages.deleteKeyboard(chatId, bot);
                SetDeleteMessages.deleteMessage(chatId, bot);
                Message message = messageDao.getMessage(Const.COMMAND_NOT_FOUND);
                SendMessage sendMessage = new SendMessage(chatId, message.getName());
                bot.execute(sendMessage);
            }
        }
        if (command != null) {
            if (command.isInitNotNormal(update, bot)) {
                clear();
                return;
            }
            boolean commandFinished = command.execute();
            if (commandFinished) {
                clear();
            }
        }
    }

    private void checkLang(long chatId) {
        if (LanguageService.getLanguage(chatId) == null) {
            LanguageService.setLanguage(chatId, Language.ru);
        }
    }

    private void commandExecute(Update update, DefaultAbsSender bot) throws TelegramApiException, IOException, SQLException {
        if (command.isInitNormal(update, bot)) {
            clear();
            return;
        }
        boolean commandFinished = command.execute();
        if (commandFinished) clear();
    }

    private void checkLanguage(long chatId) {
        if (LanguageService.getLanguage(chatId) == null) LanguageService.setLanguage(chatId, Language.ru);
    }

    private void printUpdate(Update update) {
        String dateMessage = "";
        if (update.hasMessage()) {
            dateMessage = DateUtil.getDbMmYyyyHhMmSs(new Date((long) update.getMessage().getDate() * 1000));
        }
        log.debug("New update get {} -> send response {}", dateMessage, DateUtil.getDbMmYyyyHhMmSs(new Date()));
        log.debug(UpdateUtil.toString(update));
    }

//    public static DefaultAbsSender getBot() {
//        return Main.getBot();
//    }

    private String regForGroup(Update update, String inputtedText) {
        if (update.hasMessage() && update.getMessage().hasText() && UpdateUtil.getChatId(update) > 0) {
            try {
                String[] split = update.getMessage().getText().split(" ");
                if (split[0].equals("/start") && split[1] != null && !split[1].isEmpty()) {
                    return buttonDao.getButton(75).getName();
                }
            } catch (Exception e) {
            }
        }
        return inputtedText;
    }

    void clear() {
        command.clear();
        command = null;
    }
}
