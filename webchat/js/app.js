window.app = {

    /**
     * URL address published by netty service backend
     */
    nettyServerUrl: 'ws://172.20.10.6:8888/ws',

    /**
     * URL address published by back-end
     */
    serverUrl: "http://172.20.10.6:8080/webchat_sys",

    /**
     * URL address published by image server
     */
    imgServerUrl: 'http://192.168.3.185:88/yiyang/',


    /**
     * Determine whether the string is empty
     * @param {Object} str
     * true：not empty
     * false：empty
     */
    isNotNull: function (str) {
        if (str != null && str != "" && str != undefined) {
            return true;
        }
        return false;
    },

    /**
     * Encapsulate the message prompt box. 
	 * The default Mui does not support centering and custom icons, so H5+ is used
     * @param {Object} msg
     * @param {Object} type
     */
    showToast: function (msg, type) {
        plus.nativeUI.toast(msg,
            {icon: "../images/" + type + ".png", verticalAlign: "center"})
    },
    /**
     * Save user's global object
     */
    setUserGlobalInfo: function (user) {
        var userInfoStr = JSON.stringify(user);
        plus.storage.setItem("userInfo", userInfoStr);
    },
    /**
     * Get user's global object
     */
    getUserGlobalInfo: function () {
        var userInfoStr = plus.storage.getItem("userInfo");
        return JSON.parse(userInfoStr);
    },
    /**
     * Log out
     */
    userLogout: function () {
        plus.storage.removeItem("userInfo");
    },

	/**
	 * Save the user's contact list
	 */
    setContactList: function (myFriendList) {
        var contactListStr = JSON.stringify(myFriendList);
        plus.storage.setItem("contactList", contactListStr);
    },
    /**
     * Get the user's contact list from local
     */
    getContactList: function () {
        var contactListStr = plus.storage.getItem("contactList");

        if (!this.isNotNull(contactListStr)) {
            return [];
        }

        return JSON.parse(contactListStr);
    },
	/**
	 * Get friend
	 */
    getFriendFromContactList: function (friendId) {
        var contactListStr = plus.storage.getItem("contactList");
        if (this.isNotNull(contactListStr)) {
            //if not empty, then return those information
            var contactList = JSON.parse(contactListStr);
            for (var i = 0; i < contactList.length; i++) {
                var friend = contactList[i];
                if (friend.friendUserId == friendId) {
                    return friend;
                    break;
                }
            }
        } else {
            return null;
        }

    },
    /**
     * @param {Object} myId
     * @param {Object} friendId
     * @param {Object} msg
     * @param {Object} flag Determine whether this message is sent by me or a friend 1: me 2: friend
     */
    saveUserChatHistory: function (myId, friendId, msg, flag) {
        var me = this;
        var chatKey = "chat-" + myId + "-" + friendId;
        //Whether the chat record obtained from the local cache exists
        var chatHistoryListStr = plus.storage.getItem(chatKey);

        //Variables used to store local chat objects
        var chatHistoryList;
        if (me.isNotNull(chatHistoryListStr)) {
            chatHistoryList = JSON.parse(chatHistoryListStr);
        } else {
            //if empty
            chatHistoryList = [];
        }

        //Build chat msg object
        var singleMsg = new me.ChatHistory(myId, friendId, msg, flag);
        //Append object to list
        chatHistoryList.push(singleMsg);

        //save to local
        plus.storage.setItem(chatKey, JSON.stringify(chatHistoryList));

    },

    getUserChatHistory: function (myId, friendId) {
        var me = this;
        var chatKey = "chat-" + myId + "-" + friendId;
        //Whether the chat record obtained from the local cache exists
        var chatHistoryListStr = plus.storage.getItem(chatKey);
        
        //Variables used to store local chat objects
        var chatHistoryList;
        if (me.isNotNull(chatHistoryListStr)) {
            chatHistoryList = JSON.parse(chatHistoryListStr);
        } else {
            //if empty
            chatHistoryList = [];
        }
        return chatHistoryList;
    },
    /**
     * Delete the chat records of the currently logged in user and friends
     * @param {Object} myId
     * @param {Object} friendId
     */
    deleteUserChatHistory: function (myId, friendId) {
        var chatKey = "chat-" + myId + "-" + friendId;
        plus.storage.removeItem(chatKey);
    },
    /**
     * A snapshot of chat records. Only the last message of each chat with a friend is saved
     * @param {Object} myId
     * @param {Object} friendId
     * @param {Object} msg
     * @param {Object} isRead
     */
    saveUserChatSnapshot: function (myId, friendId, msg, isRead) {
        var me = this;
        var chatKey = "chat-snapshot" + myId;
        var chatSnapshotListStr = plus.storage.getItem(chatKey);
        var chatSnapshotList;
        if (me.isNotNull(chatSnapshotListStr)) {
            chatSnapshotList = JSON.parse(chatSnapshotListStr);
            //loop the snapshot list and determine whether each element contains friendid. If it matches, delete it
            for (var i = 0; i < chatSnapshotList.length; i++) {
                if (chatSnapshotList[i].friendId == friendId) {
                    chatSnapshotList.splice(i, 1);
                    break;
                }
            }
        } else {
            chatSnapshotList = [];
        }

        var singleMsg = new me.CHatSnapshot(myId, friendId, msg, isRead);
        //append to list
        chatSnapshotList.unshift(singleMsg);

        //save to local
        plus.storage.setItem(chatKey, JSON.stringify(chatSnapshotList));

    },
    getUserChatSnapshot: function (myId) {
        var me = this;
        var chatKey = "chat-snapshot" + myId;
        var chatSnapshotListStr = plus.storage.getItem(chatKey);

        var chatSnapshotList;
        if (me.isNotNull(chatSnapshotListStr)) {
            chatSnapshotList = JSON.parse(chatSnapshotListStr);
        } else {
            chatSnapshotList = [];
        }
        return chatSnapshotList;
    },
    readUserChatSnapShot: function (myId, friendId) {
        var me = this;
        var chatKey = "chat-snapshot" + myId;
        var chatSnapshotListStr = plus.storage.getItem(chatKey);
        var chatSnapshotList;
        if (me.isNotNull(chatSnapshotListStr)) {
            chatSnapshotList = JSON.parse(chatSnapshotListStr);
            //loop the snapshot list and determine whether each element contains friendid. If it matches, replace it
            for (var i = 0; i < chatSnapshotList.length; i++) {
                var item = chatSnapshotList[i];
                if (item.friendId == friendId) {
                    item.isRead = true;
                    //replace
                    chatSnapshotList.splice(i, 1, item);
                    break;
                }
            }
            plus.storage.setItem(chatKey, JSON.stringify(chatSnapshotList));
        } else {
            return;
        }
    },
    /**
     * Delete local chat snapshot record
     * @param {Object} myId
     * @param {Object} friendId
     */
    deleteUserChatSnapshot: function (myId, friendId) {
        var me = this;
        var chatKey = "chat-snapshot" + myId;
        var chatSnapshotListStr = plus.storage.getItem(chatKey);
        var chatSnapshotList;
        if (me.isNotNull(chatSnapshotListStr)) {
            chatSnapshotList = JSON.parse(chatSnapshotListStr);
            //loop the snapshot list and determine whether each element contains friendid. If it matches, delete it
            for (var i = 0; i < chatSnapshotList.length; i++) {
                var item = chatSnapshotList[i];
                if (item.friendId == friendId) {
                    chatSnapshotList.splice(i, 1);
                    break;
                }
            }
        } else {
            return;
        }
        //Replace the original snapshot list
        plus.storage.setItem(chatKey, JSON.stringify(chatSnapshotList));
    },
    //the enumeration at the back-end
    CONNECT: 1, 
    CHAT: 2,
    SIGNED: 3,
    KEEPALIVE: 4,
    PULL_FRIEND: 5,
    /**
     * It is consistent with the chatmsg chat model object at the back-end
     * @param {Object} senderId
     * @param {Object} receiverId
     * @param {Object} msg
     * @param {Object} msgId
     */
    ChatMsg: function (senderId, receiverId, msg, msgId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.msg = msg;
        this.msgId = msgId;
    },
    /**Build DataContent model object
     * @param {Object} action
     * @param {Object} chatMsg
     * @param {Object} extand
     */
    DataContent: function (action, chatMsg, extand) {
        this.action = action;
        this.chatMsg = chatMsg;
        this.extand = extand;
    },
    ChatHistory: function (myId, friendId, msg, flag) {
        this.myId = myId;
        this.friendId = friendId;
        this.msg = msg;
        this.flag = flag;
    },
    /**
     * Functions that create snapshot objects
     * @param {Object} myId
     * @param {Object} friendId
     * @param {Object} msg
     * @param {Object} isRead Used to determine whether the message is read or unread
     */
    CHatSnapshot: function (myId, friendId, msg, isRead) {
        this.myId = myId;
        this.friendId = friendId;
        this.msg = msg;
        this.isRead = isRead;
    }

}
