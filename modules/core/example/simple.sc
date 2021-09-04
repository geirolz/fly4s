import cats.effect.IO
import fly4s.core.Fly4s
import fly4s.core.data.{Fly4sConfig, Location}
import org.flywaydb.core.internal.logging.slf4j.Slf4jLog

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

Fly4s
  .make[IO](
    url      = dbConfig.url,
    user     = dbConfig.user,
    password = dbConfig.password,
    config = Fly4sConfig(
      table     = dbConfig.migrationsTable,
      locations = Location.ofFunctor(dbConfig.migrationsLocations)
    )
  )
  .use(_.validateAndMigrate[IO])
