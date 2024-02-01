package com.tenco.bank.dto;

import lombok.Data;

@Data
public class AccountSaveFormDto {

	private String number;
	private String password;
	private Long balance;
	
	// user_id는 session 에서 들고올 예정
}
