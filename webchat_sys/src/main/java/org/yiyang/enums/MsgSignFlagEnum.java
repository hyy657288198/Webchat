package org.yiyang.enums;

/**
 * 
 * @Description: Message signing status enumeration
 */
public enum MsgSignFlagEnum {
	
	unsign(0, "Unsigned"),
	signed(1, "Signed");
	
	public final Integer type;
	public final String content;
	
	MsgSignFlagEnum(Integer type, String content){
		this.type = type;
		this.content = content;
	}
	
	public Integer getType() {
		return type;
	}  
}
