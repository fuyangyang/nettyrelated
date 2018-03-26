package com.chris.core;

import com.chris.util.LogUtil;
import io.netty.buffer.ByteBuf;
import org.apache.log4j.Logger;

public abstract class CMsg {
    static final Logger logger = Logger.getLogger(CMsg.class);

    public Integer getMsgId() {
        return onId();
    }

    public CMsg buildMsg(ByteBuf cb) {
        buffer = cb;
        //buffer.order();
        onRead();
        return this;
    }

    protected abstract int onId();

    protected abstract void onRead();

    private ByteBuf buffer;

    protected int readInt() {
        return buffer.readInt();
    }

    protected byte readByte() {
        return buffer.readByte();
    }

    protected long readLong() {
        return buffer.readLong();
    }

    protected char readChar() {
        return buffer.readChar();
    }

    protected short readShort() {
        return buffer.readShort();
    }

    protected float readFloat() {
        return buffer.readFloat();
    }

    protected double readDouble() {
        return buffer.readDouble();
    }

    protected void readBytes(byte[] bytes) {
        buffer.readBytes(bytes);
    }

    protected short readUnsignedByte() {
        return buffer.readUnsignedByte();
    }

    protected int readUnsignedShort() {
        return buffer.readUnsignedShort();
    }

    protected long readUnsignedInt() {
        return buffer.readUnsignedInt();
    }

    protected String readUTF() {
        int len = buffer.readShort();
        byte[] bytes = new byte[len];
        buffer.readBytes(bytes);
        try {
            return new String(bytes, "utf-8");
        } catch (Exception e) {
            logger.error(LogUtil.getStackMsg(e));
        }
        return new String(bytes);
    }

}
