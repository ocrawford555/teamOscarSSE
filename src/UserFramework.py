import json, requests, time

from requests.exceptions import ConnectionError

#initialise some global variables
stock = 'BAML'
url = 'http://127.0.0.1:8080/'
token = ''
stockPrice = 0
overallAvg = 0
cash = 0
maxBuy = 0
maxSell = 0

#current_milli_time = lambda: int(round(time.time() * 1000))

def getMaxBuy():
    global maxBuy
    return maxBuy

def getMaxSell():
    global maxSell
    return maxSell

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

def submitSell(urlA,volume,price,uToken):
    global stock
    urlA = urlA + 'sell/' + stock + '/' + \
           str(volume) + '/' + str(price) +'/'
    payload = {'user-token' : uToken}
    resp = requests.post(urlA,json=payload)

def update(urlA,uToken):
    global stockPrice,stock,overallAvg
    getCash(urlA,uToken)
    urlA = urlA + 'stock/' + stock
    payload = {'user-token' : uToken}
    resp = requests.get(urlA,json=payload)
    json_data = json.loads(resp.text)

    for x,v in json_data.items():
        if x == 'price':
            stockPrice = v
        elif x == 'overallAvg':
            overallAvg = v


def getCash(urlA,uToken):
    global cash,maxBuy,maxSell
    urlA += 'cash/BAML'
    payload = {'user-token' : uToken}
    resp = requests.get(urlA,json=payload)
    json_data = json.loads(resp.text)

    for x,v in json_data.items():
        if x == 'cash':
            cash = v
        elif x == 'max-can-buy':
            maxBuy = v
        elif x == 'max-can-sell':
            maxSell = v

def obtainOrderBook(urlA):
    urlA += 'orderbook/BAML'

    try:
        resp = requests.get(urlA)
        json_data = json.loads(resp.text)

        for x,v in json_data.items():
            if x == 'buy':
                print(v)
            elif x == 'sell':
                print(v)

    except ConnectionError:
        print('Error obtaining order book, sorry.')


def runAlgo(name,Buy,Sell,VolToBuy,VolToSell):
    registerUser(name, url)

    #timeStart = current_milli_time()

    while True:
            time.sleep(0.2)
            update(url,token)

            #parameters in order
            #url, quantity, price, token from registration
            if Buy() == True:
                submitBuy(url,VolToBuy(),stockPrice+1,token)

            if Sell() == True:
                submitSell(url,VolToSell(),stockPrice-1,token)

            #obtainOrderBook(url)