package doldol_server.doldol.auth.service;

import java.util.Optional;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        log.info("id: {}", loginId);

        Optional<User> user = userRepository.findByLoginIdAndIsDeletedFalse(loginId);

        if (user.isPresent()) {
            User loginUser = user.get();
            log.info("user id: {}", loginUser.getLoginId());
            return new CustomUserDetails(loginUser);
        }
        return null;
    }
}