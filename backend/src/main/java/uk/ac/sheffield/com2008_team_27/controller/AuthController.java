package uk.ac.sheffield.com2008_team_27.controller;

import jakarta.validation.Valid;
import uk.ac.sheffield.com2008_team_27.dto.TokenDTO;
import uk.ac.sheffield.com2008_team_27.dto.UserDTO;
import uk.ac.sheffield.com2008_team_27.dto.UserSignupDTO;
import uk.ac.sheffield.com2008_team_27.service.UserService;
import uk.ac.sheffield.com2008_team_27.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    public AuthController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenDTO> signUp(@RequestBody @Valid UserSignupDTO userSignupDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.signupNewUser(userSignupDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(Authentication authentication) {
        TokenDTO tokenDto = userService.loginUser(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(tokenDto);
    }

    @GetMapping("/authorities")
    public Map<String, Object> getPrincipalInfo(JwtAuthenticationToken principal) {
        Collection<String> authorities = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Map<String,Object> info = new HashMap<>();
        info.put("name", principal.getName());
        info.put("authorities", authorities);
        info.put("tokenAttributes", principal.getTokenAttributes());
        return info;
    }
}

