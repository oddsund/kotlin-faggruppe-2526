package reisesok.modell

data class Sokresultat(
    val fly: List<Fly> = emptyList(),
    val hotell: List<Hotell> = emptyList(),
    val leiebiler: List<Leiebil> = emptyList(),
    val aktiviteter: List<Aktivitet> = emptyList(),
    val feil: List<Sokfeil> = emptyList()
)
