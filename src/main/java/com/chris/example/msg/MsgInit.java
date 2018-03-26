package com.chris.example.msg;

import com.chris.core.CMsg;

public class MsgInit extends CMsg {


    @Override
    protected int onId() {
        return MsgCmd.C_INIT;
    }

    @Override
    protected void onRead() {
        this.key = readUTF();
        this.size = readInt();
    }

    /**
     * 样本池名字
     */
    private String key;

    /**
     * 样本池容量
     */
    private int size;

    public String getKey() {
        return key;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "MsgInit{" +
                "key='" + key + '\'' +
                ", size=" + size +
                '}';
    }
}
