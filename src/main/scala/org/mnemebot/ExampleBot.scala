package org.mnemebot
import com.bot4s.telegram.Implicits._
import com.bot4s.telegram.api.{RequestHandler, TelegramBot}
import com.bot4s.telegram.clients.SttpClient
import slogging.{LogLevel, LoggerConfig, PrintLoggerFactory}

/** Quick helper to spawn example bots.
  *
  * Mix Polling or Webhook accordingly.
  *
  * Example:
  * new EchoBot("123456789:qwertyuiopasdfghjklyxcvbnm123456789").run()
  *
  * @param token Bot's token.
  */
abstract class ExampleBot(val token: String) extends TelegramBot {
  LoggerConfig.factory = PrintLoggerFactory()
  // set log level, e.g. to TRACE
  LoggerConfig.level = LogLevel.TRACE

  implicit val backend = SttpBackends.default
  override val client: RequestHandler = new SttpClient(token)
}
