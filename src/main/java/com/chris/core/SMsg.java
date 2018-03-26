package com.chris.core;


import com.chris.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

public abstract class SMsg {
    static final Logger logger = Logger.getLogger(SMsg.class);
    protected ByteBuf buffer;
    private int dataLen = 0;
    protected ByteBuf copyBuffer = null;

    public static final byte SUCCESS = 0;
    public static final byte FAIL = 1;

    public SMsg(){
        buffer = Unpooled.buffer();
        buffer.writeInt(dataLen);
        buffer.writeByte(0x00);
        dataLen = 5;
    }

    public ByteBuf getBuffer(){
        if(copyBuffer != null){
            return copyBuffer.copy();
        }
        if(dataLen < 51200*100000){  //小于51200字节不压缩
            buffer.setInt(0, dataLen);
            //return buffer.copy();
        }else{
            buffer.readInt();
            buffer.readByte();

            //压缩发送的数据包
            byte[] input = new byte[buffer.readableBytes()];
            buffer.readBytes(input);

            // Create the compressor with highest level of compression
            Deflater compressor = new Deflater();
            compressor.setLevel(Deflater.BEST_COMPRESSION);
            //compressor.setLevel(Deflater.DEFAULT_COMPRESSION);

            // Give the compressor the data to compress
            compressor.setInput(input);
            compressor.finish();

            ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
            // Compress the data
            byte[] buf = new byte[1024];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
            try{
                bos.close();
            }catch(IOException e){
            }

            // Get the compressed data
            byte[] compressedData = bos.toByteArray();
            buffer.clear();
            buffer.writeInt(compressedData.length + 5);
            buffer.writeByte(0x01); //压缩标志
            buffer.writeBytes(compressedData);
            //return zipBuffer.copy();
        }
        copyBuffer = buffer;
        return copyBuffer.copy();
    }

    public ByteBuf cloneBuffer(){
        return getBuffer();
    }

    public byte[] getBytes(){
        ByteBuf buffer = getBuffer();
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return bytes;
    }

    protected void writeInt(int v){
        buffer.writeInt(v);
        dataLen += 4;
    }
    protected void writeByte(byte v){
        buffer.writeByte(v);
        dataLen += 1;
    }
    protected void writeLong(long v){
        buffer.writeLong(v);
        dataLen += 8;
    }
    protected void writeChar(char v){
        buffer.writeChar(v);
        dataLen += 1;
    }
    protected void writeShort(short v){
        buffer.writeShort(v);
        dataLen += 2;
    }
    protected void writeFloat(float v){
        buffer.writeFloat(v);
        dataLen += 4;
    }

    protected void writeDouble(double v){
        buffer.writeDouble(v);
        dataLen += 8;
    }

    protected void writeBytes(byte[] v){
        buffer.writeBytes(v);
        dataLen += v.length;
    }

    protected void writeUTF(String v){
        try{
            byte[] bytes = v.getBytes("utf-8");
            buffer.writeShort(bytes.length);
            buffer.writeBytes(bytes);
            dataLen += (bytes.length + 2);
        }catch(Exception e){
            logger.error(LogUtil.getStackMsg(e));
        }
    }

}
