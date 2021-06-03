1. How long did you spend on the coding test? What would you add to your solution if you spent more time on it? If you didn't spend much time on the coding test then use this as an opportunity to explain what you would add.
Arund 20 hours. There was various research I had to do to get some things working (particularly around ratpack and Redis).

There are todos littered around the code on how I would like to improve it. 
I would like to experiment with kubernetes, improve the logging, as well as handle json serialisation better. 
I would also like to implement usernames/passwords to associate tokens with, so that the same account can be accessed after the expiry time.

2. What was the most useful feature that was added to Java 8? Please include a snippet of code that shows how you've used it.
Java 8's most useful feature was the streams API. I have done little data traversal in this challenge and haven't really seen an excuse to use it.

At one point, when retrieving transactions, I was traversing over the list to generate a JSON list using foreach (although this was collections, not streams).
I realised afterwards that that `toString` method on a list returns in json format anyway so I figured this was a more maintainable approach (give that tests confirm the format is correct).

3. What is your favourite framework / library / package that you love but couldn't use in the task? What do you like about it so much?
I've recently been getting more in to the Spring framework and found that the initial setup using spring boot is much simpler than ratpack, although ratpack then comes with the advantage that it doesn't get in the way of the rest of the codebase, or at least to a lesser degree than Spring can.

4. What great new thing you learnt about in the past year and what are you looking forward to learn more about over the next year?
I want to modernise my understanding of Java. I've been following updates but have had little chance to use the modules API or many of the changes since Java 9.

5. How would you track down a performance issue in production? Have you ever had to do this? Can you add anything to your implementation to help with this?

In this implementation, it would be useful to extend it to include some sort of health monitoring system, that helps diagnoise which service is problematic.
This could potentially be a separate container instance that calls the API endpoints and monitors how this affects memory usage, as well as the speed in which the APIs respond.
Going further than this, potentially, could be written alongside a circuit breaker pattern such that problematic, CPU intensice services are scaled down automatically to ensure they don't over consume memory and adversly affect performance of other containers/services on this host.

In the past I have relied on checking memory usage on the server. Typically changes have been fine grained so it's fairly simple to see cause and affect. 
Whilst this isn't in production, I have been involved in a project to monitor memory usage with high server throughput using RPT scripts. We then retrieved memory usage and output this to a dashboarding system that allowed us to compare memory usage from different test runs (confirming no regressions from build to build).
I'm also aware of JVM UI memory software such as jvisualvm, but I haven't used these frequently.


6. How would you improve the APIs that you just used?
There are various todos for improvement throughout the code.
Many of these improvements are listed in the answer to question 1.
I would also like to experiment with having mutliple API services running. To start with these could all be communicating with one Redis service but going forwards we may want to introduce some sort of Cache-aside implementation for data stored in Redis.

7. Please describe yourself in JSON format.

```
{
"name":"Joseph Plant",
"Employment": "Software Developer",
"Education":[
{"University": "Bangor University", "Degree Type": "Masters", "Subject": "Computer Systems", "Grade": "Distinction"},
{"University": "Bangor University", "Degree Type": "Undergraduate", "Subject": "Computer Science", "Grade": "2:2"}
],
"Hobbies":[
"Skiing", "Guitar", "Video Games"
]
}
```

8. What is the meaning of life?

42, but what is the question?