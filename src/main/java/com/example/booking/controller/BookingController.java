package com.example.booking.controller;

import com.example.booking.dto.BookingRequest;
import com.example.booking.model.Booking;
import com.example.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/{hotelId}" )
    public ResponseEntity<?> create(@PathVariable String hotelId, @Valid @RequestBody BookingRequest req, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        // only customers can book
        boolean isCustomer = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        if (!isCustomer) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        LocalDate checkIn = LocalDate.parse(req.getCheckInDate());
        LocalDate checkOut = LocalDate.parse(req.getCheckOutDate());
        if (!checkIn.isAfter(LocalDate.now())) return ResponseEntity.status(400).body(Map.of("error", "Check-in must be a future date"));
        if (!checkOut.isAfter(checkIn)) return ResponseEntity.status(400).body(Map.of("error", "Check-out must be after check-in"));

        try {
            Booking b = bookingService.createBooking(hotelId, (String) auth.getPrincipal(), checkIn, checkOut);
            return ResponseEntity.ok(Map.of("bookingId", b.getId(), "hotelId", b.getHotelId(), "checkInDate", b.getCheckInDate().toString(), "checkOutDate", b.getCheckOutDate().toString()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> get(@PathVariable String bookingId, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        try {
            Booking b = bookingService.getBooking(bookingId);
            return ResponseEntity.ok(Map.of("bookingId", b.getId(), "hotelId", b.getHotelId(), "checkInDate", b.getCheckInDate().toString(), "checkOutDate", b.getCheckOutDate().toString()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Booking not found"));
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancel(@PathVariable String bookingId, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        boolean isManager = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_HOTEL_MANAGER"));
        if (!isManager) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Booking not found"));
        }
    }
}
