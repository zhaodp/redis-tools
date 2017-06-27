import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;

public class HashKeyValue {

    public static String getJsonLine(Jedis jedis, String key) {
        Gson gson = new Gson();
        Map<String,String> value = jedis.hgetAll(key);
        KeyValue keyValue = new KeyValue();
        keyValue.key = key;
        keyValue.value = value;
        keyValue.ttl = jedis.ttl(key);
        return gson.toJson(keyValue);
    }

    public static void loadJsonIntoRedis(Jedis jedis, String json) {
        Gson gson = new Gson();
        KeyValue keyValue = gson.fromJson(json,KeyValue.class);
        if(keyValue.value != null){
             Set<String> keys = keyValue.value.keySet();
            for(String key : keys){
                String value = keyValue.value.get(key);
                jedis.hset(keyValue.key,key,value);
            }
        }
        if(keyValue.ttl != -1){
            jedis.expire(keyValue.key,(int)keyValue.ttl);
        }
    }

    static public class KeyValue {
        public String key;
        public String type = "hash";
        public long ttl = -1;
        public  Map<String,String> value;
    }
}
