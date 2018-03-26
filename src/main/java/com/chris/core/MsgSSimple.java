package com.chris.core;

public class MsgSSimple extends SMsg{

    public MsgSSimple(){
        super();
    }

    public MsgSSimple(int id){
        super();
        writeInt(id);
    }

    public MsgSSimple(int id, byte b){
        super();
        writeInt(id);
        writeByte(b);
    }


    public void writeBoolean(boolean b){
        if(b){
            super.writeByte((byte)1);
        }else{
            super.writeByte((byte)0);
        }
    }

    public void writeMessage(int code, String msg){
        super.writeInt(code);
        super.writeUTF(msg);
    }

                                           
    public void writeInt(int v){
        super.writeInt(v);
    }
    public void writeByte(byte v){
        super.writeByte(v);
    }
    public void writeLong(long v){
        super.writeLong(v);
    }
    public void writeChar(char v){
        super.writeChar(v);
    }
    public void writeShort(short v){
        super.writeShort(v);
    }
    public void writeFloat(float v){
        super.writeFloat(v);
    }
    public void writeDouble(double v){
        super.writeDouble(v);
    }
    public void writeBytes(byte[] v){
        super.writeBytes(v);
    }
    public void writeUTF(String v){
        if(v == null){
            super.writeUTF("");
        }else{
            super.writeUTF(v);
        }
    }


    public void writeSuccessMsg(int code, String msg){
        this.writeByte(SUCCESS);
        this.writeInt(code);
        this.writeUTF(msg);
    }

    public void writeSuccessMsg(String msg){
        this.writeByte(SUCCESS);
        this.writeUTF(msg);
    }

    public void writeSuccessMsg(){
        this.writeByte(SUCCESS);
    }
}
