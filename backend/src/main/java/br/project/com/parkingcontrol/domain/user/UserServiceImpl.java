package br.project.com.parkingcontrol.domain.user;

import br.project.com.parkingcontrol.businessException.BusinessException;
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

    private void verifyUserEmpty(Optional<User> user, String username) throws UsernameNotFoundException {
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Usuário [" + username + "] não encontrado");
        }
    }

    public void existsByEmail(String login) throws BusinessException {
        validationBookName(login);
    }

    private void validationBookName(String login) throws BusinessException {
        if(repository.existsByLogin(login)) {
            throw new BusinessException("The user: " + login + " is already in use");
        }
    }
}
