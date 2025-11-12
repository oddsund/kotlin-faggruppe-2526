# Workshop 1: Test-Drevet Spring Boot API med Spring Data JPA

Denne workshopen lærer TDD med Spring Boot, MockK, og Spring Data JPA.

## Forutsetninger

- JDK 21 eller nyere
- IntelliJ IDEA (anbefalt) eller annen IDE med Kotlin-støtte
- Git

## Kom i gang
```bash
# Klon repository
git clone https://github.com/oddsund/kotlin-spring-boot-ws.git
cd spring-boot-workshop-ordrer

# Kjør tester for å verifisere setup
./gradlew test

# Start applikasjonen (valgfritt)
./gradlew bootRun
```

## Prosjektstruktur
```terminaloutput
src/main/kotlin/com/example/ordrer/
├── controller/          # REST endpoints
├── service/             # Forretningslogikk
├── model/               # DTOs og sealed classes
├── persistence/
│   ├── entity/         # JPA entities
│   └── repository/     # Spring Data repositories
└── config/              # Spring konfiguration
src/test/kotlin/com/example/ordrer/
├── controller/          # Controller-tester med @WebMvcTest
├── service/             # Service-tester med MockK
└── testdata/            # Object Mothers for test data
```

## Workshop Del 1: TDD med Controllers

### Oppgaver

Se `OrdreControllerTest.kt` for TODO-kommentarer.

1. **Gyldig ordre**: Implementer test og controller for 200 OK response
2. **Ordre for lav**: Implementer håndtering av TotalForLav
3. **Produkt utsolgt**: Implementer håndtering av UtAvLager
4. **Kunde inaktiv**: Implementer håndtering av KundeInaktiv
5. **Verifiser kall**: Bruk MockK slot for argument capture

### Nøkkelkonsepter

- Outside-in TDD
- `@WebMvcTest` for raske controller-tester
- MockK for mocking i Kotlin
- Sealed classes for type-safe feilhåndtering

## Workshop Del 2: Service-lag med Spring Data JPA

I del 2 er vi borti:
- `Optional.of()` og `Optional.empty()` wrapping
- Forskjellige APIs: `findById()` returnerer Optional, custom queries returnerer nullable
- Mye boilerplate i stubbing-kode

### Oppgaver

Se `OrdreServiceTest.kt` for TODO-kommentarer.

1. **Total for lav**: Test early return (ingen repository-kall)
2. **Kunde ikke funnet**: Mock `Optional.empty()`
3. **Kunde inaktiv**: Mock `Optional.of(inaktivKunde)`
4. **Produkt utsolgt**: Mock custom repository query (nullable)
5. **Gyldig ordre**: Test kompleksiteten i full stubbing

### Nøkkelkonsepter

- Spring Data JPA repositories
- Mocking av framework-spesifikke typer
- Object Mother pattern for test data

## Nyttige kommandoer
```bash
# Kjør alle tester
./gradlew test

# Kjør kun controller-tester
./gradlew test --tests "*Controller*"

# Kjør kun service-tester
./gradlew test --tests "*Service*"

# Start applikasjon
./gradlew bootRun

# Access H2 console (når app kjører)
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:ordrer_db
# Username: sa
# Password: (blank)
```

## Git branches

- `main` - Startpunkt med TODOs
- `workshop1-solution` - Komplett løsning fra Workshop 1
- `workshop2-start` - Samme som workshop1-solution (for de som ikke var med på WS1)

## Neste steg: Workshop 2

I Workshop 2 refaktorerer vi denne koden til **Ports & Adapters** (Hexagonal Architecture) og lærer:

- Hvordan erstatte mocks med fakes
- Hvordan separere domene fra framework
- Hvordan gjøre tester 10-100x raskere

**Samme funksjonalitet, mye bedre design!**

## Ressurser

- [Spring Data JPA documentation](https://docs.spring.io/spring-data/jpa/reference/)
- [MockK documentation](https://mockk.io/)
- [Kotlin sealed classes](https://kotlinlang.org/docs/sealed-classes.html)
- [Spring Boot testing](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html)