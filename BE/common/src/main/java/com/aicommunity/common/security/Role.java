package com.aicommunity.common.security;

/** 사용자 권한 등급. 인증 서비스가 발급하는 JWT 의 role 클레임에 담긴다. */
public enum Role {
    MEMBER,
    CURATOR,
    ADMIN
}
