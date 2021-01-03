## Security Lending using R3 Corda Blockchain platform
### Introduction
Securities lending is one of the essential business functions in the capital markets. Anyone who is looking to profit from the declining stock market would like to sell the stock at the current price (higher in his/her opinion) and lock in the profit by purchasing the stock later (at a lower price). This process is called short selling the security. Short selling allows the market participant to profit even when he/she does not own the security that he/she would like to profit from. At a macro level, short selling provides liquidity to the market and keeps the market efficient (price discovery).

To preserve market integrity, prior to selling the security the short seller is required to borrow the stock from an individual / institution the same quantity of the security that he/she is looking to short sell. The lender charges a price (determined by the supply demand) for this short-term loan. Typically, institutions who are long the security (e.g: insurance companies, asset management companies) look to earn additional yield by lending their security from long portfolio to short sellers.  

Although electronic transfer of securities is present for a while now, still there is no marketplace for securities lending. Most often these transactions take place through propriety systems. Given the multiple parties involved in the process and the need for irrefutable record of the transaction, Blockchain would fit the bill for this requirement. 
 

### Why R3 Corda?
Choice of Blockchain platform is a critical decision to be made upfront. Couple of other platforms besides R3 considered were
- **Hyperledger**

  Popular opensource Blockchain platform suited for workflow-based use cases and for public and private Blockchain implementations. Reaching consensus involves majority network participants.

- **Ethereum**

  Popular opensource Blockchain platform suited for B2C use cases. Reaching consensus involves majority participants. 
  
 R3 Corda on the other hand is more suited for financial use cases as R3 architecture does not involve non-participants in the consensus process. This is important because of the sensitivity around the financial transaction data and the efficiency it brings in the consensus process. In addition, the Corda architecture is best suited for private Blockchain network. Every participant in the network would be institutions and they would be onboarded using Doorman service.


### High level architecture


![Overview](https://github.com/Raj-Partha/seclend/blob/master/Seclend_Overview.JPG)
