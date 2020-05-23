package make.money.share.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import make.money.share.mapper.CodeMapper;
import make.money.share.mapper.SharesMapper;
import make.money.share.pojo.Code;
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
@EnableScheduling   // 2.开启定时任务  每周一删除所有的代码 在更新进去 以前代码废弃 分析也没用
public class GetCodeController {

    @Autowired
    private CodeMapper codeMapper;//    private String sharesCodes ="sz002466,sh600678,sh603103";

    //3.添加定时任务
//    @Scheduled(cron = "0/20 * * * * ?")
    @Scheduled(cron = "0 00 17 ? * 7")
    public void getShare() throws IOException, ParseException {
        System.out.println("start");
        QueryWrapper wrapper = new QueryWrapper<>();
        codeMapper.delete(wrapper);

        //1.打开浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        System.out.println("上交所");
        for(int i=600000;i<=688399;i++){
            //2.声明get请求
            HttpGet httpGet = new HttpGet("http://hq.sinajs.cn/list="+"sh"+i);
            //3.发送请求
            CloseableHttpResponse response = httpClient.execute(httpGet);
            //4.判断状态码
            if(response.getStatusLine().getStatusCode()==200){
                common(response,"sh"+i,"上交所");
            }
        }
        System.out.println("深交所");
        for(int i=1;i<=3816;i++){
            int len = (i+"").length();
            String code = "";
            switch (len){
                case 1:
                    code = "00000"+i;
                    break;
                case 2:
                    code = "0000"+i;
                    break;
                case 3:
                    code = "000"+i;
                    break;
                case 4:
                    code = "00"+i;
                    break;

            }
            HttpGet httpGet = new HttpGet("http://hq.sinajs.cn/list="+"sz"+code);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode()==200){
                common(response,"sz"+code,"深交所");
            }
        }
        System.out.println("科创板");
        for(int i=300001;i<=300823;i++){
            HttpGet httpGet = new HttpGet("http://hq.sinajs.cn/list="+"sz"+i);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode()==200){
                common(response,"sz"+i,"深交所");
            }
        }

        //5.关闭资源

        httpClient.close();
        System.out.println("end");
    }

    private  void common(CloseableHttpResponse response,String codes,String exchang) throws IOException, ParseException {
        HttpEntity entity = response.getEntity();
        //使用工具类EntityUtils，从响应中取出实体表示的内容并转换成字符串
        String string = EntityUtils.toString(entity, "utf-8");
        System.out.println(string);
        if(string.split("=")[1].length()>5){
            string = string.substring(string.indexOf("\"")+1,string.length()-2);
            String shareData[] = string.split(",");

            Code code = new Code();
            code.setCode(codes);
            code.setName(shareData[0]);
            code.setExchang(exchang);

            codeMapper.insert(code);
        }

        response.close();
    }

//    private  void common(CloseableHttpResponse response) throws IOException, ParseException {
//        HttpEntity entity = response.getEntity();
//        //使用工具类EntityUtils，从响应中取出实体表示的内容并转换成字符串
//        String string = EntityUtils.toString(entity, "utf-8");
//        System.out.println(string);
//        if(string.split("=")[1].length()>5){
//            string = string.substring(string.indexOf("\"")+1,string.length()-2);
//            String shareData[] = string.split(",");
//            Shares shares = new Shares();
//            shares.setName(shareData[0]);
//            shares.setNowprice(Double.parseDouble(shareData[3]));
//            shares.setNumber(Integer.parseInt(shareData[8])/100);
//            shares.setTotal(Long.parseLong(shareData[9].split("\\.")[0])/10000);
//            String dateFormat = "yyyy-MM-dd HH:mm:ss";
//            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
//            shares.setHappentime(format.parse(shareData[30]+" "+shareData[31]));
//
//        }
//
//        response.close();
//    }
}
