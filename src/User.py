from UserFramework import runAlgo, getMaxBuy, getMaxSell, \
    getStockPrice, getOverallAvg, getBestBuyPrice, getBestSellPrice, \
    getPointAvg, getTransactionAvg, getRateOfChange, getCash, \
    topBuyPrices, topSellPrices, topBuyQuant, topSellQuant

def Buy():
    return True

def Sell():
    return True

def priceToBuy():
    return getStockPrice()

def priceToSell():
    return getStockPrice()

def volumeToBuy():
    return getMaxBuy()

def volumeToSell():
    return getMaxSell()

#runs the game
#enter name below to begin trading!
runAlgo('Enter_Name_Here', Buy, Sell, priceToBuy, priceToSell, volumeToBuy, volumeToSell)

