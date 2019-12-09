package br.com.zukeran.tiojiro.telegrambot.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectFacesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectedFaces;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Face;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.pengrad.telegrambot.response.SendResponse;

import br.com.zukeran.tiojiro.telegrambot.config.TioTelegramBotMessages;
import br.com.zukeran.tiojiro.telegrambot.config.TioTelegramBotProperties;

@SuppressWarnings("deprecation")
@Service
public class TioTelegramBotServiceImpl implements TioTelegramBotService{
	
	private static final int ZERO = 0;
	private static final int ONE = 1;
	private static final String VAZIO = "";
	private static final String SPACE = " ";
	private static final String CMD = "/";
	private static final String SLASH = "/";
	private static final String CMD_START = "/start";
	private static final String CMD_HELP = "/help";
	private static final String PHOTO = "PHOTO";
	private static final String AUDIO = "AUDIO";
	private static final String DATE_VERSION = "2018-03-19";
	private static final String TELEGRAM_BOT_URL = "https://api.telegram.org/file/bot";
	
	@Autowired
	TelegramBot bot;
	
	@Autowired
	TioTelegramBotMessages listMessages;
	
	@Autowired
	TioTelegramBotProperties botProperties;

	@Override
	public boolean webhook(String strUpdate) throws Exception {
		boolean ret = false;
		Update update = BotUtils.parseUpdate(strUpdate);
		Message message = update.message();
		String command = getCommand(update);
		String from = getFrom(message);
		
		System.out.println("Update received from: ["+ from + "]");
		System.out.println(update.toString());
		System.out.println("Command: [" + command + "]");
		
		switch(command) {
			case CMD_START:
				ret = sendMessage(message, listMessages.start(from));
				break;
			case CMD_HELP:
				ret = sendMessage(message, listMessages.help());
				break;
			case PHOTO:
				ret = analyzePhotos(message);
				break;
			case AUDIO:
				ret = speechToText(message);
				break;
			case VAZIO:
				break;
			default:
				ret = sendMessage(message, listMessages.invalidCmd());
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
			return msg.split(SPACE)[ZERO];
		else if(update.message().photo() != null && update.message().photo().length>ZERO)
			return PHOTO;
		else if(update.message().audio() != null && update.message().audio().fileSize()>ZERO)
			return AUDIO;
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
		return from;
	}
	
	private boolean sendTyping(Message message) throws InterruptedException {
		BaseResponse baseResponse;
		baseResponse = bot.execute(new SendChatAction(message.chat().id(), ChatAction.typing.name()));

		TimeUnit.SECONDS.sleep(ONE);
		
		return baseResponse.isOk();
	}
	
	private boolean sendMessage(Message message, String msg) throws InterruptedException {
		boolean ret = true;
		SendResponse sendResponse;
		SendMessage sendMsg = new SendMessage(message.chat().id(), msg);
		sendMsg.parseMode(ParseMode.Markdown);
		
		if(sendTyping(message)) {
			sendResponse = bot.execute(sendMsg);
			ret = sendResponse.isOk();
		} else {
			ret = false;
		}
		return ret;
	}
	
	private File getFile(String fileId) throws Exception {
		GetFile request = new GetFile(fileId);
		GetFileResponse getFileResponse = bot.execute(request);
		File file = getFileResponse.file();
		return file;
	}
	
	private boolean analyzePhotos(Message message){
		boolean ret = false;
		try {
			PhotoSize photo = message.photo()[message.photo().length-ONE];
			File file = getFile(photo.fileId());
					
			IamOptions options = new IamOptions.Builder()
					  .apiKey(botProperties.getIbmAiDetectFace())
					  .build();
			
			VisualRecognition visualRecognition = new VisualRecognition(DATE_VERSION, options);
			
			DetectFacesOptions detectFacesOptions = new DetectFacesOptions.Builder()
					.url(TELEGRAM_BOT_URL + botProperties.getToken() + SLASH + file.filePath())
					.build();
			
			DetectedFaces result = visualRecognition.detectFaces(detectFacesOptions).execute();
			System.out.println(result);
			
			if(result != null && result.getImages().get(ZERO).getFaces().size()>ZERO) {
				for(Face face : result.getImages().get(ZERO).getFaces()) {
						ret = sendMessage(message, listMessages.faceMessage(face));
				}
			} else {
				ret = sendMessage(message, listMessages.faceNotFound());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ret;
		}
					
		return ret;
	}
	
	private boolean speechToText(Message message) {
		boolean ret = false;
		try {
			File file = getFile(message.audio().fileId());
			
			IamAuthenticator authenticator = new IamAuthenticator(botProperties.getIbmSpeechText());
			SpeechToText speechToText = new SpeechToText(authenticator);
			speechToText.setServiceUrl("https://gateway-lon.watsonplatform.net/speech-to-text/api");
			
			RecognizeOptions recognizeOptions = new RecognizeOptions.Builder()
			.audio(new FileInputStream(TELEGRAM_BOT_URL + botProperties.getToken() + SLASH + file.filePath()))
			.contentType("audio/mp3")
			.build();
		  	
			SpeechRecognitionResults speechRecognitionResults = speechToText.recognize(recognizeOptions).execute().getResult();
		  	
			if(speechRecognitionResults != null && speechRecognitionResults.toString().length()>ZERO) {
				ret = sendMessage(message, speechRecognitionResults.toString());			
			} else {
				ret = sendMessage(message, listMessages.audioNotFound());
			}
			
			System.out.println(speechRecognitionResults);
		  } catch (Exception e) {
		    e.printStackTrace();
		    return ret;
		  }
		
		return ret;
	}
}
