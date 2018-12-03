package org.mnemebot.utils

import dispatch._
import Defaults._
import org.mnemebot.MessageResponder.random

import scala.util.Random

object Utils {
  def get(urlStr:String) = {
    val svc = url(urlStr)
    Http.default(svc OK as.String)
  }

  def getRandomElement(list: Seq[String], random: Random = random): String = list(random.nextInt(list.length))

}