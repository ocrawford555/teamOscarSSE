from UserFramework import runAlgo, getMaxBuy, getMaxSell, \
    getPrice, getOverallAvg, getBuys, getSells, \
    getBestBuy, getBestSell, getPA, getTA, getROC

def Buy():
    return getPA(5) > getPA(20)

def Sell():
    return getPA(5) < getPA(20)

def PriceToBuy():
    currPrice = getBestBuy() + 1
    return currPrice

def PriceToSell():
    currPrice = getBestSell() - 1
    return currPrice

def VolToBuy():
    maxCanBuy = getMaxBuy()/50
    return maxCanBuy

def VolToSell():
    maxCanSell = getMaxSell()/50
    return maxCanSell

#runs the game
#enter name below
runAlgo('Python', Buy, Sell, PriceToBuy, PriceToSell, VolToBuy, VolToSell)

