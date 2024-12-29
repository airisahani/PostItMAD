package com.example.module1;

public class ReadWriteUserDetails {

    public String fullname, email, birthdate;

    public ReadWriteUserDetails(){}
    public ReadWriteUserDetails(String ETFullName, String ETEmail, String ETBirthday){
        this.fullname = ETFullName;
        this.email = ETEmail;
        this.birthdate = ETBirthday;
    }

}
