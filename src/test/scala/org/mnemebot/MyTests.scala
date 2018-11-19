package org.mnemebot

import org.junit.Test

class MyTests {
  @Test
  def testMesageResponder():Unit = {
    MessageResponder.reset()
    assert(MessageResponder.data("monica").size == 1)
    println(MessageResponder.data)

    MessageResponder.add("monica","blah")
    println(MessageResponder.data)
    assert(MessageResponder.data("monica").size == 2)

    MessageResponder.remove("monica")
    assert(MessageResponder.data.get("monica").isEmpty)
  }

  @Test
  def testMemeService() = {
    assert(MemeService.getRandomImageForTag("cortez").isDefined )
    assert(!(MemeService.getRandomImageForTag("cortez").get.imageSrc.equals((MemeService.getRandomImageForTag("cortez").get.imageSrc))))
  }
}
