package com.chris.example;

import com.chris.core.AbstractModule;
import com.chris.core.CMsg;
import com.chris.core.MsgSSimple;
import com.chris.example.msg.MsgCmd;
import com.chris.example.msg.MsgInit;
import com.chris.net.NetChannel;

public class RLRModule extends AbstractModule {

    @Override
    protected void onStart(Long id) {

    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onProcMsg(NetChannel netChannel, CMsg cmsg) {

        long start = System.nanoTime();
        Integer msgId = cmsg.getMsgId();
        switch (msgId) {
            case MsgCmd.C_INIT:
                handleInitCmd(cmsg, netChannel);
                break;
            default:
                logger.error("cmd_not_supported!");
                break;
        }
        logRt(msgId, start);
    }

    private void logRt(Integer msgId, long start) {
        logger.info("serverRt[" + msgId + "]:" + (System.nanoTime() - start));
    }

    private void handleInitCmd(CMsg msg, NetChannel netChannel) {
        logger.info("receive cammand:" + msg);
        boolean initOK = false;
        try {
            MsgInit msgInit = (MsgInit) msg;
            String key = msgInit.getKey();
            int size = msgInit.getSize();
//            initOK = xxx.init(key, size);//xxx后台返回数据
            initOK = true;
        } catch (Exception e) {
            logger.error("init error!", e);
        }
        MsgSSimple msgs = new MsgSSimple(MsgCmd.S_INIT);
        msgs.writeBoolean(initOK);
        netChannel.sendMsg(msgs.getBuffer());
    }

    @Override
    protected String onRegCMsgs() {
        return "com.chris.example.msg";
    }

    @Override
    protected void onTick(long time) {
    }

    @Override
    public int getUserCmdId() {
        return 0;
    }
}
