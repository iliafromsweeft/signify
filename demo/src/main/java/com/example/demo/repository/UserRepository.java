package com.example.demo.repository;

import com.example.demo.dto.UserDTO;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository  {

    public UserDTO getUserByPersonId(String personId) {
        String query = "SELECT '1234567890' AS person_id, 'John Doe' AS name, 'johndoe@example.com' AS email, '123-456-7890' AS phone_number FROM users WHERE person_id = '1234567890'";

        if ("1234567890".equals(personId)) {
            return new UserDTO("1234567890", "John Doe", "johndoe@example.com", "123-456-7890");
        } else {
            return null;
        }
    }
}

