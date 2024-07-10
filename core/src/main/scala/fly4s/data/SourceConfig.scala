package fly4s.data

case class SourceConfig(
  url: Option[String]                        = None,
  user: Option[String]                       = None,
  private val _password: Option[Array[Char]] = None
) {
  def password: Option[Array[Char]] = _password.map(_.clone())
}
object SourceConfig {
  private[fly4s] def fromNullable(url: String, user: String, password: String): SourceConfig =
    SourceConfig(
      url       = Option(url),
      user      = Option(user),
      _password = Option(password).map(_.toCharArray.clone())
    )
}
