package com.fingrow.domain.user.controller;

import com.fingrow.domain.user.entity.User;
import com.fingrow.domain.user.service.KakaoOAuthService;
import com.fingrow.global.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "OAuth 콜백 API", description = "OAuth 콜백 처리 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthCallbackController {

    private final KakaoOAuthService kakaoOAuthService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "카카오 OAuth 콜백", description = "카카오에서 리다이렉트된 인가 코드를 처리하여 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 코드 또는 로그인 실패")
    })
    @GetMapping("/callback")
    public ResponseEntity<?> kakaoCallback(
            @Parameter(description = "카카오 인증 코드", required = true)
            @RequestParam("code") String code,
            @Parameter(description = "에러 코드 (선택사항)")
            @RequestParam(value = "error", required = false) String error) {
        
        if (error != null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "카카오 로그인이 취소되었습니다: " + error);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            log.info("Kakao callback received with code: {}", code.substring(0, Math.min(code.length(), 10)) + "...");
            
            // 카카오 OAuth 처리
            User user = kakaoOAuthService.processKakaoLogin(code);
            
            // 액세스 토큰만 생성
            String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
            
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail() != null ? user.getEmail() : "",
                    "name", user.getName(),
                    "profileImage", user.getProfileImage() != null ? user.getProfileImage() : "",
                    "provider", user.getProvider(),
                    "role", user.getRole()
            ));
            
            log.info("Kakao login successful for user: {}", user.getName());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Kakao callback failed", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "로그인에 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}