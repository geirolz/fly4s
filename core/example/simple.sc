import cats.effect.IO
import fly4s.Fly4s
import fly4s.data.{Fly4sConfig, Locations, ValidatedMigrateResult}

case class DbConfig(
  name: String,
  driver: String,
  url: String,
  user: Option[String],
  password: Option[Array[Char]],
  migrationsTable: String,
  migrationsLocations: List[String]
)

val dbConfig: DbConfig = ???

val res: IO[ValidatedMigrateResult] = Fly4s
  .make[IO](
    url      = dbConfig.url,
    user     = dbConfig.user,
    password = dbConfig.password,
    config = Fly4sConfig(
      table     = dbConfig.migrationsTable,
      locations = Locations(dbConfig.migrationsLocations)
    )
  )
  .use(_.validateAndMigrate)
