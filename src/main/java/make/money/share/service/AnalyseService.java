package make.money.share.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import make.money.share.mapper.ResultMapper;
import make.money.share.mapper.SharesMapper;
import make.money.share.mapper.UserMapper;
import make.money.share.pojo.Code;
import make.money.share.pojo.Result;
import make.money.share.pojo.Shares;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
//成交额在5000w上
@Service
public class AnalyseService {

    @Autowired
    private SharesMapper sharesMapper;

    @Autowired
    private ResultMapper resultMapper;

    @Autowired
    private UserService userService;

    @Value("${result.windows}")
    private int windwos;

    @Value("${result.linux}")
    private int linux;

    private Map<Double,String> mapSX = new HashMap();
    private List<Double> listSX = new ArrayList();

    private Map<Double,String> mapDis = new HashMap();
    private List<Double> listDis = new ArrayList();

    private Map<Double,String> mapAr = new HashMap();
    private List<Double> listAr = new ArrayList();

    private Map<Double,String> mapRes = new HashMap();
    private List<Double> listRes = new ArrayList();

    @Async
    public void analyseShares(Code code){
        try {
            QueryWrapper<Shares> queryWrapper1  = new QueryWrapper<>();
            queryWrapper1.lambda().eq(Shares::getName,code.getName());
            queryWrapper1.orderByDesc("happentime");
            queryWrapper1.last("limit 1");
            Shares sharesone= sharesMapper.selectOne(queryWrapper1);
            if(sharesone != null){
                double nowprice = sharesone.getNowprice();
                double fiveday = sharesone.getFiveday();
                double tenday = sharesone.getTenday();
                double twentyday = sharesone.getTwentyday();
                double thirtyday = sharesone.getThirtyday();
                List<Double> listMM = new ArrayList<>();
                listMM.add(nowprice);
                if(fiveday != 0) listMM.add(fiveday);
                if(tenday != 0) listMM.add(tenday);
                if(twentyday != 0) listMM.add(twentyday);
                if(thirtyday != 0) listMM.add(thirtyday);

                double max = Collections.max(listMM);
                double min = Collections.min(listMM);
                double avg = (max - min)/nowprice;
                if(listMM.size() > 2 && avg > 0 && avg < 0.02 && sharesone.getTotal()>10000 && nowprice>10){
                    listSX.add(avg);
                    mapSX.put(avg,avg +" "+ code.getName());
                }
                double distance = (nowprice - twentyday)/nowprice;
                if(twentyday !=0 && (distance> 0.2 || distance < -0.2) && sharesone.getTotal()>10000){//0.02
                    listDis.add(distance);
                    mapDis.put(distance,distance +" "+ code.getName());
                }
            }

            QueryWrapper<Shares> queryWrapper  = new QueryWrapper<>();
            queryWrapper.lambda().eq(Shares::getName,code.getName());
            queryWrapper.orderByDesc("happentime");
            queryWrapper.last("limit 5");
            List<Shares> listShares = sharesMapper.selectList(queryWrapper);
            if(listShares.size() == 5){
                double addreduce = (listShares.get(0).getNowprice() - listShares.get(4).getNowprice()) / listShares.get(4).getNowprice();
                if( (addreduce > 0.2 || addreduce < -0.2) && listShares.get(0).getTotal()>10000){
                    listAr.add(addreduce);
                    mapAr.put(addreduce,addreduce +" "+ code.getName());
                }
                double avg = (listShares.get(0).getTotal() + listShares.get(1).getTotal()
                        + listShares.get(2).getTotal() + listShares.get(3).getTotal() + listShares.get(4).getTotal())/5;
                double result = (listShares.get(0).getTotal()- avg)/avg;
                if((result > 1.5 || result < -0.8) && listShares.get(0).getTotal()>10000){
                    listRes.add(result);
                    mapRes.put(result,result +" "+ code.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTesult() throws IOException {
        String toUser = "";
        Collections.sort(listSX);
        Collections.reverse(listSX);
        toUser = toUser + "均线接近，上升或下升空间打开：";
        for(double sx : listSX){
            String r = mapSX.get(sx);
            toUser = toUser + r;
            Result result = new Result();
            result.setNumber(Double.parseDouble(r.split(" ")[0]));
            result.setName(r.split(" ")[1]);
            result.setType(1);
            result.setHappentime(LocalDate.now());
            resultMapper.insert(result);
        }
        Collections.sort(listDis);
        Collections.reverse(listDis);
        toUser = toUser + "20均线和k线差距大：";
        for(double sx : listDis){
            String r = mapDis.get(sx);
            toUser = toUser + r;
            Result result = new Result();
            result.setNumber(Double.parseDouble(r.split(" ")[0]));
            result.setName(r.split(" ")[1]);
            result.setType(2);
            result.setHappentime(LocalDate.now());
            resultMapper.insert(result);
        }
        Collections.sort(listAr);
        Collections.reverse(listAr);
        toUser = toUser + "一周内涨幅或者跌幅较大：";
        for(double sx : listAr){
            String r = mapAr.get(sx);
            toUser = toUser + r;
            Result result = new Result();
            result.setNumber(Double.parseDouble(r.split(" ")[0]));
            result.setName(r.split(" ")[1]);
            result.setType(3);
            result.setHappentime(LocalDate.now());
            resultMapper.insert(result);
        }
        Collections.sort(listRes);
        Collections.reverse(listRes);
        toUser = toUser + "放量较多或者缩量较多：";
        for(double sx : listRes){
            String r = mapRes.get(sx);
            toUser = toUser + r;
            Result result = new Result();
            result.setNumber(Double.parseDouble(r.split(" ")[0]));
            result.setName(r.split(" ")[1]);
            result.setType(4);
            result.setHappentime(LocalDate.now());
            resultMapper.insert(result);
        }

        toUser = toUser + "最近两天出现次数：";
        QueryWrapper<Result> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ge(Result::getHappentime,LocalDate.now());
        queryWrapper.select("distinct(name) as name");
        List<Result> list = resultMapper.selectList(queryWrapper);
        for(Result result : list){
            QueryWrapper<Result> queryWrapperY = new QueryWrapper<>();
            queryWrapperY.lambda().ge(Result::getHappentime,LocalDate.now().plusDays(-1));
            queryWrapperY.lambda().eq(Result::getName,result.getName());
            List<Result> listY = resultMapper.selectList(queryWrapperY);
            if(listY != null && !listY.isEmpty()){
                toUser = toUser + result.getName()+"  "+listY.size()+" ";
            }
        }

        if(windwos == 1){
            resultWindwos();
        }

        System.out.println("结果："+toUser);
        userService.send(toUser);

        mapSX.clear();listSX.clear();
        mapDis.clear();listDis.clear();
        mapAr.clear();listAr.clear();
        mapRes.clear();listRes.clear();

        System.out.println("analyse  result  end ..........");
    }

    public void resultWindwos() throws IOException {
        FileWriter fileWritter = new FileWriter("C:\\Users\\heqiang\\Desktop\\shares.txt",true);
        fileWritter.write("=======================result start==="+ LocalDate.now()+"================" + "\r\n");

        fileWritter.write("=======================均线接近，上升或下升空间打开 start======================" + "\r\n");
        for(double sx : listSX){
            String r = mapSX.get(sx);
            fileWritter.write("均线接近，上升或下升空间打开："+ r + "\r\n");
        }
        fileWritter.write("=======================均线接近，上升或下升空间打开 end======================" + "\r\n");

        fileWritter.write("=======================20均线和k线差距大 start======================" + "\r\n");
        for(double sx : listDis){
            String r = mapDis.get(sx);
            fileWritter.write("20均线和k线差距大："+ r + "\r\n");
        }
        fileWritter.write("=======================20均线和k线差距大 end======================" + "\r\n");

        fileWritter.write("=======================一周内涨幅或者跌幅较大 start======================" + "\r\n");
        for(double sx : listAr){
            String r = mapAr.get(sx);
            fileWritter.write("一周内涨幅或者跌幅较大："+ r + "\r\n");
        }
        fileWritter.write("=======================一周内涨幅或者跌幅较大 end======================" + "\r\n");

        fileWritter.write("=======================放量较多或者缩量较多 start======================" + "\r\n");
        for(double sx : listRes){
            String r = mapRes.get(sx);
            fileWritter.write("放量较多或者缩量较多："+ r + "\r\n");
        }
        fileWritter.write("=======================放量较多或者缩量较多 end======================" + "\r\n");

        fileWritter.write("=======================result end======================" + "\r\n");
        fileWritter.write("\r\n\r\n\r\n");
        fileWritter.close();
    }
}
