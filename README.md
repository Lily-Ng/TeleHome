# TeleHome
A simple local chat server and client for playing telephone at home, alone.
Implemented in Java with a mySQL database that stores ongoing messages.

# How to Run
Compile from Chatclient.java and Chatserver.java. Run the chat server before the client.

# Overview
All client-server communication uses port 5190. The client may enter one of the four servers (A,B,C,D) to chat.

# Set Up
A mySQL database called "java" is set up and hosted locally. The schema java has one table called "chat," specified in chat.sql. The client and server must be built from source before running.
