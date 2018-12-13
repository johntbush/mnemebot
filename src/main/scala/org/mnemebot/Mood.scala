package org.mnemebot

import org.mnemebot.MessageResponder.getAllTrolls
import org.mnemebot.Moods._
import org.mnemebot.ScoreDurations.ScoreDuration
import scalikejdbc.WrappedResultSet
import scalikejdbc._

import scala.util.Random

object Mood {
  private var mood: Int = 0
  implicit val session = SqlConnection.session

  def reset = mood = 0

  def adjust(level:Int) = {
    mood = mood + level
    mood
  }

  def isHappy = mood > 0
  def isSad = mood < 0
  def isMeh = mood == 0 || (mood > 0 && mood < 6) || (mood < 0 && mood > -6)

  def isTriggered = mood < -25

  def isClimaxing = mood >= 25

  def report = s"level:$mood $currentDescription"

  def currentDescription = currentMood.toString

  def currentMood = moodVal(mood)

  def currentLevel = mood

  def moodAsInt(moodVal:MoodVal): Int = {
    moodVal match {
      case MEH => 1
      case SECURE => 2
      case GOOD => 3
      case HAPPY => 4
      case BUZZED => 5
      case CLIMAXING => 6
      case LOW => -1
      case GUARDED => -2
      case ELEVATED => -3
      case HIGH => -4
      case SEVERE => -5
      case TRIGGERED => -6
      case CONFUSED => 0
    }
  }

  def moodVal(mood:Int): MoodVal =
    mood match {
      case it if 0 until 5 contains it =>MEH
      case it if 5 until 10 contains it =>SECURE
      case it if 10 until 15 contains it =>GOOD
      case it if 15 until 20 contains it =>HAPPY
      case it if 20 until 25 contains it =>BUZZED
      case it if it >= 25 =>CLIMAXING
      case it if -5 until 0 contains it =>LOW
      case it if -10 until -5 contains it =>GUARDED
      case it if -15 until -10 contains it =>ELEVATED
      case it if -20 until -15 contains it =>HIGH
      case it if -25 until -20 contains it =>SEVERE
      case it if it < -25 =>TRIGGERED
      case _ =>CONFUSED
    }

  def addTrigger(mood:MoodVal, message:String, userOpt:Option[String] = None) ={
    val user = userOpt.getOrElse("")
    val level = moodAsInt(mood)
    sql"insert into mood_trigger (mood_level, trigger_text, username, created) values ($level, $message, $user, now(), $mood)".update.apply
  }

  def getAllTriggers(level:Int): List[MoodTrigger] ={
    sql"select id, mood_level, trigger_text from mood_trigger where mood_level=$level".map(rs => MoodTrigger(rs)).list.apply()
  }

  def getAllTriggers(moodVal: Moods.MoodVal): List[MoodTrigger] = getAllTriggers(moodAsInt(moodVal))

  def getRandomTrigger(moodVal:MoodVal) ={
    Random
      .shuffle(getAllTriggers(moodVal))
      .headOption
  }

  case class MoodTrigger(id: Int, mood:Int, message: String)
  object MoodTrigger extends SQLSyntaxSupport[MoodTrigger] {
    override val tableName = "mood_trigger"
    def apply(rs: WrappedResultSet) = new MoodTrigger(
      rs.int("id"), rs.int("mood_level"), rs.string("trigger_text"))
  }

  case class TriggerLog(id: Int, mood:Int, username: String, isTrigger: Boolean)
  object TriggerLog extends SQLSyntaxSupport[TriggerLog] {
    override val tableName = "trigger_log"
    def apply(rs: WrappedResultSet) = new TriggerLog(
      rs.int("id"), rs.int("mood_level"), rs.string("username"), rs.boolean("is_trigger"))
  }

  def logTrigger(user:String, mood:Int) = {
    sql"insert into trigger_log (mood_level, username, created) values ($mood, $user, now())".update.apply
  }

  def getReport(scoreDuration: ScoreDuration = ScoreDurations.ALL): Map[String, Int] = {
    val scores = scoreDuration match {
      case ScoreDurations.DAY =>
        sql"select username, sum(mood_level) as total from trigger_log where created > CURDATE() AND created <= CURDATE() + INTERVAL 1 DAY group by username".map {
          rs => (rs.string("username"), rs.int("total"))
        }
      case ScoreDurations.MONTH =>
        sql"select username, sum(mood_level) as total from trigger_log where created > CURDATE() - INTERVAL 1 MONTH AND created <= CURDATE() + INTERVAL 1 MONTH  group by username".map {
          rs => (rs.string("username"), rs.int("total"))
        }
      case ScoreDurations.ALL =>
        sql"select username, sum(mood_level) as total from trigger_log group by username".map {
          rs => (rs.string("username"), rs.int("total"))
        }
    }
    scores.list.apply().toMap
  }

}

object ScoreDurations {
  sealed trait ScoreDuration
  case object DAY extends ScoreDuration
  case object ALL extends ScoreDuration
  case object MONTH extends ScoreDuration
}

object Moods {
  sealed trait MoodVal
  case object MEH extends MoodVal
  case object SECURE extends MoodVal
  case object GOOD extends MoodVal
  case object HAPPY extends MoodVal
  case object BUZZED extends MoodVal
  case object CLIMAXING extends MoodVal

  case object LOW extends MoodVal
  case object GUARDED extends MoodVal
  case object ELEVATED extends MoodVal
  case object HIGH extends MoodVal
  case object SEVERE extends MoodVal
  case object TRIGGERED extends MoodVal

  case object CONFUSED extends MoodVal

  val moods = Seq(LOW, GUARDED, ELEVATED, HIGH, SEVERE, TRIGGERED)


}