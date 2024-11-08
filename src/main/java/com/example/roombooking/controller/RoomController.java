package com.example.roombooking.controller;

import com.example.roombooking.model.Room;
import com.example.roombooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    // Display all rooms
    @GetMapping
    public String showRooms(Model model) {
        model.addAttribute("rooms", roomRepository.findAll());
        return "rooms";
    }

    // Add a new room
    @PostMapping("/add")
    public String addRoom(@RequestParam String name, @RequestParam String description, @RequestParam int capacity) {
        Room room = new Room();
        room.setName(name);
        room.setDescription(description);
        room.setCapacity(capacity);
        room.setAvailable(true); // Set available to true by default
        roomRepository.save(room);
        return "redirect:/rooms";
    }
}
