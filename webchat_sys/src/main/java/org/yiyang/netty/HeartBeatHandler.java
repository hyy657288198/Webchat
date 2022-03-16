package org.yiyang.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


/**
 *Used to detect the heartbeat of the channel
 *Inherits the ChannelInboundHandlerAdapter in order not to implement the ChannelRead0 method
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state()== IdleState.READER_IDLE){
                System.out.println("Enter read idle......");
            }else if(event.state() == IdleState.WRITER_IDLE) {
                System.out.println("Enter write idle......");
            }else if(event.state()== IdleState.ALL_IDLE){
                System.out.println("Before closing the channel: the number of users is "+ChatHandler.users.size());
                Channel channel = ctx.channel();
                channel.close();
                System.out.println("After closing the channel: the number of users is "+ChatHandler.users.size());

            }
        }
    }


}
