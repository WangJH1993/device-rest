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

package org.edgexfoundry.rest;

import org.edgexfoundry.data.DeviceStore;
import org.edgexfoundry.data.ObjectStore;
import org.edgexfoundry.data.ProfileStore;
import org.edgexfoundry.domain.ScanList;
import org.edgexfoundry.domain.meta.*;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.handler.RestHandler;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.edgexfoundry.utils.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;

@Service
public class RestDriver {

  private static final EdgeXLogger logger =
      EdgeXLoggerFactory.getEdgeXLogger(RestDriver.class);

  @Value("${ISALLPOST}")
  private boolean isallpost;

  @Autowired
  ProfileStore profiles;

  @Autowired
  DeviceStore devices;

  @Autowired
  ObjectStore objectCache;

  @Autowired
  RestHandler handler;
  

  public ScanList discover() {
    ScanList scan = new ScanList();

    // TODO 4: [Optional] For discovery enabled device services:
    // Replace with Rest specific discovery mechanism
    // TODO 5: [Required] Remove next code block if discovery is not used
    for (int i = 0; i < 10; i++) {
      Map<String, String> identifiers = new HashMap<>();
      identifiers.put("name", String.valueOf(i));
      identifiers.put("address", "02:01:00:11:12:1" + String.valueOf(i));
      identifiers.put("interface", "default");
      scan.add(identifiers);
    }

    return scan;
  }


    /**
     * 执行命令发送，并根据结果修改Transaction完成情况
     * @param device  设备信息
     * @param commandName 命令名称
     * @param arguments
     */
    public String process(Device device, String commandName, String arguments) {
        String result = "";
        String path = "";
        String method = "POST";
        //判断是get/set方法
        if (!isallpost)
            method = (arguments==null)?"GET":"POST";

        List<Command> commands = device.getProfile().getCommands();  //通过commandName获取path路径
        for (Command comm: commands) {
            if (comm.getName().equals(commandName)&&"GET".equals(method)){
                path = comm.getGet().getPath();
            }else if (comm.getName().equals(commandName)){  //默认PUT和POST都看作PUT（后续优化）
                path = comm.getPut().getPath();
            }
        }
        result = processCommand(path,device.getAddressable(),arguments,method);

        return result;
    }




    public String processCommand(String path, Addressable addressable, String arguments,String method) {
        Protocol protocol = addressable.getProtocol();  //协议
        String ip = addressable.getAddress();  //ip
        int port = addressable.getPort();  //端口
        logger.debug("protocol: " + protocol + ", ip: "
                + ip +  "port"+  port  +  "path"  +  path  +  "method"  +  method  +  ", value: " + arguments);
        path = appendUrl(path,arguments);
        String url = protocol+"://"+ip+":"+port+path;
        Map<String,Object> argumentsMap = DataUtil.jsonToMap(arguments);
        arguments = argumentsMap.containsKey("putBody")?argumentsMap.get("putBody")+"":"{}";
        String result = sendSms(url,arguments,method);
        return result;
    }

  
  public static String sendSms(String url,String body,String method) {
	  try {
		  URL command = new URL(url);
		  HttpURLConnection con = (HttpURLConnection) command.openConnection();
		  if (method.equals("POST")||method.equals("PUT")) {
		    con.setRequestMethod(method);
		    con.setDoOutput(true);
		    con.setRequestProperty("Content-Type", "application/json");
		    con.setRequestProperty("Content-Length", Integer.toString(body.length()));
		    OutputStream os = con.getOutputStream();
		    os.write(body.getBytes());
		  }
		  BufferedReader res = new BufferedReader(new InputStreamReader(con.getInputStream()));
		  StringBuilder response = new StringBuilder();
		  for (String responseLine = res.readLine(); responseLine != null; responseLine =
		      res.readLine()) {
		    response.append(responseLine);
		  }
		  res.close();
		  System.out.println(new ResponseEntity<>(response.toString(), HttpStatus.OK));
		  return response.toString();
	} catch (Exception e) {
		throw new ServiceException(e);
	}
  }

  public String appendUrl(String path,String arguments){
      Map map = new HashMap();
      StringBuffer sb = new StringBuffer("?");
      try {
          map = DataUtil.jsonToMap(arguments);

          if (path.contains("{")) {
              //url填充参数
              String[] pathResult = path.split("/");
              for (String pr : pathResult) {
                  if (pr.startsWith("{") && pr.endsWith("}")) {
                      String argument = pr.substring(1, pr.length() - 1);
                      if (map.containsKey(argument)) {
                          sb.append(argument + "=" + map.get(argument) + "&");
                          map.remove(argument);
                      } else {
                          sb.append(argument + "=" + null + "&");
                      }
                  }
              }
              String str = sb.substring(0,sb.length()-1);
              String first = path.split("\\{")[0];
              path = first.substring(0,first.length()-1)+str;
          }
      }catch (Exception e){
          logger.error("There are something wrong with arguments");
          e.printStackTrace();
      }
        return path;
  }


  /**
        *  此方法的主要作用是在启动device-mqtt为服务的时候 判断在meta-data库中是否存在设备信息
           如果不包含便主动创建一个设备,所以这里没有必要去写
   */
  public void initialize() {
    // TODO 3: [Optional] Initialize the interface(s) here if necessary, runs once on
    // service startup
  }

  /**
   * 
   * @param address
   */
  public void disconnectDevice(Addressable address) {
    // TODO 6: [Optional] Disconnect devices here using driver level operations
  }

  @SuppressWarnings("unused")
  private void receive() {
    // TODO 7: [Optional] Fill with your own implementation for handling asynchronous
    // data from the driver layer to the device service
    Device device = null;
    String result = "";
    ResourceOperation operation = null;

    objectCache.put(device, operation, result);
  }
}
