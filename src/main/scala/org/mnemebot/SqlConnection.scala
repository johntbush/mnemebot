package org.mnemebot

import scalikejdbc.{AutoSession, ConnectionPool, GlobalSettings, LoggingSQLAndTimeSettings}

object SqlConnection {

  val dbName = Option(System.getenv("db")).getOrElse("bot")
  val url = s"jdbc:mysql://localhost/$dbName"

  // initialize JDBC driver & connection pool
  Class.forName("com.mysql.cj.jdbc.Driver")
  ConnectionPool.singleton(url, "admin", "admin")

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = true,
    singleLineMode = true,
    printUnprocessedStackTrace = false,
    stackTraceDepth = 15,
    logLevel = 'debug,
    warningEnabled = false,
    warningThresholdMillis = 3000L,
    warningLogLevel = 'warn
  )

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

}
