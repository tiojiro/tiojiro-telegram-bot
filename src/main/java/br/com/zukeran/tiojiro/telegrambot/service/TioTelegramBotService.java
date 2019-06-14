package br.com.zukeran.tiojiro.telegrambot.service;

import com.pengrad.telegrambot.model.User;

public interface TioTelegramBotService {
	public boolean webhook(String strUpdate) throws Exception ;
	public User getMe();
}
