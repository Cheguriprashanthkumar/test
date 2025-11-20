package org.nishgrid.clienterp.controller;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.service.LoginResult;
import org.nishgrid.clienterp.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILURE",
                    "message", "Email and Password are required."
            ));
        }

        LoginResult result = loginService.login(email, password);

        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "role", result.getRole(),
                    "message", result.getMessage()

            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILURE",
                    "message", result.getMessage()
            ));
        }
    }

    @PostMapping("/reset-password/admin")
    public ResponseEntity<?> resetAdminPassword(@RequestBody Map<String, String> payload) {
        String orgToken = payload.get("orgToken");
        String email = payload.get("email");
        String newPassword = payload.get("newPassword");

        String message = loginService.resetAdminPasswordByEmail(orgToken, email, newPassword);
        return ResponseEntity.ok(Map.of("message", message));
    }
}
