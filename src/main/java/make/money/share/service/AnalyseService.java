package make.money.share.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import make.money.share.mapper.ResultMapper;
import make.money.share.mapper.SharesMapper;
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

//成交额在1亿上 不包含证券等

//1 均线接近，上升或下升空间打开
//2 20均线和k线差距大
//3 一周内涨幅或者跌幅较大
//4 放量较多或者缩量较多
//  最近两天出现次数
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

    private static List<String> str = new ArrayList();

    private List<Result> listSX = new ArrayList<>();
    private List<Result> listDis = new ArrayList<>();
    private List<Result> listAr = new ArrayList<>();
    private List<Result> listRes = new ArrayList<>();

    static{
        str.add("银行");
        str.add("证券");
        str.add("钢铁");
    }

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
                    boolean flag = false;
                    for(int i=0; i < str.size(); i++){
                        if(code.getName().contains(str.get(i))){
                            flag = true;
                        }
                    }
                    if(flag == false){
                        Result result = new Result();
                        result.setNumber(avg);
                        result.setName(code.getName());
                        result.setCode(code.getCode());
                        result.setType(1);
                        result.setHappentime(LocalDate.now());
                        listSX.add(result);
                    }
                }
                double distance = (nowprice - twentyday)/nowprice;
                if(twentyday !=0 && (distance> 0.2 || distance < -0.2) && sharesone.getTotal()>10000){//0.02
                    boolean flag = false;
                    for(int i=0; i < str.size(); i++){
                        if(code.getName().contains(str.get(i))){
                            flag = true;
                        }
                    }
                    if(flag == false){
                        Result result = new Result();
                        result.setNumber(distance);
                        result.setName(code.getName());
                        result.setCode(code.getCode());
                        result.setType(2);
                        result.setHappentime(LocalDate.now());
                        listDis.add(result);
                    }
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
                    boolean flag = false;
                    for(int i=0; i < str.size(); i++){
                        if(code.getName().contains(str.get(i))){
                            flag = true;
                        }
                    }
                    if(flag == false){
                        Result result = new Result();
                        result.setNumber(addreduce);
                        result.setName(code.getName());
                        result.setCode(code.getCode());
                        result.setType(3);
                        result.setHappentime(LocalDate.now());
                        listAr.add(result);
                    }
                }
                double avg = (listShares.get(0).getTotal() + listShares.get(1).getTotal()
                        + listShares.get(2).getTotal() + listShares.get(3).getTotal() + listShares.get(4).getTotal())/5;
                double result = (listShares.get(0).getTotal()- avg)/avg;
                if((result > 1.5 || result < -0.8) && listShares.get(0).getTotal()>10000){
                    boolean flag = false;
                    for(int i=0; i < str.size(); i++){
                        if(code.getName().contains(str.get(i))){
                            flag = true;
                        }
                    }
                    if(flag == false){
                        Result resultR = new Result();
                        resultR.setNumber(result);
                        resultR.setName(code.getName());
                        resultR.setCode(code.getCode());
                        resultR.setType(4);
                        resultR.setHappentime(LocalDate.now());
                        listRes.add(resultR);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTesult() throws IOException {
        String toUser = "";
        toUser = toUser + "均线接近，上升或下升空间打开：";
        Collections.sort(listSX, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                double n1 =  o1.getNumber();
                double n2 =  o2.getNumber();
                if (n1 > n2) { return -1;
                } else if (n1 == n2) { return 0;
                } else { return 1;
                }
            }
        });
        for(Result result : listSX){
            toUser = toUser + result.getNumber() +" "+ result.getCode() +" " +result.getName() + "\r\n";
            resultMapper.insert(result);
        }

        toUser = toUser + "20均线和k线差距大：";
        Collections.sort(listDis, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                double n1 =  o1.getNumber();
                double n2 =  o2.getNumber();
                if (n1 > n2) { return -1;
                } else if (n1 == n2) { return 0;
                } else { return 1;
                }
            }
        });
        for(Result result : listDis){
            toUser = toUser + result.getNumber() +" "+ result.getCode() +" " +result.getName() + "\r\n";
            resultMapper.insert(result);
        }

        toUser = toUser + "一周内涨幅或者跌幅较大：";
        Collections.sort(listAr, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                double n1 =  o1.getNumber();
                double n2 =  o2.getNumber();
                if (n1 > n2) { return -1;
                } else if (n1 == n2) { return 0;
                } else { return 1;
                }
            }
        });
        for(Result result : listAr){
            toUser = toUser + result.getNumber() +" "+ result.getCode() +" " +result.getName() + "\r\n";
            resultMapper.insert(result);
        }

        toUser = toUser + "放量较多或者缩量较多：";
        Collections.sort(listRes, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                double n1 =  o1.getNumber();
                double n2 =  o2.getNumber();
                if (n1 > n2) { return -1;
                } else if (n1 == n2) { return 0;
                } else { return 1;
                }
            }
        });
        for(Result result : listRes){
            toUser = toUser + result.getNumber() +" "+ result.getCode() +" " +result.getName() + "\r\n";
            resultMapper.insert(result);
        }

        toUser = toUser + "最近两天出现次数：";
        List<Map<String,Object>> list = resultMapper.getResult(LocalDate.now().plusDays(-1));
        for(Map<String,Object> map : list){
            toUser = toUser + map.get("name")+map.get("code")+" "+map.get("counts")+"   ";
        }

        if(windwos == 1){
            resultWindwos();
        }

        System.out.println("结果："+toUser);
        userService.send(toUser);

        listSX.clear();listDis.clear();listAr.clear();listRes.clear();

        System.out.println("analyse  result  end ..........");
    }

    public void resultWindwos() throws IOException {
        FileWriter fileWritter = new FileWriter("C:\\Users\\heqiang\\Desktop\\shares.txt",true);
        fileWritter.write("=======================result start==="+ LocalDate.now()+"================" + "\r\n");

        fileWritter.write("=======================均线接近，上升或下升空间打开 start======================" + "\r\n");
        for(Result result : listSX){
            fileWritter.write("均线接近，上升或下升空间打开："+ result.getNumber() +" "+ result.getCode() +" " +result.getName() + "\r\n");
        }
        fileWritter.write("=======================均线接近，上升或下升空间打开 end======================" + "\r\n");

        fileWritter.write("=======================20均线和k线差距大 start======================" + "\r\n");
        for(Result result : listDis){
            fileWritter.write("20均线和k线差距大："+ result.getNumber() +" "+ result.getCode() +" " +result.getName() + "\r\n");
        }
        fileWritter.write("=======================20均线和k线差距大 end======================" + "\r\n");

        fileWritter.write("=======================一周内涨幅或者跌幅较大 start======================" + "\r\n");
        for(Result result : listAr){
            fileWritter.write("一周内涨幅或者跌幅较大："+ result.getNumber() +" "+ result.getCode() +" " +result.getName() + "\r\n");
        }
        fileWritter.write("=======================一周内涨幅或者跌幅较大 end======================" + "\r\n");

        fileWritter.write("=======================放量较多或者缩量较多 start======================" + "\r\n");
        for(Result result : listRes){
            fileWritter.write("放量较多或者缩量较多："+ result.getNumber() +" "+ result.getCode() +" " +result.getName() + "\r\n");
        }
        fileWritter.write("=======================放量较多或者缩量较多 end======================" + "\r\n");

        fileWritter.write("=======================result end======================" + "\r\n");
        fileWritter.write("\r\n\r\n\r\n");
        fileWritter.close();
    }
}
