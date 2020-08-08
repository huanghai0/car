package com.example.car.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectionService {

    public String HttpPostConnection(String httpUrl, String data) {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(httpUrl);
            byte[] writebytes = data.getBytes();

            HttpURLConnection postConnection = (HttpURLConnection) url.openConnection();
            postConnection.setRequestMethod("POST");//post 请求
            postConnection.setConnectTimeout(1000 * 5);
            postConnection.setReadTimeout(1000 * 5);
            postConnection.setDoInput(true);//允许从服务端读取数据
            postConnection.setDoOutput(true);//允许写入
            postConnection.setUseCaches(false);//post请求缓存设为false

            postConnection.setRequestProperty("Connection", "Keep-Alive");
            postConnection.setRequestProperty("Content-type", "application/json; charset=UTF-8");
            postConnection.setRequestProperty("accept", "application/json");
            postConnection.setRequestProperty("Content-Length", String.valueOf(writebytes.length));

            OutputStream outputStream = postConnection.getOutputStream();
            outputStream.write(data.getBytes());//把参数发送过去.
            outputStream.flush();
            outputStream.close();

            int code = postConnection.getResponseCode();
            System.out.println("!!!!!!!!!!!!!!!!" + code);
            if (code == 200) {//成功
                InputStream inputStream = postConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;//一行一行的读取
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);//把一行数据拼接到buffer里
                    System.out.println("!!!!!!!!!!!!!!!!" + buffer.toString());
                }
                bufferedReader.close();
                inputStream.close();
                postConnection.disconnect();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public String HttpGetConection(String URLParam) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(URLParam);    // 把字符串转换为URL请求地址
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000 * 6);
            connection.setReadTimeout(1000 * 6);
            connection.setDoInput(true);//允许从服务端读取数据
            connection.connect();// 连接会话
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
            connection.disconnect();// 断开连接
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("失败!");
        }
//        System.out.println(sb.toString()+"____++++++++++++++++++++++++");
        return sb.toString();
    }

    public void HttpDeleteConection(String URLParam) {
        try {
            URL url = new URL(URLParam);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("dev_key", "12345");
            connection.setReadTimeout(60000);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("DELETE");
            connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                connection.disconnect();// 断开连接
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("失败!");
        }
    }

    public String HttpPutConection(String puturl, String data) {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(puturl);
            byte[] writebytes = data.getBytes();

            HttpURLConnection postConnection = (HttpURLConnection) url.openConnection();
            postConnection.setRequestMethod("PUT");//post 请求
            postConnection.setConnectTimeout(1000 * 5);
            postConnection.setReadTimeout(1000 * 5);
            postConnection.setDoInput(true);//允许从服务端读取数据
            postConnection.setDoOutput(true);//允许写入
            postConnection.setUseCaches(false);//post请求缓存设为false

            postConnection.setRequestProperty("Connection", "Keep-Alive");
            postConnection.setRequestProperty("Content-type", "application/json; charset=UTF-8");
            postConnection.setRequestProperty("accept", "application/json");
            postConnection.setRequestProperty("Content-Length", String.valueOf(writebytes.length));

            OutputStream outputStream = postConnection.getOutputStream();
            outputStream.write(data.getBytes());//把参数发送过去.
            outputStream.flush();
            outputStream.close();

            int code = postConnection.getResponseCode();
            System.out.println("!!!!!!!!!!!!!!!!" + code);
            if (code == 200) {//成功
                InputStream inputStream = postConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;//一行一行的读取
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);//把一行数据拼接到buffer里
                    System.out.println("!!!!!!!!!!!!!!!!" + buffer.toString());
                }
                bufferedReader.close();
                inputStream.close();
                postConnection.disconnect();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
