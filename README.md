# Server
A java based multi client chat server
Problem
Build a real-time chat application, where users are chatting with each other. Use socket.io and NodeJS / or anything else.

Alice, Bob and Candy are the users. Anyone should be able to send a message to anyone else.

If X wants to send message to Y, they follow this syntax: Y:

Eg: If Alice wants to say Hi to Bob, she types this Bob: Hi

Server

node server.mjs

Clients

node alice.mjs

node bob.mjs

node candies.mjs

Requirements

Multiple users can start the client applications
User should be able to send messages to other users
User should be able to receive messages sent to them
No authentication/authorization needed
Start from stratch and build an application that supports all the above use cases

Time limit: 45 min to 1 hour

Optional (Bonus):

What happens to messages when one user is offline?
List all online users
Broadcast support
How do we handle random order of starting/restarting the applications (Eg. Client starts before the server, Client reconnects to the server)
A better CLI interface for sending and receiving messages
Dockerize/Docker-compose the setup
