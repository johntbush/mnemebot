package org.mnemebot
import org.apache.commons.collections4.map.PassiveExpiringMap
import scalikejdbc._

import scala.util.Random


case class Troll(id: Int, message: String, tags:String)
object Troll extends SQLSyntaxSupport[Troll] {
  override val tableName = "troll"
  def apply(rs: WrappedResultSet) = new Troll(
    rs.int("id"), rs.string("message"), rs.string("tags"))
}

object MessageResponder {
  implicit val session = SqlConnection.session
  val recentTrolls = new PassiveExpiringMap[Int, Troll](1000*60*5)
  val fileName = "message.data"
  val random = new Random

  def addTroll(key:String, value:String, userOpt:Option[String] = None):Unit = {
    System.out.println("adding " + key)
    val user = userOpt.getOrElse("")
    sql"insert into troll (tags, message, username, created) values ($key, $value, $user, now())".update.apply()
  }

  def getRandomElement(list: Seq[String], random: Random = random): String = list(random.nextInt(list.length))

  def getAllTrolls(msg:String) = {
    sql"select id, message, tags from troll where match(tags) against ($msg IN NATURAL LANGUAGE MODE)".map(rs => Troll(rs)).list.apply()
  }

  def getRandomResponse(msg:String) = {
    Random
      .shuffle(getAllTrolls(msg))
      .headOption
      .filter( troll => !recentTrolls.containsKey(troll.id))
      .map { troll =>
        recentTrolls.put(troll.id, troll)
        troll.message
      }
  }

  def reset() = {
    sql"truncate troll".update().apply()
    insertDefaultData()
  }

  def insertDefaultData():Unit = {
    addTroll("hillary", "Lock her up!")
    addTroll("bill", "Bill is a rapist!")
    addTroll("acosta", "Acosta is a jerk!")
    addTroll("monica", "Where is that cigar!")
    addTroll("kavanaugh", "I need a beer!")
  }

  def keys = {
    sql"select id, tags, message from troll".map(rs => Troll(rs)).list.apply()
      .map(_.tags.split(" ")).flatten.toSet
  }

  def remove(key:String) = {
    sql"delete from troll where match(tags) against ($key IN NATURAL LANGUAGE MODE)".update().apply()
  }

}
