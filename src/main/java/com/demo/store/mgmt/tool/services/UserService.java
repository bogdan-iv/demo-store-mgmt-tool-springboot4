package com.demo.store.mgmt.tool.services;
import com.demo.store.mgmt.tool.models.Authority;
import com.demo.store.mgmt.tool.models.User;
import com.demo.store.mgmt.tool.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert the custom User entity to Spring Security's UserDetails object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Password is already hashed in the DB
                .authorities(user.getAuthorities().stream()
                        .map(Authority::getAuthority) // Maps Authority object to "ROLE_ADMIN" String
                        .toArray(String[]::new))
                .disabled(!user.isEnabled())
                .build();
    }
}
