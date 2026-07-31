package com.campusnet.model;

/**
 * Plain bean backing the "my profile" self-service form. Also reused by the admin
 * user-management screens, which is why it exposes a public setRole() - admins need
 * to be able to change a user's role through the same bean.
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role = "student";
    private String fullName;
    private String email;
    private String department;
    private String bio;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
