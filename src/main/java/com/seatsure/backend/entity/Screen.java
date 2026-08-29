package com.seatsure.backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "screens")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The Magic Relationship! Many screens belong to ONE theatre.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;

    @Column(name = "screen_number", nullable = false)
    private int screenNumber;

    public Screen() {}

    // Getters and Setters
    public UUID getId() { return id; }

    public Theatre getTheatre() { return theatre; }
    public void setTheatre(Theatre theatre) { this.theatre = theatre; }

    public int getScreenNumber() { return screenNumber; }
    public void setScreenNumber(int screenNumber) { this.screenNumber = screenNumber; }
}