package com.example.roombooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CalendarController {

    @GetMapping("/availability")
    public String showCalendar() {
        return "room-calendar";  // This will render room-calendar.html
    }

    // Rest of your existing CalendarController code here
    @RestController
    @RequestMapping("/api/calendar")
    public static class CalendarApiController {
        // Your existing code from lines 25-87
    }
} 