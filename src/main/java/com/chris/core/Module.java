package com.chris.core;

import java.util.List;

import com.chris.net.NetChannel;

public interface Module {
    void start(Long gameId);
    void stop();
    void procMsg(NetChannel netChannel, CMsg cmsg);
    List<Class<?>> getCMsgs();


    int getUserCmdId();
}
