package make.money.share;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "make.money.share.mapper")
@SpringBootApplication
public class ShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShareApplication.class, args);
    }

}
