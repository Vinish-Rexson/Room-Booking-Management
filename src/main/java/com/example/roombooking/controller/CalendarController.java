package com.example.roombooking.controller;

import com.example.roombooking.model.Booking;
import com.example.roombooking.model.Room;
import com.example.roombooking.repository.RoomRepository;
import com.example.roombooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
public class CalendarController {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/availability")
    public String showCalendar() {
        return "room-calendar";
    }

    @RestController
    @RequestMapping("/api/calendar")
    public static class CalendarApiController {
        
        @Autowired
        private BookingRepository bookingRepository;
        
        @Autowired
        private RoomRepository roomRepository;

        @GetMapping("/availability")
        public List<Map<String, Object>> getAvailability(
                @RequestParam String start,
                @RequestParam String end,
                @RequestParam(required = false) String roomId) {
            
            LocalDate startDate = LocalDate.parse(start.split("T")[0]);
            LocalDate endDate = LocalDate.parse(end.split("T")[0]);
            
            List<Map<String, Object>> events = new ArrayList<>();
            
            List<Room> rooms = roomId != null && !roomId.isEmpty() ? 
                Collections.singletonList(roomRepository.findById(Long.parseLong(roomId)).orElse(null)) :
                roomRepository.findAll();

            // Sort rooms by name first
            rooms.sort((r1, r2) -> r1.getName().compareTo(r2.getName()));

            for (Room room : rooms) {
                if (room == null) continue;
                
                List<Booking> bookings = bookingRepository.findByRoomAndDateRange(room, startDate, endDate);
                
                if (bookings.isEmpty()) {
                    Map<String, Object> event = new HashMap<>();
                    event.put("roomName", room.getName());
                    event.put("available", true);
                    event.put("checkInDate", startDate);
                    event.put("checkOutDate", endDate);
                    events.add(event);
                } else {
                    // Sort bookings by check-in date
                    bookings.sort((b1, b2) -> b1.getCheckInDate().compareTo(b2.getCheckInDate()));
                    
                    for (Booking booking : bookings) {
                        Map<String, Object> event = new HashMap<>();
                        event.put("roomName", room.getName());
                        event.put("customerName", booking.getCustomerName());
                        event.put("available", false);
                        event.put("checkInDate", booking.getCheckInDate());
                        event.put("checkOutDate", booking.getCheckOutDate());
                        events.add(event);
                    }
                }
            }
            
            // Final sort of all events by room name and then by date
            events.sort((e1, e2) -> {
                int roomCompare = ((String)e1.get("roomName")).compareTo((String)e2.get("roomName"));
                if (roomCompare != 0) return roomCompare;
                
                LocalDate date1 = (LocalDate)e1.get("checkInDate");
                LocalDate date2 = (LocalDate)e2.get("checkInDate");
                return date1.compareTo(date2);
            });
            
            return events;
        }
    }
} 