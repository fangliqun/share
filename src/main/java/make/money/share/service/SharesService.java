package make.money.share.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import make.money.share.mapper.SharesMapper;
import make.money.share.pojo.Shares;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class SharesService {

    @Autowired
    private SharesMapper sharesMapper;

    @Async
    public void  insertShare(String codes){
        try {
            //1.打开浏览器
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //2.声明get请求
            HttpGet httpGet = new HttpGet("http://hq.sinajs.cn/list=" + codes);
            //3.发送请求
            CloseableHttpResponse response = httpClient.execute(httpGet);

            //4.判断状态码
            if(response.getStatusLine().getStatusCode()==200) {
                HttpEntity entity = response.getEntity();
                //使用工具类EntityUtils，从响应中取出实体表示的内容并转换成字符串
                String string = EntityUtils.toString(entity, "utf-8");
                String datas[] = string.split("\n");
                for (String data : datas) {
                    if (data.split("=")[1].length() > 5) {
                        Shares shares = new Shares();
                        shares.setCode(data.substring(11,19));
                        data = data.substring(data.indexOf("\"") + 1, data.length() - 2);
                        String shareData[] = data.split(",");
                        shares.setName(shareData[0]);
                        shares.setNowprice(Double.parseDouble(shareData[3]));
                        if (0 == Double.parseDouble(shareData[3])) {
                            continue;
                        }
                        shares.setNumber(Integer.parseInt(shareData[8]) / 100);
                        shares.setTotal(Long.parseLong(shareData[9].split("\\.")[0]) / 10000);
                        String dateFormat = "yyyy-MM-dd HH:mm:ss";
                        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                        shares.setHappentime(format.parse(shareData[30] + " " + shareData[31]));

                        double nowprivce = shares.getNowprice();
                        shares.setOneday(nowprivce);
                        QueryWrapper<Shares> sharesQueryWrapper = new QueryWrapper<>();
                        sharesQueryWrapper.lambda().eq(Shares::getName, shareData[0]);
                        sharesQueryWrapper.orderByDesc("happentime");
                        sharesQueryWrapper.last("limit 4");
                        List<Shares> sharesfive = sharesMapper.selectList(sharesQueryWrapper);
                        if (sharesfive.size() >= 4) {
                            double all = nowprivce;
                            for (Shares old : sharesfive) {
                                all = all + old.getNowprice();
                            }
                            shares.setFiveday(all / 5);
                            shares.setFiveslope((shares.getFiveday() - sharesfive.get(sharesfive.size() - 1).getFiveday()) / 5);
                        }

                        QueryWrapper<Shares> sharesQueryWrapper1 = new QueryWrapper<>();
                        sharesQueryWrapper1.lambda().eq(Shares::getName, shareData[0]);
                        sharesQueryWrapper1.orderByDesc("happentime");
                        sharesQueryWrapper1.last("limit 9");
                        List<Shares> sharesten = sharesMapper.selectList(sharesQueryWrapper1);
                        if (sharesten.size() >= 9) {
                            double all = nowprivce;
                            for (Shares old : sharesten) {
                                all = all + old.getNowprice();
                            }
                            shares.setTenday(all / 10);
                            shares.setTenslope((shares.getTenday() - sharesten.get(sharesten.size() - 1).getTenday()) / 10);
                        }

                        QueryWrapper<Shares> sharesQueryWrapper2 = new QueryWrapper<>();
                        sharesQueryWrapper2.lambda().eq(Shares::getName, shareData[0]);
                        sharesQueryWrapper2.orderByDesc("happentime");
                        sharesQueryWrapper2.last("limit 19");
                        List<Shares> sharestwentyday = sharesMapper.selectList(sharesQueryWrapper2);
                        if (sharestwentyday.size() >= 19) {
                            double all = nowprivce;
                            for (Shares old : sharestwentyday) {
                                all = all + old.getNowprice();
                            }
                            shares.setTwentyday(all / 20);
                            shares.setTwentyslope((shares.getTwentyday() - sharestwentyday.get(sharestwentyday.size() - 1).getTwentyday()) / 20);
                        }

                        QueryWrapper<Shares> sharesQueryWrapper3 = new QueryWrapper<>();
                        sharesQueryWrapper3.lambda().eq(Shares::getName, shareData[0]);
                        sharesQueryWrapper3.orderByDesc("happentime");
                        sharesQueryWrapper3.last("limit 29");
                        List<Shares> sharesthirtyday = sharesMapper.selectList(sharesQueryWrapper3);
                        if (sharesthirtyday.size() >= 29) {
                            double all = nowprivce;
                            for (Shares old : sharesthirtyday) {
                                all = all + old.getNowprice();
                            }
                            shares.setThirtyday(all / 30);
                            shares.setThirtyslope((shares.getThirtyday() - sharesthirtyday.get(sharesthirtyday.size() - 1).getThirtyday()) / 30);
                        }

                        QueryWrapper<Shares> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(Shares::getName, shareData[0]);
                        queryWrapper.orderByDesc("happentime");
                        queryWrapper.last("limit 1");
                        Shares sharesone = sharesMapper.selectOne(queryWrapper);
                        if (sharesone == null || (sharesone.getNowprice() != Double.parseDouble(shareData[3]) &&
                                sharesone.getTotal() != Long.parseLong(shareData[9].split("\\.")[0]) / 10000)) {
                            sharesMapper.insert(shares);
                        }else{
                            System.out.println("数据重复：" + shareData[0]);
                        }
                    }
                }
            }else{
                FileWriter fileWritter = new FileWriter("C:\\Users\\heqiang\\Desktop\\err.txt",true);
                fileWritter.write("获取参数失败"+ codes + "\r\n");
                fileWritter.close();
            }
            response.close();
            //5.关闭资源
            httpClient.close();
        } catch (IOException | ParseException e) {
            try {
                e.printStackTrace();
                FileWriter fileWritter = new FileWriter("C:\\Users\\heqiang\\Desktop\\err.txt",true);
                fileWritter.write("获取mysql失败"+"\r\n");
                fileWritter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }finally {

        }
    }
}
