package com.example.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.dto.PasswordResetRequestDto;
import com.example.service.TwilioService;
import com.twilio.http.Response;

import reactor.core.publisher.Mono;

@Component
public class TwilioOTPHandler {

	@Autowired
	private TwilioService twilioService;

	public Mono<ServerResponse> sendOTP(ServerRequest serverRequest) {

		return serverRequest.bodyToMono(PasswordResetRequestDto.class)
				.flatMap(dto -> twilioService.sendOTPForPasswordReset(dto))
				.flatMap(dto -> ServerResponse.status(HttpStatus.OK)
						.body(BodyInserters.fromValue(dto)));
	}
	
	public Mono<ServerResponse> validateOTP(ServerRequest serverRequest){
		return serverRequest.bodyToMono(PasswordResetRequestDto.class)
				.flatMap(dto -> twilioService.ValidateOTP(dto.getOtp(),dto.getUserName()))
				.flatMap(dto -> ServerResponse.status(HttpStatus.OK)
						.bodyValue(dto));
	
	}
}
