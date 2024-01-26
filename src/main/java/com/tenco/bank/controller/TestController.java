package com.tenco.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // view 만 불러옴 -> data 리턴 가능(ResponseData)
//@RestController 는 data 불러옴
@RequestMapping("/test") //대문
public class TestController {
	
	// 주소 설계
	// http://localhost:80/test/main
	@GetMapping("/main")
	public String mainPage() {
		// 인증 검사(먼저)
		// 유효성 검사
		// 뷰 리졸브 -> 해당하는 파일 찾아 (data)
	    // prefix: /WEB-INF/view/
		// layout/main
	    // suffix: .jsp
		return "layout/main";
	}
}