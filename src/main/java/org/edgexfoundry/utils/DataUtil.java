package org.edgexfoundry.utils;


import net.sf.json.JSONObject;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataUtil {
    private static final EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(DataUtil.class);
    /**
    * @Method:         jsonToMap
    * @Author:         WJH
    * @CreateDate:     2018/11/20 11:46
    * @UpdateUser:     WJH
    * @UpdateDate:     2018/11/20 11:46
    * @UpdateRemark:   修改内容改内容
    * @Version:        1.0
    */
    public static Map<String,Object> jsonToMap(String jsonStr){
        Map<String,Object> map = new HashMap<String,Object>();
        JSONObject json = null;
        try {
            json = JSONObject.fromObject(jsonStr);
            Iterator it = json.keys();
            while (it.hasNext()){
                String key = String.valueOf(it.next());
                if (json.get(key).toString().indexOf("{") != -1&&json.get(key).toString().indexOf("[{")==-1&&!key.equals("putBody")){
                    JSONObject jsonArray = JSONObject.fromObject(json.get(key));
                    Iterator its = jsonArray.keys();
                    while (its.hasNext()){
                        String keys = String.valueOf(its.next());
                        map.put(keys,jsonArray.get(keys).toString());
                    }
                }else{
                    map.put(key,json.get(key).toString());
                }
            }
        }catch (Exception e){
            logger.error("参数解析失败："+e);
            e.printStackTrace();
            return null;
        }
        return map;
    }

}