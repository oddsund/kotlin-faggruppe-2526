package com.example.ordre.ws2.adapter.løsning

import com.example.ordre.ws2.domain.model.løsning.ProduktLager
import com.example.ordre.ws2.domain.port.løsning.ProduktLagerPort
import com.example.ordre.ws2.persistence.entity.ProduktLagerEntity
import com.example.ordre.ws2.persistence.repository.ProduktLagerRepository
import org.springframework.stereotype.Service

@Service
class ProduktLagerAdapter(
    private val produktLagerRepository: ProduktLagerRepository
) : ProduktLagerPort {

    override fun hentLagerStatus(produktId: String): ProduktLager? {
        return produktLagerRepository.findByProduktId(produktId)?.toDomain()
    }
}

private fun ProduktLagerEntity.toDomain(): ProduktLager {
    return ProduktLager(
        produktId = this.produktId,
        antallPåLager = this.antallPåLager
    )
}

private fun ProduktLager.toEntity(): ProduktLagerEntity {
    return ProduktLagerEntity(
        produktId = this.produktId,
        antallPåLager = this.antallPåLager
    )
}
