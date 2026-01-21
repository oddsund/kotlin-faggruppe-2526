# Nye Kotlin Features Workshop

- Parprogrammering! Jobb i par og løs det dere kan/syns er interessant.
- Mål: refaktorer legacy-implementasjon til å bruke én spesifikk Kotlin-feature uten å endre testene.
- Prosjekt er konfigurert for Kotlin **2.2.0** og har compiler-flagg for eksperimentelle features (context receivers).
- Kjør tester:
  ```
  ./gradlew test
  ```
  Eller i Intellij :)
- Hvis dere får feilmelding rundt java versjon, sjekk hvilken versjon dere har installert og oppdater `jvmToolchain(24)` i `build.gradle.kts`

Struktur og oppgaver:
- 01-java-to-kotlin : konverter Java-klasse til idiomatisk Kotlin (data class, null-safety)
- 02-data-objects : bytt enum+payload til sealed interface + data object / data class
- 03-when-guards : refaktor til klarere `when`-bruk (guards)
- 04-nonlocal-control : forenkle koden ved å gå fra negativ sjekk(!it.hoppOver) til positiv sjekk (it.hoppOver)
- 05-context-params : fjern prop-drilling ved å bruke context parameters (eksempel bruker real context syntax). Trenger en liten wrapper hvis dere ikke oppdaterer testen

Løsninger ligger i `solutions/`.

Hvis dere går tom for oppgaver, ta en titt i `DISCUSSIONS.md` for smådiskusjoner rundt nye features.
