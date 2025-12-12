package es.onebox.bepass.users;

import es.onebox.bepass.users.dto.CreateUserDTO;
import es.onebox.bepass.users.dto.UserResponseDTO;
import es.onebox.bepass.users.dto.ValidateUserDTO;
import es.onebox.bepass.users.dto.ValidateUserResponseDTO;
import es.onebox.common.config.ApiConfig;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(ApiConfig.BepassApiConfig.BASE_URL + "/users")
@RestController
public class BepassUsersController {

    private final BepassUsersService userService;

    public BepassUsersController(BepassUsersService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@Valid @RequestBody CreateUserDTO user) {
       return userService.createUser(user);
    }

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.CREATED)
    public ValidateUserResponseDTO validate(@Valid @RequestBody ValidateUserDTO user) {
        return userService.validateUser(user);
    }

}
