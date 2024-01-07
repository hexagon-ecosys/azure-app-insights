Performance testing rig for Azure Application Insights
---
*src/main/java/com/hexagonali/appinsights/AzureAppInsightsTest.java* contains **main**. It accepts just one parameter whose value can be -

 - 0 (default)
 - 1
 - 2
 - 3

These correspond to the following 4 'test modes' -

 - 0: Single-threaded with short messages
 - 1: Single-threaded with long messages
 - 2: Multi-threaded with short messages
 - 3: Multi-threaded with long messages

---
The relevant test parameters can be controlled from *src/main/java/com/hexagonali/appinsights/TestParameters.java* -

 - the max length of 'short' and 'long' messages
 - the max number of threads
 - the total number of messages (across all threads)

The default values for these are -

 - max length of short messages: 500 characters
 - max length of long messages: 20000 characters
 - max number of threads: 20
 - total number of messages: 20000

---
**Configure applicationinsights.json properly to send logs to Azure.**

**Set -javaagent:applicationinsights-agent.jar as a VM option.**
