package architecture.løsningsforslag;

import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.*
import com.lemonappdev.konsist.api.verify.*
import org.junit.jupiter.api.Test

class ArchitectureTest {

    @Test
    fun `domain skal ikke avhenge av infrastructure`() {
        Konsist.scopeFromProject()
            .files
            .withPackage("..domain..")
            .assertTrue { !it.hasImport { import -> import.name.contains("infrastructure") } }
    }

    @Test
    fun `domain skal ikke avhenge av eksterne biblioteker`() {
        val allowedImports = listOf(
            "kotlin..",
            "java.time..",
            "java.util..",
            "java.lang.."
        )

        Konsist.scopeFromProject()
            .files
            .withPackage("..domain..")
            .assertTrue { klass ->
                klass.imports.all { import ->
                    allowedImports.any { allowed ->
                        import.name.startsWith(allowed.removeSuffix(".."))
                    } || import.name.startsWith("com.example.domain")
                }
            }
    }

    @Test
    fun `repositories må ha interface i domain`() {
        val domainInterfaces = Konsist.scopeFromProject()
            .interfaces()
            .withPackage("..domain..")
            .filter { it.name.endsWith("Repository") }
            .map { it.name }
            .toSet()

        Konsist.scopeFromProject()
            .classes()
            .withPackage("..infrastructure..")
            .filter { it.name.contains("Repository") }
            .assertTrue { implClass ->
                val expectedInterfaceName = implClass.name
                    .removePrefix("Jdbc")
                    .removePrefix("InMemory")

                domainInterfaces.contains(expectedInterfaceName)
            }
    }

    @Test
    fun `services må ha interface i domain`() {
        val domainInterfaces = Konsist.scopeFromProject()
            .interfaces()
            .withPackage("..domain..")
            .filter { it.name.endsWith("Service") }
            .map { it.name }
            .toSet()

        Konsist.scopeFromProject()
            .classes()
            .withPackage("..infrastructure..")
            .filter { it.name.contains("Service") }
            .assertTrue { implClass ->
                val expectedInterfaceName = implClass.name
                    .removePrefix("Jdbc")
                    .removePrefix("InMemory")

                domainInterfaces.contains(expectedInterfaceName)
            }
    }

    @Test
    fun `alle test-klasser skal ende med Test`() {
        Konsist.scopeFromTest()
            .classes()
            .assertTrue { it.name.endsWith("Test") }
    }

    @Test
    fun `integration test-klasser skal ende med IntegrationTest`() {
        Konsist.scopeFromTest()
            .classes()
            .filter { klass ->
                klass.hasAnnotation { it.name == "Testcontainers" } ||
                        klass.name.contains("Integration")
            }
            .assertTrue { it.name.endsWith("IntegrationTest") }
    }

    @Test
    fun `ingen klasser skal bruke System out println`() {
        Konsist.scopeFromProject()
            .files
            .assertTrue { file ->
                !file.text.contains("System.out.println") &&
                        !file.text.contains("println(") // Kotlin println
            }
    }

    @Test
    fun `domain klasser skal ikke ha mutable state`() {
        Konsist.scopeFromProject()
            .classes()
            .withPackage("..domain..")
            .filter { it.hasModifier(KoModifier.DATA) }
            .assertTrue { klass ->
                klass.properties().all { !it.isVar }
            }
    }

    @Test
    fun `test klasser skal ha minst én test-metode`() {
        Konsist.scopeFromTest()
            .classes()
            .filter { it.name.endsWith("Test") }
            .assertTrue { klass ->
                klass.functions().any {
                    it.hasAnnotation { annotation -> annotation.name == "Test" }
                }
            }
    }

    @Test
    fun `infrastructure skal ikke avhenge av presentation layer`() {
        Konsist.scopeFromProject()
            .files
            .withPackage("..infrastructure..")
            .assertTrue { !it.hasImport { import -> import.name.contains("presentation") } }
    }

    @Test
    fun `exceptions skal ende med Exception`() {
        Konsist.scopeFromProject()
            .classes()
            .filter { it.hasParentClass { parent -> parent.name == "Exception" } }
            .assertTrue { it.name.endsWith("Exception") }
    }
}