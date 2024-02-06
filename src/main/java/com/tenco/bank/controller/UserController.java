package com.tenco.bank.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.tenco.bank.dto.KakaoProfile;
import com.tenco.bank.dto.NaverProfile;
import com.tenco.bank.dto.NaverTokenDto;
import com.tenco.bank.dto.OAuthToken;
import com.tenco.bank.dto.SignInFormDto;
import com.tenco.bank.dto.SignUpFormDto;
import com.tenco.bank.handler.exception.CustomRestfulException;
import com.tenco.bank.repository.entity.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired // DI 처리
	private UserService userService;

	@Autowired
	private HttpSession httpSession;

	/**
	 * 회원 가입 페이지 요청
	 * 
	 * @return signUp.jsp 파일 리턴
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {
		return "user/signUp";
	}

	/**
	 * 회원 가입 요청
	 * 
	 * @param dto
	 * @return 로그인 페이지(/sign-in)
	 */
	@PostMapping("/sign-up")
	public String signProc(SignUpFormDto dto) {

		// 1. 인증검사 x
		// 2. 유효성 검사
		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new CustomRestfulException("fullname을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		// 파일 업로드
		MultipartFile file = dto.getCustomFile();
		System.out.println("file" + file.getOriginalFilename());
		if (file.isEmpty() == false) {
			// 사용자가 이미지를 업루드 했다면 기능 구현
			// 파일 사이즈 체크
			// 20MB
			if (file.getSize() > Define.MAX_FILE_SIZE) {
				throw new CustomRestfulException("파일 크기는 20MB 이상 클 수 없습니다", HttpStatus.BAD_REQUEST);
			}

			// 서버 컴퓨터에 파일 넣을 디렉토리가 있는지 검사
			String saveDirectory = Define.UPLOAD_FILE_DERECTORY;
			// 폴더가 없다면 오류 발생(파일 생성시)
			File dir = new File(saveDirectory);
			if (dir.exists() == false) {
				dir.mkdir(); // 폴더가 없스면 폴더 생성
			}

			// 파일 이름 (중복 처리 예방)
			UUID uuid = UUID.randomUUID();
			String fileName = uuid + "_" + file.getOriginalFilename();
			System.out.println("fileName : " + fileName);
			// C:\\wok_spring\\upload\ab.png
			String uploadPath = Define.UPLOAD_FILE_DERECTORY + File.separator + fileName;
			File destination = new File(uploadPath);

			try {
				file.transferTo(destination);
			} catch (IllegalStateException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 객체 상태 변경
			dto.setOriginFileName(file.getOriginalFilename());
			dto.setUploadFileName(fileName); // 실제 서버에 접근할 수 있는 fileName : UUID
		}

		userService.createUser(dto);
		return "redirect:/user/sign-in";
	}

	/**
	 * 로그인 페이지 요청
	 * 
	 * @return
	 */
	@GetMapping("/sign-in")
	public String signInPage() {
		return "user/signIn";
	}

	/**
	 * 로그인 요청 처리
	 * 
	 * @param SignInFormDto
	 * @return account/list.jsp
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDto dto) {

		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password을 입력하시오", HttpStatus.BAD_REQUEST);
		}

		User user = userService.readUser(dto);
		httpSession.setAttribute(Define.PRINCIPAL, user);
		return "redirect:/account/list";
	}

	// 로그아웃 기능 만들기
	@GetMapping("/logout")
	public String logout() {
		httpSession.invalidate();
		return "redirect:/user/sign-in";
	}

	// 카카오 로그인 API
	// http://localhost:80/user/kakao-callback?code="xxxx"
	@GetMapping("/kakao-callback")
	public String kakaoCallback(@RequestParam String code) {

		// POST 방식, Header 구성, body 구성

		RestTemplate rt1 = new RestTemplate();
		// 헤더 구성
		HttpHeaders headers1 = new HttpHeaders();
		headers1.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// 바디 구성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "2bc03148050de8d43ba4b5f8c2656bc2");
		params.add("redirect_uri", "http://localhost:80/user/kakao-callback");
		params.add("code", code);

		// 헤더 + 바디 결합
		HttpEntity<MultiValueMap<String, String>> reqMsg = new HttpEntity<>(params, headers1);

		ResponseEntity<OAuthToken> response = rt1.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
				reqMsg, OAuthToken.class);

		// 다시 요청 -- 인증 토큰 -- 사용자 정보 요청
		RestTemplate rt2 = new RestTemplate();
		// 헤더
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + response.getBody().getAccess_token());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// 바디 x
		// 결합 --> 요청
		HttpEntity<MultiValueMap<String, String>> kakaoInfo = new HttpEntity<>(headers2);
		ResponseEntity<KakaoProfile> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
				kakaoInfo, KakaoProfile.class);

		System.out.println(response2.getBody());

		KakaoProfile kakaoProfile = response2.getBody();

		// 최초 사용자 판단 여부 -- 사용자 username 존재 여부 확인
		// 기존 사용자와 카카오 이름 중복
		SignUpFormDto dto = SignUpFormDto.builder().username("OAuth_" + kakaoProfile.getProperties().getNickname())
				.fullname("Kakao").password("asd1234").build();

		User oldUser = userService.readUserByUserName(dto.getUsername());
		if (oldUser == null) {
			userService.createUser(dto);
			//
			oldUser = new User();
			oldUser.setUsername(dto.getUsername());
			oldUser.setFullname(dto.getFullname());
		}
		oldUser.setPassword(null);
		// 로그인 처리
		httpSession.setAttribute(Define.PRINCIPAL, oldUser);

		return "redirect:/account/list";
	}

	// 네이버

	@GetMapping("/naver-callback")
	public String getNaverUserInfo(@RequestParam("code") String code, @RequestParam String state) {

		log.info(code);
		log.info(state);
		RestTemplate restTemplate = new RestTemplate();

		URI uri = UriComponentsBuilder.fromUriString("https://nid.naver.com").path("/oauth2.0/token")
				.queryParam("grant_type", "authorization_code")
				.queryParam("client_id", "Uw9Cl4EnJzZ289hXdtFL")
				.queryParam("client_secret", "vdDSamvRvE")
				.queryParam("code", code)
				.queryParam("state", state).encode()
				.build()
				.toUri();
		
		ResponseEntity<NaverTokenDto> rt3 = restTemplate.getForEntity(uri, NaverTokenDto.class);

		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + rt3.getBody().getAccessToken());
		HttpEntity<Object> entity = new HttpEntity<>(headers2);
		ResponseEntity<NaverProfile> response2 = restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET,
				entity, NaverProfile.class);
		
		NaverProfile naverProfile = response2.getBody();
		
		SignUpFormDto dto = SignUpFormDto.builder().username("OAuth_" + naverProfile.getResponse().getName())
				.fullname("Naver").password("asd1234").build();

		User oldUser = userService.readUserByUserName(dto.getUsername());
		if (oldUser == null) {
			userService.createUser(dto);
			//
			oldUser = new User();
			oldUser.setUsername(dto.getUsername());
			oldUser.setFullname(dto.getFullname());
		}
		oldUser.setPassword(null);
		// 로그인 처리
		httpSession.setAttribute(Define.PRINCIPAL, oldUser);
		
		return "redirect:/account/list";
	}

	// 정보 변경 페이지 요청 처리
	@GetMapping("/userUpdate")
	public String userInfo() {

		return "user/userUpdate";
	}
}