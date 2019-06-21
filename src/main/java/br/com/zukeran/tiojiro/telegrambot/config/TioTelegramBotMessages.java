package br.com.zukeran.tiojiro.telegrambot.config;

import java.text.DecimalFormat;

import org.springframework.stereotype.Component;

import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Face;

@Component
public class TioTelegramBotMessages {

	public String start(String from) {
		StringBuilder start = new StringBuilder();
		if(from != null)
			start.append("Hello, " + from + "! ");
		else
			start.append("Hello! ");
	
		start.append("\nTry /help to see the commands.");
		return start.toString();
	}

	public String help() {
		StringBuilder help = new StringBuilder();
		help.append("You can control me by sending these commands:");
		help.append("\n/start - Welcome message.");
		help.append("\n/help - List of commands.");
		help.append("\nPhoto - Send me a photo and I'll analyze.");
		return help.toString();
	}

	public String invalidCmd() {
		return "Unrecognized command. /help";
	}

	public String faceNotFound() {
		return "I can't find any face. Send me another photo.";
	}

	public String faceMessage(Face face) {
		StringBuilder faceMessage = new StringBuilder();
		DecimalFormat df = new DecimalFormat("###.##");
		
		faceMessage.append("*Age*: Between " + face.getAge().getMin() + " and " + face.getAge().getMax());
		faceMessage.append("\n*Score*: " + df.format(100*face.getAge().getScore()) + "%\n");
		faceMessage.append("\n*Gender*: " + face.getGender().getGenderLabel());
		faceMessage.append("\n*Score*: " + df.format(100*face.getGender().getScore()) + "%\n");
		faceMessage.append("\n*Face Location*");
		faceMessage.append("\n*height*: " + face.getFaceLocation().getHeight());
		faceMessage.append("\n*width*: " + face.getFaceLocation().getWidth());
		faceMessage.append("\n*left*: " + face.getFaceLocation().getLeft());
		faceMessage.append("\n*top*: " + face.getFaceLocation().getTop());
		
		return faceMessage.toString();
	}

}
