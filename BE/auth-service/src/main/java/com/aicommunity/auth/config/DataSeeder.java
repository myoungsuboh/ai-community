package com.aicommunity.auth.config;

import com.aicommunity.auth.domain.User;
import com.aicommunity.auth.domain.UserRepository;
import com.aicommunity.common.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 개발용 기본 계정 시드 (멱등적). 큐레이터/관리자 로그인용.
 * 테스트 프로필에서는 실행하지 않는다.
 */
@Configuration
@Profile("!test")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public ApplicationRunner seedUsers(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            seed(users, encoder, "curator@ai.community", "curator1234", "큐레이터", Role.CURATOR);
            seed(users, encoder, "admin@ai.community", "admin1234", "관리자", Role.ADMIN);
        };
    }

    private void seed(UserRepository users, PasswordEncoder encoder,
                      String email, String rawPw, String nickname, Role role) {
        if (users.existsByEmail(email)) {
            return;
        }
        users.save(User.create(email, encoder.encode(rawPw), nickname, role));
        log.info("Seeded {} account: {}", role, email);
    }
}
