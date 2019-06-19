package br.com.zukeran.tiojiro.telegrambot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import br.com.zukeran.tiojiro.telegrambot.service.TioTelegramBotService;

@RestController
public class TioTelegramBotController {

	@Autowired
	private TioTelegramBotService tioBotService;
	
	@PostMapping(path = "/webhook", consumes = "application/json")
	public ResponseEntity<String> webhook(@RequestBody String strUpdate) throws Exception {
		boolean ret = tioBotService.webhook(strUpdate);
		
		if(ret)
			return new ResponseEntity<>(HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(path = "/getMe", produces = "application/json")
	public ResponseEntity<String> getMe(){
		String json = new Gson().toJson(tioBotService.getMe());
		return new ResponseEntity<>(json, HttpStatus.OK);
	}
	
}
