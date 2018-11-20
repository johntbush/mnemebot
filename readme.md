# WTF is this ?

This is a [Telegram](https://telegram.org/) bot.  You can use it to annoy the hell out of people.

It has a few commands to add data or find wikileaks link to annoy people.

You can add configuration that will fire when messages are sent to a channel the bot is part of.

If the bot finds a match for a trolling string it has in its database, it will reply with a message.

The inline part of the system is like @gif feature of telegram, only you can seed it with what content you want, 
I am going to send super annoying politcal menes to people and really irritate them, you do whatever you like.

# About the name

MemeBot was taken.  I always spell it wrong anyway, and turns out Mneme is a thing in Greek mythology.
So that makes me all clever and shit, https://en.wikipedia.org/wiki/Mneme. Somehow the Muse of memory could
be related to a bot.  I don't know exactly how, I'll leave as an exercise for the reader.

# Tech Stuff

The bot is written in Scala.  Backend storage is MySQL.
It will easily run on a free tier box.
Image scraping is done via python, more below. This is to seed the inline @MnemeBot feature.
You could create a UI to do this, or use other scripts.

The bot itself is a single process.  
You need to create your database, and seed it with whatever you want.

```
Generates wikileak links and trolls your channel:

 /start | /help - list commands

 @MnemeBot meme search

 /hrc args - generate link to search hrc emails

 /dump prints out known keys in the message scrubber

 /del key - remove key from scrubber

 /add key:response - adds new key,values to match against when searching messages (include urls and links to menes)

 /podesta args | /pod args - generate link to search podestra emails

```

Note: using key:response in the /add verses using space delim like `/add key response` is significant.  It allows you to associate many words with a response at once.  `/add red orange blue:So you like colors?` . A string with red, orange, or blue in the message would cause your response to be found.

# Build

uses sbt native packager, make whatever you want. Make yourself a DMG if that is how you roll...

## make a zip package

`sbt universal:packageBin`

## make a container

`sbt docker:publishLocal`

For more options with docker see https://www.scala-sbt.org/sbt-native-packager/formats/docker.html#build

# Configure

Look at the [sql.ddl](https://github.com/johntbush/mnemebot/blob/master/src/main/resources/sql.ddl) file, you want to make adjustments

`mysql -u root < src/main/resources/sql.ddl`

# Dependencies

- mysql 5.7+
- java 1.8+

This first go around is using mysql's full text search.  Its simple and seems to works ok so far, we will see how long that sticks around, but its nice a cheap way to get started.

# Run

```
unzip target/universal/mnemebot-0.1.zip
mnemebot-0.1/bin/mnemebot <api_token>
```

# run image scraper

Using selenium and the google chrome canary browser create scripts
that look for image urls.  The scrape.py is an example which dumps mysql insert statements, 
that can then be fed into the mysql client.

Old school web scraping tools don't work well anymore because so much html is rendered by javascript these days.

## dependencies

- python3
- google chrome canary - https://www.google.com/chrome/canary/

## init environment

```
cd scraper
./setup.sh
source .venv/bin/activate
pip install -r requirements.txt
```

## Scrape Usage
```
Usage: scrape.py [-h] URL (TAGS ...)

Arguments:
  URL        url to scrape
  TAGS       list of strings to tag image with

Options:
  -h --help
```

### example
```
python scrape.py 'https://searchconservative.com/search/?q=meme#gsc.tab=1&gsc.q=cortez%20meme&gsc.sort=' ocasio cortez ocasio-cortez commie socialist > cortez1.sql
mysql -u user -ppassword -h localhost bot < cortez1.sql
``` 


## todo

improve quality of image detection, still a lot of junk in there
