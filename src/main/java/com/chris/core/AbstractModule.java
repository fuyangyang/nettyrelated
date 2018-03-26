package com.chris.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.chris.net.NetChannel;
import com.chris.util.LogUtil;
import org.apache.log4j.Logger;

public abstract class AbstractModule implements Module{
    protected Logger logger = Logger.getLogger(this.getClass());

    //时钟线程
    private ScheduledExecutorService scheduler = null;
    private ScheduledFuture<?> future = null;
    private List<Class<?>> cmsgs = new ArrayList<Class<?>>();

    //开始模块
    public final void start(Long id){
        onStart(id);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        future = scheduler.scheduleAtFixedRate(new Runnable( ) {
            public void run() {
                tick();
            }
        }, 5, 5, TimeUnit.SECONDS);	   //5秒钟后运行，并每隔5秒运行一次
        //设定服务器KEY
    }

    //模块停止
    public final void stop(){
        onStop();
        //关闭线程
        scheduler.shutdown();
    }

    /**
     * 非线程安全
     */
    private void tick(){
        long time = System.currentTimeMillis();
        //lock.lock();
        try{
            onTick(time);
        }catch(Exception e){
            logger.error(LogUtil.getStackMsg(e));
        }
        //lock.unlock();
    }

    public final void procMsg(NetChannel netChannel, CMsg cmsg){
        try{
            onProcMsg(netChannel, cmsg);
        }catch(Exception e){
            logger.error(LogUtil.getStackMsg(e));
        }
    }

    public final List<Class<?>> getCMsgs(){
        List<Class<?>> packet = PackageFactory.getClasssFromPackage(onRegCMsgs());
        for(Class ct : packet){
            if(CMsg.class.isAssignableFrom(ct)){
                cmsgs.add(ct);
            }
        }
        return cmsgs;
    }

    protected abstract void onStart(Long id);
    protected abstract void onStop();
    protected abstract void onProcMsg(NetChannel netChannel, CMsg cmsg);
    protected abstract String onRegCMsgs();
    protected abstract void onTick(long time);

}
