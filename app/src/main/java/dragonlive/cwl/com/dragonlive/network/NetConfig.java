package dragonlive.cwl.com.dragonlive.network;

/**
 * Created by cwl on 2018/5/3.
 */

public class NetConfig {
    public  static final   String LocalServer="http://192.168.155.1:8080/ImoocBearLive";//暂时放在本地
    public  static final   String Server="http://47.106.155.189/ImoocBearLive";//正式服务器
    public  static final   String StsServerIp="http://47.106.155.189:7085";  //获取临时钥匙 用于上传到OSS
    public  static final   String ALiEndpoint= "http://oss-cn-shenzhen.aliyuncs.com";
    public  static final   String Room=Server+"/roomServlet";

}
