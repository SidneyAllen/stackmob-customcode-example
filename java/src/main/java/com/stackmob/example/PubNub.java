/**
 * Copyright 2012-2013 StackMob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stackmob.example;

import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.SDKServiceProvider;

import com.stackmob.sdkapi.http.HttpService;
import com.stackmob.sdkapi.http.request.HttpRequest;
import com.stackmob.sdkapi.http.request.GetRequest;
import com.stackmob.sdkapi.http.response.HttpResponse;
import com.stackmob.core.ServiceNotActivatedException;
import com.stackmob.sdkapi.http.exceptions.AccessDeniedException;
import com.stackmob.sdkapi.http.exceptions.TimeoutException;
import java.net.MalformedURLException;
import com.stackmob.sdkapi.http.request.PostRequest;
import com.stackmob.sdkapi.http.Header;
import com.stackmob.sdkapi.LoggerService;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

// Added JSON parsing to handle JSON posted in the body
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.io.UnsupportedEncodingException;

public class PubNub implements CustomCodeMethod {

  //Create your Twilio Acct at twilio.com and enter 
  //Your accountsid and accesstoken below.

  public static final String PUBLISH_KEY = "pub-c-530adfef-67bc-40c6-ae4f-844150faefac";
  public static final String SUBSCRIBE_KEY = "sub-c-21d90e3d-2d2f-11e2-971e-1b6d3d8711a1";
  public static final String SECRET_KEY = "tsec-c-M2NjNjg5MjItN2M5My00NGRkLTgxZjItOGIyOTIwOGY4MTk0";

  @Override
  public String getMethodName() {
    return "publish";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("message","channel");
  }  

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    int responseCode = 0;
    String responseBody = "";

	LoggerService logger = serviceProvider.getLoggerService(PubNub.class);

    //  text message you want to send
    String message = request.getParams().get("message");
    if (message == null || message.isEmpty()) {
      logger.error("Missing message");
    }

    String channel = request.getParams().get("channel");
    if (channel == null || channel.isEmpty()) {
      logger.error("Missing channel");
    }

    String action = "publish";
    String publishKey = PUBLISH_KEY;
    String subscribeKey = SUBSCRIBE_KEY;
    String secretKey = SECRET_KEY;
    String jsonpCallback = "0";
    String jsonMessage = "";

    JSONObject obj = new JSONObject();
    obj.put("msg", message);

    try {
      jsonMessage = URLEncoder.encode(obj.toString(), "UTF-8").replace("+", "%20");
    } catch (Exception e){
      logger.error(e.toString());
    }
    StringBuilder queryStr = new StringBuilder();

    queryStr.append("/");
    queryStr.append(action);
    queryStr.append("/");
    queryStr.append(publishKey);
    queryStr.append("/");
    queryStr.append(subscribeKey);
    queryStr.append("/");
    queryStr.append(secretKey);
    queryStr.append("/");
    queryStr.append(channel);
    queryStr.append("/");
    queryStr.append(jsonpCallback);
    queryStr.append("/");
    queryStr.append(jsonMessage);

    String url = "http://pubsub.pubnub.com" + queryStr;

    logger.error(url.toString());

    Header accept = new Header("Accept-Charset", "utf-8");
    Header content = new Header("Content-Type", "application/x-www-form-urlencoded");

    Set<Header> set = new HashSet();
    set.add(accept);
    set.add(content);

    try {
      HttpService http = serviceProvider.getHttpService();
      GetRequest req = new GetRequest(url,set);
             
      HttpResponse resp = http.get(req);
      responseCode = resp.getCode();
      responseBody = resp.getBody();
    } catch(TimeoutException e) {
      logger.error(e.getMessage(), e);
      responseCode = HttpURLConnection.HTTP_BAD_GATEWAY;;
      responseBody = e.getMessage();
    } catch(AccessDeniedException e) {
      logger.error(e.getMessage(), e);
      responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;;
      responseBody = e.getMessage();
    } catch(MalformedURLException e) {
      logger.error(e.getMessage(), e);
      responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;;
      responseBody = e.getMessage();
    } catch(ServiceNotActivatedException e) {
      logger.error(e.getMessage(), e);
      responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;;
      responseBody = e.getMessage();
    }
      
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("response_body", responseBody);
     
    return new ResponseToProcess(responseCode, map);
  }
}
