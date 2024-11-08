package com.example.roombooking.service;

import com.example.roombooking.model.Room;
import com.example.roombooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    // Get all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Get a room by id
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    // Add a new room
    public Room addRoom(Room room) {
        return roomRepository.save(room);
    }

    // Update room availability
    public Room updateRoom(Room room) {
        return roomRepository.save(room);
    }
}
