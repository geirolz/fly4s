# Fly4s
[![Build Status](https://github.com/geirolz/fly4s/actions/workflows/cicd.yml/badge.svg)](https://github.com/geirolz/fly4s/actions)
[![codecov](https://img.shields.io/codecov/c/github/geirolz/fly4s)](https://codecov.io/gh/geirolz/fly4s)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/db3274b55e0c4031803afb45f58d4413)](https://www.codacy.com/manual/david.geirola/fly4s?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=geirolz/fly4s&amp;utm_campaign=Badge_Grade)
[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/com.github.geirolz/fly4s-core_2.13?server=https%3A%2F%2Foss.sonatype.org)](https://mvnrepository.com/artifact/com.github.geirolz/fly4s-core)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://gh.mergify.io/badges/geirolz/fly4s&style=flat)](https://mergify.io)
[![GitHub license](https://img.shields.io/github/license/geirolz/fly4s)](https://github.com/geirolz/fly4s/blob/main/LICENSE)


A lightweight, simple and functional wrapper for Flyway using cats effect.

## How to import

Fly4s supports Scala 2.13 and 3

**Sbt**
```
  libraryDependencies += "com.github.geirolz" %% "fly4s-core" % <version>
```


## Usage

Given the following ADT config 
```scala
case class DbConfig(
  name: String,
  driver: String,
  url: String,
  user: Option[String],
  password: Option[Array[Char]],
  migrationsTable: String,
  migrationsLocations: List[String]
)
```

#### validation and migration
```scala
val res: IO[ValidatedMigrateResult] = Fly4s.make[IO](
  url                 = dbConfig.url,
  user                = dbConfig.user,
  password            = dbConfig.password,
  config = Fly4sConfig(
    table     = dbConfig.migrationsTable,
    locations = Location.ofFunctor(dbConfig.migrationsLocations)
  )
).use(_.validateAndMigrate[IO])
```