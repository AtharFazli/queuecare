package com.queuecare.controller;

import com.queuecare.dto.PasswordForm;
import com.queuecare.dto.ProfileForm;
import com.queuecare.entity.User;
import com.queuecare.exception.AturanAkunException;
import com.queuecare.exception.EmailAlreadyExistsException;
import com.queuecare.service.AuthService;
import com.queuecare.service.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Profil milik pengguna yang sedang login (R2). Tidak ada id di URL. */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserContext userContext;
    private final AuthService authService;

    public ProfileController(UserContext userContext, AuthService authService) {
        this.userContext = userContext;
        this.authService = authService;
    }

    @GetMapping
    public String profil(Model model) {
        User user = userContext.currentUser();
        model.addAttribute("peran", user.getRole().name());
        model.addAttribute("form", formProfil(user));
        model.addAttribute("passwordForm", new PasswordForm());
        return "profile";
    }

    @PostMapping
    public String simpan(@Valid @ModelAttribute("form") ProfileForm form,
                         BindingResult result,
                         Model model,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         RedirectAttributes redirect) {
        User user = userContext.currentUser();
        if (result.hasErrors()) {
            return halaman(model, user, form, new PasswordForm());
        }
        boolean emailBerubah = !form.getEmail().equalsIgnoreCase(user.getEmail());
        try {
            authService.ubahProfil(user.getId(), form.getName(), form.getEmail());
        } catch (EmailAlreadyExistsException ex) {
            result.rejectValue("email", "duplicate", ex.getMessage());
            return halaman(model, user, form, new PasswordForm());
        }

        // Email dipakai sebagai nama login, jadi sesi lama tidak lagi cocok.
        // Pilihan paling sederhana: keluar, lalu masuk ulang dengan email baru.
        if (emailBerubah) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            new SecurityContextLogoutHandler().logout(request, response, auth);
            return "redirect:/login?profil=1";
        }
        redirect.addFlashAttribute("sukses", "Profil diperbarui.");
        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String gantiPassword(@Valid @ModelAttribute("passwordForm") PasswordForm form,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirect) {
        User user = userContext.currentUser();
        if (!form.getPasswordBaru().equals(form.getKonfirmasi())) {
            result.rejectValue("konfirmasi", "mismatch", "Konfirmasi password tidak sama.");
        }
        if (result.hasErrors()) {
            return halaman(model, user, formProfil(user), form);
        }
        try {
            authService.gantiPassword(user.getId(), form.getPasswordLama(), form.getPasswordBaru());
        } catch (AturanAkunException ex) {
            result.rejectValue("passwordLama", "salah", ex.getMessage());
            return halaman(model, user, formProfil(user), form);
        }
        redirect.addFlashAttribute("sukses", "Password diganti. Kamu tetap masuk.");
        return "redirect:/profile";
    }

    private String halaman(Model model, User user, ProfileForm form, PasswordForm passwordForm) {
        model.addAttribute("peran", user.getRole().name());
        model.addAttribute("form", form);
        model.addAttribute("passwordForm", passwordForm);
        return "profile";
    }

    private ProfileForm formProfil(User user) {
        ProfileForm form = new ProfileForm();
        form.setName(user.getName());
        form.setEmail(user.getEmail());
        return form;
    }
}
