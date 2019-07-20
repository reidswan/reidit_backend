# Reidit Backend - backend for a reddit clone written in Kotlin

## Project structure
All source code is housed in `src/main/kotlin`

Database schemas and scripts are in `src/schema`

`src/test` holds tests

| Description | Location |
|---|---|
| Entry point | [Main.kt](src/main/kotlin/com/reidswan/reidit/Main.kt) |
| Ktor config | [routes/Setup.kt](src/main/kotlin/com/reidswan/reidit/routes/Setup.kt) |
| App config | [config/Config.kt](src/main/kotlin/com/reidswan/reidit/config/Config.kt) |
| Endpoint definitions | [routes/Routes.kt](src/main/kotlin/com/reidswan/reidit/routes/Routes.kt)
| Controllers & Business Logic | [controllers/*.kt](src/main/kotlin/com/reidswan/reidit/controllers/) |
| Models and Data Access | [data/*.kt](src/main/kotlin/com/reidswan/reidit/data)|
