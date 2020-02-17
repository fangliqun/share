package make.money.share.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("shares") //对应表名
public class Shares implements Serializable {
    //对应id，可不填
    @TableId(value = "id",type= IdType.AUTO)
    private int id;
    //股票名字
    private String name;
    //当前价格
    private double nowprice;
    //成交的股票数 一百股为一手
    private int number;
    //成交金额 万为单位
    private long total;
    //今日日期
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    //发生时间
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date happentime;

}