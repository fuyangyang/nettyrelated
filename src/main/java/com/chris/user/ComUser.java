package com.chris.user;

import com.chris.net.NetChannel;

/**
 * 长链接用户实体，主要保存过程参数
 */
public class ComUser {
    private NetChannel netChannel;

    private String key;

    public String getRemoteAddress(){
        if(netChannel == null){
            return "";
        }else{
            return netChannel.getRemoteAddress();
        }
    }

    public void setKey(String key) {
        this.key = key;
    }
}
