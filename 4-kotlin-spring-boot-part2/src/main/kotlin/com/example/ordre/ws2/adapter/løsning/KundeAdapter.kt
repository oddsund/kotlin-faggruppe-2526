package com.example.ordre.ws2.adapter.løsning

import com.example.ordre.ws2.domain.model.løsning.Kunde
import com.example.ordre.ws2.domain.port.løsning.KundePort
import com.example.ordre.ws2.persistence.entity.KundeEntity
import com.example.ordre.ws2.persistence.repository.KundeRepository
import org.springframework.stereotype.Service

@Service
class KundeAdapter(
    private val kundeRepository: KundeRepository
) : KundePort {

    override fun hentKunde(kundeId: Long): Kunde? {
        return kundeRepository.findById(kundeId)
            .map { it.toDomain() }  // Mapper KundeEntity -> Kunde hvis present
            .orElse(null)    // Konverterer empty Optional til null
    }
}

private fun KundeEntity.toDomain(): Kunde {
    return Kunde(
        id = this.id ?: throw IllegalStateException("KundeEntity må ha id for å mappe til domain"),
        navn = this.navn,
        erAktiv = this.erAktiv
    )
}

private fun Kunde.toEntity(): KundeEntity {
    return KundeEntity(
        id = this.id,
        navn = this.navn,
        erAktiv = this.erAktiv
    )
}
