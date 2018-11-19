package org.mnemebot



import scala.util.Random

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Query
import com.bot4s.telegram.Implicits._
import com.bot4s.telegram.api.Polling
import com.bot4s.telegram.api.declarative.{Commands, InlineQueries}
import com.bot4s.telegram.methods.ParseMode
import com.bot4s.telegram.models._

import scala.collection.mutable.MultiMap

/**
  * Let me Google that for you!
  */
class MnemeBot(token: String) extends ExampleBot(token)
  with Polling
  with InlineQueries
  with Commands {

  var inMemoryCommands = Storage.loadData()
  val random = new Random

  def hrcBtn(query: String): InlineKeyboardMarkup = InlineKeyboardMarkup.singleButton(
    InlineKeyboardButton.url("Search HRC email", hrcEmail(query)))

  def podestaBtn(query: String): InlineKeyboardMarkup = InlineKeyboardMarkup.singleButton(
    InlineKeyboardButton.url("Search Podestra email", podestaEmail(query)))


  def help() = {
    s"""Generates wikileak links and trolls your channel
       |
       | /start | /help - list commands
       |
       | /hrc args - generate link to search hrc emails
       |
       | /dump prints out known keys in the message scrubber
       |
       | /del key - remove key from scrubber
       |
       | /add key:response - adds new key,values to match against when searching messages (include urls and links to menes)
       |
       | /podesta args | /pod args - generate link to search podestra emails
       |
       | @MnemeBot args - Inline mode
      """.stripMargin
  }

  onCommand('start | 'help) { implicit msg =>
    reply(
      help(),
      parseMode = ParseMode.Markdown)
  }

  onCommand('dump){ implicit msg =>
    reply(
      (inMemoryMessageData()).keys.toString(),
      parseMode = ParseMode.Markdown)
  }
  onCommand('del){ implicit msg =>
    val key = msg.text.get.replaceFirst("/del","").trim()
    inMemoryCommands = Storage.remove(key)
    reply(
      (inMemoryMessageData()).keys.toString(),
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

  onMessage { implicit msg =>
    if (msg.text.isDefined) {
      // find a single random match
      Random.shuffle(inMemoryMessageData())
        .find { case (k, v) => msg.text.get.toLowerCase().contains(k)}
        .map { case (k, v) => reply(getRandomElement(v.toSeq)) }
    }
  }

  onCommand('bill) { implicit msg =>
    reply("bill is a rapist")
  }

  onCommand('add) { implicit msg =>
    if (msg.text.isDefined) {
      val args = msg.text.get.replaceFirst("/add","").trim()
      val data = args.split(":")
      inMemoryCommands = Storage.add(data(0), data.tail.mkString(""))
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

  def getRandomElement(list: Seq[String], random: Random = random): String = list(random.nextInt(list.length))

  def inMemoryMessageData():MultiMap[String, String] = {
    inMemoryCommands
  }

  def podestaEmail(query: String): String =
    Uri("https://wikileaks.org/podesta-emails")
      .withQuery(Query("q" -> query))
      .toString()

  def hrcEmail(query: String): String =
    Uri("https://wikileaks.org/clinton-emails")
      .withQuery(Query("q" -> query))
      .toString()


  onInlineQuery { implicit iq =>
    val query = iq.query

    if (query.isEmpty)
      answerInlineQuery(Seq())
    else if (query.equalsIgnoreCase("help")) {
      val textMessage = InputTextMessageContent(
        help(),
        disableWebPagePreview = true,
        parseMode = ParseMode.Markdown)

      val results = List(
        InlineQueryResultArticle(
          "help:" + query,
          inputMessageContent = textMessage,
          title = iq.query ,
          description = "Help",
          replyMarkup = None
        ))
      answerInlineQuery(results, cacheTime = 1)
    }
    else {

      val hrcTextMessage = InputTextMessageContent(
        query.altWithUrl(hrcEmail(query)),
        disableWebPagePreview = true,
        parseMode = ParseMode.Markdown)

      val podTextMessage = InputTextMessageContent(
        query.altWithUrl(podestaEmail(query)),
        disableWebPagePreview = true,
        parseMode = ParseMode.Markdown)


      val results = List(
        InlineQueryResultArticle(
          "hrc:" + query,
          inputMessageContent = hrcTextMessage,
          title = iq.query ,
          description = "Search Hillary's email",
          replyMarkup = hrcBtn(query)
        ),
        InlineQueryResultArticle(
          "pod:" + query,
          inputMessageContent = podTextMessage,
          title = iq.query ,
          description = "Search Podesta's email",
          replyMarkup = podestaBtn(query)
        )
      )

      answerInlineQuery(results, cacheTime = 1)
    }
  }
}