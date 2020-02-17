package make.money.share.controller;

import make.money.share.mapper.SharesMapper;
import make.money.share.pojo.Shares;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class GetSharesController {

    @Autowired
    private SharesMapper sharesMapper;

    private String sharesCodes ="sz002466,sh600678,sh603103";

    //3.添加定时任务
    @Scheduled(cron = "0/10 * * * * ?")
    public void getShare() throws IOException, ParseException {
        //1.打开浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //2.声明get请求
        HttpGet httpGet = new HttpGet("http://hq.sinajs.cn/list="+sharesCodes);
        //3.发送请求
        CloseableHttpResponse response = httpClient.execute(httpGet);
        //4.判断状态码
        if(response.getStatusLine().getStatusCode()==200){
            HttpEntity entity = response.getEntity();
            //使用工具类EntityUtils，从响应中取出实体表示的内容并转换成字符串
            String string = EntityUtils.toString(entity, "utf-8");
            System.out.println(string);

            String datas[] = string.split("\\r?\\n");
            for(String data : datas){
                data = data.substring(data.indexOf("\"")+1,data.length()-2);
                String shareData[] = data.split(",");
                Shares shares = new Shares();
                shares.setName(shareData[0]);
                shares.setNowprice(Double.parseDouble(shareData[3]));
                shares.setNumber(Integer.parseInt(shareData[8])/100);
                shares.setTotal(Long.parseLong(shareData[9].split("\\.")[0])/10000);
                String dateFormat = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                shares.setHappentime(format.parse(shareData[30]+" "+shareData[31]));

                sharesMapper.insert(shares);
            }

        }
        //5.关闭资源
        response.close();
        httpClient.close();
    }
}
