# Build
`sbt universal:packageBin`

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

install google chrome canary - https://www.google.com/chrome/canary/

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