package io.github.alancs7.twitter.config;

import io.github.alancs7.twitter.entities.Role;
import io.github.alancs7.twitter.entities.User;
import io.github.alancs7.twitter.repository.RoleRepository;
import io.github.alancs7.twitter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class AdminUserConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        userRepository.findByUsername("admin")
                .ifPresentOrElse(
                        user -> System.out.println("User already exists"),
                        () -> {
                            var user = new User();
                            user.setUsername("admin");
                            user.setPassword(passwordEncoder.encode("123"));
                            user.setRoles(Set.of(roleAdmin));

                            userRepository.save(user);
                        }
                );
    }
}
