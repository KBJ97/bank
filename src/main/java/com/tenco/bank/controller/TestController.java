package com.tenco.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.handler.exception.CustomPageException;
import com.tenco.bank.handler.exception.CustomRestfulException;

@Controller // view 만 불러옴 -> data 리턴 가능(ResponseData)
//@RestController 는 data 불러옴
@RequestMapping("/test") //대문
public class TestController {
	
	// 주소 설계
	// http://localhost:80/test/main
	@GetMapping("/main")
	public void mainPage() {
		// 인증 검사(먼저)
		// 유효성 검사
		// 뷰 리졸브 -> 해당하는 파일 찾아 (data)
	    // prefix: /WEB-INF/view/
		// layout/main
	    // suffix: .jsp
		
		//예외 발생
		throw new CustomRestfulException("페이지가 없네요", HttpStatus.NOT_FOUND);
		// return "layout/main";
	}
}