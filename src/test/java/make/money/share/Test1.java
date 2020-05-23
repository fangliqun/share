package make.money.share;

import make.money.share.pojo.Result;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Test1 {
    public static void main(String[] args) throws IOException {

        List<Result> sx = new ArrayList<>();

        Result result = new Result();
        result.setNumber(0.123156165162);
        result.setName("1");
        result.setCode("1");
        result.setType(1);
        result.setHappentime(LocalDate.now());

        Result result1 = new Result();
        result1.setNumber(0.139849826549);
        result1.setName("2");
        result1.setCode("2");
        result1.setType(1);
        result1.setHappentime(LocalDate.now());

        Result result2 = new Result();
        result2.setNumber(0.119849826549);
        result2.setName("3");
        result2.setCode("3");
        result2.setType(1);
        result2.setHappentime(LocalDate.now());

        Result result3 = new Result();
        result3.setNumber(0.109849826549);
        result3.setName("4");
        result3.setCode("4");
        result3.setType(1);
        result3.setHappentime(LocalDate.now());

        Result result4 = new Result();
        result4.setNumber(0.239849826549);
        result4.setName("5");
        result4.setCode("5");
        result4.setType(1);
        result4.setHappentime(LocalDate.now());

        sx.add(result);
        sx.add(result1);
        sx.add(result2);
        sx.add(result3);
        sx.add(result4);

        System.out.println(sx.toString());
        Collections.sort(sx, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                double n1 =  o1.getNumber();
                double n2 =  o2.getNumber();
                if (n1 > n2) {
                    return -1;
                } else if (n1 == n2) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        System.out.println(sx.toString());
    }

}
