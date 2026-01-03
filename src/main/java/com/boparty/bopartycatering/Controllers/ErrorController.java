package com.boparty.bopartycatering.Controllers;

import com.boparty.bopartycatering.Models.Order.Orders;
import com.boparty.bopartycatering.Services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ErrorController {

    private final ErrorAttributes errorAttributes;
    @Autowired
    public ErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }
    @GetMapping("/error")
    public String error(HttpServletRequest request, Model model) {

        WebRequest webRequest = new ServletWebRequest(request); // ✅ wrap it

        Map<String, Object> errorDetails = errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.of(
                        ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.EXCEPTION
                )
        );

        model.addAttribute("timestamp", errorDetails.get("timestamp"));
        model.addAttribute("status", errorDetails.get("status"));
        model.addAttribute("error", errorDetails.get("error"));
        model.addAttribute("message", errorDetails.get("message"));
        model.addAttribute("path", errorDetails.get("path"));

        return "error"; // Your Thymeleaf or JSP view
    }
}
