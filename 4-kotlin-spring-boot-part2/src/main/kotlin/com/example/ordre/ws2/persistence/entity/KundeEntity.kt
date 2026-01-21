package com.example.ordre.ws2.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "kunde")
class KundeEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,
    
    @Column(nullable = false)
    var navn: String,
    
    @Column(name = "er_aktiv", nullable = false)
    var erAktiv: Boolean
) {
    // No-arg constructor for JPA
    constructor() : this(null, "", false)
}