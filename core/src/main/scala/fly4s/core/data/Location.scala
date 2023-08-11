package fly4s.core.data

object Location {
  def apply(value: String): Location = new Location(value)
}

object Locations {
  def apply(values: String*): List[Location] = Locations(values)
  def apply(values: Seq[String])(implicit dummyImplicit: DummyImplicit): List[Location] =
    values.map(Location(_)).toList
}
