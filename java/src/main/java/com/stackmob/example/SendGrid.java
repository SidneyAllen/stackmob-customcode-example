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
import com.stackmob.sdkapi.*;

import com.stackmob.sdkapi.http.HttpService;
import com.stackmob.sdkapi.http.request.HttpRequest;
import com.stackmob.sdkapi.http.request.GetRequest;
import com.stackmob.sdkapi.http.response.HttpResponse;
import com.stackmob.core.ServiceNotActivatedException;
import com.stackmob.sdkapi.http.exceptions.AccessDeniedException;
import com.stackmob.sdkapi.http.exceptions.TimeoutException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.DatastoreException;

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
import java.util.ArrayList;

// Added JSON parsing to handle JSON posted in the body
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class SendGrid implements CustomCodeMethod {

  //Create your SendGrid Acct at sendgrid.com
  static String API_USER = "SidneyAllen";
  static String API_KEY = "stackmob0711";

  @Override
  public String getMethodName() {
    return "sendgrid_email";
  }
    
    
  @Override
  public List<String> getParams() {
    return Arrays.asList();
  }  

  private String getMailBody()
    {
        return "<!DOCTYPE html>"
        + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:og=\"http://ogp.me/ns#\">"
        + "   <head>"
        + "      <title>Wallit!</title>"
        + "      <meta property=\"og:type\" content=\"email\"/>"
        + "   </head>"
        + "   <body>"
        + "      <table cellpadding=\"7\" cellspacing=\"0\" border=\"0\" width=\"700\">"
        + "         <tr>"
        + "            <td align=\"center\">"
        + "               <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">"
        + "                  <tr><td align=\"center\" height=\"120\"><img src=\"https://wallitapp.com/img/email/wallit-top-685x120.jpg\" border=\"0\" title=\"Wallit!\" alt=\"Wallit!\"></td></tr>"
        + "                  <tr>"
        + "                     <td>"
        + "                        <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"95%\">"
        + "                           <tr>"
        + "                              <td style=\"padding-left:30px font-family:Helvetica\">"
        + "                                 <p style=\"font-weight:bold color:#676767 font-size:26px\">Hi Justine,</p>"
        + "                                 <p style=\"color:#257eb6  font-size:40px\">Welcome to Wallit!</p>"
        + "                                 <table cellpadding=\"2\" cellspacing=\"0\" border=\"0\" style=\"font-family:Helvetica color:#676767 font-size:18px\">"
        + "                                    <tr><td colspan=\"2\">Here's how to get the most out of Wallit!:<br/><br/></td></tr>"
        + "                                    <tr><td width=\"25\"><img src=\"https://wallitapp.com/img/email/dot.jpg\" border=\"0\"></td><td>Scroll the app for walls of interest</td></tr>"
        + "                                    <tr><td><img src=\"https://wallitapp.com/img/email/dot.jpg\" border=\"0\"></td><td>Follow walls you like (by clicking on the star)</td></tr>"
        + "                                    <tr><td><img src=\"https://wallitapp.com/img/email/dot.jpg\" border=\"0\"></td><td>Make a post (by clicking on the plus)</td></tr>"
        + "                                    <tr><td><img src=\"https://wallitapp.com/img/email/dot.jpg\" border=\"0\"></td><td>Create your own wall by going to the side nav (click on the three bars)</td></tr>"
        + "                                 </table>"
        + "                                 <br/>"
        + "                                 <table cellpadding=\"2\" cellspacing=\"0\" border=\"0\" style=\"font-family:Helvetica color:#676767 font-size:18px \">"
        + "                                    <tr><td colspan=\"2\">Wallit! is your very own public bulletin board.  For example if you are student you can use Wallit! to:<br/><br/></td></tr>"
        + "                                    <tr><td width=\"25\"><img src=\"https://wallitapp.com/img/email/dot.jpg\" border=\"0\"></td><td>Promote or find an event</td></tr>"
        + "                                    <tr><td><img src=\"https://wallitapp.com/img/email/dot.jpg\" border=\"0\"></td><td>Recruit classmates or sign up for a club</td></tr>"
        + "                                    <tr><td><img src=\"https://wallitapp.com/img/email/dot.jpg\" border=\"0\"></td><td>Sell or buy texts </td></tr>"
        + "                                    <tr><td><img src=\"https://wallitapp.com/img/email/dot.jpg\" border=\"0\"></td><td>Share or discover emergency alerts </td></tr>"
        + "                                 </table>"
        + "                                 <p style=\"color:#676767 font-size:18px \">"
        + "                                    Connect with anyone nearby to accomplish anything. Just Wallit!<br/><br/>"
        + "                                    <b>Thanks for joining the Wallit! community!</b><br/><br/>"
        + "                                    <b>-Veysel, Founder of Wallit!</b>"
        + "                                 </p>"
        + "                                 <p style=\"color:#257eb6 font-size:40px \">Get started today!</p>"
        + "                              </td>"
        + "                           </tr>"
        + "                        </table>"
        + "                     </td>"
        + "                  </tr>"
        + "                  <tr>"
        + "                     <td style=\"padding-left:10px\" valign=\"middle\">"
        + "                        <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"padding:0 \">"
        + "                           <tr style=\"padding:0 \">"
        + "                              <td style=\"padding:0 \"><img src=\"https://wallitapp.com/img/email/phone-00-222x655.jpg\" border=\"0\"></td>"
        + "                              <td style=\"padding:0 \"><img src=\"https://wallitapp.com/img/email/phone-01-222x655.jpg\" border=\"0\"></td>"
        + "                              <td style=\"padding:0 \"><img src=\"https://wallitapp.com/img/email/phone-02-223x655.jpg\" border=\"0\"></td>"
        + "                           </tr>"
        + "                        </table>                        "
        + "                     </td>"
        + "                  </tr>"
        + "                  <tr><td style=\"padding-left:30px \" valign=\"middle\" height=\"40\"><img src=\"https://wallitapp.com/img/email/hr-seperator-637x2.jpg\" border=\"0\"></td></tr>"
        + "                  <tr>"
        + "                     <td>"
        + "                        <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"95%\">"
        + "                           <tr>"
        + "                              <td style=\"padding-left:30px font-family:Helvetica \">"
        + "                                 <p style=\"font-weight:bold color:#227bb4 font-size:30px \">Want to learn more?</p>"
        + "                                 <p style=\"color:#676767 font-size:18px \">Click <a href=\"#\" style=\"color:#0099cc \">here</a> for a guide on how to make the most of Wallit!</p>"
        + "                                 <p style=\"font-weight:bold color:#227bb4 font-size:30px \">Have questions or feedback?</p>"
        + "                                 <p style=\"color:#676767 font-size:18px \">"
        + "                                    We'd love to hear from you! Click <a href=\"#\" style=\"color:#0099cc \">here</a> to submit a question or feedback. If you prefer communicating with us through the app, click on the \"Feedback\" button located on the left sidebar."
        + "                                 </p>"
        + "                              </td>"
        + "                           </tr>"
        + "                        </table>"
        + "                     </td>"
        + "                  </tr>"
        + "                  <tr><td style=\"padding-left:30px \">"
        + "                     <table cellpadding=\"12\" cellspacing=\"0\" border=\"0\" width=\"650\" bgcolor=\"cbcbcb\">"
        + "                        <tr>"
        + "                           <td style=\"color:#666666 font-family:Helvetica font-weight:bold font-size:22px \">Don't forget to share your Wallit! experience!</td>"
        + "                           <td width=\"40\"><a href=\"#\"><img src=\"https://wallitapp.com/img/email/facebook.jpg\" border=\"0\" title=\"Share on Facebook\" alt=\"Share on Facebook\"></a></td>"
        + "                           <td width=\"40\"><a href=\"#\"><img src=\"https://wallitapp.com/img/email/twitter.jpg\" border=\"0\" title=\"Share on Twitter\" alt=\"Share on Twitter\"></a></td>"
        + "                        </tr>"
        + "                     </table>"
        + "                  </td></tr>"
        + "                  <tr><td style=\"padding-left:30px \" >"
        + "                     <br/><br/>"
        + "                     <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"\" style=\"color:#0099cc font-family:Helvetica font-size:16px \">"
        + "                        <tr>"
        + "                           <td style=\"padding-right:15px \"><a href=\"#\" style=\"color:#0099cc \">Refer a Friend</a></td><td style=\"padding-right:15px \"> | </td>"
        + "                           <td style=\"padding-right:15px \"><a href=\"#\" style=\"color:#0099cc \">Unsubscribe</a></td><td style=\"padding-right:15px \"> | </td>"
        + "                           <td style=\"padding-right:15px \"><a href=\"#\" style=\"color:#0099cc \">Privacy Policy</a></td><td style=\"padding-right:15px \"> | </td>"
        + "                           <td style=\"padding-right:15px \"><a href=\"#\" style=\"color:#0099cc \">Contact Us</a></td>"
        + "                        </tr>"
        + "                     </table>"
        + "                  </td></tr>"
        + "               </table>"
        + "               <br/><br/>"
        + "            </td>"
        + "         </tr>"
        + "      </table>"
        + "   </body>"
        + "</html>"
        + "";
    }


  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    int responseCode = 0;
    String responseBody = "";
    String username = "";
    String subject = "";
    String text = "";
    String from = "";
    String to = "";
    String toname = "";
    String body = "";
    String url = "";
    
    LoggerService logger = serviceProvider.getLoggerService(SendGrid.class);
    //Log the JSON object passed to the StackMob Logs
    logger.debug(request.getBody());
    
    JSONParser parser = new JSONParser();
    try {
      Object obj = parser.parse(request.getBody());
      JSONObject jsonObject = (JSONObject) obj;

      //We use the username passed to query the StackMob datastore
      //and retrieve the user's name and email address
      username = (String) jsonObject.get("username");

      // The following values could be static or dynamic
      subject = (String) jsonObject.get("subject");
      text = (String) jsonObject.get("text");
      from = (String) jsonObject.get("from");
    } catch (ParseException e) {
      logger.error(e.getMessage(), e);
      responseCode = -1;
      responseBody = e.getMessage();
    }
	
    if (username == null || username.isEmpty()) {
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "the username passed was empty or null");
      return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
    }
    	
    // get the StackMob datastore service and assemble the query
    DataService dataService = serviceProvider.getDataService();
    	 
    // build a query
    List<SMCondition> query = new ArrayList<SMCondition>();
    query.add(new SMEquals("username", new SMString(username)));

    SMObject userObject;
    List<SMObject> result;
    try {
      // return results from user query
      result = dataService.readObjects("user", query);
      if (result != null && result.size() == 1) {
        userObject = result.get(0);
        to = userObject.getValue().get("email").toString();
        toname = userObject.getValue().get("name").toString();
      } else {
        HashMap<String, String> errMap = new HashMap<String, String>();
        errMap.put("error", "no user found");
        errMap.put("detail", "no matches for the username passed");
        return new ResponseToProcess(HttpURLConnection.HTTP_OK, errMap); // http 500 - internal server error
      }
      
    } catch (InvalidSchemaException e) {
      HashMap<String, String> errMap = new HashMap<String, String>();
      errMap.put("error", "invalid_schema");
      errMap.put("detail", e.toString());
      return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
    } catch (DatastoreException e) {
      HashMap<String, String> errMap = new HashMap<String, String>();
      errMap.put("error", "datastore_exception");
      errMap.put("detail", e.toString());
      return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
    } catch(Exception e) {
      HashMap<String, String> errMap = new HashMap<String, String>();
      errMap.put("error", "unknown");
      errMap.put("detail", e.toString());
      return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
    }
    
    if (subject == null || subject.equals("")) {
      logger.error("Subject is missing");
    }

    //Encode any parameters that need encoding (i.e. subject, toname, text)
    try {
      subject = URLEncoder.encode(subject, "UTF-8");
      text = URLEncoder.encode(text, "UTF-8");
      toname = URLEncoder.encode(toname, "UTF-8");
      //body = URLEncoder.encode(text, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      logger.error(e.getMessage(), e);
    }

    String queryParams = "api_user=" + API_USER + "&api_key=" + API_KEY + "&to=" + to + "&toname=" + toname + "&subject=" + subject + "&from=" + from  + "&html=" + getMailBody();

    //url =  "https://www.sendgrid.com/api/mail.send.json?" + queryParams;
    url =  "https://www.sendgrid.com/api/mail.send.json";


    Header accept = new Header("Accept-Charset", "utf-8");
    Header content = new Header("Content-Type", "application/x-www-form-urlencoded");

    Set<Header> set = new HashSet();
    set.add(accept);
    set.add(content);

    try {
    
      JSONObject jsonObj = new JSONObject();
      jsonObj.put("html", getMailBody());

      jsonObj.put("api_user", API_USER);
      jsonObj.put("api_key", API_KEY);
      jsonObj.put("to", to);
      jsonObj.put("subject", subject);
      jsonObj.put("from", from);

      logger.error(queryParams);
      logger.error(url);
      logger.error(set.toString());

        
      HttpService http = serviceProvider.getHttpService();
          
      PostRequest req = new PostRequest(url,set,queryParams);

      HttpResponse resp = http.post(req);
      responseCode = resp.getCode();
      responseBody = resp.getBody();
                  
    } catch(TimeoutException e) {
      logger.error(e.getMessage(), e);
      responseCode = -1;
      responseBody = e.getMessage();
                 
    } catch(AccessDeniedException e) {
      logger.error(e.getMessage(), e);
      responseCode = -1;
      responseBody = e.getMessage();
              
    } catch(MalformedURLException e) {
      logger.error(e.getMessage(), e);
      responseCode = -1;
      responseBody = e.getMessage();
           
    } catch(ServiceNotActivatedException e) {
      logger.error(e.getMessage(), e);
      responseCode = -1;
      responseBody = e.getMessage();
    }

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("response_body", responseBody);

    return new ResponseToProcess(responseCode, map);
  }
}
