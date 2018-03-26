#-*- coding:utf-8 -*-
        #!/bin/env python
        import socket
        import struct

class CMsg:
        def __init__(self, head):
        self.buffer = struct.pack("!i", head)


        def writeInt(self, val):
        self.buffer += struct.pack("!i", val)

        def writeFloat(self, val):
        self.buffer += struct.pack("!f", val)

        def writeBool(self, val):
        self.buffer += struct.pack("!?", val)

        def writeLong(self, val):
        self.buffer += struct.pack("!l", val)

        def writeByte(self, val):
        self.buffer += struct.pack("!c", val)

        def writeString(self, val):
        strlen = len(val)
        self.buffer += struct.pack("!h", strlen)
        self.buffer += struct.pack("!%ds"%strlen, val)

        def getBuffer(self):
        lens = len(self.buffer)
        return struct.pack("!i", lens + 5) + struct.pack("!c", "0") + self.buffer


class SMsg:
        def __init__(self, buffer):
        buf = buffer[9:]
        self.buffer = buf
        self.offset =0
        self.length = len(buf)

        def readInt(self):
        if self.offset+4 > self.length:
        return None
        val = struct.unpack("!i", self.buffer[self.offset:self.offset+4])
        self.offset += 4
        return int(val[0])

        def readShort(self):
        if self.offset+2 > self.length:
        return None
        val = struct.unpack("!h", self.buffer[self.offset:self.offset+2])
        self.offset += 2
        return int(val[0])

        def readFloat(self):
        if self.offset+4 > self.length:
        return None
        val = struct.unpack("!f", self.buffer[self.offset:self.offset+4])
        self.offset += 4
        return float(val[0])

        def readBool(self):
        if self.offset+1 > self.length:
        return None
        val = struct.unpack("!?", self.buffer[self.offset:self.offset+1])
        self.offset += 1
        return val[0]

        def readLong(self):
        if self.offset+8 > self.length:
        return None
        val = struct.unpack("!l", self.buffer[self.offset:self.offset+8])
        self.offset += 8
        return long(val[0])

        def readByte(self):
        if self.offset+1 > self.length:
        return None
        val = struct.unpack("!c", self.buffer[self.offset:self.offset+1])
        self.offset += 1
        return str(val[0])

        def readString(self):
        strlen =  self.readShort()
        if strlen == None:
        return None

        if self.offset + strlen > self.length:
        return None
        val = struct.unpack("!%ds"%strlen, self.buffer[self.offset:self.offset + strlen])
        self.offset += strlen
        return str(val[0])

