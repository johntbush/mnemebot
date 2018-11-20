package org.mnemebot

import org.junit.Test

class MyTests {
  @Test
  def testMesageResponder():Unit = {
    MessageResponder.reset()
    MessageResponder.getRandomResponse("monica")
    assert(MessageResponder.getAllTrolls("monica").size == 1)

    MessageResponder.add("monica","blah")
    assert(MessageResponder.getAllTrolls("monica").size == 2)

    MessageResponder.remove("monica")
    assert(MessageResponder.getAllTrolls("monica").isEmpty)
  }

  @Test
  def testMemeService() = {
    assert(MemeService.getRandomImageForTag("cortez").isDefined )
    assert(MemeService.getImagesForTag("cortez").size > 1)
  }
}
