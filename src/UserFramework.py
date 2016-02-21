import json, requests, time, sys, random

#initialise some global variables
stock = 'BAML'
url = 'http://127.0.0.1:8080/'
token = ''
stockPrice = 0
cash = 0

#current_milli_time = lambda: int(round(time.time() * 1000))

def registerUser(name, urlA):
    global token
    urlA+='register/'
    payload = {'name' : name, 'email': 'PythonIsAwesome'}
    resp = requests.post(urlA,json=payload)
    json_data = json.loads(resp.text)

    for x,v in json_data.items():
        if x == 'user-token':
            token = v

def submitBuy(urlA,volume,price,uToken):
    global stock
    urlA = urlA + 'buy/' + stock + '/' + \
           str(volume) + '/' + str(price) +'/'
    payload = {'user-token' : uToken}
    resp = requests.post(urlA,json=payload)
    #json_data = json.loads(resp.text)

    #for x,v in json_data.items():
    #    print v

def submitSell(urlA,volume,price,uToken):
    global stock
    urlA = urlA + 'sell/' + stock + '/' + \
           str(volume) + '/' + str(price) +'/'
    payload = {'user-token' : uToken}
    resp = requests.post(urlA,json=payload)
    #json_data = json.loads(resp.text)

    #for x,v in json_data.items():
    #    print v

def update(uToken,urlA):
    global stockPrice,stock
    urlA = urlA + 'stock/' + stock
    payload = {'user-token' : uToken}
    resp = requests.get(urlA,json=payload)
    json_data = json.loads(resp.text)

    for x,v in json_data.items():
        if x == 'price':
            stockPrice = v

def getCash(uToken,urlA):
    global cash
    urlA += 'cash/BAML'
    payload = {'user-token' : uToken}
    resp = requests.get(urlA,json=payload)
    json_data = json.loads(resp.text)

    for x,v in json_data.items():
        if x == 'cash':
            cash = v

registerUser('Python', url)

#timeStart = current_milli_time()

while True:
   # if current_milli_time() - timeStart > 31000:
    #    sys.exit(0)
    #else:
        time.sleep(0.1)
        update(token,url)

        #url, quantity, price, token from registration
        submitBuy(url,random.randint(1,20),stockPrice+3,token)
        submitSell(url,random.randint(1,20),stockPrice-2,token)

        getCash(token,url)
        print(cash)