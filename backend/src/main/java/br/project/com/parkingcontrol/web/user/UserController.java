package br.project.com.parkingcontrol.web.user;

import br.project.com.parkingcontrol.util.BusinessException;
import br.project.com.parkingcontrol.util.AuthenticationResponse;
import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.user.UserRepository;
import br.project.com.parkingcontrol.domain.user.UserServiceImpl;
import br.project.com.parkingcontrol.util.ResponseData;
import br.project.com.parkingcontrol.util.TokenGenerator;
import jakarta.validation.Valid;
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
            existsByLoginUser(user.getLogin());
            setUserPassword(user);

            LocalDateTime registrationDate = generateDateTime();
            setRegistrationDate(registrationDate, user);

            User userModel = createUserModel(user, registrationDate);
            saveUser(userModel);

            String token = generateTokenAuthentication(user, userModel);

            defineUserIdInUserData(userModel.getId());
            defineUserLoginInUserData(user.getLogin());
            AuthenticationResponse response = setUserDataInAuthenticationResponse(token);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(response));
        } catch(BusinessException err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));
        }
    }

    @PatchMapping("/edit_price")
    public ResponseEntity<Object> editUser(@RequestBody @Valid User user,
                                           @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            User userModel = getUserModel(userId);

            User updateUser = createUpdateUserModel(userId, user, userModel);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(updateUser));
        } catch(Exception err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));
        }
    }

    private String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        return tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
    }

    private Integer extractUserIdFromToken(String token) {
        return tokenGenerator.extractUserIdFromToken(token);
    }

    private User getUserModel(Integer userId) {
        return userServiceImpl.getUserModel(userId);
    }

    private String generateTokenAuthentication(User user,
                                               User userModel) {
        return tokenGenerator.generateTokenAuthentication(user.getLogin(), userModel.getId());
    }

    private LocalDateTime generateDateTime() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }

    private void existsByLoginUser(String login) throws BusinessException {
        userServiceImpl.existsByEmail(login);
    }

    private User createUpdateUserModel(Integer userId,
                                       User user,
                                       User userModel) {
        return new User.Builder()
                .setId(userId)
                .setLogin(userModel.getLogin())
                .setPricePerHour(user.getPricePerHour())
                .setPassword(userModel.getPassword())
                .setRegistrationDate(userModel.getRegistrationDate())
                .build();
    }

    private User createUserModel(User user,
                                 LocalDateTime registrationDate) {
        return new User.Builder()
                .setLogin(user.getLogin())
                .setPassword(user.getPassword())
                .setRegistrationDate(registrationDate)
                .build();
    }

    private void setRegistrationDate(LocalDateTime registrationDate,
                                     User user) {
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

    private void saveUser(User userModel) {
        ResponseEntity.ok(userServiceImpl.saveUser(userModel));
    }

    private AuthenticationResponse setUserDataInAuthenticationResponse(String token) {
        return new AuthenticationResponse.Builder()
                .setToken(token)
                .setData(userData)
                .build();
    }
}