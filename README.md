# teamOscarSSE
Simulated Stock Exchange for Part IB Group Project

--

teamOscarSSE is a simulated stock exchange allowing users to write trading algorithms which compete against each other. 
- The system provides an API for users to issue buy and sell orders, as well as query the state of the exchange (such as stock prices and the order book) via HTTP requests.
- The server accepts HTTP requests from clients (allowing a language agnostic API for users) and processes and matches orders in the exchange. 
- A market maker algorithm submits buy/sell requests to the order book so that the algorithms have enough liquidity to trade, dynamically setting bid and offer prices responding to trading activity. 
- Background activity by bots allow different market scenarios (such as bull and bear markets) to be simulated. 
- A dynamic leaderboard displays graphical visualizations of the performance of each user's algorithm, providing real-time feedback for each round.

--

### Run instructions

#### Client
Create (or edit) a user by editing `/src/uk/ac/cam/teamOscarSSE/client/User.java`.

Run on the command line:

`javac -d bin -sourcepath src src/uk/ac/cam/teamOscarSSE/client/*.java`

After starting the server, execute the following command to start a client:

`java uk.ac.cam.teamOscarSSE.client.User`

#### Server
Run on the command line:

`javac -d bin -sourcepath src src/uk/ac/cam/teamOscarSSE/server/*.java`

Start the server with the following command:

`java uk.ac.cam.teamOscarSSE.server.FinalServer`

#### Leaderboard
Open `interface/index.html` while the server is running.
