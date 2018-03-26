package com.chris.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.chris.util.LogUtil;
import com.chris.util.Pair;
import io.netty.buffer.ByteBuf;
import org.apache.log4j.Logger;

public class ModuleManager {

    private static HashMap<String,Module> modules = new HashMap<String,Module>();
    private static HashMap<Integer,Module> cmdToModule = new HashMap<Integer,Module>();
    private static  HashMap<Integer, Class<?>> msgs = new HashMap<Integer, Class<?>>();

    private static HashMap<Long,Module> modulesGameId = new HashMap<Long,Module>();

    static final Logger logger = Logger.getLogger(ModuleManager.class);

    private static ModuleManager instance = null;

    public static ModuleManager getInstance(){
        if(instance == null){
            instance = new ModuleManager();
        }
        return instance;
    }

    public Module getModuleByGmaeId(long gameId){
        return  modulesGameId.get(gameId);
    }


    /**
     * 注册模块
     * @param moduleObject
     */
    public void regModules(Pair<String,Long> moduleObject){
        try{
            Module module = (Module)Class.forName(moduleObject.first()).newInstance();
            if(modules.get(moduleObject.first()) == null){
                module.start(moduleObject.second());
                modules.put(moduleObject.first(), module);
                modulesGameId.put(moduleObject.second(), module);
                List<Class<?>> list = module.getCMsgs();
                for(Class<?> c : list){
                    Integer cmdId = ((CMsg)c.newInstance()).getMsgId();
                    cmdToModule.put(cmdId, module);
                    msgs.put(cmdId, c);
                    //logger.info(Integer.toHexString(cmdId) + " : " + module.getClass().getName());
                }
            }else{
                logger.info("module [" + moduleObject.first() + "] is exist!");
            }
        }catch(Exception e){
            logger.error(LogUtil.getStackMsg(e) + moduleObject.toString());
        }
    }

    public void onStop(){
        Iterator<Module> it =  modules.values().iterator();
        while (it.hasNext()){
            Module module = it.next();
            module.stop();
        }
    }

    public static CMsg buildMsg(ByteBuf cb) {
        CMsg msg = null;
        try {
            int msgID = cb.readInt();
            //logger.info("get msgID: " + Integer.toHexString(msgID));
            msg = find(msgID);
            if (msg != null) {
                msg.buildMsg(cb);
            }
        } catch (Exception e) {
            logger.error(LogUtil.getStackMsg(e));
            return null;
        }
        return msg;
    }

    public static  Module findModule(Integer msgId){
        return cmdToModule.get(msgId);
    }

    public static Module getModule(String name){
        return modules.get(name);
    }

    private static CMsg find(Integer msgId) {
        try {
            Class<?> c = msgs.get(msgId);
            if (c != null){
                return (CMsg)c.newInstance();
            }else{
                logger.warn("Not MsgID:" + Integer.toHexString(msgId));
            }
        } catch (InstantiationException e) {
            logger.error(LogUtil.getStackMsg(e));
        } catch (IllegalAccessException e) {
            logger.error(LogUtil.getStackMsg(e));
        }
        return null;
    }

}

