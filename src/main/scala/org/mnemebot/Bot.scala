package org.mnemebot

object Bot extends App  {
    if (args.length != 1) {
        println("""you must supply the telegram token as an arg""")
        System.exit(1)
    }
    val token = args(0)
    val bot = new MnemeBot(token)
    SqlConnection.session
    val eol = bot.run()
    println("Running...")
}



