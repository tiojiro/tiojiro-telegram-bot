package br.com.zukeran.tiojiro.telegrambot.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.pengrad.telegrambot.response.SendResponse;

import br.com.zukeran.tiojiro.telegrambot.config.TioTelegramBotConfig;

@Service
public class TioTelegramBotServiceImpl implements TioTelegramBotService{
	
	private static final String VAZIO = "";
	
	@Autowired
	private TioTelegramBotConfig tioBotConfig;

	@Override
	public boolean webhook(String strUpdate) throws Exception {
		SendResponse sendResponse;
		BaseResponse baseResponse;
		
		TelegramBot bot = tioBotConfig.telegramBot();
		Update update = BotUtils.parseUpdate(strUpdate);
		
		System.out.println("Mensagem recebida: ["+ update.message().text() + "]");
		
		baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));
		System.out.println("Chat Action enviada? [" + baseResponse.isOk() + "]");
		
		TimeUnit.SECONDS.sleep(1);
		
		if(!VAZIO.equals(update.message().from().username()))
			sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Olá, " + update.message().from().username() + "!"));
		else if (!VAZIO.equals(update.message().from().firstName()))
			sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Olá, " + update.message().from().firstName() + "!"));
		else
			sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Olá!"));
		System.out.println("Mensagem enviada? [" +sendResponse.isOk() + "]");
		
		return sendResponse.isOk();
	}

	@Override
	public User getMe() {
		GetMeResponse getMeResponse;
		TelegramBot bot = tioBotConfig.telegramBot();
		
		getMeResponse = bot.execute(new GetMe());

		return getMeResponse.user();
		
	}
}
