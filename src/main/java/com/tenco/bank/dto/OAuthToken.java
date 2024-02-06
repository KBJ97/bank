package com.tenco.bank.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
// json 형식의 코딩 컨벤션의 스네이크 케이스를 카멜 노테이션으로 변경하기
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OAuthToken {
	private String access_token;
	private String token_type;
	private String refresh_token;
	private String scope;
	private Integer expires_in;
	
}
