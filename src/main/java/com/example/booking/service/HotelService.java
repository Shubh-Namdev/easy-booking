package com.example.booking.service;

import com.example.booking.model.Hotel;
import com.example.booking.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HotelService {
    private final HotelRepository repo;

    public HotelService(HotelRepository repo) {
        this.repo = repo;
    }

    public Hotel create(Hotel h) { return repo.save(h); }
    public List<Hotel> all() { return repo.findAll(); }
    public Hotel get(String id) { return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Hotel not found")); }
    public Hotel update(String id, Hotel upd) {
        var ex = get(id);
        if (upd.getName() != null) ex.setName(upd.getName());
        if (upd.getDescription() != null) ex.setDescription(upd.getDescription());
        if (upd.getLocation() != null) ex.setLocation(upd.getLocation());
        ex.setAvailableRooms(upd.getAvailableRooms());
        ex.setTotalRooms(upd.getTotalRooms());
        return repo.save(ex);
    }
    public void delete(String id) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Hotel not found");
        repo.deleteById(id);
    }
}
