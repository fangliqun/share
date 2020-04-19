package make.money.share.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import make.money.share.mapper.SharesMapper;
import make.money.share.pojo.Code;
import make.money.share.pojo.Shares;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class AnalyseService {

    @Autowired
    private SharesMapper sharesMapper;

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
                if(listMM.size() > 2 && avg > 0 && avg < 0.002){
                    listSX.add(avg);
                    mapSX.put(avg,avg + code.getName());
                }
                double distance = (nowprice - twentyday)/nowprice;
                if(twentyday !=0 && (distance> 0.2 || distance < -0.2)){//0.02
                    listDis.add(distance);
                    mapDis.put(distance,distance + code.getName());
                }
            }

            QueryWrapper<Shares> queryWrapper  = new QueryWrapper<>();
            queryWrapper.lambda().eq(Shares::getName,code.getName());
            queryWrapper.orderByDesc("happentime");
            queryWrapper.last("limit 5");
            List<Shares> listShares = sharesMapper.selectList(queryWrapper);
            if(listShares.size() == 5){
                double addreduce = (listShares.get(0).getNowprice() - listShares.get(4).getNowprice()) / listShares.get(4).getNowprice();
                if( addreduce > 0.2 || addreduce < -0.2){
                    listAr.add(addreduce);
                    mapAr.put(addreduce,addreduce + code.getName());
                }
                double avg = (listShares.get(0).getTotal() + listShares.get(1).getTotal()
                        + listShares.get(2).getTotal() + listShares.get(3).getTotal() + listShares.get(4).getTotal())/5;
                double result = (listShares.get(0).getTotal()- avg)/avg;
                if(result > 1.5 || result < -0.8){
                    listRes.add(result);
                    mapRes.put(result,result + code.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTesult() throws IOException {
        FileWriter fileWritter = new FileWriter("C:\\Users\\heqiang\\Desktop\\shares.txt",true);
        fileWritter.write("=======================result start==="+ LocalDate.now()+"================" + "\r\n");

        fileWritter.write("=======================均线接近，上升或下升空间打开 start======================" + "\r\n");
        Collections.sort(listSX);
        Collections.reverse(listSX);
        for(double sx : listSX){
            fileWritter.write("均线接近，上升或下升空间打开："+ mapSX.get(sx) + "\r\n");
        }
        fileWritter.write("=======================均线接近，上升或下升空间打开 end======================" + "\r\n");

        fileWritter.write("=======================20均线和k线差距大 start======================" + "\r\n");
        Collections.sort(listDis);
        Collections.reverse(listDis);
        for(double sx : listDis){
            fileWritter.write("20均线和k线差距大："+ mapDis.get(sx) + "\r\n");
        }
        fileWritter.write("=======================20均线和k线差距大 end======================" + "\r\n");

        fileWritter.write("=======================一周内涨幅或者跌幅较大 start======================" + "\r\n");
        Collections.sort(listAr);
        Collections.reverse(listAr);
        for(double sx : listAr){
            fileWritter.write("一周内涨幅或者跌幅较大："+ mapAr.get(sx) + "\r\n");
        }
        fileWritter.write("=======================一周内涨幅或者跌幅较大 end======================" + "\r\n");

        fileWritter.write("=======================放量较多或者缩量较多 start======================" + "\r\n");
        Collections.sort(listRes);
        Collections.reverse(listRes);
        for(double sx : listRes){
            fileWritter.write("放量较多或者缩量较多："+ mapRes.get(sx) + "\r\n");
        }
        fileWritter.write("=======================放量较多或者缩量较多 end======================" + "\r\n");

        fileWritter.write("=======================result end======================" + "\r\n");
        fileWritter.write("\r\n\r\n\r\n");
        fileWritter.close();

        mapSX.clear();listSX.clear();
        mapDis.clear();listDis.clear();
        mapAr.clear();listAr.clear();
        mapRes.clear();listRes.clear();
    }
}
