package vn.iotstar.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.dto.ForgotPasswordDTO;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.EmailService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository, 
                          RoleRepository roleRepository, 
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // --- 1. LOGIN ---
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // --- 2. REGISTER ---
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("user") RegisterDTO dto,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            model.addAttribute("errorMessage", "Email này đã được sử dụng!");
            return "auth/register";
        }

        Role userRole = roleRepository.findByNameIgnoreCase("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));

        User user = new User();
        user.setEmail(dto.getEmail().toLowerCase());
        user.setFullName(dto.getFullName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(userRole);

        userRepository.save(user);

        return "redirect:/login?verified=true";
    }

    // --- 3. FORGOT PASSWORD ---
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("forgotDTO", new ForgotPasswordDTO());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid @ModelAttribute("forgotDTO") ForgotPasswordDTO dto,
                                        BindingResult result,
                                        HttpSession session,
                                        Model model) {
        if (result.hasErrors()) {
            return "auth/forgot-password";
        }

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(dto.getEmail());
        if (userOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Email không tồn tại trong hệ thống!");
            return "auth/forgot-password";
        }

        // Tạo mã OTP 6 chữ số
        String otp = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("reset_otp", otp);
        session.setAttribute("reset_email", dto.getEmail());

        // Gửi qua email
        try {
            emailService.sendEmail(dto.getEmail(), "Mã xác thực đổi mật khẩu", 
                    "Mã OTP của bạn là: " + otp);
        } catch (Exception e) {
            System.out.println("Gửi mail thất bại: " + e.getMessage());
        }

        return "redirect:/verify-otp";
    }

    // --- 4. VERIFY OTP & RESET PASSWORD ---
    @GetMapping("/verify-otp")
    public String showVerifyOtpForm() {
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String processVerifyOtp(@RequestParam("otp") String otp,
                                   HttpSession session,
                                   Model model) {
        String sessionOtp = (String) session.getAttribute("reset_otp");
        if (sessionOtp != null && sessionOtp.equals(otp)) {
            return "redirect:/reset-password";
        }
        model.addAttribute("errorMessage", "Mã OTP không chính xác!");
        return "auth/verify-otp";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm() {
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("password") String newPassword,
                                       HttpSession session,
                                       Model model) {
        String email = (String) session.getAttribute("reset_email");
        if (email == null) {
            return "redirect:/forgot-password";
        }

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Dọn session
            session.removeAttribute("reset_otp");
            session.removeAttribute("reset_email");

            return "redirect:/login?reset=true";
        }

        model.addAttribute("errorMessage", "Có lỗi xảy ra, vui lòng thử lại!");
        return "auth/reset-password";
    }
}