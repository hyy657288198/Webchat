package org.yiyang.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.yiyang.bo.UserBO;
import org.yiyang.enums.OperatorFriendRequestTypeEnum;
import org.yiyang.enums.SearchFriendsStatusEnum;
import org.yiyang.pojo.ChatMsg;
import org.yiyang.pojo.FriendsRequest;
import org.yiyang.pojo.User;
import org.yiyang.services.UserServices;
import org.yiyang.utils.FastDFSClient;
import org.yiyang.utils.FileUtils;
import org.yiyang.utils.MyJSONResult;
import org.yiyang.utils.MD5Utils;
import org.yiyang.vo.FriendsRequestVO;
import org.yiyang.vo.MyFriendsVO;
import org.yiyang.vo.UserVo;

import java.util.List;

@Controller
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    UserServices userServices;

    @Autowired
    FastDFSClient fastDFSClient;

    //login and register
    @RequestMapping("/registerOrlogin")
    @ResponseBody
    public MyJSONResult registerOrlogin(User user){
        User userResult = userServices.queryUserNameIsExist(user.getUsername());
        if(userResult!=null){//login
            if(!userResult.getPassword().equals(MD5Utils.getPwd(user.getPassword()))){
                return MyJSONResult.errorMap("Wrong password.");
            }
        }else{//register
            user.setNickname("");
            user.setQrcode("");
            user.setPassword(MD5Utils.getPwd(user.getPassword()));
            user.setFaceImage("");
            user.setFaceImageBig("");

            userResult = userServices.insert(user);
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userResult, userVo);
        return MyJSONResult.ok(userVo);
    }

    @RequestMapping("/uploadFaceBase64")
    @ResponseBody
    public MyJSONResult uploadFaceBase64(@RequestBody UserBO userBO) throws Exception {
        String base64Data = userBO.getFaceData();
        String userFacePath = "D:\\"+userBO.getUserId()+"userFaceBase64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);
        MultipartFile multipartFile = FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(multipartFile);
        System.out.println(url);
        String thump = "_150x150.";
        String[] arr = url.split("\\.");
        String thumpImgUrl = arr[0]+thump+arr[1];
        User user = new User();
        user.setId(userBO.getUserId());
        user.setFaceImage(thumpImgUrl);
        user.setFaceImageBig(url);
        User result = userServices.updateUserInfo(user);
        return  MyJSONResult.ok(result);
    }

    @RequestMapping("/searchFriend")
    @ResponseBody
    public MyJSONResult searchFriend(String myUserId, String friendUserName){
        /**
         * 1.if the username doesn't exist, return no such person
         * 2.if the username equals to the user self, return can't add yourself
         * 3.if the username is already your friend, return already has this friend
         */
        Integer status = userServices.preconditionSearchFriends(myUserId,friendUserName);
        if(status== SearchFriendsStatusEnum.SUCCESS.status){
            User user = userServices.queryUserNameIsExist(friendUserName);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user,userVo);
            return MyJSONResult.ok(userVo);
        }else{
            String msg = SearchFriendsStatusEnum.getMsgByKey(status);
            return MyJSONResult.errorMsg(msg);
        }
    }

    @RequestMapping("/addFriendRequest")
    @ResponseBody
    public MyJSONResult addFriendRequest(String myUserId, String friendUserName){
        if(StringUtils.isBlank(myUserId)|| StringUtils.isBlank(friendUserName)){
            return MyJSONResult.errorMsg("Information is empty.");
        }
        /**
         * 1.if the username doesn't exist, return no such person
         * 2.if the username equals to the user self, return can't add yourself
         * 3.if the username is already your friend, return already has this friend
         */
        Integer status = userServices.preconditionSearchFriends(myUserId,friendUserName);
        if(status==SearchFriendsStatusEnum.SUCCESS.status){
            userServices.sendFriendRequest(myUserId,friendUserName);
        }else{
            String msg = SearchFriendsStatusEnum.getMsgByKey(status);
            return MyJSONResult.errorMsg(msg);
        }
        return MyJSONResult.ok();
    }

    @RequestMapping("/queryFriendRequest")
    @ResponseBody
    public MyJSONResult queryFriendRequest(String userId){
        List<FriendsRequestVO> friendRequestList = userServices.queryFriendRequestList(userId);
        return MyJSONResult.ok(friendRequestList);
    }

    @RequestMapping("/operFriendRequest")
    @ResponseBody
    public MyJSONResult operFriendRequest(String acceptUserId, String sendUserId, Integer operType){
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setAcceptUserId(acceptUserId);
        friendsRequest.setSendUserId(sendUserId);
        if(operType== OperatorFriendRequestTypeEnum.IGNORE.type){
            userServices.deleteFriendRequest(friendsRequest);
        }else if(operType==OperatorFriendRequestTypeEnum.PASS.type){
            userServices.passFriendRequest(sendUserId,acceptUserId);
        }
        List<MyFriendsVO> myFriends = userServices.queryMyFriends(acceptUserId);
        return MyJSONResult.ok(myFriends);
    }

    @RequestMapping("/myFriends")
    @ResponseBody
    public MyJSONResult myFriends(String userId){
        if (StringUtils.isBlank(userId)){
            return MyJSONResult.errorMsg("user id is null");
        }
        List<MyFriendsVO> myFriends = userServices.queryMyFriends(userId);
        return MyJSONResult.ok(myFriends);
    }

    @RequestMapping("/setNickname")
    @ResponseBody
    public MyJSONResult setNickName(User user){
        User userResult = userServices.updateUserInfo(user);
        return MyJSONResult.ok(userResult);
    }

    @RequestMapping("/getUser")
    public String getUserById(String id, Model model){
        User user = userServices.getUserById(id);
        model.addAttribute("user", user);
        return "user_list";
    }

    @RequestMapping("/getUnReadMsgList")
    @ResponseBody
    public MyJSONResult getUnReadMsgList(String acceptUserId){
        if(StringUtils.isBlank(acceptUserId)){
            return MyJSONResult.errorMsg("Accept user id shouldn't be empty.");
        }
        List<ChatMsg> unReadMsgList = userServices.getUnReadMsgList(acceptUserId);
        return MyJSONResult.ok(unReadMsgList);

    }
}
