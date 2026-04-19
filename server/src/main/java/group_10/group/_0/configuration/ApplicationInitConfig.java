package group_10.group._0.configuration;

import group_10.group._0.Enum.ROLE;
import group_10.group._0.entity.Users;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UsersRepository repository){
        return args -> {
            if (repository.findByEmail("admin@gmail.com").isEmpty()){
                Users user = Users.builder()
                        .email("admin@gmail.com")
                        .matKhau(passwordEncoder.encode("Admin@123456789"))
                        .role(ROLE.ADMIN)
                        .build();

                repository.save(user);
                log.warn("admin user has been created with default password: Admin@123456789, please change it");
            }
        };
    }
}