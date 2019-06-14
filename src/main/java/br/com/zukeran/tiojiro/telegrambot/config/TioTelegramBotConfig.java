package br.com.zukeran.tiojiro.telegrambot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pengrad.telegrambot.TelegramBot;

@Configuration
public class TioTelegramBotConfig {
	
	@Autowired
	private TioTelegramBotProperties botProperties;
	
	@Bean
	public TelegramBot telegramBot() {
		TelegramBot bot = new TelegramBot(botProperties.getToken());
		return bot;
	}
}
