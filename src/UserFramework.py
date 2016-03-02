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

#return top 5 from both buy and sell order book
buys = []
sells = []

pointAvg = []
transAvg = []
rateOfChange = []

def getMaxBuy():
    global maxBuy
    return maxBuy

def getMaxSell():
    global maxSell
    return maxSell

def getStockPrice():
    global stockPrice
    return stockPrice

def getOverallAvg():
    global overallAvg
    return overallAvg

def topBuyPrices():
    global buys
    mini = min(5, len(buys))
    top = [0, 0, 0, 0, 0]
    if len(buys) != 0:
        for i in range(0,mini):
            temp = buys[i]
            top[i] = temp.values()[0]
    return top

def topSellPrices():
    global sells
    mini = min(5, len(sells))
    top = [0, 0, 0, 0, 0]
    if len(sells) != 0:
        for i in range(0,mini):
            temp = sells[i]
            top[i] = temp.values()[0]
    return top

def topBuyQuant():
    global buys
    mini = min(5, len(buys))
    top = [0, 0, 0, 0, 0]
    if len(buys) != 0:
        for i in range(0,mini):
            temp = buys[i]
            top[i] = temp.values()[1]
    return top

def topSellQuant():
    global sells
    mini = min(5, len(sells))
    top = [0, 0, 0, 0, 0]
    if len(sells) != 0:
        for i in range(0,mini):
            temp = sells[i]
            top[i] = temp.values()[1]
    return top

def getBestBuyPrice():
    global buys
    if buys!= []:
        for k in buys:
            for a,b in k.items():
                return b
    else:
        return getStockPrice()

def getBestSellPrice():
    global sells
    if sells != []:
        for k in sells:
            for a,b in k.items():
                return b
    else:
        return getStockPrice()

def getPointAvg(index):
    global pointAvg
    if index < len(pointAvg) and pointAvg != []:
        return pointAvg[index]
    else:
        return 0

def getTransactionAvg(index):
    global transAvg
    if index < len(transAvg) and transAvg != []:
        return transAvg[index]
    else:
        return 0

def getRateOfChange(index):
    global rateOfChange
    if index < len(rateOfChange) and rateOfChange != []:
        return rateOfChange[index]
    else:
        return 0

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
    global stockPrice,stock,overallAvg,\
        pointAvg,transAvg,rateOfChange
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
        elif x == 'pointAvg':
            pointAvg = v
        elif x == 'transactionAvg':
            transAvg = v
        elif x == 'rateOfChange':
            rateOfChange = v

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
    global buys,sells
    urlA += 'orderbook/BAML'

    try:
        resp = requests.get(urlA)
        json_data = json.loads(resp.text)

        for x,v in json_data.items():
            if x == 'buy':
                buys = v[0:5]
            elif x == 'sell':
                sells = v[0:5]

    except ConnectionError:
        print('Error obtaining order book, sorry.')

def debugPrint():
    global stockPrice, overallAvg, cash, maxBuy, maxSell, \
    buys, sells, pointAvg, transAvg, rateOfChange

    print('Stock : ' + str(stockPrice))
    print('OveAge : ' + str(overallAvg))
    print('Cash : ' + str(cash))
    print('Max Buy : ' + str(maxBuy))
    print('Max Sell : ' + str(maxSell))
    print('pointAvg : ' + str(pointAvg[0]))
    print('transAvg : ' + str(transAvg[0]))
    print('ROC : ' + str(rateOfChange[2]))



def runAlgo(name,Buy,Sell,PriceToBuy,PriceToSell,VolToBuy,VolToSell):
    registerUser(name, url)

    #timeStart = current_milli_time()

    while True:
            time.sleep(0.2)
            update(url,token)

            #parameters in order
            #url, quantity, price, token from registration
            if Buy() == True:
                submitBuy(url,VolToBuy(),PriceToBuy(),token)

            if Sell() == True:
                submitSell(url,VolToSell(),PriceToSell(),token)

            obtainOrderBook(url)
            #debugPrint()