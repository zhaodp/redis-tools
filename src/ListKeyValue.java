import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;

public class ListKeyValue {

    public static String getJsonLine(Jedis jedis, String key) {
        Gson gson = new Gson();
        KeyValue keyValue = new KeyValue();
        keyValue.key = key;
        keyValue.value = jedis.lrange(key,0,-1);
        keyValue.ttl = jedis.ttl(key);
        return gson.toJson(keyValue);
    }

    public static void loadJsonIntoRedis(Jedis jedis, String json) {
        Gson gson = new Gson();
        KeyValue keyValue = gson.fromJson(json,KeyValue.class);
        if(keyValue.value != null){
             Collections.reverse( keyValue.value);
            for (String value : keyValue.value){
                jedis.lpush(keyValue.key,value);
            }
        }

        if(keyValue.ttl != -1){
            jedis.expire(keyValue.key,(int)keyValue.ttl);
        }
    }

    static public class KeyValue {
        public String key;
        public String type = "list";
        public long ttl = -1;
        public List<String> value;
    }
}
