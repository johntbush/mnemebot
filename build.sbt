name := "mnemebot"

version := "0.1"

scalaVersion := "2.12.7"

// Core with minimal dependencies, enough to spawn your first bot.
libraryDependencies += "com.bot4s" %% "telegram-core" % "4.0.0-RC2"

// Extra goodies: Webhooks, support for games, bindings for actors.
libraryDependencies += "com.bot4s" %% "telegram-akka" % "4.0.0-RC2"

// https://mvnrepository.com/artifact/junit/junit
libraryDependencies += "junit" % "junit" % "4.10" % Test


enablePlugins(JavaAppPackaging)