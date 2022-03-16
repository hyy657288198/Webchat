package org.yiyang.utils;

/**
 *@ Description: custom response data structure
 * This class is provided for portal, IOS, Android and wechat mall
 * After the portal accepts such data, it needs to use the method of this class to convert it into the data type format (class, or list)
 * Others are handled by themselves. It mainly makes some response status and message return, and you also need to wrap some of your own data
 * 200: indicates success
 * 500: indicates an error. The error information is in the MSG field
 * 501: bean validation error. No matter how many errors are returned to the form of map
 * 502: an error occurred when the interceptor intercepted the user token
 * 555: exception throw information
 */
public class MyJSONResult {

    // Respond to business status
    private Integer status;

    // Response to message
    private String msg;

    // Data in response
    private Object data;
    
    private String ok;

    public static MyJSONResult build(Integer status, String msg, Object data) {
        return new MyJSONResult(status, msg, data);
    }

    public static MyJSONResult ok(Object data) {
        return new MyJSONResult(data);
    }

    public static MyJSONResult ok() {
        return new MyJSONResult(null);
    }
    
    public static MyJSONResult errorMsg(String msg) {
        return new MyJSONResult(500, msg, null);
    }
    
    public static MyJSONResult errorMap(Object data) {
        return new MyJSONResult(501, "error", data);
    }
    
    public static MyJSONResult errorTokenMsg(String msg) {
        return new MyJSONResult(502, msg, null);
    }
    
    public static MyJSONResult errorException(String msg) {
        return new MyJSONResult(555, msg, null);
    }

    public MyJSONResult() {

    }

    public MyJSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public MyJSONResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Boolean isOK() {
        return this.status == 200;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

	public String getOk() {
		return ok;
	}

	public void setOk(String ok) {
		this.ok = ok;
	}

}
