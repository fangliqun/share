package make.money.share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import make.money.share.pojo.Shares;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SharesMapper extends BaseMapper<Shares> {
    void updateCode(@Param("name")String name,@Param("code")String code);
}
