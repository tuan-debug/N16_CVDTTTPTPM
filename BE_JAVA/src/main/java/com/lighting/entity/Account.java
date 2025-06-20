package com.lighting.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class Account {

    @Id
    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Indexed(unique = true)
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @NotBlank(message = "Phone number is required")
    private String phone;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password is required")
    private String password;

    private Gender gender;

    @NotBlank(message = "Address line is required")
    private String addressLine1;

    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Ward is required")
    private String ward;

    private String postalCode;

    private String addressNote;

    private Boolean defaultAddress = false;

    @DBRef
    private List<Order> orders = new ArrayList<>();

    @DBRef
    private List<Product> favorites = new ArrayList<>();

    @DBRef
    private List<Product> cart = new ArrayList<>();

    private Role role = Role.USER;

    private Boolean isBlocked = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Enums
    public enum Gender {
        MALE("male"),
        FEMALE("female"),
        OTHER("other");

        private final String value;

        Gender(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Role {
        USER("user"),
        ADMIN("admin");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
