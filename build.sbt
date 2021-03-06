name := "mnemebot"

version := "0.1"

scalaVersion := "2.12.7"

maintainer := "johntylerbush@gmail.com"

// Core with minimal dependencies, enough to spawn your first bot.
libraryDependencies += "com.bot4s" %% "telegram-core" % "4.0.0-RC2"

// Extra goodies: Webhooks, support for games, bindings for actors.
libraryDependencies += "com.bot4s" %% "telegram-akka" % "4.0.0-RC2"

// https://mvnrepository.com/artifact/junit/junit
libraryDependencies += "junit" % "junit" % "4.10" % Test

// https://mvnrepository.com/artifact/mysql/mysql-connector-java
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.13"

// https://mvnrepository.com/artifact/org.scalikejdbc/scalikejdbc
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "3.3.1"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

// https://mvnrepository.com/artifact/commons-codec/commons-codec
libraryDependencies += "commons-codec" % "commons-codec" % "1.9"

libraryDependencies += "org.apache.commons" % "commons-collections4" % "4.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

libraryDependencies += "org.dispatchhttp" %% "dispatch-core" % "1.0.0"

// https://mvnrepository.com/artifact/com.google.guava/guava
libraryDependencies += "com.google.guava" % "guava" % "12.0"

logBuffered in Test := false

enablePlugins(JavaAppPackaging)