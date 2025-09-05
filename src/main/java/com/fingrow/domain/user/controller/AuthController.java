package com.fingrow.domain.user.controller;

import com.fingrow.domain.user.entity.User;
import com.fingrow.domain.user.repository.UserRepository;
import com.fingrow.domain.user.service.KakaoOAuthService;
import com.fingrow.global.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 인증 관련 API 컨트롤러
 * 카카오 OAuth 로그인, 사용자 정보 조회, 로그아웃 기능을 제공합니다.
 */
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final KakaoOAuthService kakaoOAuthService;

    /**
     * 카카오 로그인 처리 (POST 방식)
     * 클라이언트에서 JSON 형태로 카카오 인증 코드를 전달받아 로그인을 처리합니다.
     * 
     * @param request 카카오 인증 코드가 포함된 요청 데이터 (JSON 형태)
     * @return JWT 액세스 토큰과 사용자 정보를 포함한 응답
     */
    @Operation(summary = "카카오 로그인 (POST)", description = "카카오 인증 코드를 JSON으로 받아 액세스 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"user\": {\"id\": 1, \"email\": \"user@example.com\", \"name\": \"홍길동\"}}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 코드 또는 로그인 실패")
    })
    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(
            @Parameter(description = "카카오 인증 코드", required = true,
                    schema = @Schema(example = "{\"code\": \"authorization_code_from_kakao\"}"))
            @RequestBody Map<String, String> request) {
        try {
            String authorizationCode = request.get("code");
            
            // 인증 코드 유효성 검증
            if (authorizationCode == null || authorizationCode.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Authorization code is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // 카카오 OAuth 인증 처리
            User user = kakaoOAuthService.processKakaoLogin(authorizationCode);
            
            // JWT 액세스 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
            
            // 응답 데이터 구성
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
            log.error("Kakao login failed", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "로그인에 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }



    /**
     * 현재 로그인된 사용자 정보 조회
     * JWT 토큰을 통해 인증된 사용자의 상세 정보를 조회합니다.
     * 
     * @param authentication Spring Security에서 제공하는 인증 정보
     * @return 사용자 상세 정보 (id, email, name, profileImage, provider, role)
     */
    @Operation(summary = "현재 사용자 정보 조회", description = "JWT 토큰을 통해 인증된 현재 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"id\": 1, \"email\": \"user@example.com\", \"name\": \"홍길동\", \"profileImage\": \"https://example.com/profile.jpg\", \"provider\": \"KAKAO\", \"role\": \"USER\"}"))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @Parameter(hidden = true) Authentication authentication) {
        
        // 인증 상태 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // JWT 토큰에서 사용자 ID 추출
        String userId = (String) authentication.getPrincipal();
        
        // 사용자 정보 조회 및 응답 구성
        return userRepository.findById(Long.parseLong(userId))
                .map(user -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId());
                    userInfo.put("email", user.getEmail());
                    userInfo.put("name", user.getName());
                    userInfo.put("profileImage", user.getProfileImage());
                    userInfo.put("provider", user.getProvider());
                    userInfo.put("role", user.getRole());
                    
                    return ResponseEntity.ok(userInfo);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * 카카오 OAuth 콜백에서 인가 코드만 반환 (GET 방식)
     * 실제 로그인 처리는 하지 않고 인가 코드만 클라이언트에 반환합니다.
     * 
     * @param code 카카오에서 전달받은 인증 코드
     * @param error 카카오에서 전달받은 에러 코드 (선택적)
     * @return 인가 코드만 포함한 응답
     */
    @Operation(summary = "카카오 인가 코드 반환", description = "카카오 OAuth 콜백에서 인가 코드만 반환합니다 (로그인 처리 없음).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인가 코드 반환 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": \"authorization_code_from_kakao\"}"))),
            @ApiResponse(responseCode = "400", description = "카카오 인증 오류")
    })
    @GetMapping("/kakao/code")
    public ResponseEntity<?> getKakaoAuthCode(
            @Parameter(description = "카카오 인증 코드", required = true)
            @RequestParam("code") String code,
            @Parameter(description = "에러 코드 (선택사항)")
            @RequestParam(value = "error", required = false) String error) {
        
        // 카카오 OAuth 에러 처리
        if (error != null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "카카오 로그인이 취소되었습니다: " + error);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            log.info("Returning Kakao authorization code: {}", code.substring(0, Math.min(code.length(), 10)) + "...");
            
            // 인가 코드만 반환 (실제 로그인 처리는 하지 않음)
            Map<String, String> response = new HashMap<>();
            response.put("code", code);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error returning Kakao authorization code", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "인가 코드 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 사용자 로그아웃 처리
     * 현재는 클라이언트 측에서 토큰 삭제를 통한 로그아웃을 지원합니다.
     * JWT 토큰은 서버에서 상태를 관리하지 않으므로 클라이언트에서 토큰을 제거하면 됩니다.
     * 
     * @return 로그아웃 성공 메시지
     */
    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Successfully logged out\"}")))   
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT는 stateless하므로 서버에서 별도 처리 없이 성공 응답만 반환
        // 실제 로그아웃은 클라이언트에서 토큰을 삭제함으로써 완료됨
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully logged out");
        
        return ResponseEntity.ok(response);
    }
}