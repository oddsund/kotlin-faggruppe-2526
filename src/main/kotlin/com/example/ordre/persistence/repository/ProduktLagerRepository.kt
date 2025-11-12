package com.example.ordre.persistence.repository

import com.example.ordre.persistence.entity.ProduktLagerEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProduktLagerRepository : JpaRepository<ProduktLagerEntity, String> {
    // Custom query metode - Spring Data genererer implementasjon
    fun findByProduktId(produktId: String): ProduktLagerEntity?
}