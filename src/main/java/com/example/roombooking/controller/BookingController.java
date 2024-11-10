package com.example.roombooking.controller;

import com.example.roombooking.model.Booking;
import com.example.roombooking.model.Room;
import com.example.roombooking.repository.BookingRepository;
import com.example.roombooking.repository.RoomRepository;
import com.example.roombooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BookingController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    /**
     * Show the room dashboard with all rooms and their status.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Room> rooms = roomRepository.findAll();
        model.addAttribute("rooms", rooms);
        return "room-dashboard";
    }

    /**
     * Show the booking form for a selected room.
     */
    @GetMapping("/book-room")
    public String showBookingForm(Model model) {
        model.addAttribute("rooms", roomRepository.findAll());
        return "book-room";  // This will return the booking form view
    }

    /**
     * Handle the form submission for booking a room.
     */
    @PostMapping("/book-room")
    public String bookRoom(@RequestParam Long roomId, 
                           @RequestParam String customerName, 
                           @RequestParam String checkInDate, 
                           @RequestParam String checkOutDate,
                           @RequestParam int numberOfGuests, 
                           Model model) {

        // Parse dates
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);

        // Validate dates
        if (checkOut.isBefore(checkIn)) {
            model.addAttribute("error", "Check-out date must be after check-in date");
            model.addAttribute("rooms", roomRepository.findAll());
            return "book-room";
        }

        // Try to make the booking
        Booking booking = bookingService.makeBooking(roomId, checkIn, checkOut, customerName, numberOfGuests);

        if (booking != null) {
            model.addAttribute("message", "Room booked successfully!");
        } else {
            model.addAttribute("message", "Sorry, the room is not available or has insufficient capacity.");
        }
        
        return "booking-confirmation";
    }
}
