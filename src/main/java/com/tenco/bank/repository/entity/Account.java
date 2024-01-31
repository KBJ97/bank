package com.tenco.bank.repository.entity;

import java.security.Timestamp;
import java.text.DecimalFormat;

import org.springframework.http.HttpStatus;

import com.tenco.bank.handler.exception.CustomPageException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
	
	private Integer id;
	private String number;
	private String password;
	private Long balance;
	private Integer userId;	
	private Timestamp createdAt;
	
	// 출금 기능
	public void withdraw(Long amount) {
		// 방어적 코드 작성 - todo (내가 가진 잔액보다 큰 값 X, -값 X)
		this.balance -= amount;
	}
	// 입금 기능
	public void deposit(Long amount) {
		this.balance += amount;
	}
	// todo
	// 패스워드 체크
	public void checkPassword(String password) {
		if(this.password.equals(password) == false) {
			throw new CustomPageException("계좌 비밀번호가 틀렸습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	// 잔액 여부 확인 기능
	public void checkBalance(Long amount) {
		if(this.balance < amount) {
			throw new CustomPageException("출금 잔액이 부족합니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	// 계좌 소유자 확인 기능
	public void checkOwner(Integer principalId) {
		if(this.userId != principalId) {
			throw new CustomPageException("계좌 소유자가 아닙니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	// 포메터
	public String formatBalance() {
		DecimalFormat df = new DecimalFormat("#,###");
		String formaterNumber = df.format(balance);
		return formaterNumber + "원";
	}
}