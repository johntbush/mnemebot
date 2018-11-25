package org.mnemebot

import dispatch._, Defaults._

object Utils {
  def get(urlStr:String) = {
    val svc = url(urlStr)
    Http.default(svc OK as.String)
  }
}
