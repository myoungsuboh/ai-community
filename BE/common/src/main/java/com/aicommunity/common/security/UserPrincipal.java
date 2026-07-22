package com.aicommunity.common.security;

import java.util.UUID;

/**
 * 인증된 사용자 정보. JWT 액세스 토큰에서 복원되어 SecurityContext 에 담긴다.
 * 컨트롤러에서 @AuthenticationPrincipal UserPrincipal 로 주입받는다.
 */
public record UserPrincipal(UUID id, String email, String nickname, Role role) {
}
