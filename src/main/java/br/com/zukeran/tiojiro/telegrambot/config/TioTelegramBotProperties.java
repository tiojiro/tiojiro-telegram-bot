package br.com.zukeran.tiojiro.telegrambot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("br.com.zukeran.tiojiro.telegrambot")
public class TioTelegramBotProperties {
	private String token;
	private String ibmAiDetectFace;
	private String ibmSpeechText;
	
	public String getIbmSpeechText() {
		return ibmSpeechText;
	}

	public void setIbmSpeechText(String ibmSpeechText) {
		this.ibmSpeechText = ibmSpeechText;
	}

	public String getIbmAiDetectFace() {
		return ibmAiDetectFace;
	}

	public void setIbmAiDetectFace(String ibmAiDetectFace) {
		this.ibmAiDetectFace = ibmAiDetectFace;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
