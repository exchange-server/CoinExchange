package com.bizzan.bitrade.moduyun;

public class SmsVoiceVerifyCodeSenderResult {

/*
{
    "result": 0, //0表示成功，非0表示失败
    "errmsg": "", //result非0时的具体错误信息
    "ext": "some msg", //可选字段，用户的session内容，Kewail server回包中会原样返回
    "callid": "xxxx" //标识本次发送id
}

*/
	public int result;
	public String errmsg;
	public String ext = "";
	public String callid;

	public String toString() {
		if (0 == result) {
			return String.format(
					"SmsVoiceVerifyCodeSenderResult\nresult %d\nerrmsg %s\next %s\ncallid %s",
					result, errmsg, ext, callid);
		} else {
			return String.format(
					"SmsVoiceVerifyCodeSenderResult\nresult %d\nerrmsg %s\next %s",
					result, errmsg, ext);
		}
	}
}
