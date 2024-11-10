package com.example.roombooking.controller;

import com.example.roombooking.model.Booking;
import com.example.roombooking.model.Room;
import com.example.roombooking.repository.BookingRepository;
import com.example.roombooking.repository.RoomRepository;
import com.example.roombooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        return "dashboard";
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

    @GetMapping("/api/rooms")
    @ResponseBody
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    @PostMapping("/api/checkout/{roomId}")
    @ResponseBody
    public ResponseEntity<?> checkoutRoomById(@PathVariable Long roomId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.setAvailable(true);
            roomRepository.save(room);
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/checkout/{roomId}")
    public String showCheckoutPage(@PathVariable Long roomId, Model model) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        // Get all bookings and take the most recent one
        List<Booking> bookings = bookingRepository.findByRoomAndCheckOutDateGreaterThanEqualOrderByCheckOutDateAsc(room, LocalDate.now());
        
        if (!bookings.isEmpty()) {
            // Get the most recent booking
            Booking currentBooking = bookings.get(0);
            model.addAttribute("booking", currentBooking);
            return "checkout";
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/checkout/{roomId}")
    public String processCheckout(@PathVariable Long roomId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            List<Booking> bookings = bookingRepository.findByRoomAndCheckOutDateGreaterThanEqualOrderByCheckOutDateAsc(room, LocalDate.now());
            
            if (!bookings.isEmpty()) {
                Booking booking = bookings.get(0);
                // Reset room capacity
                room.setCapacity(room.getCapacity() + booking.getNumberOfGuests());
                room.setAvailable(true);
                roomRepository.save(room);
                bookingRepository.delete(booking);
            }
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/api/booking/{id}")
    @ResponseBody
    public ResponseEntity<Booking> getBooking(@PathVariable Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/checkout/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> checkoutBooking(@PathVariable Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            Room room = booking.getRoom();
            
            // Reset room capacity and availability
            room.setCapacity(room.getCapacity() + booking.getNumberOfGuests());
            room.setAvailable(true);
            
            // Save changes
            roomRepository.save(room);
            bookingRepository.delete(booking);
            
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.notFound().build();
    }
}
