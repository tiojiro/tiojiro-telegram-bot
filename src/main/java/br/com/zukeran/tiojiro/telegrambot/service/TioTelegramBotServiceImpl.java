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

@Service
public class TioTelegramBotServiceImpl implements TioTelegramBotService{
	
	private static final String VAZIO = "";
	private static final String SPACE = " ";
	private static final String CMD = "/";
	private static final String CMD_START = "/start";
	private static final String CMD_IMG = "/img";
	
	@Autowired
	TelegramBot bot;

	@Override
	public boolean webhook(String strUpdate) throws Exception {
		boolean ret = false;
		Update update = BotUtils.parseUpdate(strUpdate);
		String command = getCommand(update);
		
		switch(command) {
			case CMD_START:
				ret = sendHelloMessage(update);
				break;
			case CMD_IMG:
				ret = sendHelloMessage(update);
				break;
		}
		
		return ret;
	}

	@Override
	public User getMe() {
		GetMeResponse getMeResponse = bot.execute(new GetMe());
		return getMeResponse.user();
	}
	
	private String getCommand(Update update) {
		String msg = update.message().text();
		if(msg.startsWith(CMD))
			return msg.split(SPACE)[0];
		else
			return VAZIO;
	}
	
	private String getFrom(Update update) {
		String from = null;
		String userName = update.message().from().username();
		String firstName =  update.message().from().firstName();
		String id = update.message().from().id().toString();
		
		if (firstName != null && !VAZIO.equals(firstName))
			from = "firsName: " + firstName;
		else if(userName != null && !VAZIO.equals(userName))
			from = "userName: " + userName;
		else if(id != null && !VAZIO.equals(id))
			from = "id: " + id;
		
		System.out.println("Update received from: ["+ from + "]");
		
		return from;
	}
	
	private boolean sendHelloMessage (Update update) throws InterruptedException {
		boolean ret = true;
		SendResponse sendResponse;
		String from = getFrom(update);
		
		if(sendTyping(update)) {
			if(from != null)
				sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Olá, " + from + "!"));
			else
				sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Olá!"));
			
			ret = sendResponse.isOk();
		} else {
			ret = false;
		}
		
		return ret;
	}
	
	private boolean sendTyping(Update update) throws InterruptedException {
		BaseResponse baseResponse;
		baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));

		TimeUnit.SECONDS.sleep(1);
		
		return baseResponse.isOk();
	}
}
