package com.chris.example.msg;

import com.chris.core.CMsg;

public class MsgInsert extends CMsg{
    @Override
    protected int onId() {
        return MsgCmd.C_INSERT;
    }

    @Override
    protected void onRead() {
        this.key = readUTF();
        this.data = readUTF();
        this.loss = readFloat();
    }

    private String key;
    private String data;
    private float loss;

    public String getKey() {
        return key;
    }

    public String getData() {
        return data;
    }

    public float getLoss() {
        return loss;
    }

    @Override
    public String toString() {
        return "MsgInsert{" +
                "key='" + key + '\'' +
                ", data='" + data + '\'' +
                ", loss=" + loss +
                '}';
    }
}
