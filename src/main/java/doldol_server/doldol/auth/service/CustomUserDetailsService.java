package doldol_server.doldol.auth.service;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String loginId) {
        Optional<User> user = userRepository.findByLoginId(loginId);

        if(user.isPresent()){
            return new CustomUserDetails(user.get());
        }
        return null;
    }
}
