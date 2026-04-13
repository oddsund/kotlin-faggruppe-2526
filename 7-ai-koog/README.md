# Workshop 7: AI med Koog

Dette er baseline-applikasjonen for Workshop 7. Den bygger direkte på Workshop 5 (Ktor + Exposed), men Koin er fjernet til fordel for manuell DI via `AppFactory`. AI-funksjonalitet legges til i løpet av workshopen.

## Forutsetninger

- JDK 21+
- En Anthropic API-nøkkel satt som miljøvariabel: `ANTHROPIC_API_KEY`

## Kjøre applikasjonen

```bash
./gradlew :7-ai-koog:run
```

Applikasjonen starter på `http://localhost:8080`.

## Verifisere Koog-oppsett (HelloKoog.kt)

Før workshopen starter, kjør `HelloKoog.kt` for å bekrefte at API-nøkkelen din fungerer og at Koog er konfigurert riktig:

```bash
ANTHROPIC_API_KEY=sk-ant-... ./gradlew :7-ai-koog:runHelloKoog
```

Eller kjør `no.bekk.workshop.ai.HelloKoogKt` direkte fra IDE-en din.

Du skal se en respons fra Claude Haiku i terminalen.

## Kjøre tester

```bash
./gradlew :7-ai-koog:test
```

## Struktur

```
src/main/kotlin/no/bekk/workshop/
├── ai/
│   └── HelloKoog.kt        ← Oppstartssjekk for workshopen
├── domain/                 ← Domenemodell (Ordre, Kunde, ValideringsResultat, ...)
├── repository/             ← KundeRepository, LagerRepository + Exposed-implementasjoner
├── dto/                    ← Request/response-typer
├── db/                     ← Exposed-tabeller
├── plugins/                ← Ktor-plugins (Routing, Serialization, Koog)
├── AppFactory.kt           ← Composition root / manuell DI
├── AppConfig.kt
└── Application.kt
```

## AI-funksjonalitet

Koog (`ai.koog:koog-agents:0.8.0`) er lagt til som dependency og Koog-pluginen er koblet inn i Ktor-applikasjonen. Selve AI-funksjonaliteten (verktøy, agenter, strukturert output) legges til i løpet av workshopen.
