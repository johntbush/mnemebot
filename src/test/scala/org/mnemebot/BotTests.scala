package org.mnemebot

import org.scalatest.FunSuite
import scalikejdbc._

class BotTests extends FunSuite {
  implicit val session = SqlConnection.session

  test("basic MessageResponder test") {
    MessageResponder.reset()
    MessageResponder.getRandomResponse("monica")
    assert(MessageResponder.getAllTrolls("monica").size == 1)

    MessageResponder.addTroll("monica","blah")
    assert(MessageResponder.getAllTrolls("monica").size == 2)

    MessageResponder.remove("monica")
    assert(MessageResponder.getAllTrolls("monica").isEmpty)
  }

  test("test MessageResponder spam filter") {
    MessageResponder.reset()
    MessageResponder.getRandomResponse("monica")
    assert(MessageResponder.getRandomResponse("monica").isEmpty)
    MessageResponder.addTroll("monica","blah2")
    assert(MessageResponder.getRandomResponse("monica").isDefined)
    assert(MessageResponder.getRandomResponse("monica").isEmpty)
  }

  test("test MessageResponder fuzzy search") {
    MessageResponder.reset()
    assert(MessageResponder.getRandomResponse("monoca").isDefined)
    MessageResponder.addTroll("monica","blah")
    assert(MessageResponder.getRandomResponse("monoc").isDefined)

  }

  test("mood") {
    MessageResponder.addTroll("black","amen, African American...", -1)
    assert(MessageResponder.getRandomResponse("jew, black guy, and pope walk into a bar...").isDefined)
    assert(Mood.currentLevel == -1)
  }

  test("test lookups with the MemeService") {
    MemeService.reset()

    sql"insert into image (image_src, source, source_type, tags, title) values ('http://we.com/1','manual','uri','cortez','blah blah')".update().apply()
    sql"insert into image (image_src, source, source_type, tags, title) values ('http://we.com/2','manual','uri','cortez','blah blah 2')".update().apply()

    assert(MemeService.getRandomImageForTag("cortez").isDefined )
    assert(MemeService.getImagesForTag("cortez").size > 1)
  }

  test("test meme image tags") {
    MemeService.reset()
    MemeService.add("cortez","http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg", None)
    assert(MemeService.getAllMemeImages().size == 1)
    assert(MemeService.tagExists("cortez"))
    assert(!MemeService.tagExists("cortez2"))
    MemeService.add("cortez2","http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg", Some("frankzappa"))
    assert(MemeService.getAllMemeImages().size == 2)
    assert(MemeService.tagExists("cortez2"))
    assert(MemeGenerator.getMemeUrlFromMessage(
      "/create text1,text2,cortez").getOrElse("") == "https://memegen.link/custom/text1/text2.jpg?alt=http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg")
  }

  test("foe"){
    val memeBot= new MnemeBot("asdf")
    MessageResponder.reset()
    MessageResponder.addFoe("cnn")
    MessageResponder.addFoe("msnbc")
    val foes = MessageResponder.getFoes()
    assert(foes.size == 2)
    assert(foes.contains("cnn"))
    assert(foes.contains("msnbc"))
    assert(MessageResponder.urlFilter("asdf http://www.cnn.com qwera qweer"))
    assert(!MessageResponder.urlFilter("asdf http://www.breitbart.com qwera qweer"))

    MessageResponder.addTroll("cnn","fake news!", -1)

    assert(MessageResponder.getRandomResponse("asdf asdf https://www.cnn.com/2018/12/01/politics/george-h-w-bush-dead/index.html asdfasdf ").get.equals("fake news!"))
    assert(MessageResponder.getRandomResponse("asdf asdf https://www.breitbart.com/politics/2018/12/01/political-world-pays-tribute-to-george-h-w-bush/ asdfasdf ").isEmpty)

  }

  test("test meme create"){
    val memeBot= new MnemeBot("asdf")
    val createCommand1 = memeBot.parseCreateMessage("create text1,text2,http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg")
    assert(createCommand1.isDefined && createCommand1.get.equals(CreateCommand("text1","text2","http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg")))

    val createCommand2 = memeBot.parseCreateMessage("create text1 a,text2 b,http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg")
    assert(createCommand2.isDefined && createCommand2.get.equals(CreateCommand("text1 a","text2 b","http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg")))


    assert(MemeGenerator.getMemeUrlFromMessage(
      "/create text1 ?,text2 #,http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg").getOrElse("") ==
      "https://memegen.link/custom/text1_~q/text2_~h.jpg?alt=http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg")


   assert(
      MemeGenerator.getMemeUrl(createCommand2.get.top, createCommand2.get.bottom, createCommand2.get.urlOrTag).getOrElse("") ==
        "https://memegen.link/custom/text1_a/text2_b.jpg?alt=http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg")

    assert(MemeGenerator.getMemeUrlFromMessage(
      "/create text1,text2,http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg").getOrElse("") ==
    "https://memegen.link/custom/text1/text2.jpg?alt=http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg")
    assert(MemeGenerator.getMemeUrlFromMessage(
      "/create why_am_i,so_stupid,http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg").getOrElse("") ==
      "https://memegen.link/custom/why__am__i/so__stupid.jpg?alt=http://www.themainewire.com/wp-content/uploads/2018/08/Alexandria.jpg")

  }
}
