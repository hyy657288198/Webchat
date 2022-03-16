package org.yiyang.mapper;

import org.yiyang.vo.FriendsRequestVO;
import org.yiyang.vo.MyFriendsVO;

import java.util.List;

public interface UserMapperCustom {
    List<FriendsRequestVO> queryFriendRequestList(String acceptUserId);
    List<MyFriendsVO> queryMyFriends(String userId);
    void batchUpdateMsgSigned(List<String> msgIdList);

}
