package make.money.share.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import make.money.share.mapper.CodeMapper;
import make.money.share.mapper.ResultMapper;
import make.money.share.mapper.SharesMapper;
import make.money.share.pojo.Code;
import make.money.share.pojo.Result;
import make.money.share.pojo.Shares;
import make.money.share.service.AnalyseService;
import make.money.share.service.SharesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class GetSharesController {

    @Autowired
    private CodeMapper codeMapper;

    @Autowired
    private SharesService sharesService;

    @Autowired
    private AnalyseService analyseService;

    @Autowired
    private SharesMapper sharesMapper;

    @Autowired
    private ResultMapper resultMapper;

    List<Code> codeList;
//    3.添加定时任务 13秒
    @Scheduled(cron = "0 20 17 ? * 1-7")
    public void getShare() throws InterruptedException, IOException {
        FileWriter fileWritter = new FileWriter("C:\\Users\\heqiang\\Desktop\\err.txt",true);
        fileWritter.write("=======================getShare start==="+LocalDate.now()+"================" + "\r\n");
        if(!codeList.isEmpty()){
            int count = 0;
            String codes = "";
            for (int i =0; i<codeList.size();i++){
                if(count == 100 || (i ==(codeList.size()-1))){//最后没满足大于100也要执行
                    codes = codes + "," +  codeList.get(i).getCode();
                    sharesService.insertShare(codes);
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
        Thread.sleep(1000*60*30);
        fileWritter.write("=======================getShare end======================" + "\r\n");
        fileWritter.write("\r\n\r\n\r\n");
        fileWritter.close();
    }
    //策略  40秒  10秒出结果 连接阿里云更慢 但是要考虑结束输出时间
    @Scheduled(cron = "0 50 17 ? * 1-7")
    public void analyseShare() throws IOException, InterruptedException {
        for(Code code : codeList){
            analyseService.analyseShares(code);
        }
        Thread.sleep(1000*60*20);
        System.out.println("analyse  result  to ........");
        analyseService.toTesult();
    }

    @Scheduled(cron = "0 00 17 ? * 1-7")//最新代码
    public void getCodeNew(){
        System.out.println("get all code");
        QueryWrapper<Code> codeQueryWrapper = new QueryWrapper<>();
        codeList = codeMapper.selectList(codeQueryWrapper);
    }

//    @PostConstruct
    // 更新代码和去空格
    public void getCode(){
        QueryWrapper<Code> codeQueryWrapper = new QueryWrapper<>();
        codeList = codeMapper.selectList(codeQueryWrapper);
        for(Code code : codeList){
            updateCode(code);
        }
    }

    @Async
    public void  updateCode(Code code){
        sharesMapper.updateCode(code.getName(),code.getCode());
    }

}
