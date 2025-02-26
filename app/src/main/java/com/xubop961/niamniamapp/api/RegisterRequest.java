package com.xubop961.niamniamapp.api;

public class RegisterRequest {
    private String firstName;
    private String email;
    private String password;

    public RegisterRequest(String firstName, String email, String password) {
        this.firstName = firstName;
        this.email = email;
        this.password = password;
    }

    // Getters y setters (opcional si Retrofit usa reflexión)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

