import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 用于导出redis数据
 * User: zhaodp
 * Date: 2016-7-15
 */
public class RedisDumper {
    @Parameter(names = {"--host", "-h"}, description = "redis server host")
    String host;
    @Parameter(names = {"--port", "-p"}, description = "redis server host")
    int port = 6379;
    @Parameter(names = {"--password", "-a"}, description = "redis auth")
    String password;
    @Parameter(names = {"--index", "-i"}, description = "redis db index")
    int index;
    @Parameter(names = {"--dataPath", "-d"}, description = "the path where dumped data save to ")
    String dataPath;

    public static void main(String[] args) {
        RedisDumper redisDumper = new RedisDumper();
        new JCommander(redisDumper, args);
        redisDumper.doDump();
    }

    private void doDump() {
        Jedis jedis = new Jedis(host,port);
        if(password != null)
          jedis.auth(password);
        jedis.select(index);
        BufferedWriter bw = null;
        try {
            File dataFile = new File(dataPath);
            FileWriter fileWriter = new FileWriter(dataFile);
            bw = new BufferedWriter(fileWriter);

            ScanResult<String> result = jedis.scan("0");
            while (true) {
                List<String> keyList = result.getResult();
                for (int i = 0; i < keyList.size(); i++) {
                    String key = keyList.get(i);
                    if ("string".equals(jedis.type(key))) {
                        bw.write(StringKeyValue.getJsonLine(jedis, key));
                        bw.newLine();
                    } else if ("zset".equals(jedis.type(key))) {
                        bw.write(ZSetKeyValue.getJsonLine(jedis, key));
                        bw.newLine();
                    } else if ("set".equals(jedis.type(key))) {
                        bw.write(SetKeyValue.getJsonLine(jedis, key));
                        bw.newLine();
                    } else  if("list".equals(jedis.type(key))) {
                        bw.write(ListKeyValue.getJsonLine(jedis, key));
                        bw.newLine();
                    } else  if("hash".equals(jedis.type(key))) {
                        bw.write(HashKeyValue.getJsonLine(jedis, key));
                        bw.newLine();
                    }
                }
                //遍历结束
                if("0".equals(result.getStringCursor())){
                    break;
                }
                result = jedis.scan(result.getStringCursor());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Complete!");
    }
}
