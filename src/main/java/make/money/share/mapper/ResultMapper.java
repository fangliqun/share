package make.money.share.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import make.money.share.pojo.Result;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ResultMapper extends BaseMapper<Result> {
    public List<Map<String,Object>> getResult(@Param("date") LocalDate date);
}
