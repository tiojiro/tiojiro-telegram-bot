package br.com.zukeran.tiojiro.telegrambot.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.pengrad.telegrambot.response.SendResponse;

import br.com.zukeran.tiojiro.telegrambot.config.TioTelegramBotMsgProperties;

@Service
public class TioTelegramBotServiceImpl implements TioTelegramBotService{
	
	private static final String VAZIO = "";
	private static final String SPACE = " ";
	private static final String CMD = "/";
	private static final String CMD_START = "/start";
	private static final String CMD_IMG = "/img";
	private static final String CMD_HELP = "/help";
	private static final String PHOTO = "PHOTO";
	
	@Autowired
	TelegramBot bot;
	
	@Autowired
	TioTelegramBotMsgProperties msgProperties;

	@Override
	public boolean webhook(String strUpdate) throws Exception {
		boolean ret = false;
		Update update = BotUtils.parseUpdate(strUpdate);
		Message message = update.message();
		String command = getCommand(update);
		
		switch(command) {
			case CMD_START:
				ret = sendHelloMessage(message);
				break;
			case CMD_IMG:
				ret = sendMessage(message, msgProperties.getImg());
				break;
			case CMD_HELP:
				ret = sendMessage(message, msgProperties.getHelp());
				break;
			case PHOTO:
				analyzePhotos(message);
				break;
			case VAZIO:
				ret = sendMessage(message, msgProperties.getInvalid());
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
		if(msg != null && !VAZIO.equals(msg) && msg.startsWith(CMD))
			return msg.split(SPACE)[0];
		else if(update.message().photo() != null && update.message().photo().length>0)
			return PHOTO;
		else
			return VAZIO;
	}
	
	private String getFrom(Message message) {
		String from = null;
		String userName = message.from().username();
		String firstName =  message.from().firstName();
		String id = message.from().id().toString();
		
		if (firstName != null && !VAZIO.equals(firstName))
			from = firstName;
		else if(userName != null && !VAZIO.equals(userName))
			from = userName;
		else if(id != null && !VAZIO.equals(id))
			from = id;
		
		System.out.println("Update received from: ["+ from + "]");
		
		return from;
	}
	
	private boolean sendHelloMessage (Message message) throws InterruptedException {
		boolean ret = true;
		SendResponse sendResponse;
		String from = getFrom(message);
		StringBuilder msg = new StringBuilder();
		
		if(sendTyping(message)) {
			if(from != null)
				msg.append("Hello, " + from + "! ");
			else
				msg.append("Hello! ");
			
			msg.append(msgProperties.getStart());
			sendResponse = bot.execute(new SendMessage(message.chat().id(), msg.toString()));
			ret = sendResponse.isOk();
		} else {
			ret = false;
		}
		
		return ret;
	}
	
	private boolean sendTyping(Message message) throws InterruptedException {
		BaseResponse baseResponse;
		baseResponse = bot.execute(new SendChatAction(message.chat().id(), ChatAction.typing.name()));

		TimeUnit.SECONDS.sleep(1);
		
		return baseResponse.isOk();
	}
	
	private boolean sendMessage(Message message, String msg) throws InterruptedException {
		boolean ret = true;
		SendResponse sendResponse;
		
		if(sendTyping(message)) {
			sendResponse = bot.execute(new SendMessage(message.chat().id(), msg));
			ret = sendResponse.isOk();
		} else {
			ret = false;
		}
		
		return ret;
	}
	
	private boolean sendReplyMessage(Message message, String msg) throws InterruptedException {
		boolean ret = true;
		SendResponse sendResponse;
		
		if(sendTyping(message)) {
			sendResponse = bot.execute(new SendMessage(message.chat().id(), msg));
			ret = sendResponse.isOk();
		} else {
			ret = false;
		}
		
		return ret;
	}
	
	private void analyzePhotos(Message message) throws InterruptedException {
		for(PhotoSize photo : message.photo()) {
			sendReplyMessage(message.replyToMessage(), "sendReplyMessage " + photo.fileId());
			sendMessage(message, "sendMessage " + photo.fileId());
		}
	}
}
