package make.money.share;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Test1 {
    public static void main(String[] args) throws IOException {
//        File file =new File("C:\\Users\\heqiang\\Desktop\\shares.txt");//写入不到？？？
//        if(!file.exists()){
//            file.createNewFile();
//        }
//        FileWriter fileWritter = new FileWriter(file.getName(),true);
//        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
//        bufferWritter.write("----sssssssss----------------------------------------");
//        bufferWritter.flush();
//        bufferWritter.close();

//        String str="hello world!";
//        FileWriter writer = null;
//        try {
//            writer = new FileWriter("C:\\Users\\heqiang\\Desktop\\shares.txt",true);
//            writer.write(str);
////            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        String str = "var hq_str_sh601002=晋亿实业,6.070,6.100,5.8";
//        System.out.println(str.substring(11,19));

        List<String> str = new ArrayList();
        str.add("银行");
        str.add("证券");
        boolean flag = false;
        for(int i=0; i < str.size(); i++){
            if("红塔证券".contains(str.get(i))){
                flag = true;
            }
        }
        if(flag == false){
            System.out.println("111111111111");
        }
        System.out.println("红塔证券".contains("证券"));
    }

}
