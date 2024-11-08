package com.example.roombooking.controller;

import com.example.roombooking.model.Booking;
import com.example.roombooking.model.Room;
import com.example.roombooking.repository.BookingRepository;
import com.example.roombooking.repository.RoomRepository;
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

    /**
     * Show the room dashboard with all rooms and their status.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Room> rooms = roomRepository.findAll();  // Fetch all rooms from the database
        model.addAttribute("rooms", rooms);  // Add rooms to the model
        return "room-dashboard";  // Return the view name for Thymeleaf to render
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
                           Model model) {

        // Fetch the room by ID
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));

        // Validate dates: ensure check-out is after check-in
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);
        if (checkOut.isBefore(checkIn)) {
            model.addAttribute("message", "Check-out date must be after check-in date.");
            return "book-room";  // Return to the booking form if validation fails
        }

        // Create and save the booking
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setCustomerName(customerName);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);

        // Save the booking in the repository
        bookingRepository.save(booking);

        // Show confirmation message
        model.addAttribute("message", "Room booked successfully!");
        return "booking-confirmation";  // Return the confirmation view
    }
}
