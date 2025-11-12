package com.example.ordre.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "kunde")
class KundeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(nullable = false)
    var navn: String,
    
    @Column(name = "er_aktiv", nullable = false)
    var erAktiv: Boolean
) {
    // No-arg constructor for JPA
    constructor() : this(null, "", false)
    
    override fun toString(): String {
        return "KundeEntity(id=$id, navn='$navn', erAktiv=$erAktiv)"
    }
}