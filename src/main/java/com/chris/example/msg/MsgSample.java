package com.chris.example.msg;

import com.chris.core.CMsg;

public class MsgSample extends CMsg {
    @Override
    protected int onId() {
        return MsgCmd.C_SAMPLE;
    }

    @Override
    protected void onRead() {
        this.key = readUTF();
        this.size = readInt();
    }

    private String key;
    private int size;

    public String getKey() {
        return key;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "MsgSample{" +
                "key='" + key + '\'' +
                ", size=" + size +
                '}';
    }
}
