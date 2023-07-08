package br.project.com.parkingcontrol.web.user;

import br.project.com.parkingcontrol.businessException.BusinessException;
import br.project.com.parkingcontrol.util.authenticationResponse.AuthenticationResponse;
import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.user.UserRepository;
import br.project.com.parkingcontrol.domain.user.UserServiceImpl;
import br.project.com.parkingcontrol.util.TokenGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class UserController {

    private final UserRepository repository;
    private final UserServiceImpl userServiceImpl;
    private final PasswordEncoder encoder;
    private final TokenGenerator tokenGenerator;
    private static Map<String, Object> userData = new HashMap<>();

    public UserController(UserRepository repository,
                          UserServiceImpl userServiceImpl,
                          PasswordEncoder encoder,
                          TokenGenerator tokenGenerator) {
        this.repository = repository;
        this.userServiceImpl = userServiceImpl;
        this.tokenGenerator = tokenGenerator;
        this.encoder = encoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody User user) {
        try {
            existsByLoginUser(user);
            setUserPassword(user);

            LocalDateTime registrationDate = generateDateTime();
            setRegistrationDate(registrationDate, user);

            User userModel = createUserModel(user, registrationDate);
            saveUser(userModel);

            String token = generateTokenAuthentication(user, userModel);

            defineUserIdInUserData(userModel.getId());
            defineUserLoginInUserData(user.getLogin());
            AuthenticationResponse response = setUserDataInAuthenticationResponse(token);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch(BusinessException err) {
            return BusinessException.handleBusinessException(err, HttpStatus.UNAUTHORIZED.value());
        }
    }

    private String generateTokenAuthentication(User user,
                                               User userModel) {
        return tokenGenerator.generateTokenAuthentication(user.getLogin(), userModel.getId());
    }

    private LocalDateTime generateDateTime() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }

    private void existsByLoginUser(User user) throws BusinessException {
        userServiceImpl.existsByEmail(user.getLogin());
    }

    private User createUserModel(User user, LocalDateTime registrationDate) {
        return new User.Builder()
                .setLogin(user.getLogin())
                .setPassword(user.getPassword())
                .setRegistrationDate(registrationDate)
                .build();
    }

    private void setRegistrationDate(LocalDateTime registrationDate, User user) {
        user.setRegistrationDate(registrationDate);
    }

    private void defineUserLoginInUserData(String login) {
        userData.put("email", login);
    }

    private void defineUserIdInUserData(Integer id) {
        userData.put("id", id);
    }

    private void setUserPassword(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
    }

    private AuthenticationResponse setUserDataInAuthenticationResponse(String token) {
        return new AuthenticationResponse.Builder()
                .setToken(token)
                .setData(userData)
                .build();
    }

    private void saveUser(User userModel) {
        ResponseEntity.ok(repository.save(userModel));
    }
}
