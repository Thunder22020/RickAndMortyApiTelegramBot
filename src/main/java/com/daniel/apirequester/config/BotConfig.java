package com.daniel.apirequester.config;

import lombok.SneakyThrows;
import com.daniel.apirequester.bot.TelegramBot;
import com.daniel.apirequester.service.ApiService;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {
    @Bean
    @SneakyThrows
    public TelegramBot telegramBot(
            @Value("${bot.token}") String botToken,
            @Value("${bot.name}") String botName,
            DefaultBotOptions options,
            TelegramBotsApi telegramBotsApi,
            ApiService apiService
    ) {
        var bot = new TelegramBot(options, botToken, apiService, botName);
        telegramBotsApi.registerBot(bot);
        return bot;
    }

    @Bean
    @SneakyThrows
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public DefaultBotOptions botOptions() {
        return new DefaultBotOptions();
    }
}
