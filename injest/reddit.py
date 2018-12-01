import requests

print(requests.get('https://www.reddit.com/r/the_donald/top/.json?count=100').json())

#curl -s 'https://www.reddit.com/r/the_donald/top/.json?count=100' | jq '.data.children[] | {url:.data.url,title:.data.title}'