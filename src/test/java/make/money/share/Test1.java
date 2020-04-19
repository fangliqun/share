package make.money.share;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

        String str="hello world!";
        FileWriter writer = null;
        try {
            writer = new FileWriter("C:\\Users\\heqiang\\Desktop\\shares.txt",true);
            writer.write(str);
//            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
