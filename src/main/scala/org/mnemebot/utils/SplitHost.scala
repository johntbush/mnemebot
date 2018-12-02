package org.mnemebot.utils

import scala.collection.JavaConversions.asScalaBuffer
import scala.util.matching.Regex
import com.google.common.net.InternetDomainName

case class SplitHost(subdomain: String, domain: String, tld: String)

object SplitHost {
  val LenientURLParse = new Regex("""^(\S+:)?//(.*@)?([^/:]+)""", "scheme", "user", "domain")
  val IP = new Regex("""^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$""")
  val regex = """(http|ftp|https)://([\w_-]+(?:(?:\.[\w_-]+)+))([\w.,@?^=%&:/~+#-]*[\w@?^=%&/~+#-])?""".r

  def fromURL(url: String): SplitHost = {
    val fullDomain = LenientURLParse findFirstMatchIn (url) map (_.group("domain")) getOrElse ("")
    val extracted = try {
      Some(InternetDomainName fromLenient (fullDomain))
    }
    catch {
      case iex: IllegalArgumentException => None
    }

    extracted match {
      case Some(idn) => {
        val suffix = idn.publicSuffix
        val subdomainAndDomain = idn.parts dropRight (suffix.parts.size)
        val domain = subdomainAndDomain.lastOption getOrElse ("")
        val subdomain = subdomainAndDomain.init mkString (".")
        SplitHost(subdomain, domain, suffix.name)
      }
      case _ => {
        if (isIP(fullDomain)) {
          SplitHost("", fullDomain, "")
        }
        else {
          val subdomainAndDomain = fullDomain.split(".").toList
          val domain = subdomainAndDomain.lastOption getOrElse ("")
          val subdomain = subdomainAndDomain.init mkString (".")
          SplitHost(subdomain, domain, "")
        }
      }
    }
  }

  def extractUrls(text:String) = regex.findAllIn(text).toList

  def extraxtSplitHost(text:String) = extractUrls(text).map(fromURL)

  def extractDomains(text:String) = extractUrls(text).map(fromURL).map(_.domain)

  def isIP(possibleIP: String) = IP.findFirstIn(possibleIP).isDefined
}
