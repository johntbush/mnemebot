package org.mnemebot

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Query
import com.bot4s.telegram.Implicits._
import com.bot4s.telegram.api.Polling
import com.bot4s.telegram.api.declarative.{Commands, InlineQueries}
import com.bot4s.telegram.methods.{ParseMode, SendMessage}
import com.bot4s.telegram.models.{InputMessageContent, _}
import org.apache.commons.codec.digest.DigestUtils
import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Let me annoy the shit out of you and everyone on this channel
  */
class MnemeBot(token: String) extends ExampleBot(token)
  with Polling
  with InlineQueries
  with Commands {

  def hrcBtn(query: String): InlineKeyboardMarkup = InlineKeyboardMarkup.singleButton(
    InlineKeyboardButton.url("Search HRC email", hrcEmail(query)))

  def podestaBtn(query: String): InlineKeyboardMarkup = InlineKeyboardMarkup.singleButton(
    InlineKeyboardButton.url("Search Podestra email", podestaEmail(query)))


  def helpText() = {
    s"""```
       |Generate memes and troll your channel:
       |
       | /help - list commands
       | /create top_text,bottom_text,image - generates a meme
       | /add tag image_url - adds a new image to meme repo
       | /list - lists meme tags
       | /tadd key:response - adds new key,values to match against when searching messages (include urls and links to menes)
       | /del key - remove key from scrubber
       | /dump prints out known keys in the message scrubber
       | /podesta args | /pod args - generate link to search podestra emails
       | /hrc args - generate link to search hrc emails
       |
       | Inline Options:
       | @MnemeBot create
       | @MnemeBot list
       |```
      """.stripMargin
  }

  onCommand('start | 'help) { implicit msg =>
    reply(
      helpText(),
      parseMode = ParseMode.Markdown
    )
  }

  onCommand('dump){ implicit msg =>
    reply(
      MessageResponder.keys.toString(),
      parseMode = ParseMode.Markdown)
  }
  onCommand('del){ implicit msg =>
    val key = msg.text.get.replaceFirst("/del","").trim()
    reply(
      MessageResponder.keys.toString(),
      parseMode = ParseMode.Markdown)
  }

  onCommand('podesta | 'pod) { implicit msg =>
    withArgs { args =>
      val query = "Search Podesta's email for: " + args.mkString(" ")

      replyMd(
        query.altWithUrl(podestaEmail(query)),
        disableWebPagePreview = true
      )
    }
  }

  onCommand('search) { implicit msg =>
    withArgs { args =>
      val query = "Search Podesta's email for: " + args.mkString(" ")

      replyMd(
        query.altWithUrl(podestaEmail(query)),
        disableWebPagePreview = true
      )
    }
  }

  onMessage { implicit msg =>
    if (msg.text.isDefined)
        MessageResponder.getRandomResponse(msg.text.get).map(response =>
          reply(response)
        )
  }

  onCommand('bill) { implicit msg =>
    reply("bill is a rapist")
  }

  onCommand('create) { implicit msg =>
    val response =
      MemeGenerator
      .getMemeUrlFromMessage(msg.text.get)
      .fold("unable to make your meme, maybe your image tag is wrong, try /list")(url =>s"""[your meme's link](${url})""")
    reply(
      text = response,
      parseMode = ParseMode.Markdown)
  }

  onCommand('list) { implicit msg =>
    MemeService.getAllMemeImages().foreach { memeImage =>
      reply(
        text = s"[${memeImage.tag}‌‌](${memeImage.url})",
        parseMode = ParseMode.Markdown)
    }
  }

  onCommand('tadd) { implicit msg =>
    if (msg.text.isDefined) {
      val args = msg.text.get.replaceFirst("/tadd","").trim()
      val data = args.split(":")
      MessageResponder.addTroll(data(0), data.tail.mkString(""), msg.from.flatMap(_.username))
    }
  }

  onCommand('add) { implicit msg =>
    if (msg.text.isDefined) {
      val args = msg.text.get.replaceFirst("/add","").trim()
      val data = args.split(" ")
      MemeService.add(data(0), data.tail.mkString(" "), msg.from.flatMap(_.username))
    }
  }


  onCommand('hrc) { implicit msg =>
    withArgs { args =>
      val query = "Search Hillary's email for: "+ args.mkString(" ")
      replyMd(
        query.altWithUrl(hrcEmail(query)),
        disableWebPagePreview = true
      )
    }
  }

  def podestaEmail(query: String): String =
    Uri("https://wikileaks.org/podesta-emails")
      .withQuery(Query("q" -> query))
      .toString()

  def hrcEmail(query: String): String =
    Uri("https://wikileaks.org/clinton-emails")
      .withQuery(Query("q" -> query))
      .toString()



  def parseCreateMessage(query:String):Option[CreateCommand] = {
    if (!query.startsWith("create"))
      None
    else if (query.isEmpty || !query.contains(" ") || !query.contains(","))
      None
    else {
      val piece = query.replaceFirst("create","").trim()
      val pieces = piece.split(",")
      Option(CreateCommand(pieces(0).trim, pieces(1).trim, pieces(2).trim))
    }
  }

  onInlineQuery { implicit iq =>
    val query = iq.query
    val command = query.split(" ")(0)
    command match {
      case "create" =>
        answerInlineQuery(
          Seq.empty[InlineQueryResultPhoto],
          switchPmParameter = Option("create_memes"),
          switchPmText = Option("create_memes"),
          cacheTime = 1)
      case "list" =>
        answerInlineQuery(MemeService.getAllMemeImages().map { memeImage =>
          InlineQueryResultPhoto(
            id = DigestUtils.sha256Hex(memeImage.tag),
            photoUrl = memeImage.url,
            thumbUrl = memeImage.url,
            caption = memeImage.tag,
            title = memeImage.tag,
            description = memeImage.tag,
          )
        }, cacheTime = 1)
      case _ =>
    }
  }

  def getAnswer(createCommand:CreateCommand): Seq[InlineQueryResultPhoto] ={
    MemeGenerator.getMemeUrl(createCommand.top, createCommand.bottom, createCommand.urlOrTag)
      .fold(Seq.empty[InlineQueryResultPhoto]) { url =>
        Seq(InlineQueryResultPhoto(
          id = DigestUtils.sha256Hex(createCommand.top + createCommand.bottom + createCommand.urlOrTag),
          photoUrl = url,
          thumbUrl = url
        ))
      }

  }
}


// @MnemeBot create test a,test b,hillary
case class CreateCommand(top:String, bottom:String, urlOrTag:String)


