
# Logging servlet filter

This project just implements logging servlet filter, which logs requests and responses.

I originally had this implementation in io.github.antonsjava:web-filter. But I found some limitations 
in configuration which I'm not able to implement in original project.  

There are two main branches 

 - 2.x  - javax version
 - 3.x  - jakarta version

API is same in both branches 

## log messages

There are three log messages produced by filter 
 
 - message displayed before request is processed
 - message with request data displayed after request is processed
 - message with response data displayed after request is processed

You can configure if the message is displayed and there are options how to configure request 

### request start message

This message looks like 
~~~
REQ[2] POST /rest/book vvv
~~~

In general it contains

 - prefix (in example 'REQ'). You can configure different text. If prefix text is null whole message is not diplayed.  
 - number (in example '[2]'). This is request index counted from start of the application. It is useful to pair this three messages together
 - request method (in example 'POST'). 
 - request path (in example '/rest/book'). 
 - constant string (in example 'vvv'). Just marks that other log messages from request processing is bellow this message


### request data message

This message looks like 
~~~
REQ[2] POST /rest/book identity(admin) headers(Token: 1234) payload[{"title":"pokus","abstractText":"something long","author":{"id":"t1"}}] size: 70
~~~

In general it contains

 - prefix (in example 'REQ'). You can configure different text. If prefix text is null whole message is not diplayed.  
 - number (in example '[2]'). This is request index counted from start of the application. It is useful to pair this three messages together
 - request method (in example 'POST'). 
 - request path (in example '/rest/book'). 
 - user identity (in example 'identity(admin)'). Principal name. Present only if configured.
 - header info (in example 'headers(Token: 1234)'). Request header info. Present only if configured (You must specify header formatter).
 - payload info (in example 'payload[{"ti....] size: 70'). Request body info. Present only if configured (You must specify body formatter).


### response data message

This message looks like 
~~~
RES[2] POST /rest/book status: 200 time: 122 headers(Token: 1234) payload[1703929952718] size: 13
~~~

In general it contains

 - prefix (in example 'RES'). You can configure different text. If prefix text is null whole message is not diplayed.  
 - number (in example '[2]'). This is request index counted from start of the application. It is useful to pair this three messages together
 - request method (in example 'POST'). 
 - request path (in example '/rest/book'). 
 - status (in example 'status: 200'). Response status.
 - time (in example 'time: 122'). Response time.
 - header info (in example 'headers(Token: 1234)'). Request header info. Present only if configured (You must specify header formatter).
 - payload info (in example 'payload[1703929952718] size: 13'). Request body info. Present only if configured (You must specify body formatter).


## Configuration

In spring boot you only create bean of type LogFilter.

LogFilter is configured as list of triplets. 
 
 - request condition
 - response condition (if this is defined no start request message is displayed and filter processing is done even message is not displayed because of condition)
 - output configuration

LogFilter for each request try to find first triplet which fulfil conditions and make output using that configuration. 

### request condition

 - you can build condition exactly as you write condition on paper like path().startsWith("/public").and().method().equals("POST")
 - you can use custom condition - you can define your own condition
 - you can use any() condition - means always true
 - you can use several request string conditions in format resolveStringFromRequest().applyStringConditoon() (like .path().contains("boo"))
 - you can combine conditoons with not(), or(), and(). lb(), rb() (lb and rb are brackets)

### response condition

 - you can build condition exactly as you write condition on paper like path().startsWith("/public").and().method().equals("POST")
 - you can use custom condition - you can define your own condition
 - you can use any() condition - means always true
 - you can use statusOK() condition - status is 2xx
 - you can use several response string conditions in format resolveStringFromRequest().applyStringConditoon() (like .path().contains("boo"))
 - you can combine conditoons with not(), or(), and(). lb(), rb() (lb and rb are brackets)

If you define response condition

 - filter will not print restest start message 
 - filter must process request data even message is not displayed - because response must be evaluated fisrt

So use them only of you really need it and with combination with request condition. 

## Maven usage

```
   <dependency>
      <groupId>io.github.antonsjava</groupId>
      <artifactId>log-servlet-filter</artifactId>
      <version>LASTVERSION</version>
   </dependency>
```
You can find LASTVERSION [here](https://mvnrepository.com/artifact/io.github.antonsjava/log-servlet-filter)




