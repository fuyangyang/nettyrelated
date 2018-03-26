package com.chris.net;

import com.chris.user.ComUser;
import com.chris.util.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class NetChannel {
    private Long id;   							//唯一编号
    //网络通道类型
    public static final int NET_SOCKET = 1;     //socket网络
    public static final int NET_WEBSOCKET = 2;  //websocket网络


    private ChannelHandlerContext ctx;
    private int type;

    //统计
    private long readByteCount = 0;  	//读取到的字节数
    private long readMsgCount = 0;  	//读取消息的数量
    private long readMsgTime = 0;       //最后读取的时间

    //统计
    private long writeByteCount = 0;  	//读取到的字节数
    private long writeMsgCount = 0;  	//读取消息的数量
    private long writeMsgTime = 0;      //最后读取的时间
    private String remoteAddress = null;	//远程地址


    private ComUser user;  //链接用户实体

    public void setId(Long id){
        this.id = id;
    }
    public Long getId(){
        return this.getId();
    }

    public void incrReadByteCount(int len){
        readByteCount += len;
        readMsgCount++;
        readMsgTime = System.currentTimeMillis();
    }

    public NetChannel(ChannelHandlerContext ctx, int type){
        this.ctx = ctx;
        this.type = type;
    }

    public String getRemoteAddress(){
        try{
            if(remoteAddress == null){
                if(ctx != null && ctx.channel() != null && ctx.channel().remoteAddress() != null){
                    String address = ctx.channel().remoteAddress().toString();
                    if(address == null) return "";
                    int n = address.indexOf(":");
                    if(n <= 0){
                        remoteAddress = "";
                        return remoteAddress;
                    }
                    address = address.substring(1, n);
                    remoteAddress = address;
                }else{
                    remoteAddress = "";
                }
            }
            return remoteAddress;
            //return ctx.channel().remoteAddress().toString();
        }catch(Exception e){
        }
        return "";
    }

    public void sendMsg(ByteBuf byteBuf){
        if(ctx != null){
            Channel channel = ctx.channel();
            if(channel != null && channel.isOpen()){
                if(type == NET_SOCKET){
                    channel.write(byteBuf);
                }else{
                    WebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
                    channel.writeAndFlush(frame);
                    writeByteCount += byteBuf.readableBytes();  	//读取到的字节数
                    writeMsgCount++;  								//读取消息的数量
                    writeMsgTime = System.currentTimeMillis();      //最后读取的时间
                }
            }
        }
    }
    

    //主动关闭连接
    public void close(){
        ctx.attr(Config.NET_CHANNEL_STATE).set(null); //加上这一句主要是系统主动关闭连接时不要在网络底层触发网络中断事件去调用channelInactive函数
        ctx.channel().close();
        ctx.close();

    }

    public long getReadByteCount(){
        return this.readByteCount;
    }
    public long getReadMsgCount(){
        return this.readMsgCount;
    }
    public long getReadMsgTime(){
        return this.readMsgTime;
    }
    public long getWriteByteCount(){
        return this.writeByteCount;
    }
    public long getWriteMsgCount(){
        return this.writeMsgCount;
    }
    public long getWriteMsgTime(){
        return this.writeMsgTime;
    }

    public ComUser getUser() {
        return user;
    }

    public void setUser(ComUser user) {
        this.user = user;
    }
}
