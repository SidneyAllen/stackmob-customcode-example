package com.stackmob.example;
/**
 * Copyright 2013 StackMob
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

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.*;
import com.stackmob.sdkapi.LoggerService;
import com.stackmob.sdkapi.SDKServiceProvider;

import java.net.HttpURLConnection;
import java.util.*;
import java.util.logging.Logger;

public class SelectivePut implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "update_deal";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("title","body","deal_detail_id");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    int responseCode = 0;
    String responseBody = "";
    Boolean validOwner = false;

    LoggerService logger = serviceProvider.getLoggerService(SelectivePut.class);  //Log to the StackMob Custom Code Console

    logger.debug("Start UPDATE");

    String strTitle = request.getParams().get("title");
    String strBody = request.getParams().get("body");
    String strDealDetailId = request.getParams().get("deal_detail_id");

    if ( Util.strCheck(strTitle) ) {
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "the title passed was null or empty.");
      return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
    }

    if ( Util.strCheck(strBody) ) {
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "the body passed was null or empty.");
      return new ResponseToProcess(HttpURLConnection.HTTP_BAD_REQUEST, errParams); // http 400 - bad request
    }

    logger.debug("user: " + request.getLoggedInUser() );
    String username = request.getLoggedInUser();


    logger.debug("Username: " + username);

    if (username == null || username.isEmpty()) {
      logger.debug("Got Username");
      HashMap<String, String> errParams = new HashMap<String, String>();
      errParams.put("error", "no user is logged in");
      return new ResponseToProcess(HttpURLConnection.HTTP_UNAUTHORIZED, errParams); // http 401 - unauthorized
    }

    DataService dataService = serviceProvider.getDataService();   // get the StackMob datastore service and assemble the query

    // build a query based on the USERNAME
    List<SMCondition> query = new ArrayList<SMCondition>();
    query.add(new SMEquals("sm_owner", new SMString("user/" + username)));
    query.add(new SMEquals("deal_detail_id", new SMString(strDealDetailId)));

    SMObject dealObject;
    List<SMObject> result;
    try {
      // return results from user query
      result = dataService.readObjects("deal_detail", query);
      if (result != null && result.size() == 1) {
        // We found the object, and it belongs to the owner, let's update it.
        //dealObject = result.get(0);
        logger.debug("Query Match");
        validOwner = true;

      } else {
        logger.debug("Query No Match");
        HashMap<String, String> errMap = new HashMap<String, String>();
        errMap.put("error", "user not logged in");
        return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, errMap); // http 500 - internal server error
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


    if(validOwner) {
      logger.debug("Valid owner");
      try {
        List<SMUpdate> update = new ArrayList<SMUpdate>();
        update.add(new SMSet("title", new SMString(strTitle)));
        update.add(new SMSet("body", new SMString(strBody)));

        SMObject updateResult = dataService.updateObject("deal_detail", new SMString(strDealDetailId), update); // todo schema with todo_id = todo1
        responseBody = updateResult.toString();
        responseCode = 200;
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
    }  else {
      logger.debug("No Valid Owner");
    }

    logger.debug(responseBody);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("response_body", responseBody);

    return new ResponseToProcess(responseCode, map);
  }
}