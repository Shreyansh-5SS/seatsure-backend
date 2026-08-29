package com.seatsure.backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "theatres")
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public Theatre() {}

    // Getters and Setters
    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}