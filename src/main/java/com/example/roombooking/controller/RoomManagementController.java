package com.example.roombooking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.roombooking.repository.RoomRepository;
import com.example.roombooking.model.Room;

@RestController
@RequestMapping("/api")
public class RoomManagementController {

    private final RoomRepository roomRepository;

    public RoomManagementController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping("/manage/rooms")
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @PostMapping("/manage/rooms")
    public ResponseEntity<Room> addRoom(@RequestBody Room room) {
        room.setAvailable(true); // New rooms are available by default
        Room savedRoom = roomRepository.save(room);
        return ResponseEntity.ok(savedRoom);
    }

    @DeleteMapping("/manage/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/manage/rooms/{id}/checkout")
    public ResponseEntity<?> checkoutRoom(@PathVariable Long id) {
        return roomRepository.findById(id)
            .map(room -> {
                room.setAvailable(false);
                roomRepository.save(room);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
} 