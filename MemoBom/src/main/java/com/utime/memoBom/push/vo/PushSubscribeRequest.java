package com.utime.memoBom.push.vo;

//PushSubscribeRequest.java
import java.util.Map;

public class PushSubscribeRequest {
 public Map<String, Object> subscription; // endpoint, keys, expirationTime
 public Map<String, Object> extra;        // userAgent 등
}
