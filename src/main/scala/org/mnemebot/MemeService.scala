package org.mnemebot

import scalikejdbc._
import scala.util.Random

object MemeService {
  implicit val session = SqlConnection.session

  def reset() ={
    sql"truncate image".update().apply()
    sql"truncate meme_image".update().apply()
  }

  def getAllMemeImages() = {
    sql"select tag, url from meme_image".map(rs => MemeImage(rs)).list.apply()
  }

  def lookupTag(tag:String) = {
    sql"select tag, url from meme_image where tag=$tag".map(rs => MemeImage(rs)).single().apply()
  }


  def tagExists(tag:String) = {
    sql"select tag, url from meme_image where tag=$tag".map(rs => MemeImage(rs)).list.apply().nonEmpty
  }

  def add(tag:String, url:String, userOpt:Option[String] = None):Unit = {
    if (!tagExists(tag)) {
      val user = userOpt.getOrElse("")
      sql"insert into meme_image (tag, url, username, created) values ($tag, $url, $user, now())".update.apply()
    }
  }

  def getRandomImages(tag:String) = {
    Random.shuffle(sql"select image_src, title, source, source_type from image where match(title,tags) against ($tag IN NATURAL LANGUAGE MODE)".map(rs => TaggedImage(rs)).list.apply()).take(10)
  }

  def getImagesForTag(tag:String) = {
    Random.shuffle(sql"select image_src, title, source, source_type from image where match(title,tags) against ($tag IN NATURAL LANGUAGE MODE)".map(rs => TaggedImage(rs)).list.apply())
  }

  def getRandomImageForTag(tag:String) = {
    Random.shuffle(sql"select image_src, title, source, source_type from image where match(title,tags) against ($tag IN NATURAL LANGUAGE MODE)".map(rs => TaggedImage(rs)).list.apply()).headOption
  }
}

case class MemeImage(tag: String, url: String)
object MemeImage extends SQLSyntaxSupport[MemeImage] {
  override val tableName = "meme_image"
  def apply(rs: WrappedResultSet) = new MemeImage(
    rs.string("tag"), rs.string("url"))
}

case class TaggedImage(imageSrc: String, source: String, sourceType:String, title:String)
object TaggedImage extends SQLSyntaxSupport[TaggedImage] {
  override val tableName = "image"
  def apply(rs: WrappedResultSet) = new TaggedImage(
    rs.string("image_src"), rs.string("source"), rs.string("source_type"), rs.string("title"))
}