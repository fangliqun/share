package make.money.share.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

public class StokeData {

    private static String time ="2018-09-2809:00:00";
    private static int num = 1;
    private static boolean flag = false;

    private static BufferedWriter bw = null;
    public static void main(String[] args) throws InterruptedException, IOException {

        String url = "http://data.eastmoney.com/zjlx/detail.html";
        get(url);
        for(num = 1;num <= 74 && flag; num++) {
            get(url);
        }
        if(bw != null) {
            bw.close();
        }
        flag = false;
    }



    private static void stream(Stream<String> lines) throws IOException, ParseException {
        String line = lines.filter(l ->  l.contains("dataurl:") )
                .map(l -> l.substring(l.lastIndexOf("token"), l.lastIndexOf("{")))
                //ChangePercent 涨跌排序
                //BalFlowMain 今日主力净流入-> 净额 排序

//			   <li data="C._AB">全部股票</li>
//             <li data="C._A">沪深A股</li>
//             <li data="C.2">沪市A股</li>
//             <li data="C._SZAME">深市A股</li>
//             <li data="C.80">创业板</li>
//             <li data="C.13">中小板</li>
//             <li data="C.3">沪市B股</li>
//             <li data="C.7">深市B股</li>


//			<li data="DCFFITA">今日排行<i></i></li>
//            <li data="DCFFITA3">3日排行<i></i></li>
//            <li data="DCFFITA5">5日排行<i></i></li>
//            <li data="DCFFITA10">10日排行<i></i></li>
                .map(l -> "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=ct&st=(ChangePercent)&sr=-1&p=1&ps=50&js=var%20CHKjvtxf={pages:(pc),date:%222014-10-22%22,data:[(x)]}&"+l+"&cmd=C._AB&sty=DCFFITA")
                .map(l -> getData(l))
                .map(l -> l.substring(l.indexOf("[")+1,l.lastIndexOf("]")))
                .map(l -> l.replaceAll(" ", "").replaceAll("\t", ""))
                .flatMap(l -> Stream.of(l.split("\",\"")))
                . findFirst()
                .get().split(",")[15];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        Date date = sdf.parse(line);
        sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        time = sdf.format(date);
        File f = new File("F:\\stoke_data\\"+time+".txt");
        flag = false;
        if(!f.exists()) {
            bw = new BufferedWriter(new FileWriter(f));
            flag = true;
        }
    }



    private static void write(Stream<String> lines) throws IOException {
        lines.filter(l ->  l.contains("dataurl:") )
                .map(l -> l.substring(l.lastIndexOf("token"), l.lastIndexOf("{")))
                .map(l -> "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=ct&st=(ChangePercent)&sr=-1&p="+num+"&ps=50&js=var%20CHKjvtxf={pages:(pc),date:%222014-10-22%22,data:[(x)]}&"+l+"&cmd=C._AB&sty=DCFFITA")
                .map(l -> getData(l))
                .map(l -> l.substring(l.indexOf("[")+1,l.lastIndexOf("]")))
                .map(l -> l.replaceAll(" ", "").replaceAll("\t", ""))
                .flatMap(l -> Stream.of(l.split("\",\"")))
                .map(l -> l.replaceAll(" ", "").replaceAll("\"", ""))
                .forEach(l ->{

                            try {
                                bw.write(l);
                                bw.newLine();
                            } catch (IOException e) {
                                System.out.println(l);
                            }
                        }
                );
        bw.flush();
    }




    /**
     * 接口调用 GET
     */
    public static void get(String urls) {
        try {
            URL url = new URL(urls); // 把字符串转换为URL请求地址
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
            if (System.currentTimeMillis()%2==0) {
                connection.setRequestProperty("Content-Type", "application/x-javascript");
                connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 UBrowser/6.2.4094.1 Safari/537.36");
            }
            connection.connect();// 连接会话
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Stream<String> lines = br.lines();

            if(!flag) {
                stream(lines);
            }else {
                write(lines);
            }

            br.close();// 关闭流
            connection.disconnect();// 断开连接
//	            System.out.println(sb.toString());
        } catch (Exception e) {
            System.out.println("请求失败!");
            e.printStackTrace();
        }
    }


    /**
     * 接口调用 GET
     */
    public static String getData(String urls) {
        try {
            URL url = new URL(urls); // 把字符串转换为URL请求地址
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
            if (System.currentTimeMillis()%2==0) {
                connection.setRequestProperty("Content-Type", "application/x-javascript");
                connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 UBrowser/6.2.4094.1 Safari/537.36");
            }
            connection.connect();// 连接会话
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = br.readLine();
            br.close();// 关闭流
            connection.disconnect();// 断开连接
//	            System.out.println(sb.toString());
            return line;
        } catch (Exception e) {
            System.out.println("请求失败!");
        }
        return null;
    }
}
