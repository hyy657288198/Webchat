package org.yiyang.enums;

/**
 * 
 * @Description: Enumeration for ignoring or accepting requests from friends
 */
public enum OperatorFriendRequestTypeEnum {
	
	IGNORE(0, "Ignore"),
	PASS(1, "Accept");
	
	public final Integer type;
	public final String msg;
	
	OperatorFriendRequestTypeEnum(Integer type, String msg){
		this.type = type;
		this.msg = msg;
	}
	
	public Integer getType() {
		return type;
	}  
	
	public static String getMsgByType(Integer type) {
		for (OperatorFriendRequestTypeEnum operType : OperatorFriendRequestTypeEnum.values()) {
			if (operType.getType() == type) {
				return operType.msg;
			}
		}
		return null;
	}
	
}
