package com.chris.main;

import com.chris.core.ModuleManager;
import com.chris.net.NettyWebSocketServer;
import com.chris.util.LogUtil;
import com.chris.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMain {
    static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    static NettyWebSocketServer serviceSocket;
    public static void stop(){
        serviceSocket.stop();
    }
    public static void main(String[] args) {
        try {
            ModuleManager moduleManager = ModuleManager.getInstance();
            moduleManager.regModules(Pair.of("com.chris.example.RLRModule",-1L));
            
            try {
                serviceSocket =  new NettyWebSocketServer(8800);
                serviceSocket.start();
            } catch (Exception e) {
                logger.error("[ERROR]" + e.getMessage());
            }

            
        }catch (Exception e)
        {
            logger.error(LogUtil.getStackMsg(e));
            System.exit(-1);
        }
    }
}
