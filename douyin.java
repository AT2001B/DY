package pachpng;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pachpng.Entity.user;

public class douyin {
	/**
	 * 我电脑的useragent Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36
	 * (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 Edg/92.0.902.67
	 * 
	 * 手机useragent Mozilla/5.0 (Linux; Android 10; HarmonyOS; ANA-AN00; HMSCore
	 * 6.0.0.306) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.93
	 * HuaweiBrowser/11.1.3.300 Mobile Safari/537.36
	 * 
	 * 访问https://mcs.snssdk.com//v1//user//ssid 加header等可以得到返回json ssid参数
	 * 
	 * 短视频列表url https://www.iesdouyin.com/web/api/v2/aweme/post/
	 * ?sec_uid=MS4wLjABAAAAEAmNkrlC8tMh0VkR-G9Jctio68GtlZtkcRNMv7_Epus
	 *  &count=21
	 * &max_cursor=0 
	 * &aid=1128 
	 * &_signature=l6IC-gAA9rp4X05HE9YfT5eiAu 
	 * &dytk=
	 * 
	 */
	static int num=0;
	static String PCUSERAGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 Edg/92.0.902.67";
	static String MOBLIEUSERAGENT = "Mozilla/5.0 (Linux; Android 10; HarmonyOS; ANA-AN00; HMSCore 6.0.0.306) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.93 HuaweiBrowser/11.1.3.300 Mobile Safari/537.36";
	static String USERINFO = "https://www.iesdouyin.com/web/api/v2/user/info/?";// 后面加sec_uid参数访问用户详情
	static String VEDIOLISTSURL = "https://www.iesdouyin.com/web/api/v2/aweme/post/?";
	static String home = "https://www.amemv.com/share/user/";// 后面加uid可以访问到对象主页,还需加参数sec_uid

	static String uid = "59099838701";//

	// static String shortUrl = "https://v.douyin.com/ewtkTFb";
	// static String shortUrl = "https://v.douyin.com/ewnyskt/";
	// static String shortUrl = "https://v.douyin.com/ewcmTgF/";

	public static void main(String[] args) {
		// 得到视频主页用户的 SecUid
		String secUid = getSecUid("https://v.douyin.com/eK1eShp/");

//		// 根据得到的SecUid得到用户相关信息
//		user user = new user(secUid);
//		getUserInfo(secUid, uesr);
		getvediolist(secUid, "0", "l6IC-gAA9rp4X05HE9YfT5eiAu","1128", "", "21");
		System.out.println("一共"+num+"条数据!"+"ok");
		user user = new user(secUid);
		getUserInfo(secUid, user);
		System.out.println(user);
	}
	/**
	 * 
	 * 
	 * @param secUid
	 * @param max_cursor
	 * @param _signature
	 * @param aid
	 * @param dytk
	 * @param count
	 */
	public static void getvediolist(String secUid, String max_cursor, String _signature, String aid, String dytk,
			String count) {
		String url = VEDIOLISTSURL + "sec_uid=" + secUid + "&max_cursor=" + max_cursor + "&_signature=" + _signature
				+ "&aid=" + aid + "&count=" + count + "&dytk=";
		// 创建CloseableHttpClient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建get方法
		HttpGet get = new HttpGet(url);
		// 为get方法添加请求头
		get.addHeader("user-agent",MOBLIEUSERAGENT);
		// 用httpclient对象发送请求,并接收response
		CloseableHttpResponse response;
		try {
			response = httpclient.execute(get);
			// 判断服务器是否正确返回
			if (response.getStatusLine().getStatusCode() == 200) {
				if (response.getEntity() != null) {
					String JSON = EntityUtils.toString(response.getEntity());
					// 解析出视频地址
					List<String> list = parseVedioJSON(JSON);
					Iterator<String> iterator = list.iterator();
					while(iterator.hasNext()) {
						String str = iterator.next();
						num++;
						System.out.println(str);
					}
					// 递归调用
					String nmax_cursor = parsemax_cursor(JSON);
					if(!nmax_cursor.equals("0")) {
						getvediolist(secUid,nmax_cursor,_signature, aid,  dytk, count) ;
						}
					}
					
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @param secUid
	 * @param user   空的user对象;用于存储数据
	 */
	public static void getUserInfo(String secUid, user user) {
		String url = USERINFO + "sec_uid" + "=" + secUid;
		// System.out.println(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet get = new HttpGet(url);
		get.addHeader("User-Agent", MOBLIEUSERAGENT);
		get.addHeader("Cookie",
				"passport_csrf_token_default=0da59d93e93e2b78d1a086b5f501a6e2; passport_csrf_token=0da59d93e93e2b78d1a086b5f501a6e2; ttwid=1%7Ctbpr8RT64RQc-es77WvMbFbDgYmKyYnqgeHuM6eZWkU%7C1628317869%7Cdbe9ce22100a34f4902a168c8485a5b0f843178aeb0d86d8019db98592b12b3a");

		try {
			CloseableHttpResponse response = httpclient.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				if (response.getEntity() != null) {
					String JSON = EntityUtils.toString(response.getEntity());
					String signature = parseUserInfo(JSON, "signature");
					String nickname = parseUserInfo(JSON, "nickname");
					String follower_count = parseUserInfo(JSON, "follower_count");
					String favoriting_count = parseUserInfo(JSON, "favoriting_count");
					String following_count = parseUserInfo(JSON, "following_count");
					String aweme_count = parseUserInfo(JSON, "aweme_count");
					String total_favorited = parseUserInfo(JSON, "total_favorited");
					user.setAweme_count(aweme_count);
					user.setFavoriting_count(favoriting_count);
					user.setFollower_count(follower_count);
					user.setFollowing_count(following_count);
					user.setSignature(signature);
					user.setTotal_favorited(total_favorited);
					user.setNickname(nickname);
				}
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param shortUrl 主页分享链接
	 * @return SecUid的值
	 */
	public static String getSecUid(String shortUrl) {
		// 禁用重定向的设置
		RequestConfig config = RequestConfig.custom().setRedirectsEnabled(false).build();

		// 创建CloseableHttpClient对象
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(config).build();

		// 创建get方法
		HttpGet get = new HttpGet(shortUrl);

		// 为get方法添加请求头
		get.addHeader("user-agent", MOBLIEUSERAGENT);
		get.addHeader("cookie",
				"passport_csrf_token_default=0da59d93e93e2b78d1a086b5f501a6e2; passport_csrf_token=0da59d93e93e2b78d1a086b5f501a6e2; ttwid=1%7Ctbpr8RT64RQc-es77WvMbFbDgYmKyYnqgeHuM6eZWkU%7C1628317869%7Cdbe9ce22100a34f4902a168c8485a5b0f843178aeb0d86d8019db98592b12b3a");
		// 用httpclient对象发送请求,并接收response
		CloseableHttpResponse response;
		try {
			response = httpclient.execute(get);
			String SecUid = null;
			// 判断服务器是否正确返回
			if (response.getStatusLine().getStatusCode() == 302) {
				//
				if (response.getEntity() != null) {
					String html = EntityUtils.toString(response.getEntity(), "utf-8");
					StringBuffer sb = new StringBuffer(html);

					int start = sb.indexOf("sec_uid") + "sec_uid".length() + 1;
					int end = 0;
					int index = 0;
					while (end < start) {
						end = sb.indexOf("&", index);
						index++;
					}
					String secUid = sb.substring(start, end);

					return secUid;
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param url  下载视频地址
	 * @param path 指定要下载到的位置
	 */
	public static void Downloadvdio(String url, String path) {
		// 创建CloseableHttpClient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();

		// 创建get方法
		HttpGet get = new HttpGet(url);
		// 为get方法添加请求头
		get.addHeader("user-agent", MOBLIEUSERAGENT);

		get.addHeader("cookie",
				"ttwid=1%7CIcTFYmx0a6sya_5qYXfvKADXPwmqP3IZdgn-Me1r3yc%7C1628138904%7Cfc01b270402352a9e1404d8209c866918b80eb241607912585c0ee5248225b93; MONITOR_WEB_ID=1810d7c5-535f-4f4f-bc14-a32373031605; douyin.com; passport_csrf_token_default=264b8e9033427145cdf42d0c270751f7; passport_csrf_token=264b8e9033427145cdf42d0c270751f7; s_v_web_id=verify_ks0gcoe8_08DMFr68_tXA9_433Y_BRZr_JiiT3wHkLReI");
		get.addHeader("Range", "bytes=0-");
		// 2805814

		// 用httpclient对象发送请求,并接收response
		CloseableHttpResponse response;
		try {
			response = httpclient.execute(get);
//			String header = response.getFirstHeader("Content-Type").getValue();
//			System.out.println(response.getStatusLine().getStatusCode() + "\n" + header);

			// 判断服务器是否正确返回
			if (response.getStatusLine().getStatusCode() == 206) {

				//
				if (response.getEntity() != null) {
					// 将返回结果转化为二进制 并写入文件
					byte[] byteArray = EntityUtils.toByteArray(response.getEntity());
					FileUtils.writeByteArrayToFile(new File(path), byteArray);
					System.out.println("该视频下载完毕");
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param JSON
	 * @param targetName 只能返回一般数据 ,不能返回数组,对象等
	 * @return 返回截取的json数据
	 */
	public static String parseUserInfo(String JSON, String targetName) {
		StringBuffer sb = new StringBuffer(JSON);
		String target = "\"" + targetName + "\":";
		int start = sb.indexOf(target) + target.length();
		int end = 0;
		int index = 0;
		while (end < start) {
			end = sb.indexOf(",", index);
			index++;
		}
		String result = sb.substring(start, end);
		result = result.replaceAll("\"", "");
		result = result.replaceAll("}", "");
		return result;
	}
	/**
	 * 
	 * @param JSON
	 * @return 返回List<String>其中包含视频下载位置
	 */
	public static List<String> parseVedioJSON(String json) {
		List<String> list = new ArrayList<String>();
		JSONObject jsonObject = JSONObject.fromObject(json);
		JSONArray aweme_list = (JSONArray) jsonObject.get("aweme_list");
		Object[] vedios = aweme_list.toArray();
		Iterator iterator = aweme_list.iterator();
		while(iterator.hasNext()){
			JSONObject next = (JSONObject) iterator.next();
			JSONObject vedio = (JSONObject) next.get("video");
			JSONObject play_addr = (JSONObject) vedio.get("play_addr");
			JSONArray urllist = (JSONArray) play_addr.get("url_list");
			list.add((String) urllist.get(0));
		}
		return list;
	}
	/**
	 * 
	 * @param jSON
	 * @param max_cursor
	 * @return
	 */
	private static String parsemax_cursor(String json) {
		JSONObject jsonObject = JSONObject.fromObject(json);
		Object object =  jsonObject.get("max_cursor");
		return object.toString();
	}
}
