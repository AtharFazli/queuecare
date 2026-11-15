package com.queuecare.controller;

import com.queuecare.dto.AdminUserForm;
import com.queuecare.entity.User;
import com.queuecare.enums.Role;
import com.queuecare.exception.AturanAkunException;
import com.queuecare.exception.EmailAlreadyExistsException;
import com.queuecare.service.AdminUserService;
import com.queuecare.service.UserContext;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Pengelolaan akun oleh Admin: daftar, cari, filter role, tambah, ubah, aktif/nonaktif (R12). */
@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final UserContext userContext;

    public AdminUserController(AdminUserService adminUserService, UserContext userContext) {
        this.adminUserService = adminUserService;
        this.userContext = userContext;
    }

    @GetMapping
    public String daftar(@RequestParam(value = "q", required = false) String q,
                         @RequestParam(value = "role", required = false) String role,
                         @RequestParam(value = "hal", defaultValue = "1") int hal,
                         Model model) {
        model.addAttribute("baris", adminUserService.daftar(q, parseRole(role), hal));
        model.addAttribute("q", q);
        model.addAttribute("role", role);
        model.addAttribute("daftarRole", Role.values());
        return "admin/users";
    }

    @GetMapping("/tambah")
    public String tambahForm(Model model) {
        return formulir(model, new AdminUserForm(), "Tambah pengguna", null);
    }

    @PostMapping
    public String tambah(@Valid @ModelAttribute("form") AdminUserForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formulir(model, form, "Tambah pengguna", null);
        }
        try {
            adminUserService.tambah(form);
        } catch (AturanAkunException | EmailAlreadyExistsException ex) {
            return gagal(redirect, ex.getMessage());
        }
        redirect.addFlashAttribute("sukses", "Pengguna ditambahkan.");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/ubah")
    public String ubahForm(@PathVariable Long id, Model model) {
        User target = adminUserService.cari(id);
        AdminUserForm form = new AdminUserForm();
        form.setName(target.getName());
        form.setEmail(target.getEmail());
        form.setRole(target.getRole().name());
        return formulir(model, form, "Ubah pengguna", id);
    }

    @PostMapping("/{id}/ubah")
    public String ubah(@PathVariable Long id,
                       @Valid @ModelAttribute("form") AdminUserForm form,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formulir(model, form, "Ubah pengguna", id);
        }
        try {
            adminUserService.ubah(id, form, userContext.currentUser());
        } catch (AturanAkunException | EmailAlreadyExistsException ex) {
            return gagal(redirect, ex.getMessage());
        }
        redirect.addFlashAttribute("sukses", "Pengguna diperbarui.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/status")
    public String ubahStatus(@PathVariable Long id,
                             @RequestParam(value = "aktif", defaultValue = "true") boolean aktif,
                             RedirectAttributes redirect) {
        try {
            User target = adminUserService.ubahStatus(id, aktif, userContext.currentUser());
            redirect.addFlashAttribute("sukses", aktif
                    ? "Akun " + target.getEmail() + " diaktifkan."
                    : "Akun " + target.getEmail() + " dinonaktifkan.");
        } catch (AturanAkunException ex) {
            return gagal(redirect, ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    private String formulir(Model model, AdminUserForm form, String judul, Long id) {
        model.addAttribute("form", form);
        model.addAttribute("judul", judul);
        model.addAttribute("id", id);
        model.addAttribute("daftarRole", Role.values());
        return "admin/user-form";
    }

    private String gagal(RedirectAttributes redirect, String pesan) {
        redirect.addFlashAttribute("error", pesan);
        return "redirect:/admin/users";
    }

    /** Role tidak dikenal dianggap tanpa filter. */
    private Role parseRole(String nilai) {
        if (nilai == null || nilai.isBlank()) {
            return null;
        }
        try {
            return Role.valueOf(nilai);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
