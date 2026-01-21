package com.example.ordre.persistence.repository

import com.example.ordre.persistence.entity.KundeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface KundeRepository : JpaRepository<KundeEntity, Long> {
    // Spring Data genererer implementasjon automatisk
    // findById(id: Long): Optional<KundeEntity> - inherited
}