package org.yiyang.services;

import org.yiyang.netty.ChatMsg;
import org.yiyang.pojo.FriendsRequest;
import org.yiyang.pojo.User;
import org.yiyang.vo.FriendsRequestVO;
import org.yiyang.vo.MyFriendsVO;

import java.util.List;

public interface UserServices {

    User getUserById(String id);

    User queryUserNameIsExist(String username);

    User insert(User user);

    User updateUserInfo(User user);

    Integer preconditionSearchFriends(String myUserId, String friendUserName);

    void sendFriendRequest(String myUserId, String friendUserName);

    List<FriendsRequestVO> queryFriendRequestList(String acceptUserId);

    void deleteFriendRequest(FriendsRequest friendsRequest);

    void passFriendRequest(String sendUserId, String acceptUserId);

    List<MyFriendsVO> queryMyFriends(String acceptUserId);

    String saveMsg(ChatMsg chatMsg);

    void updateMsgSigned(List<String> msgIdList);

    List<org.yiyang.pojo.ChatMsg> getUnReadMsgList(String acceptUserId);
}
