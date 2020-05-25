package make.money.share;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class cpkPortJavaTest {
	public static void main(String[] args) {
		// 此处需要传递的3个参数，必填项
		String name = "jczq";// 彩种
		String uid = "1570922";// 用户ID
		String token = "901a6ce51bf1d23c926e694493cef93887342dc7";// token
		String format = "";// 输出格式 	json 或 json3 或 xml 或 xml2 或 xml5
		String phase = "20200518";// 期号搜索 20200518

		String url = "http://api.caipiaokong.com/lottery/";
		url += "?name=" + name;
		url += "&format=json";// 数据格式，此文件仅支持json
		url += "&uid=" + uid;
		url += "&token=" + token;
		url += "&phase=" + phase;

		String urlAll = new StringBuffer(url).toString();
		String charset = "UTF-8";
		String jsonResult = get(urlAll, charset);// 得到JSON字符串
		JSONObject object = JSONObject.fromObject(jsonResult);// 转化为JSON类
		try {
			Iterator it = object.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = object.getString(key);
				JSONObject object1 = JSONObject.fromObject(value);// 转化为JSON类
				String outputStr = "id:" + key;
				outputStr += " number:" + object1.getString("number");
				outputStr += " dateline:" + object1.getString("dateline");
				System.out.println(outputStr);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 *
	 * @param urlAll:请求接口
	 * @param charset:字符编码
	 * @return 返回json结果
	 */
	public static String get(String urlAll, String charset) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";// 模拟浏览器
		try {
			URL url = new URL(urlAll);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(30000);
			connection.setConnectTimeout(30000);
			connection.setRequestProperty("User-agent", userAgent);
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, charset));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
