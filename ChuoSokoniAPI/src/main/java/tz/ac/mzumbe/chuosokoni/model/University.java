package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "universities")
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "university_id")
    private Integer universityId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public University() {
    }

    public University(String name,
                      String location,
                      String contactEmail,
                      String contactPhone,
                      String status,
                      LocalDateTime createdAt) {
        this.name = name;
        this.location = location;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getUniversityId() {
        return universityId;
    }

    public void setUniversityId(Integer universityId) {
        this.universityId = universityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "University{" +
                "universityId=" + universityId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof University)) return false;
        University that = (University) o;
        return Objects.equals(universityId, that.universityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(universityId);
    }
}
