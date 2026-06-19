package models.pojo;

import static java.lang.String.format;

public class RegistrationResponsePojoModel {
    Integer id;
    String username;
    String firstName;
    String lastName;
    String email;
    String remoteAddr;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getUsername() {
        return username;
    }
    @Override
    public String toString() {
        return format("{\"id\": \"%s\", \"username\": \"%s\", \"firstname\": \"%s\", " +
                "\"lastname\": \"%s\", \"email\": \"%s\", \"remoteAddr\": \"%s\"}",
                this.username, this.id, this.email, this.firstName, this.lastName, this.remoteAddr);

    }
}
