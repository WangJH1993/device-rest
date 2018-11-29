/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
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
 *
 * @microservice: device-rest
 * @author: Tyler Cox, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.edgexfoundry.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/device")
public class CommandController {

  @Autowired
  private CommandHandler command;

  @RequestMapping(value = "/{deviceIds}/{cmd}", method =
      { RequestMethod.PUT, RequestMethod.POST, RequestMethod.GET })
  public Callable<Map<String,String>> getCommand(@PathVariable String deviceIds,
      @PathVariable String cmd,
      @RequestBody(required = false) String arguments) {
    Callable<Map<String,String>> callable = new Callable<Map<String,String>>() {
      @Override
      public Map<String,String> call() throws Exception {
        return command.getMultipleResponses(deviceIds, cmd, arguments);
      }
    };
    return callable;
  }

  @RequestMapping(value = "/all/{cmd}", method =
      { RequestMethod.PUT, RequestMethod.POST, RequestMethod.GET })
  public Callable<Map<String,String>> getCommands(@PathVariable String cmd,
      @RequestBody(required = false) String arguments) {
    Callable<Map<String,String>> callable = new Callable<Map<String,String>>() {
      @Override
      public Map<String,String> call() throws Exception {
        return command.getResponses(cmd, arguments);
      }
    };
    return callable;
  }
}
