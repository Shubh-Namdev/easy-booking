package com.example.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegisterRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = ".*\\d.*", message = "must contain a digit")
    @Pattern(regexp = ".*[A-Z].*", message = "must contain uppercase")
    @Pattern(regexp = ".*[^a-zA-Z0-9].*", message = "must contain special char")
    private String password;

    private String firstName;
    private String lastName;
    private String role; // optional

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
