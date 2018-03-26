#-*- coding:utf-8 -*-
#!/bin/env python
from websocket import create_connection, WebSocket

from msg_base import CMsg
from msg_base import SMsg
import msg_cmd

class PythonClient:
    def __init__(self, ip, port):
        self.ip = ip
        self.port = port
        self.sock = None
        self.modelName = None

    def init(self, modelName, size):
        self.modelName = modelName
        if self.sock is None:
            self.sock = create_connection(("ws://%s:%s/ws" % (self.ip, self.port)),1000)
            return self.create_pool(size)
        else:
            return True

    def close(self):
        if self.sock is None:
            return True
        self.sock.close()
