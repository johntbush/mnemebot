package org.mnemebot

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Query
import com.bot4s.telegram.Implicits._
import com.bot4s.telegram.api.Polling
import com.bot4s.telegram.api.declarative.{Commands, InlineQueries}
import com.bot4s.telegram.methods.ParseMode
import com.bot4s.telegram.models._
import org.apache.commons.codec.digest.DigestUtils

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
       | @MnemeBot meme search
      """.stripMargin
  }

  onCommand('start | 'help) { implicit msg =>
    reply(
      help(),
      parseMode = ParseMode.Markdown)
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
    if (msg.text.isDefined) {
      MessageResponder.getRandomResponse(msg.text.get).map(response =>
        reply(response)
      )
    }
  }

  onCommand('bill) { implicit msg =>
    reply("bill is a rapist")
  }

  onCommand('add) { implicit msg =>
    if (msg.text.isDefined) {
      val args = msg.text.get.replaceFirst("/add","").trim()
      val data = args.split(":")
      MessageResponder.add(data(0), data.tail.mkString(""), msg.from.flatMap(_.username))
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


  onInlineQuery { implicit iq =>
    val query = iq.query

    if (query.isEmpty)
      answerInlineQuery(Seq())
    else {
      val results = MemeService.getRandomImages(query).map { image =>
        InlineQueryResultGif(
          id = DigestUtils.sha256Hex(image.imageSrc),
          title = Option(image.title),
          gifUrl = image.imageSrc,
          thumbUrl = image.imageSrc,
        )
      }
      answerInlineQuery(results, cacheTime = 1)
    }
  }
}
