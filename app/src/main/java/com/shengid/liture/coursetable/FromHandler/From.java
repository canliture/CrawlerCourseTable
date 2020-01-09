package com.shengid.liture.coursetable.FromHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.shengid.liture.coursetable.Activity.LoginActivity;
import com.shengid.liture.coursetable.Activity.MainActivity;
import com.shengid.liture.coursetable.Entity.CourseInfoEntity;
import com.shengid.liture.coursetable.Entity.CourseTable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class From {

    //file request parameters
    private static final  String personType = "0";
    private static String userNumber;
    private static String password;
    private static String verifyCode;

    private final static String loginHomePage = "http://kdjw.hnust.edu.cn:8080/kdjw/Logon.do?method=logon";
    private final static String verifyImgSrcURL = "http://kdjw.hnust.edu.cn:8080/kdjw/verifycode.servlet";
    //private final static String verifyImgDestURL = "C:\\Users\\Liture\\Desktop\\verifyCodeImg.jpg";
    private final static String getTableBaseURL = "http://kdjw.hnust.edu.cn:8080/kdjw/tkglAction.do?method=goListKbByXs";

    private final static String cookieName = "JSESSIONID";

    private static Map<String, String> reqData;       //request parameters [personType, username, password, RANDOMCODE]

    private static Map<String, String> cookies = new HashMap<>();       //save cookies

    public From(){ }

    public void submit(Context context, String user, String pwd, String codeStr){
        new Thread( () -> {

                userNumber = user;
                password = pwd;
                verifyCode = codeStr;

                reqData = new HashMap<>();
                reqData.put("personType", personType);                  //  0 - students or teachers , 1 - parents
                reqData.put("USERNAME", userNumber);
                reqData.put("PASSWORD", password);
                reqData.put("RANDOMCODE", verifyCode);

                Connection loginConn = null;
                Connection.Response loginResponse = null;

                try{
                    //start login in...
                    loginConn = Jsoup.connect(loginHomePage);
                    loginConn.cookies(cookies);                                         // login using cookies
                    Log.e("Cookie", cookies.get(cookieName) );
                    loginConn.data(reqData);									        // login parameters

                    loginResponse = loginConn.execute();	                            // try to login

                    //whether login in?
                    if( loginResponse.statusCode() == 200 ){                             //if Login in successfully
                        System.out.println("Login Status: " + loginResponse.statusCode() + ", " + loginResponse.statusMessage());
                        // TODO change date
                        Connection	tableConn = Jsoup.connect(getTableBaseURL + "&istsxx=no" + "&xnxqh=2019-2020-1" + "&zc=" + "&xs0101id="+ userNumber );
                        tableConn.cookies(cookies);
                        Connection.Response responseTablePage = null;

                        responseTablePage = tableConn.execute();
                        Log.e("Cookie", cookies.get(cookieName) );

                        ((LoginActivity)context).handlerSubmit(context, responseTablePage.body() );
                    } else //Login Failed
                        ((LoginActivity)context).handlerSubmit(context, null );

                } catch (Exception e){
                    //network error or failed to login
                    Log.e("EEEEERRRRRR", "Login Failed or network error....!!!");
                    ((LoginActivity)context).handlerSubmit(context, null );
                }

        }).start();               //A long code segment, don't forget add ".start()".
    }

    public void getVerifyCodeImage(Context context)  {

        new Thread( () ->{
            Connection conn = Jsoup.connect(verifyImgSrcURL).ignoreContentType(true);
            Map<String, String> header = new HashMap<String, String>();
            header.put("User-Agent", "  Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
            conn.headers(header);
            Connection.Response response;
            Bitmap verifyImage = null;
            try {
                response = conn.execute();                                               //request
                cookies = response.cookies();                                           //get cookie
                Log.e("Cookie, Img", cookies.get("JSESSIONID") );
                verifyImage = BitmapFactory.decodeStream( response.bodyStream() );      //get image
            } catch (IOException e) {
                ((LoginActivity)context).loadImage(null);
            }
            ((LoginActivity)context).loadImage(verifyImage);
        } ).start();

    }

}
