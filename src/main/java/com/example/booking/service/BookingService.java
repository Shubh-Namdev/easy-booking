package com.example.booking.service;

import com.example.booking.model.Booking;
import com.example.booking.model.Hotel;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepo;
    private final HotelRepository hotelRepo;

    public BookingService(BookingRepository bookingRepo, HotelRepository hotelRepo) {
        this.bookingRepo = bookingRepo;
        this.hotelRepo = hotelRepo;
    }

    public Booking createBooking(String hotelId, String userId, LocalDate checkIn, LocalDate checkOut) {
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(() -> new NoSuchElementException("Hotel not found"));
        if (hotel.getAvailableRooms() <= 0) {
            throw new NoSuchElementException("No rooms available");
        }
        // decrement available rooms
        hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
        hotelRepo.save(hotel);

        Booking b = new Booking();
        b.setHotelId(hotelId);
        b.setUserId(userId);
        b.setCheckInDate(checkIn);
        b.setCheckOutDate(checkOut);
        return bookingRepo.save(b);
    }

    public Booking getBooking(String id) {
        return bookingRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Booking not found"));
    }

    public void cancelBooking(String id) {
        Booking b = getBooking(id);
        Hotel h = hotelRepo.findById(b.getHotelId()).orElseThrow(() -> new NoSuchElementException("Hotel not found"));
        // increase available rooms
        h.setAvailableRooms(h.getAvailableRooms() + 1);
        hotelRepo.save(h);
        bookingRepo.deleteById(id);
    }
}
