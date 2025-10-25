package com.example.booking.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Hotel {
    @Id
    private String id = UUID.randomUUID().toString();

    private String name;
    private String location;

    @Column(length = 2000)
    private String description;

    private int totalRooms;
    private int availableRooms;

    public Hotel() {}

    // getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    public int getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(int availableRooms) { this.availableRooms = availableRooms; }
}
