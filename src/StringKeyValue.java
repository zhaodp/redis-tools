import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

public class StringKeyValue {

    public static String getJsonLine(Jedis jedis, String key) {
        Gson gson = new Gson();
        String value = jedis.get(key);
        KeyValue keyValue = new KeyValue();
        keyValue.key = key;
        keyValue.value = value;
        keyValue.ttl = jedis.ttl(key);
        return gson.toJson(keyValue);
    }

    public static void loadJsonIntoRedis(Jedis jedis, String json) {
        Gson gson = new Gson();
        KeyValue keyValue = gson.fromJson(json,KeyValue.class);
        jedis.set(keyValue.key,keyValue.value);
        if(keyValue.ttl != -1){
            jedis.expire(keyValue.key,(int)keyValue.ttl);
        }
    }

    static public class KeyValue {
        public String key;
        public String type = "string";
        public long ttl = -1;
        public String value;
    }
}
