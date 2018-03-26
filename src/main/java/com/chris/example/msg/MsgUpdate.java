package com.chris.example.msg;

import com.chris.core.CMsg;

import java.util.Arrays;

public class MsgUpdate extends CMsg {
    @Override
    protected int onId() {
        return MsgCmd.C_UPDATE;
    }

    @Override
    protected void onRead() {
        this.key = readUTF();
        this.size = readInt();
        index = new int[size];
        loss = new float[size];
        for (int i = 0; i < size; i++) {
            this.index[i] = readInt();
        }

        for (int i = 0; i < size; i++) {
            this.loss[i] = readFloat();
        }
    }

    private String key;
    private int size;
    private int[] index;
    private float[] loss;

    public String getKey() {
        return key;
    }

    public int getSize() {
        return size;
    }

    public int[] getIndex() {
        return index;
    }

    public float[] getLoss() {
        return loss;
    }

    @Override
    public String toString() {
        return "MsgUpdate{" +
                "key='" + key + '\'' +
                ", size=" + size +
                ", index=" + Arrays.toString(index) +
                ", loss=" + Arrays.toString(loss) +
                '}';
    }
}
