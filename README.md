# Fly4s
[![Build Status](https://github.com/geirolz/fly4s/actions/workflows/cicd.yml/badge.svg?query=branch%3A0.x-9.x)](https://github.com/geirolz/fly4s/actions?query=branch%3A0.x-9.x)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/32b85d22894d479491bed9bbf64a2651)](https://app.codacy.com/gh/geirolz/fly4s/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/32b85d22894d479491bed9bbf64a2651)](https://app.codacy.com/gh/geirolz/fly4s/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/com.github.geirolz/fly4s_2.13?server=https%3A%2F%2Foss.sonatype.org)](https://mvnrepository.com/artifact/com.github.geirolz/fly4s)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/geirolz/fly4s&style=flat)](https://mergify.io)
[![GitHub license](https://img.shields.io/github/license/geirolz/fly4s)](https://github.com/geirolz/fly4s/blob/0.x-9.x/LICENSE)

<div align="center">
 <img src="images/logo.png" alt="logo" width="50%"/>
</div>

A lightweight, simple and functional wrapper for Flyway using cats-effect.

### Compatibility matrix

|                               **Fly4s**                               | **Flyway** |                        **Branch**                        |
|:---------------------------------------------------------------------:|:----------:|:--------------------------------------------------------:|
| [0.x](https://github.com/geirolz/fly4s/releases?q=v0.&expanded=false) |    9.x     | [0.x-9.x](https://github.com/geirolz/fly4s/tree/0.x-9.x) |
| [1.x](https://github.com/geirolz/fly4s/releases?q=v1.&expanded=false) |    10.x    |    [main](https://github.com/geirolz/fly4s/tree/main)    |


The most famous library to handle database migrations in Java is for sure Flyway.
It works very well and the community edition has a lot of features as well.
But Flyway APIs are written in the standard OOP paradigm, so throwing exceptions, manually managing resources, etc...

`Fly4s` is a lightweight, simple and functional wrapper for Flyway.
The aim of `Fly4s` is straightforward, wrapping the `Flyway` APIs to guarantee
referential transparency, pureness, resource handling and type safety.
To achieve this goal, `Fly4s` use the typelevel libraries `cats` and `cats-effect`.

- [Getting started](#getting-started)
- [Migrations files](#migrations-files)
- [Defining database configuration](#defining-database-configuration)
- [Instantiating Fly4s](#instantiating-fly4s)
- [Using Fly4s](#using-fly4s)
- [Conclusions](#conclusions)
- [Useful links](#useful-links)
---

### Adopters
If you are using Fly4s in your company, please let me know and I'll add it to the list! It means a lot to me.

<a href="https://www.codacy.com/">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://www.codacy.com/hubfs/Codacy_2023/Images/logo_codacy_white.svg">
   <source media="(prefers-color-scheme: light)" srcset="https://www.codacy.com/hubfs/Codacy_2023/Images/logo_codacy.svg">
   <img alt="Codacy Logo" height=55 >
 </picture>
</a>

### Getting started
Fly4s supports Scala 2.13 and 3.
The first step, import the `Fly4s` library in our SBT project.
So, add the dependency in your `build.sbt` file.
Fly4s depends on Flyway, so we'll have access to Flyway as well

```sbt
libraryDependencies += "com.github.geirolz" %% "fly4s" % "0.1.0"
```

Remember to also import the specific database module from Flyway

https://documentation.red-gate.com/flyway/flyway-cli-and-api/supported-databases

### Migrations files
As the plain Flyway, we have to create a folder that will contain our migrations scripts, often in `resources/db`.

In this folder, we have to put all our migration. We can have:
- [Baseline migrations](https://flywaydb.org/documentation/tutorials/baselineMigrations)
- [Repeatable migrations](https://flywaydb.org/documentation/tutorials/repeatable)
- [Undo migrations](https://flywaydb.org/documentation/tutorials/undo)

For this example, we are going to use a simple `baseline migration` to add a table to our database schema.

Baseline migrations are versioned and executed only when needed. The version is retrieved from the script file name.

So in this case, `V001__create_user_table.sql`, the version will be `001`(remember the double underscore after `V`).

Here we have our first migration(for MySQL database)

`resources/db/V001__create_user_table.sql`
```sql
CREATE TABLE `user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(30) NOT NULL,
    `surname` varchar(30) NOT NULL
);
```

### Defining database configuration
A good practice is to create a case class to handle the database configuration(this combined with PureConfig 
or others
config libraries make your app very robust from the configuration point of view)

Let's create a simple case class to achieve this.
```scala
case class DatabaseConfig(
  url: String,
  user: Option[String],
  password: Option[Array[Char]],
  migrationsTable: String,
  migrationsLocations: List[String]
)
``` 

N.B. apart from the common fields such `url`, `user` and `password` we'll use: `migrationsTable` to define the
Flyway table name(used to store the migration status) and `migrationsLocations` to specify a list
of the folders that contain our migration scripts.

### Instantiating Fly4s
Ok so, now we have all our migration scripts in our folder(`resources/db`), we have `Fly4s` as a dependency 
of our project,
and we have a case class that will contain the database configuration.

To instantiate `Fly4s` we can use `make` to create a new DataSource(under the hood) starting from the parameters
or `makeFor` in order to create it for an already existent `DataSource`(for example from Doobie HikariDataSource).
`make` and `makeFor` method returns a [`Resource`](https://typelevel.org/cats-effect/docs/std/resource) type class
that when released/interrupted safely close the `DataSource` connection.

In both `make` and `makeFor` methods, we can specify the parameter `config`. `Fly4sConfig` is a trivial wrapper for
flyway `Configuration` but instead of having a builder we have a case class.

```scala
import fly4s.*
import fly4s.data.*
import cats.effect.*

val dbConfig: DatabaseConfig = DatabaseConfig(
  url                 = "url",
  user                = Some("user"),
  password            = None,
  migrationsTable     = "flyway",
  migrationsLocations = List("db")
)
// dbConfig: DatabaseConfig = DatabaseConfig(
//   url = "url",
//   user = Some(value = "user"),
//   password = None,
//   migrationsTable = "flyway",
//   migrationsLocations = List("db")
// )

val fly4sRes: Resource[IO, Fly4s[IO]] = Fly4s.make[IO](
  url                 = dbConfig.url,
  user                = dbConfig.user,
  password            = dbConfig.password,
  config = Fly4sConfig(
    table     = dbConfig.migrationsTable,
    locations = Locations(dbConfig.migrationsLocations)
  )
)
// fly4sRes: Resource[IO, Fly4s[IO]] = Allocate(
//   resource = cats.effect.kernel.Resource$$$Lambda/0x0000000705335800@3e68279d
// )
```

### Using Fly4s
Ok, we have done with the configuration!
We are ready to migrate our database schema with the power of Flyway and the safety of Functional Programming!

We can use `use` or `evalMap` from `Resource` to safely access to the Fly4s instance. In case we have
multiple `Resource`s in our application probably `evalMap` allow us to better combine them using and releasing
them all together at the same time.

We can create a simple util method to do this

```scala
import fly4s.implicits.*

def migrateDb(dbConfig: DatabaseConfig): Resource[IO, MigrateResult] =
  Fly4s.make[IO](
    url                 = dbConfig.url,
    user                = dbConfig.user,
    password            = dbConfig.password,
    config = Fly4sConfig(
      table     = dbConfig.migrationsTable,
      locations = Locations(dbConfig.migrationsLocations)
    )
  ).evalMap(_.validateAndMigrate.result)
```

### Conclusions
We have done it! So, to recap, we have:
1. Created a folder under `resources` to put our migrations(`db`)
2. Imported `Fly4s` as a dependency in our project
3. Created a configuration case class to describe our database configuration
4. Instantiated a `Fly4s` instance creating a new `DataSource`
5. Migrated our database using `validateAndMigrate`
6. At the application shutdown/interruption `Resource`(from cats-effect) will safely release the `DataSource`

With a few lines, we have migrated our database safely handling the connection and the configuration.

As flyway, Fly4s provides multiple methods such as:
- validateAndMigrate
- migrate
- undo
- validate
- clean
- info
- baseline
- repair


##### Useful links
- https://flywaydb.org/documentation
- https://typelevel.org/cats/
- https://typelevel.org/cats-effect/
- https://pureconfig.github.io/
