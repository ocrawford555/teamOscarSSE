from UserFramework import runAlgo, getMaxBuy, getMaxSell

def Buy():
    return True

def Sell():
    return True

def VolToBuy():
    maxCanBuy = getMaxBuy()
    return maxCanBuy/150

def VolToSell():
    maxCanSell = getMaxSell()
    return maxCanSell/150

runAlgo('Python',Buy,Sell,VolToBuy,VolToSell)

