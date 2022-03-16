package org.yiyang.enums;

/**
 * 
 * @Description: Enumeration of actions to send messages
 */
public enum MsgActionEnum {
	
	CONNECT(1, "Initialize the connection for the first time (or reconnect)"),
	CHAT(2, "Chat message"),
	SIGNED(3, "Message signing"),
	KEEPALIVE(4, "Client keeps heartbeat"),
	PULL_FRIEND(5, "Pull friends");
	
	public final Integer type;
	public final String content;
	
	MsgActionEnum(Integer type, String content){
		this.type = type;
		this.content = content;
	}
	
	public Integer getType() {
		return type;
	}  
}
