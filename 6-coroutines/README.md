# Coroutines Workshop

En praktisk workshop for å lære Kotlin coroutines gjennom et reisesøk-domene.

## Beskrivelse

I denne workshopen bygger du en reisesøk-tjeneste som aggregerer fly, hotell og leiebil fra flere leverandører. Gjennom øvelsene lærer du:

- **Del 2:** Gjøre blokkerende kode til suspend-funksjoner
- **Del 3:** Structured concurrency og context-arv
- **Del 4:** Parallell eksekvering med async/await
- **Del 5:** Feilhåndtering med supervisorScope

## Forutsetninger

- JDK 21
- IntelliJ IDEA (anbefalt)

## Kom i gang

```bash
./gradlew :6-coroutines:test
```

Testene vil feile til du implementerer oppgavene. Les feilmeldingene - de gir hint om hva som mangler.

## Mappestruktur

```
6-coroutines/
├── src/
│   ├── main/kotlin/reisesok/
│   │   ├── modell/           # Delte dataklasser (Fly, Hotell, etc.)
│   │   ├── util/             # Hjelpefunksjoner (log)
│   │   ├── del2/             # Oppgave: blokkerende -> suspend
│   │   ├── del4/             # Oppgave: parallell eksekvering
│   │   ├── del5/             # Oppgave: feilhåndtering
│   │   └── valgfritt/        # Bonusoppgaver (timeout, flow, mdc)
│   └── test/kotlin/reisesok/
│       ├── del2/             # Tester for del 2
│       ├── del3/             # Kun tester - utforsk structured concurrency
│       ├── del4/             # Tester for del 4
│       ├── del5/             # Tester for del 5
│       └── valgfritt/        # Tester for bonusoppgaver
└── losningsforslag/          # Referanseløsninger (kikk om du sitter fast)
```

## Hver del er uavhengig

Hver del (del2, del3, del4, del5) har sin egen pakke med egne providers og service. Hvis du henger etter på én del, kan du hoppe til neste del og starte fresh.

## Løsningsforslag

Mappen `losningsforslag/` inneholder referanseløsninger for hver del. Bruk dem om du sitter fast, men prøv først selv!

## Tips

- Les testenes feilmeldinger - de gir hint om hva som er feil
- Bruk `log()` for å se hva som skjer (tråd-navn, timestamps)
- Kjør med `-Dkotlinx.coroutines.debug` (allerede satt i build.gradle.kts) for å se coroutine-info i tråd-navn
