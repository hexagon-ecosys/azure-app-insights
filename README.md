*src/main/java/com/hexagonali/appinsights/AzureAppInsightsTest.java* contains **main**

main accepts 1 parameter whose value can be -

 - 0 (default)
 - 1
 - 2
 - 3

These correspond to the following 4 'run' modes -

 - 0: Single-threaded with short messages
 - 1: Single-threaded with long messages
 - 2: Multi-threaded with short messages
 - 3: Multi-threaded with long messages

---
Relevant test parameters can be controlled from *src/main/java/com/hexagonali/appinsights/TestParameters.java* -

 - max length of 'short' and 'long' messages
 - max number of threads total
 - number of messages (across all threads)

---
**Configure applicationinsights.json properly to send logs to Azure.**

**Set -javaagent:applicationinsights-agent.jar as a VM option.**
