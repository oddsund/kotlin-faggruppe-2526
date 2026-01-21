package workshop.control

fun prosesserAvlesninger(avlesninger: List<Avlesning>): List<String> {
    val prosessert = mutableListOf<String>()

    for (avlesning in avlesninger) {
        avlesning.let {
            // Litt tydeligere hva som gjøres når en slipper negativ if-sjekk og kan bruke continue
            if (it.hoppOver) continue
            prosessert += "avlesning:${avlesning.id}:${avlesning.verdi}"

        }
    }

    return prosessert
}
