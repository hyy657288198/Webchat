# Webchat
A simple chat app that imitates wechat and separates the front and back ends.

Learn this project from https://www.bilibili.com/video/BV18g4y1z7rL?p

If you would like to run this app, I suggest you use IDEA to open webchat_sys and use HBuilder to open webchat, and change several IP addresses according to readme in these two folders. In addition, you also need to use Webchat.sql to create a dedicated database and use fastDFS and Nginx to build a picture server on Linux system. After that, you need to connect your computer and mobile phone to the same network. Then, you need to run webchat_sys first, run webchat on your mobile phone, and remember to open your nginx server. Finally, you will be able to use it successfully.


This app includes functions such as user login and registration, uploading and saving avatars, adding friends by searching friends with QR code or page, contacts book and real-time chat.

The home page of this app is the login and registration page. If the user's information cannot be found in the database, it will automatically help the user to register. At the same time, it will create a unique QR code storing the user ID, which is convenient for other users to add users by scanning the QR code. After logging in, the user can change and save the personal avatar, nickname and other information, or add friends through the discover page. The friend request will be displayed at the top of the chat list page. In addition, users can select friends on the chat list page or contacts page to chat in real-time, delete chat records, and so on.

Users' personal information, chat records, friends and other information will be stored in the database. If the user receives a message while offline, the server will store it in the database in an unsigned status. When the user logs in again, those messages will be displayed and prompt the user for unread messages. The information of the user's Avatar, QR code and other pictures will be stored in the database by network address. When necessary, it will be obtained from the picture server built with fastDFS and Nginx. Webchat also uses netty's heartbeat mechanism to monitor the process. If the user remains all idle for a period of time, the user's process will be automatically closed.


·Use Mui, H5 + to complete the rendering and code implementation of front-end   
·Implement back-end with springboot framework   
·Use mybatis, a persistence layer framework, to operate the database   
·Keep data persistent storage with MySQL   
·Use the websocket provided by netty to read, write and send messages, so as to complete real-time communication   
·Build a high-performance distributed image server on Linux system with fastDFS and Nginx to store images
