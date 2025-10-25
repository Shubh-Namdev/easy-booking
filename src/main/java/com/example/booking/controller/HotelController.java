package com.example.booking.controller;

import com.example.booking.dto.HotelRequest;
import com.example.booking.model.Hotel;
import com.example.booking.model.Role;
import com.example.booking.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) { this.hotelService = hotelService; }

    @GetMapping
    public ResponseEntity<List<Hotel>> all() {
        return ResponseEntity.ok(hotelService.all());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody HotelRequest req, Authentication auth) {
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        Hotel h = new Hotel();
        h.setName(req.getName());
        h.setLocation(req.getLocation());
        h.setDescription(req.getDescription());
        h.setTotalRooms(req.getTotalRooms());
        h.setAvailableRooms(req.getAvailableRooms());
        return ResponseEntity.ok(hotelService.create(h));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody HotelRequest req, Authentication auth) {
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_HOTEL_MANAGER"))) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        try {
            Hotel upd = new Hotel();
            upd.setName(req.getName());
            upd.setDescription(req.getDescription());
            upd.setLocation(req.getLocation());
            upd.setAvailableRooms(req.getAvailableRooms());
            upd.setTotalRooms(req.getTotalRooms());
            Hotel res = hotelService.update(id, upd);
            return ResponseEntity.ok(res);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Hotel not found"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, Authentication auth) {
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        try {
            hotelService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Hotel not found"));
        }
    }
}
