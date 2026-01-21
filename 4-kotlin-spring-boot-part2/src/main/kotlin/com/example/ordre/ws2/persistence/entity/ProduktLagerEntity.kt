package com.example.ordre.ws2.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "produkt_lager")
class ProduktLagerEntity(
    @Id
    @Column(name = "produkt_id")
    var produktId: String,
    
    @Column(name = "antall_paa_lager", nullable = false)
    var antallPåLager: Int
) {
    // No-arg constructor for JPA
    constructor() : this("", 0)
    
    override fun toString(): String {
        return "ProduktLagerEntity(produktId='$produktId', antallPåLager=$antallPåLager)"
    }
}