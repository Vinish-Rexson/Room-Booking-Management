package com.example.roombooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.roombooking.repository.BookingRepository;
import com.example.roombooking.repository.RoomRepository;
import com.example.roombooking.model.Room;
import com.example.roombooking.model.Booking;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/availability")
    public List<Map<String, Object>> getAvailability(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(required = false) Long roomId) {
        
        List<Map<String, Object>> availability = new ArrayList<>();
        
        List<Room> rooms = roomId != null ? 
            Collections.singletonList(roomRepository.findById(roomId).orElse(null)) :
            roomRepository.findAll();

        for (Room room : rooms) {
            if (room == null) continue;

            // Get bookings for this room in the date range
            List<Booking> bookings = bookingRepository.findByRoomAndDateRange(
                room, start, end);

            // Add occupied periods
            for (Booking booking : bookings) {
                Map<String, Object> event = new HashMap<>();
                event.put("roomId", room.getId());
                event.put("roomName", room.getName());
                event.put("id", booking.getId());
                event.put("customerName", booking.getCustomerName());
                event.put("checkInDate", booking.getCheckInDate());
                event.put("checkOutDate", booking.getCheckOutDate());
                event.put("available", false);
                availability.add(event);
            }

            // Add available periods
            LocalDate current = start;
            while (!current.isAfter(end)) {
                if (isDateAvailable(current, bookings)) {
                    Map<String, Object> event = new HashMap<>();
                    event.put("roomId", room.getId());
                    event.put("roomName", room.getName());
                    event.put("checkInDate", current);
                    event.put("checkOutDate", current.plusDays(1));
                    event.put("available", true);
                    availability.add(event);
                }
                current = current.plusDays(1);
            }
        }
        
        return availability;
    }

    private boolean isDateAvailable(LocalDate date, List<Booking> bookings) {
        return bookings.stream().noneMatch(booking ->
            !date.isBefore(booking.getCheckInDate()) && 
            !date.isAfter(booking.getCheckOutDate()));
    }
} 