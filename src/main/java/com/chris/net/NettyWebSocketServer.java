package com.chris.net;

import com.chris.util.LogUtil;
import org.apache.log4j.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyWebSocketServer {
    static final Logger logger = Logger.getLogger(NettyWebSocketServer.class);
    private final int port;
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    public NettyWebSocketServer(int port) {
        this.port = port;
    }

    public void start() {
        try{
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WebSocketServerInitializer());

                Channel ch = b.bind(port).sync().channel();
                logger.info("Web socket server started at port " + port + '.');
                logger.info("Open your browser and navigate to http://localhost:" + port + '/');

                //ch.closeFuture().sync();		//此处挂起
            } finally {
                //bossGroup.shutdownGracefully();
                //workerGroup.shutdownGracefully();
            }
        }catch(Exception e){
            logger.error(LogUtil.getStackMsg(e));
        }
    }

    public void stop(){
        if(bossGroup != null){
            bossGroup.shutdownGracefully();
        }
        if(workerGroup != null){
            workerGroup.shutdownGracefully();
        }
    }
}
