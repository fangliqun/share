package make.money.share.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("code") //对应表名
public class Code implements Serializable {

    //对应id，可不填
    @TableId(value = "id",type= IdType.AUTO)
    private int id;

    private String code;

    private String name;

    private String exchang;
}
