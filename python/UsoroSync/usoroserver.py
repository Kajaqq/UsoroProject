#! /usr/bin/env python
import asyncore
import socket
import webbrowser
import pyperclip
from getip import broadcast_IP
import config

host = ''
port = 1488


class Server(asyncore.dispatcher):
    def __init__(self):
        asyncore.dispatcher.__init__(self)
        # broadcaster()
        self.create_socket(socket.AF_INET, socket.SOCK_STREAM)
        self.set_reuse_addr()
        self.bind(('', port))
        self.listen(5)
        print "Waiting for Connection"

    def handle_accept(self):
        # when we get a client connection start a dispatcher for that
        # client
        sock, address = self.accept()
        sock.setblocking(True)
        print 'Connection by', address[0]
        EchoHandler(sock)


def broadcaster():
    broadcast_IP(54545)


class EchoHandler(asyncore.dispatcher_with_send):
    # dispatcher_with_send extends the basic dispatcher to have an output
    # buffer that it writes whenever there's content
    def handle_read(self):
        data = self.recv(1024)
        print data
        config.msg = data
        if data:
            if data.startswith("CB"):
                data = data.replace("CB", "")
                print("Got " + data + " Adding to clipboard.")
                pyperclip.copy(data)
            elif data.startswith('http' or 'www'):
                print data
                webbrowser.open(data)
            elif data.startswith('NOTIF'):
                print data
s = Server()
asyncore.loop()

