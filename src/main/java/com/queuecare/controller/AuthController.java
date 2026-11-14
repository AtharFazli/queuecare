package com.queuecare.controller;

import com.queuecare.dto.RegisterRequest;
import com.queuecare.exception.EmailAlreadyExistsException;
import com.queuecare.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Konfirmasi password tidak sama dengan password.");
        }
        if (result.hasErrors()) {
            return "register";
        }
        try {
            authService.register(request);
        } catch (EmailAlreadyExistsException ex) {
            result.rejectValue("email", "duplicate", ex.getMessage());
            return "register";
        }
        redirectAttributes.addFlashAttribute("sukses", "Akun dibuat. Silakan masuk.");
        return "redirect:/login";
    }
}
