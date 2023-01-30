package com.example.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.config.TwilioConfig;
import com.example.dto.OtpStatus;
import com.example.dto.PasswordResetRequestDto;
import com.example.dto.PasswordResetResponseDto;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import reactor.core.publisher.Mono;

@Service
public class TwilioService {
	@Autowired
	private TwilioConfig twilioConfig;

	
	Map<String,String> otpMap=new HashMap<>();
	
	public Mono<PasswordResetResponseDto> sendOTPForPasswordReset(PasswordResetRequestDto passwordResetRequestDto) {
	PasswordResetResponseDto passwordResetResponseDto=null;
	try {
	PhoneNumber to=new PhoneNumber(passwordResetRequestDto.getNumber());
		PhoneNumber from =new PhoneNumber(twilioConfig.getTrialNumber());
		String otp=generateOTP();
		String otpMessage="Dear Customer ,your otp is ##" + otp + "## use this passcode to complte your transaction .Thank you.";
		Message message=Message
				.creator(to,from,
						otpMessage)
				.create();
		
		otpMap.put(passwordResetRequestDto.getUserName(),otp);
		passwordResetResponseDto =new PasswordResetResponseDto(OtpStatus.DELIVERED,otpMessage);

		
	}catch (Exception ex) {
		
		passwordResetResponseDto =new PasswordResetResponseDto(OtpStatus.FAILED,ex.getMessage());
	}
	
	return Mono.just(passwordResetResponseDto);
	}
	
	public Mono<String> ValidateOTP(String userInputOtp,String userName){
	if(userInputOtp.equals(otpMap.get(userName))) {
		return Mono.just("Valid OTP Please");
	}else {
		return Mono.error(new IllegalArgumentException("Invalid"));
	}
	}
	
	private String generateOTP() {
		return new DecimalFormat("000000")
				.format(new Random().nextInt(999999));
	
}
}
