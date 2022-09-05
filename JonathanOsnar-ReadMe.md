How to run code:
- Clone the code from my github
- Run One Frame container
- Adjust configuration based on 
- Run the program
- Use Postman to test API

Design consideration:
The requirement is that One Frame service only allow up to 1k request per day / token. I'm assuming we only alloted this single token and that's become the limitation. Although the specification say it should cater for more than 10k request / day, which can be accompodated by having a cache data that will be refreshed as needed. The specification does say the cache must not exceed 5 minutes old. In reality, I don't know when the user would request it, so the worst case scenario is when user keep requesting every 5 minutes, which amount to 288 request / day, still below the limit.

Now, it's important to note that the request to one frame doesn't care about the payload. One request asking for 1 pair versus one request asking for 10 pairs is treated as same one request. I can use it for advantage. If we optimize, 1k quota can allow 1.44 minutess or 1:26.4 refresh time. So I think a 1.5 minutes refresh time (which send 960 request a day) can give the users fairly updated data within alloted quota. The rest of 40 requests can be used for retries, although I would assume One Frame has a good availability. 

Better yet, instead of keeping certain interval, I think it's better to refresh on demand. This way, instead of someone lucky enough to request at the mark of 1.5 minutes, the first user to request after 1.5 minutes will always get near real time data. So the algorithm should look like this:
User request -> Last data exceed 1.5 minutes? -> Yes -> Refresh Rate and Save to Cache -> Return pair from cache
					      -> No -> Return pair from cache


Below are some of my remarks during implementation:
- For some reason, my there is something with my port 8080. When One Frame was on port 8080, the program can't find it (404 not found), even though postman can hit the API just fine. 
- The compiler is strict and feels great to have assistant to watch for unused variables and imports.
- Found out that config should use Kebab case, which doesn't exactly match variable name in ApplicationConfig class. Might need to think of better naming convention for long config name.
- Was going to use http4s client, but tinkering for 1 days to no avail, finally settled with akka for the API client.
- Since I want to refresh the entire cache whenever needed, I'll just map the entire currencies combination. At first glance, seems there's no enumeration for case classes, so I have to manually write it. The Map required for HttpRequest Query Parameters also only yield the last pair, since the parameter name share the same key, so I have to resort to creating long Uri string.
- Since I control the API call to One Frame, there won't be a chance for One Frame to failing look up the rate.
- It seems that there I would need to create my own parser to decode offsetDateTime from JSON. I'll skip it for now, since i use the current time for the timestamp data. I also use the existing Rate structure, adding ask and bid since it's provided by One Frame and I don't think it will break the current subscriber since I only add new fields and leave the old ones alone.
- I try to implement as much as I can in functional programming way, but i haven't figured out how to implement RateCache immutable. Passing the config to the API client is also not as easy as I imagined, had to break immutability rule.
- Not sure if it's intentional, but if user give unknown currency, the program will fail at the very first when it try to convert the string to Currency data type. So I add dummy currency code to map as unknown currency.
- The error handling is very elusive. Took a while on how to understand Either keyword means, and how to operate around it. But in the end, it doesn't seems to be sufficient to just only returning the error class. Somewhere along the line, the exception got swallowed and end up crashing (in non fatal way) the process, but I can't point my finger on it.

Things to improve:
1. Add Unit Test, something that I would love to see how FP got unit tested.
2. Redo RateCache in Functional Programming way
3. I think there's a better way to abstract API client, in case other service is introduced or One Frame add another endpoint.
4. Error handling. Right now there are 2 ways the API failed:
	1. When user give unknown currency pair -> finally managed to do it
	2. When user send API while One Frame service is down and the program haven't cache any rates from One Frame before (the very beginning of running app). Once it managed to get rates from One Frame, it will try to get from cache so it's independent from One Frame in this regards. Even when it is time to refresh, although it fail to update the data, it still retain the old one, so user still get the rate, albeit a bit outdated. Might want to consider clear the cache after 5 minutes to invalidate the cache though.
5. Use http4s client instead of akka and circe instead of json spray. Although nothing wrong with using akka and its json spray, Since we already has http4s and circe, I think it's much better to use the same library, to minimize the number of dependencies. In fact, this was my first attempt but after 1 day I can't seems to get it to work, I switched to akka and json spray. I would like to revisit this again though.
6. There is a potential performance issue. Since One Frame is in my local, it didn't hit me before, but One Frame is supposedly a 3rd party source over the internet. When we are talking about internet, bandwidth become an issue. Throwing all those pairs combinations to one frame will result in a very big json response, which might result in significant delay (or even timeout) for sending them over internet. One way to fix this, is to reduce the number of pair combinations so the json become smaller. We can achieve this by setting a currency as base currency, then only ask for possible pairs with that base currency. For example, we set USD as base currency, then ask for USDEUR, USDCAD, USDAUD, and so on. We don't ask for CADAUD. Then, we work internally by calculating other pairs by converting that to base currency. Suppose we want CADAUD, we can look up to USDCAD rate to turn that to USD, then use USDAUD rate to turn it into AUD, which equivalent with CADAUD. Right now One Frame doesn't work that way (I think One Frame give random number) but I have verified that this is working with Google rate. If this is still taking too long (there are 180 currencies out there, so 179 pairs of JSON), we can drop the on-demand refresh and instead have it refresh every 1.5 minutes in the background. The user will always get their rate from the cache and so in user's experience, performance won't be a problem.

Conclusion:
It has been an exciting (and eye + head scratching) moment, doing this take home assesment. So many new stuffs I'm not aware of. The design didn't took long to make, but the actual implementation is filled with "nani the heck?" moments. When I finally manage to make the first API call, the dopamine effect goes over the top :D. I was too naive when saying the FP is just like the old procedural programming. It's not. The immutability rule take that to another level, for a good reason. Machine is consistent, either consistently right, or consistently wrong, as long as we don't change the parameter. This is consistent with my way of programming, where I always make small step then check if I'm on the right track, and use the working one as the fact base to go for next step. After doing this assessment, so many imperfection that I want to polish. So many things I still have to learn beyond the horizon! I'm looking forward to learn how the expert done it in Paidy :D