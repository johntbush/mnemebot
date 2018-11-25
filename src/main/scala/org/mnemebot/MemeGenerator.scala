package org.mnemebot

import java.net.URLEncoder

object MemeGenerator {

  /**
    * from https://memegen.link/
    *
    * tilde + Q (~q) → question mark (?)
    * tilde + P (~p) → percentage (%)
    * tilde + H (~h) → hashtag/pound (#)
    * tilde + S (~s) → slash (/)
    * 2 single qutoes ('') → double quote (")
    *
    * underscore (_) → space ()
    * dash (-) → space ()
    * 2 underscores (__) → underscore (_)
    * 2 dashes (--) → dash (-)
    * "weLoveMemes" → "we love memes"
    **/
  def encode(str: String) = {
    str
      .replaceAll("\\?","~q")
      .replaceAll("#","~h")
      .replaceAll("/","~s")
      .replaceAll("%","~p")
      .replaceAll("//","''")
      .replaceAll("-","--")
      .replaceAll("_","__")
      .replaceAll(" ", "_")
  }

  def getMemeUrl(top:String, bottom:String, urlOrTag:String): Option[String] = {
    val urlOpt =
      if (urlOrTag.toLowerCase.startsWith("http")) {
        Option(urlOrTag)
      } else {
        MemeService.lookupTag(urlOrTag).fold(None:Option[String])(x => Option(x.url))
      }
    urlOpt.map { url =>
      s"https://memegen.link/custom/${encode(top)}/${encode(bottom)}.jpg?alt=${url}"
    }
  }
  def getMemeUrlFromMessage(msg:String): Option[String] = {
      val args = msg.replaceFirst("/create","").trim()
      val pieces = args.split(",")
      val topText = pieces(0)
      val bottomText = pieces(1)
      val urlOrTag = pieces.drop(2).mkString(",")
      getMemeUrl(topText, bottomText, urlOrTag)
    }
}
