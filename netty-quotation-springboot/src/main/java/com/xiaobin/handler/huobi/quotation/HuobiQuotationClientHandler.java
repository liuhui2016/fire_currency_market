package com.xiaobin.handler.huobi.quotation;

import com.xiaobin.common.AbstractNettyClient;
import com.xiaobin.utils.GZipUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
@ChannelHandler.Sharable
public class HuobiQuotationClientHandler extends SimpleChannelInboundHandler<Object> {

    private AbstractNettyClient abstractNettyClient;
    private WebSocketClientHandshaker handshaker;

    public HuobiQuotationClientHandler(WebSocketClientHandshaker handshake, AbstractNettyClient abstractNettyClient){
        this.handshaker = handshake;
        this.abstractNettyClient = abstractNettyClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(channel, (FullHttpResponse) msg);
            ctx.newPromise().setSuccess();
            return;
        }else if (msg instanceof FullHttpResponse){
            FullHttpResponse response = (FullHttpResponse)msg;
            //this.listener.onFail(response.status().code(), response.content().toString(CharsetUtil.UTF_8));
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }else {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof BinaryWebSocketFrame) {
                //火币网的数据是压缩过的，所以需要我们进行解压
                BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
                String receive = decodeByteBuf(binaryFrame.content());
                log.info("收到消息（压缩过的）:{}", receive);
                abstractNettyClient.onReceiveMessage(receive);
            } else if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) frame;
                String text = textWebSocketFrame.text();
                log.info("收到消息（文本消息）:{}", text);
            } else if (frame instanceof PongWebSocketFrame) {
                log.info("websocket client recived pong!");
            } else if (frame instanceof CloseWebSocketFrame) {
                log.info("WebSocket Client Received Closing.");
                channel.close();
            }
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("websocket client disconnected.");
        // TODO 重新连接出现异常
        if(!abstractNettyClient.isAlive()){
            abstractNettyClient.reConnect();
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("websocket client出现异常，异常信息：{}",cause);
        if (!ctx.newPromise().isDone()) {
            ctx.newPromise().setFailure(cause);
        }
        ctx.channel().close();
        ctx.close();
    }

    /**
     * 解压数据
     */
    private String decodeByteBuf(ByteBuf buf) throws Exception {
        byte[] temp = new byte[buf.readableBytes()];
        buf.readBytes(temp);
        // gzip 解压
        temp = GZipUtils.decompress(temp);
        return new String(temp, StandardCharsets.UTF_8);
    }
}
