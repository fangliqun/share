package make.money.share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import make.money.share.pojo.Shares;
import make.money.share.pojo.SharesData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SharesDataMapper extends BaseMapper<SharesData> {
    void updateCode(@Param("name") String name, @Param("code") String code);
}
