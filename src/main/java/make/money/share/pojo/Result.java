package make.money.share.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("result") //对应表名
public class Result implements Serializable {
    //对应id，可不填
    @TableId(value = "id",type= IdType.AUTO)
    private int id;

    private String name;

    private String code;
    //分析结果值
    private double number;

    private int type;

    private LocalDate happentime;
}
