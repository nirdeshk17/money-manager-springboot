package com.alienCoders.moneymanger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity // Marks this class as a JPA entity (maps the class to a database table)
@Table(name = "tbl_profiles") // Specifies the exact table name in the database
@Data // Lombok: Generates getters, setters, equals(), hashCode(), and toString() methods
@AllArgsConstructor // Lombok: Generates a constructor with all fields as parameters
@NoArgsConstructor  // Lombok: Generates a no-argument constructor (needed by JPA/Hibernate)
@Builder            // Lombok: Enables the builder pattern for easy object creation
public class ProfileEntity {
    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates IDs (DB handles it)
    private Long id;
    private String fullName;
    @Column(unique = true) // Ensures the email must be unique in the database
    private String email;
    private String password;
    private String profileImage;
    @Column(updatable = false) // Field value is set only once (not updated later)
    @CreationTimestamp // Hibernate: Automatically sets current timestamp at record creation
    private LocalDateTime createdAt;
    @UpdateTimestamp // Hibernate: Automatically updates timestamp whenever record is updated
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private String activationToken;

    @PrePersist // JPA callback: runs before the entity is INSERTED into the database
    public void prePersist() {
        if (isActive == null) {
            isActive = false; // Ensures default value (inactive) if not explicitly set
        }
    }
}
