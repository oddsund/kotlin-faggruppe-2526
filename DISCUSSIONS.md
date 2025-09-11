# Diskusjonspunkter (fremtidige features)

## Rich errors
KEEP: https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0441-rich-errors-motivation.md#example

- Hvordan ville eksplisitte feiltyper endre koden vi skriver?
- Er det noe du ville brukt, eller foretrekker du å gjøre det på en annen måte?

```kotlin
data class User(val id: String)
error object NotFound

fun load(): User | NotFound

when (val user = load()) {
    is User -> println("Hello, ${user.name}")
    is Notfound -> println("Not found!")
}
```

## Name-based destructuring
KEEP: https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0438-name-based-destructuring.md

- Hvilke problemer løser det?
- Hva syns dere om syntaksen med gjentatt bruk av val?
- Er det noe dere tror vil bli brukt mye?

```kotlin
// Eksisterende posisjonsbasert
val (name, age) = person

// Ny navnebasert
(val name, val age) = person
```


## Static properties & extension functions
KEEP: https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0427-static-member-type-extension.md

- Det løses i dag med Companion Object - tror dere at dere vil foretrekke den ene eller den andre?
- Kan dere tenke dere noen eksempler hvor en vil legge til et statisk medlem til en klasse fra et bibliotek?
