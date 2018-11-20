# WTF is this ?

This is a [Telegram](https://telegram.org/) bot.  You can use it to annoy the hell out of people.

It has a few commands to add data or find wikileaks link to annoy people.

You can add configuration that will fire when messages are sent to a channel the bot is part of.

If the bot finds a match for a trolling string it has in its database, it will reply with a message.

The inline part of the system is like @gif feature of telegram, only you can seed it with what content you want.  

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
# Build
`sbt universal:packageBin`

# Configure

Look at the sql.ddl file, you want to make adjustments

`mysql -u root < src/main/resources/sql.ddl`

# Dependencies

- mysql 5.7+
- java 1.8+

# Run

Sorry I don't have a damn container for you, this crap is easy enough to just copy and fire up, 
you want a container, you little baby, go ahead and submit a PR.

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
