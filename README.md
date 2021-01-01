
 git clone https://github.com/corda/corda.git


./gradlew clean deployNodes

./build/nodes/runnodes

http GET  "http://localhost:10020/api/template/me"

http PUT  "http://localhost:10020/api/template/selfIssueSec?noOfStocks=100&symbol=IBM"

http PUT "http://localhost:10020/api/template/createSecLedger?noOfStocks=100&symbol=IBM&partyName=O%3DPartyB,%20L%3DNew%20York,%20C%3DUS"


