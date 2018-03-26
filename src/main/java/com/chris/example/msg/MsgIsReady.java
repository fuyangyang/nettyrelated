package com.chris.example.msg;

import com.chris.core.CMsg;

public class MsgIsReady extends CMsg {
    @Override
    protected int onId() {
        return MsgCmd.C_ISREADY;
    }

    @Override
    protected void onRead() {
        this.key = readUTF();
    }

    private String key;

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "MsgIsReady{" +
                "key='" + key + '\'' +
                '}';
    }
}
