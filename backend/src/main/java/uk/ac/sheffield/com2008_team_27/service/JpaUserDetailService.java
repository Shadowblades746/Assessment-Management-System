package uk.ac.sheffield.com2008_team_27.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import uk.ac.sheffield.com2008_team_27.domain.SecurityUser;
import uk.ac.sheffield.com2008_team_27.exceptions.EmailExistsException;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public JpaUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
