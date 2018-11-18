package org.mnemebot

import java.io._
import java.util.Base64
import java.nio.charset.StandardCharsets.UTF_8

import scala.io.Source
import scala.util.Try

object Storage {
  val fileName = "message.data"
  var data = loadData(fileName)

  def add(key:String, value:String) = {
    System.out.println("adding " + key)
    data += key ->value
    storeData(fileName)
    data
  }

  def defaultData() = {
    Map(
      "hillary" -> "Lock her up!",
      "bill" -> "Bill is a rapist!",
      "acosta" -> "Acosta is a jerk!",
      "monica" -> "Where is that cigar!",
      "kavanaugh" -> "I need a beer!"
    )
  }

  def remove(key:String) = {
    System.out.println("removing " + key)
    data -= key
    storeData(fileName)
    data
  }

  def loadData(file:String = fileName) = {
    Try {
      val data = Source.fromFile(file).mkString
      deserialise(data).asInstanceOf[Map[String, String]]
    }.toOption.getOrElse(defaultData())
  }

  def storeData(file:String) ={
    val writer = new PrintWriter(new File(fileName))
    writer.write(serialise(data))
    writer.close()
  }

  def serialise(value: Any): String = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close
    new String(
      Base64.getEncoder().encode(stream.toByteArray),
      UTF_8
    )
  }

  def deserialise(str: String): Any = {
    val bytes = Base64.getDecoder().decode(str.getBytes(UTF_8))
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val value = ois.readObject
    ois.close
    value
  }
}
