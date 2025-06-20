package com.lighting.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "addresses")
public class Address {

    @Id
    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Address line is required")
    private String addressLine1;

    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Ward is required")
    private String ward;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotBlank(message = "Gender is required")
    private String gender; // male, female, other

    private String addressNote = "";

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
