package fly4s.data

abstract sealed class LoggerType(val tpeValue: String)
object LoggerType {
  case object Auto extends LoggerType("auto")
  case object Console extends LoggerType("console")
  case object Slf4j extends LoggerType("slf4j")
  case object Log4j2 extends LoggerType("log4j2")
  case object ApacheCommons extends LoggerType("apache-commons")
  case class Custom(fullyQualifiedClassName: String) extends LoggerType(fullyQualifiedClassName)

  def toFlywayValue(ltype: LoggerType): String = ltype.tpeValue

  def fromFlywayValue(value: String): LoggerType = value match {
    case Auto.tpeValue          => Auto
    case Console.tpeValue       => Console
    case Slf4j.tpeValue         => Slf4j
    case Log4j2.tpeValue        => Log4j2
    case ApacheCommons.tpeValue => ApacheCommons
    case custom                 => Custom(custom)
  }
}
