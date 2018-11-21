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
  def testMesageResponderSpam():Unit = {
    MessageResponder.reset()
    MessageResponder.getRandomResponse("monica")
    assert(MessageResponder.getRandomResponse("monica").isEmpty)
    MessageResponder.add("monica","blah")
    assert(MessageResponder.getRandomResponse("monica").isDefined)
    assert(MessageResponder.getRandomResponse("monica").isEmpty)
  }


  @Test
  def testMemeService() = {
    assert(MemeService.getRandomImageForTag("cortez").isDefined )
    assert(MemeService.getImagesForTag("cortez").size > 1)
  }
}
