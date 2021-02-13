package com.jenkins.api.client;

import com.jenkins.api.tools.JsonParserTools;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author jinglv
 * @date 2021/02/12
 */
public class ClientApi {
    public static final Logger logger = LoggerFactory.getLogger(ClientApi.class);

    /**
     * Jenkins请求地址
     */
    private final String host;
    /**
     * Jenkins请求端口
     */
    private final String port;
    /**
     * Jenkins请求认证登录用户名
     */
    private final String username;
    /**
     * Jenkins请求认证登录密码
     */
    private final String password;
    /**
     * Jenkins Job名称
     */
    private final String jobName;
    /**
     * Jenkins Api请求Context_Type, text
     */
    private static final String TYPE_TEXT = "plan/text";
    /**
     * Jenkins Api请求Context_Type, json
     */
    private static final String TYPE_JSON = "application/json";

    public String getJobName() {
        return jobName;
    }

    public ClientApi() {
        String propFileName = "jenkins-config.properties";
        Properties prop = loadFromEnvProperties(propFileName);
        host = prop.getProperty("host");
        port = prop.getProperty("port");
        username = prop.getProperty("username");
        password = prop.getProperty("password");
        jobName = prop.getProperty("job_name");
    }

    /**
     * 获取Jenkins Job最后一次构建的Number
     *
     * @return Integer
     * @throws Exception 异常
     */
    public int getLastBuildNumber() throws Exception {
        String path = "job/" + jobName + "/lastBuild/buildNumber";
        return Integer.parseInt(get(path, TYPE_TEXT));
    }

    /**
     * 执行Jenkins Job
     *
     * @throws Exception 异常
     */
    public void runBuild() throws Exception {
        String path = "job/" + jobName + "/build";
        post(path, TYPE_TEXT);
    }

    /**
     * 执行Jenkins Job是否完成
     *
     * @param buildNumber jenkins job build number
     * @return boolean
     * @throws Exception 异常
     */
    public boolean isJobBuilding(int buildNumber) throws Exception {
        String path = "job/" + jobName + "/" + buildNumber + "/api/json";
        String resultJson = get(path, TYPE_JSON);
        return JsonParserTools.getJsonBoolean(resultJson, "building");
    }

    public String getJobResult(int buildNumber) throws Exception {
        String path = "job/" + jobName + "/" + buildNumber + "/api/json";
        String rJson = get(path, TYPE_JSON);
        return JsonParserTools.getJsonValue(rJson, "result");
    }

    /**
     * get请求
     *
     * @param path    请求地址
     * @param enctype 鉴权认证
     * @return api请求返回结果
     * @throws Exception 异常
     */

    public String get(String path, String enctype) throws Exception {
        String url = "http://" + host + ":" + port + "/" + path;
        Client client = null;
        ClientResponse response = null;
        String rs;
        try {
            client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter(username, password));
            WebResource webResource = client.resource(url);
            response = webResource.accept(enctype).get(ClientResponse.class);
            if (response != null && response.getStatus() >= 200 && response.getStatus() <= 206) {
                rs = response.getEntity(String.class);
            } else {
                throw new Exception("Response status error for running " + url + " task!");
            }
        } finally {
            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.destroy();
            }
        }
        return rs;
    }

    /**
     * post请求
     *
     * @param path    请求地址
     * @param enctype 鉴权认证
     * @throws Exception 异常
     */
    public void post(String path, String enctype) throws Exception {
        String url = "http://" + host + ":" + port + "/" + path;
        Client client = null;
        ClientResponse response = null;
        try {
            client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter(username, password));
            WebResource webResource = client.resource(url);
            response = webResource.accept(enctype).post(ClientResponse.class);
            if (response == null || response.getStatus() < 200 || response.getStatus() > 206) {
                throw new Exception("Response status error for running " + url + " task!");
            }
        } finally {
            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.destroy();
            }
        }
    }

    /**
     * 读取properties配置文件
     *
     * @param propFileName 配置文件名
     * @return 返回配置文件内容
     */
    private Properties loadFromEnvProperties(String propFileName) {
        Properties prop = new Properties();
        // 读入envProperties属性文件
        try {
            ClassLoader classLoader = ClientApi.class.getClassLoader();
            InputStream in = classLoader.getResourceAsStream(propFileName);
            // 加载属性列表
            prop.load(in);
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            logger.error("配置文件加载失败，请检查 " + propFileName + "文件是否存在！");
        }
        return prop;
    }
}
