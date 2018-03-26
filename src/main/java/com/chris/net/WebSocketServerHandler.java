package com.chris.net;

import com.chris.core.CMsg;
import com.chris.core.Module;
import com.chris.core.ModuleManager;
import com.chris.user.ComUser;
import com.chris.util.Config;
import com.chris.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * wenlong.zwl
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    static final Logger logger = Logger.getLogger(WebSocketServerHandler.class);
    private static final String WEBSOCKET_PATH = "/ws";
    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        try{
            if (msg instanceof FullHttpRequest) {
                handleHttpRequest(ctx, (FullHttpRequest) msg);
            } else if (msg instanceof WebSocketFrame) {
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            }
        }catch(TooLongFrameException e){
            logger.info("io.netty.handler.codec.TooLongFrameException");
        }catch(Exception e){
            //logger.error(LogUtil.getStackMsg(e));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        try{
            ctx.flush();
        }catch(Exception e){
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        if(ctx.channel() != null && ctx.channel().remoteAddress() != null)
        {
            logger.info(" Client collect from " + ctx.channel().remoteAddress().toString());
        }
        else {
            logger.info(" Client collect from " + ctx.name());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //GameConfig.lock();
        try{
            NetChannel netChannel = (NetChannel)ctx.attr(Config.NET_CHANNEL_STATE).get();
            if(netChannel != null){
                ComUser user = netChannel.getUser();
                if(user != null){
                    logger.info("[AUDIT] net remove: " + user.getRemoteAddress());
                }
                else{
                    logger.info("[AUDIT] net remove: " + netChannel.getRemoteAddress());
                }
            }
        }catch(Exception e){
            logger.error(LogUtil.getStackMsg(e));
        }finally{
            //GameConfig.unlock();
        }
    }

    /**
     * 处理空闲超时事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        try{
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                if (e.state() == IdleState.READER_IDLE) {
                    logger.info("[AUDIT] net remove as Idle");
                    ctx.close();
                } else if (e.state() == IdleState.WRITER_IDLE) {
                    //ctx.writeAndFlush(new PingMessage());
                }
            }
        }catch(Exception e){
            //logger.error(LogUtil.getStackMsg(e));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try{
            logger.error(LogUtil.getStackMsg(cause));
            ctx.close();
        }catch(Exception e){
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //检测是否为WebSocketFrame
        if ((!(frame instanceof BinaryWebSocketFrame)) && (!(frame instanceof ContinuationWebSocketFrame))) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        //      if (!(frame instanceof TextWebSocketFrame)) {
        //          throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        //      }

        //      // Send the uppercase string back.
        //      String request = ((TextWebSocketFrame) frame).text();
        //      if (logger.isLoggable(Level.FINE)) {
        //          logger.fine(String.format("%s received %s", ctx.channel(), request));
        //      }
        //      ctx.channel().write(new TextWebSocketFrame(request.toUpperCase()));

        //ctx.channel().write(new BinaryWebSocketFrame(byteBuf));

        //ByteBuf byteBuf = ((ByteBuf) frame.content());
        //处理数据封包问题
        frameDecoder(ctx, frame);
    }


    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // Handle a bad request.
        if (!req.getDecoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        // Allow only GET methods.
        if (req.getMethod() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }
        if(req.getUri() == null){
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }
        

        // Send the demo page and favicon.ico
        if ("/".equals(req.getUri())) {
            ByteBuf content = WebSocketServerIndexPage.getContent(getWebSocketLocation(req));
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
            setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;
        }
        if ("/favicon.ico".equals(req.getUri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            sendHttpResponse(ctx, req, res);
            return;
        }

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
            getWebSocketLocation(req), null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            //WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            try{
                handshaker.handshake(ctx.channel(), req);
            }catch(WebSocketHandshakeException e){
                logger.info("WebSocketHandshakeException");
            }
        }
    }

    private static void sendHttpResponse(
        ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        return "ws://" + req.headers().get(HOST) + WEBSOCKET_PATH;
    }

    //封包处理
    public static final AttributeKey<Object> FRAME_STATE = new AttributeKey<Object>("FRAME_STATE");
    private void frameDecoder(ChannelHandlerContext ctx, WebSocketFrame frame){
        try{
            ByteBuf frameBuf = ((ByteBuf) frame.content());
            //存在封包，取出数据组合
            ByteBuf buf =  (ByteBuf)ctx.attr(FRAME_STATE).get();
            //如果没有封包，直接处理frame的数据包
            if(buf == null){
                int length = 0;
                while(true){
                    length = frameBuf.readableBytes();
                    if(length == 0){ //没有数据了，直接返回
                        return;
                    }
                    if(length < 4){  //跳出循环
                        break;
                    }
                    frameBuf.markReaderIndex();
                    length = frameBuf.readInt();
                    frameBuf.resetReaderIndex();
                    if (frameBuf.readableBytes() < length) {
                        break;
                    }
                    ByteBuf newBuf = frameBuf.readBytes(length);
                    procByteBuf(ctx, newBuf);
                }
                //如果还有剩下数据，放入封包中
                length = frameBuf.readableBytes();
                byte[] arr = new byte[length];
                frameBuf.readBytes(arr);
                buf = Unpooled.buffer();
                buf.writeBytes(arr);
                ctx.attr(FRAME_STATE).set(buf);
            }else{
                logger.info("有组包");
                int length = frameBuf.readableBytes();
                byte[] arr = new byte[length];
                frameBuf.readBytes(arr);
                buf.writeBytes(arr);

                //处理封包的数据
                while(true){
                    int readLen = buf.readableBytes();
                    if(readLen < 4) {
                        ctx.attr(FRAME_STATE).set(buf);
                        logger.info("填充了数据包 :" + readLen);
                        return;
                    }

                    buf.markReaderIndex();
                    length = buf.readInt();
                    buf.resetReaderIndex();
                    if (buf.readableBytes() < length) {
                        logger.info("填充了数据包 , 可读的：" + buf.readableBytes() + "长度：" + length);
                        ctx.attr(FRAME_STATE).set(buf);
                        return;
                    }

                    //There's enough bytes in the buffer. Read it.
                    ByteBuf newBuf = buf.readBytes(length);
                    procByteBuf(ctx, newBuf);

                    if(buf.readableBytes() <= 0) {
                        ctx.attr(FRAME_STATE).set(null);
                        return;
                    }
                }
            }
        }catch(Exception e){
            ctx.attr(FRAME_STATE).set(null);
            logger.error(LogUtil.getStackMsg(e));
        }
    }

    private void procByteBuf(ChannelHandlerContext ctx, ByteBuf newBuf){
        try{
            int len = newBuf.readInt();			//读走长度
            newBuf.readByte();					//读走压缩标志

            CMsg cmsg = ModuleManager.buildMsg(newBuf);
            if(cmsg != null){
                Module module = ModuleManager.findModule(cmsg.getMsgId());
                if(module != null){
                    NetChannel netChannel = (NetChannel)ctx.attr(Config.NET_CHANNEL_STATE).get();
                    if(netChannel == null){
                        netChannel = new NetChannel(ctx, NetChannel.NET_WEBSOCKET);
                        ctx.attr(Config.NET_CHANNEL_STATE).set(netChannel);
                    }
                    netChannel.incrReadByteCount(len);	//增加读取到得包得数量
                    module.procMsg(netChannel, cmsg);
                }else{
                    logger.info("无处理此命令的模块， cmd:" + Integer.toHexString(cmsg.getMsgId()));
                }
            }else{
                logger.info("无此命令");
            }
        }catch(Exception e){
            logger.error(LogUtil.getStackMsg(e));
        }
    }
}

