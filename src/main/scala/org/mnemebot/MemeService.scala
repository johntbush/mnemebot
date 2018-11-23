package org.mnemebot

import scalikejdbc._
import scala.util.Random

object MemeService {
  implicit val session = SqlConnection.session

  def reset() ={
    sql"truncate troll".update().apply()
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

case class TaggedImage(imageSrc: String, source: String, sourceType:String, title:String)
object TaggedImage extends SQLSyntaxSupport[TaggedImage] {
  override val tableName = "image"
  def apply(rs: WrappedResultSet) = new TaggedImage(
    rs.string("image_src"), rs.string("source"), rs.string("source_type"), rs.string("title"))
}