package architecture

import org.junit.jupiter.api.Test

class ArchitectureTest {
    
    @Test
    fun `domain skal ikke avhenge av infrastructure`() {
        // TODO: Hent alle klasser i domain-package
        // TODO: Verifiser at de ikke har imports fra infrastructure
        // Hint: Konsist.scopeFromProject().classes().withPackage("..domain..")
        //       .shouldNot().dependOn("..infrastructure..")
    }
    
    @Test
    fun `domain skal ikke avhenge av eksterne biblioteker`() {
        // TODO: Domain skal være "rent" - ingen avhengigheter til
        // Spring, Exposed, HTTP-klienter, etc.
        // Tillatte unntak: kotlin.*, java.time.*
    }
    
    @Test
    fun `repositories må ha interface i domain`() {
        // TODO: Alle klasser som ender med "Repository" i infrastructure
        // må ha et tilsvarende interface i domain
    }
    
    @Test
    fun `services må ha interface i domain`() {
        // TODO: Samme som over, men for services
        // Hint: infrastructure implementasjoner kan f.eks hete "JdbcOrderRepository"
        // og domain interface må da hete "OrderRepository"
    }
    
    @Test
    fun `alle test-klasser skal ende med Test`() {
        // TODO: Verifiser at alle test-klasser følger navnekonvensjon
        // Hint: Konsist har funksjon for å sjekke test sources
    }
    
    @Test
    fun `integration test-klasser skal ende med IntegrationTest`() {
        // TODO: Alle klasser som bruker @Testcontainers eller har "Integration" i navnet
        // skal ende med "IntegrationTest"
    }
    
    @Test
    fun `ingen klasser skal bruke System-out-println`() {
        // TODO: Finn alle kall til System.out.println
        // Dette burde være logger i stedet
    }
}