package br.com.zukeran.tiojiro.telegrambot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("br.com.zukeran.tiojiro.telegrambot.msg")
public class TioTelegramBotMsgProperties {
	private String start;
	private String help;
	
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getHelp() {
		return help;
	}
	public void setHelp(String help) {
		this.help = help;
	}

}
