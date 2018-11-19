package org.mnemebot

import java.io._
import java.util.Base64
import java.nio.charset.StandardCharsets.UTF_8

import collection.mutable.{HashMap, MultiMap, Set}
import scala.io.Source
import scala.util.{Random, Try}

object MessageResponder {
  val fileName = "message.data"
  var data = loadData(fileName)
  val random = new Random

  def add(key:String, value:String) = {
    System.out.println("adding " + key)
    data.addBinding(key, value)
    storeData(fileName)
    data
  }

  def getRandomElement(list: Seq[String], random: Random = random): String = list(random.nextInt(list.length))

  def getRandomResponse(msg:String) = {
    Random.shuffle(data)
      .find { case (k, v) => msg.toLowerCase().contains(k)}
      .map { case (k, v) => getRandomElement(v.toSeq) }

  }

  def reset() = {
    data = defaultData()
  }

  def defaultData():MultiMap[String, String] = {
    val mm = new HashMap[String, Set[String]] with MultiMap[String, String]
    mm.addBinding("hillary", "Lock her up!")
    mm.addBinding("bill", "Bill is a rapist!")
    mm.addBinding("acosta", "Acosta is a jerk!")
    mm.addBinding("monica", "Where is that cigar!")
    mm.addBinding("kavanaugh", "I need a beer!")
    mm
  }

  def keys = {
    data.keys
  }

  def remove(key:String) = {
    System.out.println("removing " + key)
    data.remove(key)
    storeData(fileName)
    data
  }

  def loadData(file:String = fileName) = {
    Try {
      val data = Source.fromFile(file).mkString
      deserialise(data).asInstanceOf[MultiMap[String, String]]
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
