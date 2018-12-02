package org.mnemebot

import org.mnemebot.utils.SplitHost
import org.scalatest.FunSuite

class UrlFilterTests extends FunSuite {
  test("splithost") {
    val urls = Array(
      "https://www.zerohedge.com/news/2018-11-29/air-force-commander-fired-drawing-dicks-inside-b-52-cockpit",
      "https://www.dailywire.com/news/38884/leaked-documents-google-employees-considered-ashe-schow?utm_source=facebook&utm_medium=social&utm_content=062316-news&utm_campaign=benshapiro&fbclid=IwAR1y1LjBwoZgMTgjDViSjjhCH8NYJsHscg6k7BPW0zmL1Efp5AejViypUOM",
      "https://www.cnn.com/2018/12/01/politics/george-h-w-bush-dead/index.html",
      "https://www.msnbc.com/brian-williams/watch/fmr-pres-george-h-w-bush-dies-at-the-age-of-94-1385550915731",
      "https://www.cbsnews.com/news/michael-cohen-attorneys-say-he-deserves-no-prison-time/",
      "https://www.huffingtonpost.com/entry/texas-voter-fraud-prison_us_5c01a9afe4b0a173c02305c1",
      "https://www.breitbart.com/politics/2018/12/01/political-world-pays-tribute-to-george-h-w-bush/",
      "https://www.foxbusiness.com/markets/markets-to-close-on-dec-5-after-trump-names-it-national-day-of-mourning-for-george-h-w-bush",
      "https://dailycaller.com/2018/12/01/michael-cohen-robert-mueller-time-served/",
      "https://www.nytimes.com/2018/12/01/us/politics/trump-bush-praise-history.html?action=click&module=Spotlight&pgtype=Homepage",
      "https://www.washingtonpost.com/opinions/bill-clinton-george-hw-bushs-oval-office-note-to-me-revealed-the-heart-of-who-he-was/2018/12/01/e32966de-f56e-11e8-80d0-f7e1948d55f4_story.html?utm_term=.27e8d0027df4"
    )
    val s = urls.map(url => SplitHost.fromURL(url))
    println(s.toString)
  }

  test("extract urls") {
    val data = Array(
      "blah blah blah https://yahoo.com x y z",
      "asdf https://dailycaller.com/2018/12/01/michael-cohen-robert-mueller-time-served/ adf  werer https://www.msnbc.com/brian-williams/watch/fmr-pres-george-h-w-bush-dies-at-the-age-of-94-1385550915731 "
    )
    val s = data.map(x=>SplitHost.extractUrls(x))
    println(s.toString)
    val s2 = data.flatMap(SplitHost.extractDomains)
    println(s2.toString)
  }
}
