#! /usr/bin/env python
import asyncore, socket
from getip import get_ip


def ipfinder():
    address = '<brodcast>'
    print "Finding Server IP, make sure you have UsoroServer running on your device!"
    getip = get_ip(address, port)
    print "IP Found, connecting!"
    ip = unicode(getip.handle_read())
    print ip
    return ip


host = "192.168.1.110"
# host = raw_input("Input the Phone IP: ")
port = 1488


class Client(asyncore.dispatcher):
    def __init__(self, data):
        asyncore.dispatcher.__init__(self)
        self.create_socket(socket.AF_INET, socket.SOCK_STREAM)
        self.set_reuse_addr()
        self.socket.setblocking(True)
        try:
            self.connect((host, port))
            self.send(data)
        except Exception as e:
            print("Connection failed " + "Error: " + str(e))
        else:
            print("Connected successful")
            self.close()


def sendmsg(msg):
    Client(msg)
    asyncore.loop(1)
