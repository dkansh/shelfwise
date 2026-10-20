package com.shelfwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "branches")
@Getter
@Setter
public class LibraryBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String code;

    private String address;

    private String phone;

    // free text for now, e.g. "Mon-Fri 9-18, Sat 10-14"
    private String openingHours;

    private Boolean active;
}
