package no.bekk.workshop.db

import org.jetbrains.exposed.sql.Table

object Kunder : Table("kunde") {
    val id = long("id").autoIncrement()
    val navn = varchar("navn", 255)
    val erAktiv = bool("er_aktiv")

    override val primaryKey = PrimaryKey(id)
}

object Lager : Table("lager") {
    val produktId = varchar("produkt_id", 50)
    val antall = integer("antall")

    override val primaryKey = PrimaryKey(produktId)
}
