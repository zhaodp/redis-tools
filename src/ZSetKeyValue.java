import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: zhaodp
 * Date: 16-7-15
 * Time: 上午11:44
 * To change this template use File | Settings | File Templates.
 */
public class ZSetKeyValue{
    public static String getJsonLine(Jedis jedis, String key) {
        Gson gson = new Gson();
        Set<Tuple> set = jedis.zrangeWithScores(key,0,-1);
        KeyValue keyValue = new KeyValue();
        keyValue.key = key;
        keyValue.ttl = jedis.ttl(key);
        keyValue.value = new ArrayList<ZSetMember>();
        for (Tuple tuple : set) {
           ZSetMember zSetMember = new ZSetMember();
            zSetMember.score = tuple.getScore();
            zSetMember.value = tuple.getElement();
            keyValue.value.add(zSetMember);
        }
        return gson.toJson(keyValue);
    }
    public static void loadJsonIntoRedis(Jedis jedis, String json) {
        Gson gson = new Gson();
        KeyValue keyValue = gson.fromJson(json,KeyValue.class);
        if(keyValue.value != null){
            for (ZSetMember zSetMember : keyValue.value) {
                 jedis.zadd(keyValue.key,zSetMember.score,zSetMember.value);
            }
            if(keyValue.ttl != -1){
                jedis.expire(keyValue.key,(int)keyValue.ttl);
            }
        }
    }
    static class ZSetMember{
        public double score;
        public String value;
    }

    static class KeyValue{
        public String key;
        public String type = "zset";
        public long ttl = -1;
        public List<ZSetMember> value;
    }
}
