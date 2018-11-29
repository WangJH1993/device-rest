package org.edgexfoundry.controller;

import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.handler.CoreDataMessageHandler;
import org.edgexfoundry.handler.RestHandler;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("entrance/recognition")
public class FaceRecognitionController {
    private static final EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(FaceRecognitionController.class);

    @Autowired
    private CoreDataMessageHandler coreDataMessageHandler;

    @Autowired
    private RestHandler restHandler;

    @RequestMapping(value = "/report", method = RequestMethod.POST)
    public ResponseEntity<String> report(@RequestBody Map<String,Object> params){
        System.out.println("=================");
        List<Reading> readings = new ArrayList<Reading>();
        try {
            //TODO 01 参数非空判断
            if (params.isEmpty()){  //参数为空

            }
            //TODO 02 生成List<Reading>集合，Reading格式是name:commandName，value:123， device:上海智诚
            String deviceName = params.get("deviceName")+"";  //获取设备名称
            if (deviceName!=null&&!deviceName.equals("")){
                Reading reading = coreDataMessageHandler.buildReading("reportRec",params.toString(),deviceName);
                readings.add(reading);
                Map<String,String> responseResult = restHandler.sendTransaction(deviceName,readings);

            }else{
                logger.error("The deviceName can not bee null");
            }

        }catch (Exception e ){

        }
        return new ResponseEntity<>("123", HttpStatus.OK);
    }
}
