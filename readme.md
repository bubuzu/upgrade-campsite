####Summary
* This is a "test project" made for Upgrade by Andrey Khabarov
* It used Java + Spring (It's not my main stack at the moment)

###Details
* I didn't cover everything with Unit tests. For production level code I will never do this.
* For data store I used in-memory storage (ConcurrentHashMap)
* Storage interface allows to use any key-value database (DynamoDb for example)
* All logic related to booking I put into controller (to be storage agnostic)
* I didn't make any optimisation for free-dates endpoint (In real system it should use precalculated cache)

###Testing
* Unit test
* Manual testing via Postman
