package org.yiyang.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.yiyang.SpringUtil;
import org.yiyang.enums.MsgActionEnum;
import org.yiyang.services.UserServices;
import org.yiyang.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;


/**
 *Handler for processing messages
 *In netty, this frame is used to handle text objects for websocket
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    //Channel for recording and managing all clients
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //Get the message transmitted by the client
        String content = msg.text();
        //1.Get the message from the client
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        Integer action = dataContent.getAction();
        Channel channel =  ctx.channel();
        //2.Judge the message type and process different services according to different types
        if(action == MsgActionEnum.CONNECT.type){
            //2.1 When websocket is opened for the first time, initialize the channel and associate the used channel with the userid
            String senderId = dataContent.getChatMsg().getSenderId();
            UserChanelRel.put(senderId,channel);

            //test
            for (Channel c: users) {
                System.out.println(c.id().asLongText());
            }
            UserChanelRel.output();
        } else if(action == MsgActionEnum.CHAT.type){
            //2.2 for chat type messages, save the chat record to the database and mark the message as unsigned
            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgContent = chatMsg.getMsg();
            String senderId = chatMsg.getSenderId();
            String receiverId = chatMsg.getReceiverId();
            //save the message to the database and mark it as unsigned
            UserServices userServices = (UserServices) SpringUtil.getBean("userServicesImpl");
            String msgId = userServices.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);

            //send msg
            Channel receiverChannel = UserChanelRel.get(receiverId);
            if(receiverChannel ==null){
                //offline user
            }else{
                //When the receiverchannel is not empty, check whether the corresponding channel exists from the channelgroup
                Channel findChanel = users.find(receiverChannel.id());
                if(findChanel!=null){
                    //online user
                    receiverChannel.writeAndFlush(
                            new TextWebSocketFrame(
                                    JsonUtils.objectToJson(dataContentMsg)
                            )
                    );
                }else{
                    //offline user
                }
            }


        } else if(action == MsgActionEnum.SIGNED.type){
            //2.3 sign in for specific messages, and modify the corresponding message in the database to signed
            UserServices userServices = (UserServices) SpringUtil.getBean("userServicesImpl");
            //The extend in the signed message represents the message id to be signed, which is split by commas
            String msgIdsStr = dataContent.getExtand();
            String[] msgsId = msgIdsStr.split(",");

            List<String> msgIdList = new ArrayList<>();
            for (String mid: msgsId) {
                if(StringUtils.isNotBlank(mid)){
                    msgIdList.add(mid);
                }
            }
            System.out.println(msgIdList.toString());
            if(msgIdList!=null && !msgIdList.isEmpty() && msgIdList.size()>0){
                //batch receipt
                userServices.updateMsgSigned(msgIdList);
            }

        } else if(action == MsgActionEnum.KEEPALIVE.type){
            //2.4 heartbeat type message
            System.out.println("Received heartbeat type message from channel【"+channel+"");
        }

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String chanelId = ctx.channel().id().asShortText();
        System.out.println("channel removed：channel id is："+chanelId);

        users.remove(ctx.channel());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //when an exception occurs, close the connection and remove it from the channelgroup
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
