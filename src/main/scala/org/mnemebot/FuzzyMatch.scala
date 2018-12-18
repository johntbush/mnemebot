package org.mnemebot

import scala.math.{max,min}

object FuzzyMatch {
  def isMatch[A](a: Iterable[A], b: Iterable[A], distanceDiff:Int) = {
    distance(a,b) <= distanceDiff
  }

  def distance[A](a: Iterable[A], b: Iterable[A]) =
    ((0 to b.size).toList /: a)((prev, x) =>
      (prev zip prev.tail zip b).scanLeft(prev.head + 1) {
        case (h, ((d, v), y)) => min(min(h + 1, v + 1), d + (if (x == y) 0 else 1))
      }) last
}
