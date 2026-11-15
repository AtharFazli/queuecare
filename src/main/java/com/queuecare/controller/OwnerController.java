package com.queuecare.controller;

import com.queuecare.dto.BusinessForm;
import com.queuecare.dto.CapacityForm;
import com.queuecare.dto.ScheduleForm;
import com.queuecare.dto.ServiceForm;
import com.queuecare.entity.Business;
import com.queuecare.entity.BusinessSchedule;
import com.queuecare.entity.Service;
import com.queuecare.service.ActiveBusiness;
import com.queuecare.service.BusinessService;
import com.queuecare.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
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

@Controller
@RequestMapping("/owner")
public class OwnerController {

    private static final String BISNIS = "/owner/business";
    private static final String DAFTAR_BISNIS = "/owner/businesses";
    private static final String LAYANAN = "/owner/services";
    private static final String JADWAL = "/owner/schedules";
    private static final String KAPASITAS = "/owner/capacity";

    private final BusinessService businessService;
    private final CategoryService categoryService;
    private final ActiveBusiness activeBusiness;

    public OwnerController(BusinessService businessService,
                           CategoryService categoryService,
                           ActiveBusiness activeBusiness) {
        this.businessService = businessService;
        this.categoryService = categoryService;
        this.activeBusiness = activeBusiness;
    }

    /** Mengalihkan Owner yang belum punya atau belum memilih bisnis. */
    private String tanpaBisnis(HttpSession session) {
        String tujuan = activeBusiness.semuaMilikSaya().isEmpty() ? BISNIS + "/baru" : DAFTAR_BISNIS;
        return "redirect:" + tujuan;
    }

    // ===== daftar bisnis dan pilihan =====

    @GetMapping("/businesses")
    public String daftarBisnis(Model model) {
        model.addAttribute("bisnis", activeBusiness.semuaMilikSaya());
        return "owner/businesses";
    }

    @PostMapping("/businesses/{id}/pilih")
    public String pilihBisnis(@PathVariable Long id, HttpSession session, RedirectAttributes flash) {
        try {
            activeBusiness.pilih(session, id);
        } catch (AccessDeniedException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:" + BISNIS;
    }

    // ===== bisnis =====

    @GetMapping("/business")
    public String ringkasan(HttpSession session, Model model) {
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return tanpaBisnis(session);
        }
        model.addAttribute("bisnis", business);
        return "owner/business";
    }

    @GetMapping("/business/baru")
    public String bisnisBaru(Model model) {
        return formulirBisnis(model, null, null);
    }

    @GetMapping("/business/{id}")
    public String ubahBisnis(@PathVariable Long id, Model model) {
        return formulirBisnis(model, businessService.bisnisSaya(id), null);
    }

    @PostMapping("/business/baru")
    public String simpanBisnisBaru(@Valid @ModelAttribute("businessForm") BusinessForm form,
                                   BindingResult hasil, Model model, HttpSession session,
                                   RedirectAttributes flash) {
        if (hasil.hasErrors()) {
            return formulirBisnis(model, null, form);
        }
        Business business = businessService.buatBisnis(form);
        activeBusiness.pilih(session, business.getId());
        flash.addFlashAttribute("sukses",
                "Bisnis dibuat dengan status PENDING. Admin akan mengubahnya menjadi ACTIVE.");
        return "redirect:" + BISNIS;
    }

    @PostMapping("/business/{id}")
    public String simpanBisnis(@PathVariable Long id,
                               @Valid @ModelAttribute("businessForm") BusinessForm form,
                               BindingResult hasil, Model model, RedirectAttributes flash) {
        if (hasil.hasErrors()) {
            return formulirBisnis(model, businessService.bisnisSaya(id), form);
        }
        return simpan(() -> businessService.ubahBisnis(id, form), "Bisnis diperbarui.", BISNIS, flash);
    }

    private String formulirBisnis(Model model, Business business, BusinessForm form) {
        model.addAttribute("bisnis", business);
        model.addAttribute("kategori", categoryService.semua());
        if (form != null) {
            model.addAttribute("businessForm", form);
        } else if (business == null) {
            model.addAttribute("businessForm", new BusinessForm());
        } else {
            BusinessForm dariEntity = new BusinessForm();
            dariEntity.setName(business.getName());
            dariEntity.setCategoryId(business.getCategory().getId());
            dariEntity.setDescription(business.getDescription());
            dariEntity.setAddress(business.getAddress());
            dariEntity.setPhone(business.getPhone());
            dariEntity.setMaxCapacity(business.getMaxCapacity());
            dariEntity.setActiveStaffCount(business.getActiveStaffCount());
            model.addAttribute("businessForm", dariEntity);
        }
        return "owner/business-form";
    }

    // ===== layanan =====

    @GetMapping("/services")
    public String layanan(HttpSession session, Model model) {
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return tanpaBisnis(session);
        }
        model.addAttribute("bisnis", business);
        model.addAttribute("layanan", businessService.layananSaya(business));
        return "owner/services";
    }

    @GetMapping("/services/baru")
    public String layananBaru(HttpSession session, Model model) {
        if (activeBusiness.bisnisAktif(session) == null) {
            return tanpaBisnis(session);
        }
        return formulirLayanan(model, null, null);
    }

    @GetMapping("/services/{id}")
    public String ubahLayanan(@PathVariable Long id, HttpSession session, Model model) {
        if (activeBusiness.bisnisAktif(session) == null) {
            return tanpaBisnis(session);
        }
        return formulirLayanan(model, businessService.layananSaya(id), null);
    }

    @PostMapping("/services/baru")
    public String simpanLayananBaru(@Valid @ModelAttribute("serviceForm") ServiceForm form,
                                    BindingResult hasil, Model model, HttpSession session,
                                    RedirectAttributes flash) {
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return tanpaBisnis(session);
        }
        if (hasil.hasErrors()) {
            return formulirLayanan(model, null, form);
        }
        return simpan(() -> businessService.buatLayanan(business, form), "Layanan disimpan.", LAYANAN, flash);
    }

    @PostMapping("/services/{id}")
    public String simpanLayanan(@PathVariable Long id,
                                @Valid @ModelAttribute("serviceForm") ServiceForm form,
                                BindingResult hasil, Model model, HttpSession session,
                                RedirectAttributes flash) {
        if (activeBusiness.bisnisAktif(session) == null) {
            return tanpaBisnis(session);
        }
        if (hasil.hasErrors()) {
            return formulirLayanan(model, businessService.layananSaya(id), form);
        }
        return simpan(() -> businessService.ubahLayanan(id, form), "Layanan diperbarui.", LAYANAN, flash);
    }

    /** Layanan cukup dinonaktifkan, tidak dihapus. */
    @PostMapping("/services/{id}/status")
    public String statusLayanan(@PathVariable Long id,
                                @RequestParam(value = "aktif", defaultValue = "false") boolean aktif,
                                HttpSession session, RedirectAttributes flash) {
        if (activeBusiness.bisnisAktif(session) == null) {
            return tanpaBisnis(session);
        }
        return simpan(() -> businessService.ubahStatusLayanan(id, aktif),
                aktif ? "Layanan diaktifkan." : "Layanan dinonaktifkan.", LAYANAN, flash);
    }

    private String formulirLayanan(Model model, Service service, ServiceForm form) {
        model.addAttribute("layanan", service);
        if (form != null) {
            model.addAttribute("serviceForm", form);
        } else if (service == null) {
            model.addAttribute("serviceForm", new ServiceForm());
        } else {
            ServiceForm dariEntity = new ServiceForm();
            dariEntity.setName(service.getName());
            dariEntity.setDescription(service.getDescription());
            dariEntity.setPrice(service.getPrice());
            dariEntity.setEstimatedDurationMinutes(service.getEstimatedDurationMinutes());
            dariEntity.setActive(service.isActive());
            model.addAttribute("serviceForm", dariEntity);
        }
        return "owner/service-form";
    }

    // ===== jadwal operasional =====

    @GetMapping("/schedules")
    public String jadwal(HttpSession session, Model model) {
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return tanpaBisnis(session);
        }
        model.addAttribute("bisnis", business);
        model.addAttribute("jadwal", businessService.jadwalSaya(business));
        return "owner/schedules";
    }

    @GetMapping("/schedules/baru")
    public String jadwalBaru(HttpSession session, Model model) {
        if (activeBusiness.bisnisAktif(session) == null) {
            return tanpaBisnis(session);
        }
        return formulirJadwal(model, null, null);
    }

    @GetMapping("/schedules/{id}")
    public String ubahJadwal(@PathVariable Long id, HttpSession session, Model model) {
        if (activeBusiness.bisnisAktif(session) == null) {
            return tanpaBisnis(session);
        }
        return formulirJadwal(model, businessService.jadwalSaya(id), null);
    }

    @PostMapping("/schedules/baru")
    public String simpanJadwalBaru(@Valid @ModelAttribute("scheduleForm") ScheduleForm form,
                                   BindingResult hasil, Model model, HttpSession session,
                                   RedirectAttributes flash) {
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return tanpaBisnis(session);
        }
        if (hasil.hasErrors()) {
            return formulirJadwal(model, null, form);
        }
        return simpan(() -> businessService.simpanJadwal(business, null, form), "Jadwal disimpan.", JADWAL, flash);
    }

    @PostMapping("/schedules/{id}")
    public String simpanJadwal(@PathVariable Long id,
                               @Valid @ModelAttribute("scheduleForm") ScheduleForm form,
                               BindingResult hasil, Model model, HttpSession session,
                               RedirectAttributes flash) {
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return tanpaBisnis(session);
        }
        if (hasil.hasErrors()) {
            return formulirJadwal(model, businessService.jadwalSaya(id), form);
        }
        return simpan(() -> businessService.simpanJadwal(business, id, form), "Jadwal diperbarui.", JADWAL, flash);
    }

    @PostMapping("/schedules/{id}/hapus")
    public String hapusJadwal(@PathVariable Long id, HttpSession session, RedirectAttributes flash) {
        if (activeBusiness.bisnisAktif(session) == null) {
            return tanpaBisnis(session);
        }
        return simpan(() -> businessService.hapusJadwal(id), "Jadwal dihapus.", JADWAL, flash);
    }

    private String formulirJadwal(Model model, BusinessSchedule jadwal, ScheduleForm form) {
        model.addAttribute("jadwal", jadwal);
        if (form != null) {
            model.addAttribute("scheduleForm", form);
        } else if (jadwal == null) {
            model.addAttribute("scheduleForm", new ScheduleForm());
        } else {
            ScheduleForm dariEntity = new ScheduleForm();
            dariEntity.setDayOfWeek(jadwal.getDayOfWeek());
            dariEntity.setOpenTime(jadwal.getOpenTime());
            dariEntity.setCloseTime(jadwal.getCloseTime());
            dariEntity.setClosed(jadwal.isClosed());
            model.addAttribute("scheduleForm", dariEntity);
        }
        return "owner/schedule-form";
    }

    // ===== kapasitas dan petugas aktif =====

    @GetMapping("/capacity")
    public String kapasitas(HttpSession session, Model model) {
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return tanpaBisnis(session);
        }
        model.addAttribute("bisnis", business);
        CapacityForm form = new CapacityForm();
        form.setMaxCapacity(business.getMaxCapacity());
        form.setActiveStaffCount(business.getActiveStaffCount());
        model.addAttribute("capacityForm", form);
        return "owner/capacity";
    }

    @PostMapping("/capacity")
    public String simpanKapasitas(@Valid @ModelAttribute("capacityForm") CapacityForm form,
                                  BindingResult hasil, Model model, HttpSession session,
                                  RedirectAttributes flash) {
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return tanpaBisnis(session);
        }
        if (hasil.hasErrors()) {
            model.addAttribute("bisnis", business);
            return "owner/capacity";
        }
        return simpan(() -> businessService.ubahKapasitas(business, form),
                "Kapasitas dan petugas aktif disimpan.", KAPASITAS, flash);
    }

    /** Jalankan aksi, lalu redirect dengan pesan sukses atau galat. */
    private String simpan(Runnable aksi, String pesan, String tujuan, RedirectAttributes flash) {
        try {
            aksi.run();
        } catch (AccessDeniedException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:" + tujuan;
        }
        flash.addFlashAttribute("sukses", pesan);
        return "redirect:" + tujuan;
    }
}
