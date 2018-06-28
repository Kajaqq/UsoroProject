import Queue
import asyncore, socket
from multiprocessing.managers import State

host = '<broadcast>'
port = 54545
data = "Request"


class get_ip(asyncore.dispatcher):
    def __init__(self, host, port):
        asyncore.dispatcher.__init__(self)
        self.create_socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        self.sendto(data, ('<broadcast>',54545))
        print "Broadcast sent!"

    def handle_read(self):
        while 1:
            try:
                recv_data, addr = self.recvfrom(1500)
                print "x"
                if recv_data:
                    print recv_data
                else:
                    print "Got nothing from server"
                ip = addr[0]
                return ip
                self.handle_close()
            except socket.error:
                        if str(socket.error) == "[Errno 35] Resource temporarily unavailable":
                                time.sleep(0)
                                continue

    def writable(self):
        return False

    def handle_write(self):
        if not self.send_queue.empty():
            send_obj = self.send_queue.get()
            state = State.SUCCESS
            try:
                sent = self.sendto(send_obj['data'], (send_obj['hostname'], send_obj['port']))
                if sent < len(send_obj['data']):
                    pass
            except Exception as e:
                print e

    def handle_close(self):
        asyncore.dispatcher.close(self)
        self.close()


class broadcast_IP(asyncore.dispatcher):
    def __init__(self, port):
        asyncore.dispatcher.__init__(self)
        self.callback = None
        self.port = port
        self.create_socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.set_reuse_addr()
        self.bind(('', port))
        self.send_queue = Queue.Queue()

    def handle_connect(self):
        pass

    def handle_read(self):
            recv_data, addr = self.recvfrom(1500)
            print recv_data
            self.sendto("*"+recv_data, addr)
            print

    def handle_close(self):
        asyncore.dispatcher.close(self)
        print "Closing"
        self.close()

