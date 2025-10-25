package com.example.booking;

import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.HotelRequest;
import com.example.booking.dto.UserRegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAndHotelBookingTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void registerLoginAndCreateHotelAndBookFlow() throws Exception {
        // register admin
        UserRegisterRequest admin = new UserRegisterRequest();
        admin.setEmail("admin@example.com"); admin.setPassword("Test@1234"); admin.setRole("ADMIN");
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists());

        // register manager
        UserRegisterRequest mgr = new UserRegisterRequest();
        mgr.setEmail("mgr@example.com"); mgr.setPassword("Test@1234"); mgr.setRole("HOTEL_MANAGER");
        mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mgr)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists());

        // register customer
        UserRegisterRequest cust = new UserRegisterRequest();
        cust.setEmail("cust@example.com"); cust.setPassword("Test@1234");
        var create = mockMvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(cust)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists()).andReturn();
        String custToken = mapper.readTree(create.getResponse().getContentAsString()).get("token").asText();

        // login customer
        var login = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(cust)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists()).andReturn();
        String loginToken = mapper.readTree(login.getResponse().getContentAsString()).get("token").asText();

        // Create hotel as admin
        HotelRequest hotel = new HotelRequest();
        hotel.setName("Test Hotel"); hotel.setLocation("Loc"); hotel.setDescription("Desc"); hotel.setTotalRooms(1); hotel.setAvailableRooms(1);

        // get admin token
        var adminLogin = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(admin)))
                .andExpect(status().isOk()).andReturn();
        String adminToken = mapper.readTree(adminLogin.getResponse().getContentAsString()).get("token").asText();

        var createHotel = mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(hotel)).header("Authorization", "Bearer "+adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").exists()).andReturn();
        String hotelId = mapper.readTree(createHotel.getResponse().getContentAsString()).get("id").asText();

        // Book room as customer
        BookingRequest bk = new BookingRequest();
        bk.setCheckInDate(java.time.LocalDate.now().plusDays(2).toString());
        bk.setCheckOutDate(java.time.LocalDate.now().plusDays(3).toString());

        var bookingResp = mockMvc.perform(post("/bookings/"+hotelId).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(bk)).header("Authorization", "Bearer "+custToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.bookingId").exists()).andReturn();
        String bookingId = mapper.readTree(bookingResp.getResponse().getContentAsString()).get("bookingId").asText();

        // manager cancel booking
        var mgrLogin = mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mgr)))
                .andExpect(status().isOk()).andReturn();
        String mgrToken = mapper.readTree(mgrLogin.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(delete("/bookings/"+bookingId).header("Authorization", "Bearer "+mgrToken)).andExpect(status().isNoContent());
    }
}
