package br.project.com.parkingcontrol.domain.user;

import br.project.com.parkingcontrol.util.BusinessException;
import br.project.com.parkingcontrol.data.UserDetail;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> user = repository.findByLogin(login);

        verifyUserEmpty(user, login);

        return new UserDetail(user);
    }

    @Transactional
    public User saveUser(User user) {
        return repository.save(user);
    }

    private void verifyUserEmpty(Optional<User> user, String username) throws UsernameNotFoundException {
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User [" + username + "] is not found");
        }
    }

    public void existsByEmail(String login) throws BusinessException {
        validationLogin(login);
    }

    private void validationLogin(String login) throws BusinessException {
        if(repository.existsByLogin(login)) {
            throw new BusinessException("The email: " + login + " is already in use");
        }
    }

    public User getUserModel(Integer userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
