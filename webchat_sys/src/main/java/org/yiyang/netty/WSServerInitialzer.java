package org.yiyang.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class WSServerInitialzer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //get pipeline
        ChannelPipeline pipeline = channel.pipeline();
        //HTTP codec required by websocket
        pipeline.addLast(new HttpServerCodec());
        //There are some data streams generated on HTTP, ranging from large to small. We process them by ChunkedWriteHandler
        pipeline.addLast(new ChunkedWriteHandler());
        //Aggregate http message into request or response
        pipeline.addLast(new HttpObjectAggregator(1024*64));

        /**
         * For the client, if no read / write heartbeat (all) is sent to the server within 1 minute, it will actively disconnect
         * If there are read idle and write idle, no processing will be done
         */
        pipeline.addLast(new IdleStateHandler(20,40,60));
        //Custom idle state detection handler
        pipeline.addLast(new HeartBeatHandler());

        /**
         * This handler will help you deal with some heavy and complex things
         * Will help you handle the handshake: close, ping, pong
         * *For websockets, they are all transmitted by frames, and different data types have different frames
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //Custom handler
        pipeline.addLast(new ChatHandler());



    }
}
