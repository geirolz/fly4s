package fly4s.data

sealed trait LoggerType
object LoggerType {
  case object Auto extends LoggerType
  case object Console extends LoggerType
  case object Slf4j extends LoggerType
  case object Log4j2 extends LoggerType
  case object ApacheCommons extends LoggerType
  case class Custom(fullyQualifiedClassName: String) extends LoggerType

  def fromFlywayValue(value: String): LoggerType = value match {
    case "auto"           => Auto
    case "console"        => Console
    case "slf4j"          => Slf4j
    case "log4j2"         => Log4j2
    case "apache-commons" => ApacheCommons
    case custom           => Custom(custom)
  }

  def toFlywayValue(ltype: LoggerType): String =
    ltype match {
      case LoggerType.Auto                            => "auto"
      case LoggerType.Console                         => "console"
      case LoggerType.Slf4j                           => "slf4j"
      case LoggerType.Log4j2                          => "log4j2"
      case LoggerType.ApacheCommons                   => "apache-commons"
      case LoggerType.Custom(fullyQualifiedClassName) => fullyQualifiedClassName
    }
}
