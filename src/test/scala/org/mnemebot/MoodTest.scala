package org.mnemebot

import org.mnemebot.Moods.{ELEVATED, SEVERE, TRIGGERED}
import org.scalatest.FunSuite

class MoodTest extends FunSuite {
  test("test") {
    Mood.adjust(1)
    assert(Mood.isMeh)
    assert(Mood.isHappy)
    Mood.adjust(-1)
    assert(Mood.isMeh)
    Mood.adjust(-1)
    assert(Mood.isSad)
    Mood.adjust(-26)
    (-26 until 26).foreach { x =>
      Mood.adjust(1)
      println(Mood.report)
    }
  }

  test("storage") {
    Mood.addTrigger(TRIGGERED, "oh my god, why don't you just die!")
    val r1 = Mood.getAllTriggers(TRIGGERED)
    assert(!r1.isEmpty)
    assert(r1.head.message.equals("oh my god, why don't you just die!"))
    Mood.addTrigger(TRIGGERED, "you are a fascist!")
    Mood.addTrigger(SEVERE, "I hate you!")
    Mood.addTrigger(ELEVATED, "yeah whatever")
    val r2 = Mood.getAllTriggers(TRIGGERED)
    assert(r2.size == 2)
    val r3 = Mood.getRandomTrigger(TRIGGERED)
    assert(r3.isDefined)
    assert(r2.contains(r3.get))
  }
}
