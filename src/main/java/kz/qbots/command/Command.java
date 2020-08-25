package kz.qbots.command;

import kz.qbots.dao.DaoFactory;
import kz.qbots.dao.implement.*;
import kz.qbots.util.BotUtil;
import kz.qbots.util.Const;
import kz.qbots.util.SetDeleteMessages;
import kz.qbots.util.UpdateUtil;
import kz.qbots.util.type.WaitingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;


@NoArgsConstructor
public abstract class Command {

    @Getter
    @Setter
    protected long id;
    protected Long chatId;
    protected Update update;
    @Getter
    @Setter
    protected long messageId;
    protected String markChange;
    protected int updateMessageId;
    protected DefaultAbsSender bot;
    protected int lastSentMessageID;
    protected int lastSentMessageWithoutKeyboardID;
    protected static BotUtil botUtils;
    protected String updateMessageText;
    protected String updateMessagePhoto;
    protected String updateMessagePhone;
    protected WaitingType waitingType = WaitingType.START;
    protected String editableTextOfMessage;
    protected static final String next = "\n";
    protected static final String space = " ";
    protected final static boolean EXIT = true;
    protected final static boolean COMEBACK = false;
    protected Message updateMessage;
    protected String updateMessageFile;

    protected static DaoFactory factory = DaoFactory.getFactory();
    protected static ButtonDao buttonDao = factory.getButtonDao();
    protected static MessageDao messageDao = factory.getMessageDao();
    protected static KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    protected static UserDao userDao = factory.getUserDao();
    protected static AdminDao adminDao = factory.getAdminDao();
    protected static ReportDao reportDao = factory.getReportDao();
    protected static GroupDao groupDao = factory.getGroupDao();
    protected static ReportArchiveDao reportArchiveDao = factory.getReportArchiveDao();
    protected static FileDao fileDao = factory.getFileDao();


    public abstract boolean execute() throws SQLException, TelegramApiException;

    protected int sendMessage(long messageId) throws TelegramApiException {
        return sendMessage(messageId, chatId);
    }

    protected int sendMessage(long messageId, long chatId) throws TelegramApiException {
        return sendMessage(messageId, chatId, null);
    }

    protected int sendMessage(long messageId, long chatId, Contact contact) throws TelegramApiException {
        return sendMessage(messageId, chatId, contact, null);
    }

    protected int sendMessage(long messageId, long chatId, Contact contact, String photo) throws TelegramApiException {
        lastSentMessageID = botUtils.sendMessage(messageId, chatId, contact, photo);
        return lastSentMessageID;
    }

    protected int sendMessage(String text) throws TelegramApiException {
        return sendMessage(text, chatId);
    }

    protected int sendMessage(String text, long chatId) throws TelegramApiException {
        return sendMessage(text, chatId, null);
    }

    protected int sendMessage(String text, long chatId, Contact contact) throws TelegramApiException {
        lastSentMessageID = botUtils.sendMessage(text, chatId);
        if (contact != null) {
            botUtils.sendContact(chatId, contact);
        }
        return lastSentMessageID;
    }

    protected void deleteMessage() {
        deleteMessage(chatId, lastSentMessageID);
    }

    protected void deleteMessage(int messageId) {
        deleteMessage(chatId, messageId);
    }

    public boolean isInitNormal(Update update, DefaultAbsSender bot) {
        if (botUtils == null) botUtils = new BotUtil(bot);
        this.update = update;
        this.bot = bot;
        chatId = UpdateUtil.getChatId(update);
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            updateMessage = callbackQuery.getMessage();
            updateMessageText = callbackQuery.getData();
            updateMessageId = updateMessage.getMessageId();
            editableTextOfMessage = callbackQuery.getMessage().getText();
        } else if (update.hasMessage()) {
            updateMessage = update.getMessage();
            updateMessageId = updateMessage.getMessageId();
            if (updateMessage.hasText()) updateMessageText = updateMessage.getText();
            if (updateMessage.hasPhoto()) {
                int size = update.getMessage().getPhoto().size();
                updateMessagePhoto = update.getMessage().getPhoto().get(size - 1).getFileId();
            } else {
                updateMessagePhoto = null;
            }
            if (updateMessage.hasDocument()) {
                updateMessageFile = update.getMessage().getDocument().getFileId();
            } else {
                updateMessageFile = null;
            }
        }
        if (hasContact()) updateMessagePhone = update.getMessage().getContact().getPhoneNumber();
        if (markChange == null) markChange = getText(Const.EDIT_BUTTON_ICON);
        return COMEBACK;
    }

    protected void deleteMessage(long chatId, int messageId) {
        botUtils.deleteMessage(chatId, messageId);
    }

    protected String getText(int messageIdFromBD) {
        return messageDao.getMessageText(messageIdFromBD);
    }

    public void clear() {
        update = null;
        bot = null;
    }

    protected boolean isRegistered() {
        return userDao.isRegistered(chatId);
    }


    protected boolean isButton(int buttonId) {
        return updateMessageText.equals(buttonDao.getButtonText(buttonId));
    }

    public boolean isInitNotNormal(Update update, DefaultAbsSender bot) {
        if (botUtils == null) {
            botUtils = new BotUtil(bot);
        }
        this.update = update;
        this.bot = bot;
        chatId = UpdateUtil.getChatId(update);
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            updateMessage = callbackQuery.getMessage();
            updateMessageText = callbackQuery.getData();
            updateMessageId = updateMessage.getMessageId();
            editableTextOfMessage = callbackQuery.getMessage().getText();
        } else if (update.hasMessage()) {
            updateMessage = update.getMessage();
            updateMessageId = updateMessage.getMessageId();
            if (updateMessage.hasText()) {
                updateMessageText = updateMessage.getText();
            }
            if (updateMessage.hasPhoto()) {
                int size = update.getMessage().getPhoto().size();
                updateMessagePhoto = update.getMessage().getPhoto().get(size - 1).getFileId();
            } else {
                updateMessagePhoto = null;
            }
        }
        if (hasContact()) {
            updateMessagePhone = update.getMessage().getContact().getPhoneNumber();
        }
        if (markChange == null) {
            markChange = getText(Const.EDIT_BUTTON_ICON);
        }
        return false;
    }

    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected boolean hasContact() {
        return update.hasMessage() && update.getMessage().getContact() != null;
    }

    protected void sendMessageWithAddition() throws TelegramApiException {
        deleteMessage(updateMessageId);
        kz.qbots.entity.standart.Message message = messageDao.getMessage(messageId);
        sendMessage(messageId, chatId, null, message.getPhoto());
        System.out.println(update.getMessage().getMessageId());
        if (message.getFile() != null) {
            try {
                switch (message.getFileType()) {
                    case audio:
                        bot.execute(new SendAudio()
                                .setAudio(message.getFile())
                                .setChatId(chatId));
                    case video:
                        bot.execute(new SendVideo()
                                .setVideo(message.getFile())
                                .setChatId(chatId));
                    case document:
                        bot.execute(new SendDocument()
                                .setChatId(chatId)
                                .setDocument(message.getFile()));
                }
            } catch (TelegramApiException e) {
                getLogger().error("Exception by send file for message " + messageId, e);
            }
        }
    }

//    protected boolean isAdmin() {
//        return adminDao.isAdmin(chatId);
//    }

    protected String getLinkForUser(long chatId, String userName) {
        return String.format("<a href = \"tg://user?id=%s\">%s</a>", String.valueOf(chatId), userName);
    }

    protected int toDeleteMessage(int messageDeleteId) {
        SetDeleteMessages.addMessage(chatId, messageDeleteId);
        return messageDeleteId;
    }

    protected int toDeleteKeyboard(int messageDeleteId) {
        SetDeleteMessages.addKeyboard(chatId, messageDeleteId);
        return messageDeleteId;
    }

    protected int sendMessageWithKeyboard(int messageId, ReplyKeyboard keyboard) throws TelegramApiException {
        return sendMessageWithKeyboard(getText(messageId), keyboard);
    }

    protected int sendMessageWithKeyboard(String text, int keyboardId) throws TelegramApiException {
        return sendMessageWithKeyboard(text, keyboardMarkUpDao.select(keyboardId));
    }

    protected int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard) throws TelegramApiException {
        lastSentMessageID = sendMessageWithKeyboard(text, keyboard, chatId);
        return lastSentMessageID;
    }

    protected int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId) throws TelegramApiException {
        return botUtils.sendMessageWithKeyboard(text, keyboard, chatId);
    }

    protected int sendMessageWithoutKeyboard(int messageId) throws TelegramApiException {
        return sendMessageWithoutKeyboard(getText(messageId));
    }

//    protected int sendMessageWithoutKeyboard(String text, int keyboardId) throws TelegramApiException {
//        return sendMessageWithoutKeyboard(text, keyboardMarkUpDao.select(keyboardId));
//    }

    protected int sendMessageWithoutKeyboard(String text) throws TelegramApiException {
        lastSentMessageID = sendMessageWithoutKeyboard(text,chatId);
        return lastSentMessageID;
    }

    protected int sendMessageWithoutKeyboard(String text, long chatId) throws TelegramApiException{
        return botUtils.sendMessageWithoutKeyboard(text,chatId);
    }

    protected boolean hasCallbackQuery() {
        return update.hasCallbackQuery();
    }

    protected boolean hasPhoto() {
        return update.hasMessage() && update.getMessage().hasPhoto();
    }

    protected boolean hasDocument() {
        return update.hasMessage() && update.getMessage().hasDocument();
    }

    protected boolean hasAudio() {
        return update.hasMessage() && update.getMessage().getAudio() != null;
    }

    protected boolean hasVideo() {
        return update.hasMessage() && update.getMessage().getVideo() != null;
    }

    protected boolean hasMessageText() { return  update.hasMessage() && update.getMessage().hasText(); }

    protected boolean isAdmin() {
        return adminDao.isAdmin(chatId);
    }

}
