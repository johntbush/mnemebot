package org.mnemebot

import scalikejdbc._
import scala.util.Random

object MemeService {
  // initialize JDBC driver & connection pool
  Class.forName("com.mysql.cj.jdbc.Driver")
  ConnectionPool.singleton("jdbc:mysql://localhost/bot", "admin", "admin")

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = true,
    singleLineMode = true,
    printUnprocessedStackTrace = false,
    stackTraceDepth= 15,
    logLevel = 'debug,
    warningEnabled = false,
    warningThresholdMillis = 3000L,
    warningLogLevel = 'warn
  )

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

  def getRandomImages(tag:String): Seq[TaggedImage] = {
    val like = s"$tag%"
    Random.shuffle(sql"select * from image where tag like $like".map(rs => TaggedImage(rs)).list.apply()).take(10)
  }

  def getRandomImageForTag(tag:String):Option[TaggedImage] = {
    Random.shuffle(sql"select * from image where tag = $tag".map(rs => TaggedImage(rs)).list.apply()).headOption
  }
}

case class TaggedImage(imageSrc: String, source: String, sourceType:String, tag:String)
object TaggedImage extends SQLSyntaxSupport[TaggedImage] {
  override val tableName = "image"
  def apply(rs: WrappedResultSet) = new TaggedImage(
    rs.string("image_src"), rs.string("source"), rs.string("source_type"), rs.string("tag"))
}