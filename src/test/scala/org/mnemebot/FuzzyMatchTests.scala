package org.mnemebot

import org.scalatest.FunSuite

class FuzzyMatchTest extends FunSuite {
  test("test1") {
    assert(FuzzyMatch.distance("poop","doop") == 1)
    assert(FuzzyMatch.distance("clinton","clintin") == 1)
    assert(FuzzyMatch.isMatch("clinton","clintin", 1))
    assert(FuzzyMatch.isMatch("clinton","clintn", 1))
    assert(FuzzyMatch.isMatch("cortez","cortex", 1))
  }
}

