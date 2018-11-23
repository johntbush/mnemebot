package org.mnemebot

import org.scalatest.FunSuite

class MyTests extends FunSuite {

  test("basic MessageResponder test") {
    MessageResponder.reset()
    MessageResponder.getRandomResponse("monica")
    assert(MessageResponder.getAllTrolls("monica").size == 1)

    MessageResponder.add("monica","blah")
    assert(MessageResponder.getAllTrolls("monica").size == 2)

    MessageResponder.remove("monica")
    assert(MessageResponder.getAllTrolls("monica").isEmpty)
  }

  test("test MessageResponder spam filter") {
    MessageResponder.reset()
    MessageResponder.getRandomResponse("monica")
    assert(MessageResponder.getRandomResponse("monica").isEmpty)
    MessageResponder.add("monica","blah")
    assert(MessageResponder.getRandomResponse("monica").isDefined)
    assert(MessageResponder.getRandomResponse("monica").isEmpty)
  }

  test("test lookups with the MemeService") {
    MemeService.reset()
    import scalikejdbc._
    implicit val session = SqlConnection.session

    sql"insert into image (image_src, source, source_type, tags, title) values ('http://we.com/1','manual','uri','cortez','blah blah')".update().apply()
    sql"insert into image (image_src, source, source_type, tags, title) values ('http://we.com/2','manual','uri','cortez','blah blah 2')".update().apply()

    assert(MemeService.getRandomImageForTag("cortez").isDefined )
    assert(MemeService.getImagesForTag("cortez").size > 1)
  }
}
