package kz.qbots;


import kz.qbots.config.Bot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;


@Slf4j
public class Main {

    private static DefaultAbsSender bot;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        log.info("ApiContextInitializer.InitNormal()");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Bot bot = new Bot();
        Main.bot = bot;
        try {
            telegramBotsApi.registerBot(bot);
            log.info("Bot was registered: " + bot.getBotUsername());
        } catch (TelegramApiRequestException e) {
            log.error("Error in main", e);
        }
    }

    public static DefaultAbsSender getBot() {
        return bot;
    }
}
