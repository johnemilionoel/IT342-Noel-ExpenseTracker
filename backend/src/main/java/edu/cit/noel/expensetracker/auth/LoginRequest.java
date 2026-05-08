package edu.cit.noel.expensetracker.auth;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;

}