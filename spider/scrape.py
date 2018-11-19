"""Usage: scrape.py [-h] URL (TAGS ...)

Arguments:
  URL        url to scrape
  TAGS       list of strings to tag image with

Options:
  -h --help

"""
import os
import tldextract
import json
from docopt import docopt

from selenium import webdriver
from selenium.webdriver.chrome.options import Options

def image_filter(image):
  try:
    if image and image.get_attribute("src"):
      tld = tldextract.extract(image.get_attribute("src"))
      if tld.domain not in ['google'] and "thumb" not in tld.subdomain:
        return True
  except:
    return False

def url_filter(link, seen_links):
  try:
    if link.get_attribute("href") and link.get_attribute("href").startswith('http'):
      href = link.get_attribute("href")
      if (href not in seen_links):
        tld = tldextract.extract(href)
        seen_links.append(href)
        if tld.domain not in ['google']:
          return True
      seen_links.append(href)
    return False
  except:
    return False

if __name__ == '__main__':
  args = docopt(__doc__)
  chrome_options = Options()
  chrome_options.add_argument("--headless")
  chrome_options.binary_location = '/Applications/Google Chrome Canary.app/Contents/MacOS/Google Chrome Canary'

  #https://searchconservative.com/search/?q=meme#gsc.tab=1&gsc.q=cortez%20meme&gsc.sort=
  driver = webdriver.Chrome(executable_path=os.path.abspath("chromedriver"), options=chrome_options)
  driver.get(args['URL'])
  tags = args['TAGS']
  seen_links = []
  links = [link.get_attribute("href") for link in driver.find_elements_by_xpath("//a") if url_filter(link, seen_links)]

  for link in links:
    driver.get(link)
    driver.implicitly_wait(3)
    images = [image.get_attribute("src") for image in driver.find_elements_by_xpath("//img") if image_filter(image)]
    for image in images:
      for tag in tags:
        print("INSERT IGNORE INTO IMAGE (source, image_src, tag, source_type) values ('{}','{}','{}','spider');".format(link, image, tag))
