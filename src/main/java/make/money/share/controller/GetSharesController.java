package make.money.share.controller;

import com.baomidou.mybatisplus.core.conditions.query.Query;
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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class GetSharesController {

    @Autowired
    private SharesMapper sharesMapper;//    private String sharesCodes ="sz002466,sh600678,sh603103";

    @Autowired
    private CodeMapper codeMapper;

    List<Code> codeList;

//    3.添加定时任务
//    @Scheduled(cron = "0/20 * * * * ?")
    @Scheduled(cron = "0 0 20 ? * MON-FRI")
    public void getShare() throws IOException, ParseException {
        System.out.println("start");
        //1.打开浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();

        if(!codeList.isEmpty()){
            int count = 0;
            String codes = "";
            for (int i =0; i<codeList.size();i++){
                if(count > 100){
                    //2.声明get请求
                    HttpGet httpGet = new HttpGet("http://hq.sinajs.cn/list=" + codes);
                    //3.发送请求
                    CloseableHttpResponse response = httpClient.execute(httpGet);
                    //4.判断状态码
                    if(response.getStatusLine().getStatusCode()==200){
                        HttpEntity entity = response.getEntity();
                        //使用工具类EntityUtils，从响应中取出实体表示的内容并转换成字符串
                        String string = EntityUtils.toString(entity, "utf-8");
                        String datas[] = string.split("\n");
                        for(String data : datas){
                            if(data.split("=")[1].length()>5){
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

                                double nowprivce = shares.getNowprice();
                                shares.setOneday(nowprivce);
                                QueryWrapper<Shares> sharesQueryWrapper = new QueryWrapper<>();
                                sharesQueryWrapper.lambda().eq(Shares::getName, shareData[0]);
                                sharesQueryWrapper.orderByDesc("happentime");
                                sharesQueryWrapper.last("limit 4");
                                List<Shares> sharesfive = sharesMapper.selectList(sharesQueryWrapper);
                                if(sharesfive.size() >= 4){
                                    double all = nowprivce;
                                    for(Shares old: sharesfive){
                                        all = all + old.getNowprice();
                                    }
                                    shares.setFiveday(all/5);
                                    shares.setFiveslope((shares.getFiveday() - sharesfive.get(sharesfive.size()-1).getFiveday())/5);
                                }

                                QueryWrapper<Shares> sharesQueryWrapper1 = new QueryWrapper<>();
                                sharesQueryWrapper1.lambda().eq(Shares::getName, shareData[0]);
                                sharesQueryWrapper1.orderByDesc("happentime");
                                sharesQueryWrapper1.last("limit 9");
                                List<Shares> sharesten = sharesMapper.selectList(sharesQueryWrapper1);
                                if(sharesten.size() >= 9){
                                    double all = nowprivce;
                                    for(Shares old: sharesten){
                                        all = all + old.getNowprice();
                                    }
                                    shares.setTenday(all/10);
                                    shares.setTenslope((shares.getTenday() - sharesten.get(sharesten.size()-1).getTenday())/10);
                                }

                                QueryWrapper<Shares> sharesQueryWrapper2 = new QueryWrapper<>();
                                sharesQueryWrapper2.lambda().eq(Shares::getName, shareData[0]);
                                sharesQueryWrapper2.orderByDesc("happentime");
                                sharesQueryWrapper2.last("limit 9");
                                List<Shares> sharestwentyday = sharesMapper.selectList(sharesQueryWrapper2);
                                if(sharestwentyday.size() >= 19){
                                    double all = nowprivce;
                                    for(Shares old: sharestwentyday){
                                        all = all + old.getNowprice();
                                    }
                                    shares.setTwentyday(all/20);
                                    shares.setTwentyslope((shares.getTwentyday() - sharestwentyday.get(sharestwentyday.size()-1).getTwentyday())/20);
                                }

                                sharesMapper.insert(shares);
                            }
                        }
                    }
                    response.close();

                    count = 0;
                    codes = "";
                }else{
                    if("".equals(codes)){
                        codes = codes + codeList.get(i).getCode();
                    }else{
                        codes = codes + "," +  codeList.get(i).getCode();
                    }
                }
                count++;
            }
        }
        //5.关闭资源
        httpClient.close();
        System.out.println("end");
    }
    //策略
    @Scheduled(cron = "0 0 21 ? * MON-FRI")
    public void analyseShares() {
        for(Code code : codeList){
            QueryWrapper<Shares> queryWrapper1  = new QueryWrapper<>();
            queryWrapper1.lambda().eq(Shares::getName,code.getName());
            queryWrapper1.orderByDesc("happentime");
            queryWrapper1.last("limit 1");
            List<Shares> listSharesOne = sharesMapper.selectList(queryWrapper1);
            if(listSharesOne.size() == 1){
                double nowprice = listSharesOne.get(0).getNowprice();
                double fiveday = listSharesOne.get(0).getFiveday();
                double tenday = listSharesOne.get(0).getTenday();
                double twentyday = listSharesOne.get(0).getTwentyday();
                List<Double> listMM = new ArrayList<>();
                listMM.add(nowprice);
                if(fiveday != 0) listMM.add(fiveday);
                if(tenday != 0) listMM.add(tenday);
                if(twentyday != 0) listMM.add(twentyday);

                double max = Collections.max(listMM);
                double min = Collections.min(listMM);
                if(listMM.size() > 1 && (max - min) < 2){
                    System.out.println("均线接近，上升或下升空间打开：" + code.getName());
                }

                if(twentyday !=0 && (nowprice - twentyday) > 5 ){
                    System.out.println("20均线和k线差距大：" + code.getName());
                }
            }


            QueryWrapper<Shares> queryWrapper  = new QueryWrapper<>();
            queryWrapper.lambda().eq(Shares::getName,code.getName());
            queryWrapper.orderByDesc("happentime");
            queryWrapper.last("limit 5");
            List<Shares> listShares = sharesMapper.selectList(queryWrapper);
            if(listShares.size() == 5){
                if((listShares.get(0).getNowprice() - listShares.get(4).getNowprice()) / listShares.get(4).getNowprice() > 0.05){
                    System.out.println("一周内涨幅较大：" + code.getName());
                }
            }

        }
    }

    @PostConstruct
    public void getCode(){
        System.out.println("get all code");

        QueryWrapper<Code> codeQueryWrapper = new QueryWrapper<>();
        codeList = codeMapper.selectList(codeQueryWrapper);
    }
}
