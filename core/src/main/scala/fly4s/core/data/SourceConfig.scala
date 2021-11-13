package fly4s.core.data

case class SourceConfig(
  url: Option[String]                        = None,
  user: Option[String]                       = None,
  private val _password: Option[Array[Char]] = None
) {
  def password: Option[Array[Char]] = _password.map(_.clone())
}
object SourceConfig {
  private[core] def fromNullable(url: String, user: String, password: String): SourceConfig =
    SourceConfig(
      url       = Option(url),
      user      = Option(user),
      _password = Option(password).map(_.toCharArray.clone())
    )
}
