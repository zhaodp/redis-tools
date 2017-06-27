import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: zhaodp
 * Date: 16-7-15
 * Time: 上午11:44
 * To change this template use File | Settings | File Templates.
 */
public class SetKeyValue {
    public static String getJsonLine(Jedis jedis, String key) {
        Gson gson = new Gson();
        Set<String> set = jedis.smembers(key);
        KeyValue keyValue = new KeyValue();
        keyValue.key =key;
        keyValue.value = set;
        keyValue.ttl = jedis.ttl(key);
        return gson.toJson(keyValue);
    }
    public static void loadJsonIntoRedis(Jedis jedis, String json) {
        Gson gson = new Gson();
        KeyValue keyValue = gson.fromJson(json,KeyValue.class);
        if(keyValue.value != null){
            for (String member : keyValue.value) {
                jedis.sadd(keyValue.key,member);
            }
            if(keyValue.ttl != -1){
                jedis.expire(keyValue.key,(int)keyValue.ttl);
            }
        }
    }
    static class KeyValue{
        public String key;
        public String type = "set";
        public long ttl = -1;
        public Set<String> value;
    }
}
