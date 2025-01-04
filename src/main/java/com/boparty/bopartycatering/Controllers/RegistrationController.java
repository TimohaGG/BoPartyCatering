package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.User.User;
import com.boparty.bopartycatering.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping("/registration")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "User/registration";
    }

   @PostMapping("/registration")
    public String register(@BindParam User user, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/registration";
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        if(!userService.saveUser(user)) {
           return "redirect:/registration";
        }
        return "redirect:/";
   }
}
