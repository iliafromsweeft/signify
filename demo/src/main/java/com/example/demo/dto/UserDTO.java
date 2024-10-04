package com.example.demo.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String personId; // National ID
    private String name;
    private String email;
    private String phoneNumber;

    @Override
    public String toString() {
        return "UserDTO{" +
                "personId='" + personId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

