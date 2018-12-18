package org.mnemebot
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.collections4.map.PassiveExpiringMap
import org.mnemebot.utils.SplitHost
import scalikejdbc._

import scala.util.Random

case class Troll(id: Int, message: String, tags:String, mood:Int)
object Troll extends SQLSyntaxSupport[Troll] {
  override val tableName = "troll"
  def apply(rs: WrappedResultSet) = new Troll(
    rs.int("id"), rs.string("message"), rs.string("tags"), rs.int("mood"))
}

object MessageResponder extends LazyLogging {
  implicit val session = SqlConnection.session
  val recentTrolls = new PassiveExpiringMap[Int, Troll](1000*60*5)
  val fileName = "message.data"
  val random = new Random

  def addTroll(key:String, value:String, mood:Int = 0, userOpt:Option[String] = None):Unit = {
    logger.info("adding " + key)
    val user = userOpt.getOrElse("")
    sql"insert into troll (tags, message, username, created, mood) values ($key, $value, $user, now(), $mood)".update.apply()
  }

  def addFoe(sld:String, userOpt:Option[String] = None):Unit = {
    logger.info("adding " + sld + " to foes")
    val user = userOpt.getOrElse("")
    sql"insert into sld_match (sld, friend, username, created) values (${sld.toLowerCase()}, 0, $user, now())".update.apply()
  }

  def getAllTrolls(msg:String) = {
    val results = sql"select id, message, tags, mood from troll where match(tags) against ($msg IN NATURAL LANGUAGE MODE)".map(rs => Troll(rs)).list.apply()
    if (results.nonEmpty) {
      results
    } else {
      val words = msg.split("\\s+")
      sql"select id, message, tags, mood from troll"
        .map(rs => Troll(rs))
        .list
        .apply
        .filter { troll =>
          val tags = troll.tags.split("\\s+")
          tags.exists { tag =>
            words.exists( word =>
              (word.length >= 5 && FuzzyMatch.isMatch(word, tag, 1)) ||
              (word.length >= 8 && FuzzyMatch.isMatch(word, tag, 2))
            )
          }
        }
    }
  }

  def urlFilter(text:String) = {
    // if it contains a url and match foe return true
    // else false
    val urls = SplitHost.extractDomains(text)
    if (urls.isEmpty)
      true
    else
      getFoes().intersect(SplitHost.extractDomains(text)).nonEmpty
  }

  def getRandomResponse(msg:String) = {
    Random
      .shuffle(getAllTrolls(msg))
      .headOption
      .filter { troll =>
        !recentTrolls.containsKey(troll.id) && urlFilter(msg)
      }.map { troll =>
        recentTrolls.put(troll.id, troll)
        Mood.adjust(troll.mood)
        troll.message
      }
  }

  //TODO cache
  def getFoes() ={
    sql"select sld from sld_match where friend=0".map(rs => rs.string(1)).list.apply()
  }

  def getFriends() ={
    sql"select sld from sld_match where friend=1".map(rs => rs.string(1)).list.apply()
  }

  def reset() = {
    sql"truncate troll".update().apply()
    sql"truncate sld_match".update().apply()
    insertDefaultData()
    recentTrolls.clear()
  }

  def insertDefaultData():Unit = {
    addTroll("hillary", "Lock her up!")
    addTroll("bill", "Bill is a rapist!")
    addTroll("acosta", "Acosta is a jerk!")
    addTroll("monica", "Where is that cigar!")
    addTroll("kavanaugh", "I need a beer!")
  }

  def keys = {
    sql"select id, tags, message, mood from troll".map(rs => Troll(rs)).list.apply()
      .map(_.tags.split(" ")).flatten.toSet
  }

  def remove(key:String) = {
    sql"delete from troll where match(tags) against ($key IN NATURAL LANGUAGE MODE)".update().apply()
  }

}
