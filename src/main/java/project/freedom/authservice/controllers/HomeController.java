package project.freedom.authservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import project.freedom.authservice.entity.LoginRequest;
import project.freedom.authservice.entity.User;
import project.freedom.authservice.service.HomeService;

import java.util.Date;

@Tag(name = "Home Controller", description = "No login needed")

@RestController
public class HomeController {
    @Autowired
    private HomeService userService;
    @Autowired
    SecurityContextRepository securityContextRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/health")
    public String health() {
        return "updated: Health is green as on  " + new Date();
    }

    @PostMapping("/swagger-login")
    @Operation(summary = "Login endpoint for Swagger UI",
            description = "Authenticates the user and returns the JSESSIONID cookie.")

    public ResponseEntity<String> swaggerLogin(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        try {
            // 1. Authenticate the user using the AuthenticationManager (same as your /login)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // 2. Store the Authentication in the SecurityContext (very important for session)
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get the request from the RequestContextHolder
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            securityContextRepository.saveContext(context, request, response);

            // 3. Extract the JSESSIONID cookie from the response
            String jsessionId = response.getHeader("Set-Cookie");
            if (jsessionId != null && jsessionId.contains("JSESSIONID=")) {
                jsessionId = jsessionId.substring(jsessionId.indexOf("JSESSIONID="));
                jsessionId = jsessionId.split(";")[0];
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("JSESSIONID not found in response");
            }

            // 4. Return the JSESSIONID
            return ResponseEntity.ok(jsessionId); // Return only the cookie value

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed: " + e.getMessage());
        }
    }


    @PostMapping("/signup")
    //@RequestParam User user won't work — you should use @RequestBody for JSON payloads.
    public User signup(@RequestBody User user) {
        return userService.save(user);
    }

    @Operation(summary = "Deprecreated for swagger use /swagger-login instead, use it only with curl"
            , description = "Deprecreated for swagger use /swagger-login instead, use it only with curl, ")
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            return "Login successful!";
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    @GetMapping("/invalid-session")
    public String invalidSession() {
        return "invalid session";
    }


}
