package utils

sealed trait H2Settings {

  val name: String

  val options: Map[String, String]

  def mysqlMode: H2Settings =
    addOption("MODE" -> "MYSQL")

  def closeDelay(v: Int): H2Settings =
    addOption("DB_CLOSE_DELAY" -> v.toString)

  def addOption(o: (String, String)): H2Settings

  def getUrl(): String = {

    val baseUrl = this match {
      case _: H2InMemorySettings => s"jdbc:h2:mem:$name"
      case s: H2InFileSettings   => s"jdbc:h2:${s.dir}"
    }

    val optionsAsString: Option[String] = {
      Option.when(options.nonEmpty) {
        options.map { case (k, v) => s"$k=$v" }.mkString(";")
      }
    }

    optionsAsString match {
      case Some(optionsAsString) => s"$baseUrl;$optionsAsString"
      case None                  => baseUrl
    }
  }
}

case class H2InMemorySettings(name: String, options: Map[String, String]) extends H2Settings {
  override def addOption(o: (String, String)): H2InMemorySettings = copy(options = options + o)
}

case class H2InFileSettings(name: String, parentDir: String, options: Map[String, String]) extends H2Settings {

  val dir: String = s"$parentDir/$name"

  override def addOption(o: (String, String)): H2InFileSettings = copy(options = options + o)
}

object H2Settings {

  def inMemory(name: String, options: Map[String, String] = Map.empty): H2Settings =
    H2InMemorySettings(name, options)

  def inFile(
    name: String,
    parentDir: String = "./target/test-data/db",
    options: Map[String, String] = Map.empty
  ): H2Settings =
    H2InFileSettings(name, parentDir, options)
}
