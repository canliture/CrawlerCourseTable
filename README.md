# 课程表的实现（1）（基于强智科技教务系统） #
  
> 1，本小系统服务于在校大学生。用户可以根据代码定制安装自己的轻量级课程表app在手机上(当然，可以把网络请求部分修改移植到PC或者Web平台上)。  <br/>
> 2，好处：再也不用为了在手机上看课表而专门去下载30~60MB不等大小并且有各种干扰信息的APP了。 同时，许多教务系统只能用IE登陆，是否让人恼火？ <br/>
> 3，这里尝试做个简单的课表查询小程序(Android)。当然对于这里所提到的网络请求都清楚了，可以实现更多基于教务系统周边更有意思的应用。 <br/>
> 4，源代码可以在github上查看

## 阅读前 ##

- 在阅读本文章，代码的时候假定你已经对 __HTTP的GET，POST__ 有基本的认识。知道 __HTML的form__ 表单干什么用。
 
- 对基于 __"强智科技教务系统"__ 的同学友好。因为他们做的登陆接口，查询接口相差不大。我找了几个跟本校教务系统界面差不多的系统,查看了 __验证码接口，登陆接口__ 。发现几乎没什么变化。所以我猜内部的课表接口是重用的一套代码。<br/>
下面是我找到的跟本校的教务系统同一风格并且验证码接口，登陆接口一样的教务系统界面截图。（至于内部课表的接口我就不知道是否跟本校的接口格式一致，大家具体情况具体分析）<br/>

![](  https://i.imgur.com/vjiK45C.png)

  当然这个登陆接口也类似啦

![](  https://i.imgur.com/vqmqntq.png)

- 当然对于非 __"强智科技教务系统"__ 的同学来说，也很容易根据代码分析，然后写出适合自己学校的课表查看小程序。

-  __代码重在分析网络请求，筛选重要信息。__ 这是任何人都能学到的知识。

## 源码说明 ##
本代码最终数据呈现平台为 __Android__ ,在解析接口，获得数据过程中会讲解怎么将其呈现在界面上。当然你也可以做基于web，PC端的。

__代码仍有不足，后续会更新，可持续关注哟：学期灵活切换；验证码识别。__

__有任何问题可以@我，canliture@outlook.com__

## 代码模块解析 ##

### 1. 网络请求 ###

  教务系统登陆通用 __三要素__ ：__账号，密码，验证码__(Tips: 有些系统验证码没有，实现更容易； 我学校教务系统里有学生，家长选项，默认设置学生就好)
  
  有了所述的三要素，我们就可以进入教务系统进行各项操作啦。__下面是分析网络请求__。

  打开浏览器（这里我们用Chrome浏览器，当然firefox也有类似的如下操作），进入教务网登陆界面，按F12弹出 __开发者工具__ 点击 __Network__ ，再在最左侧点击教务网的 __请求路径名__ ，可以看到Request Headers（请求头）。(如果点击Network什么都看不到那就在开发者工具打开的前提下刷新一下教务网就行了)。请求头里 __最重要__ 的当然是 __Cookie__ 啦。

  ![](https://i.imgur.com/RA6HP8U.png)

  __Cookie在这里作为登陆的唯一标识。也就是说，你登陆后，把Cookie保存下来，使用这个Cookie（存活期内）能够访问这个教务系统下的任意地址的网页，数据。__

  下面可以做个小实验验证这个观点，这个实验理解了，后面程序的 __模拟登陆教务网__ 也可以理解了。

  假定我的 __教务网地址__ 是： __kk.edu.cn/jww/__  ，我登陆成功并且 __跳转地址__ 到 __kk.edu.cn/jww/framework/main.jsp__ ，复制成功登陆时用的Cookie值。在浏览器上打开一个新标签页，输入  __教务网地址__  ：  __kk.edu.cn/jww/__ ，此时（一般情况）是没有登陆的，我们再次打开开发者工具，这次不点击Network了，我们点击  __Application__ ，双击Cookie值，修改它为我们之前登陆的Cookie值(如下图)，再把本标签页地址栏的地址换为登陆后我们想访问的地址（比如说登陆后跳转的地址）并回车！我们已经算是登陆教务系统并且进入系统了！

  ![]( https://i.imgur.com/rv7Efkr.png)

  下面是登陆教务网的核心代码。
	
    Connection loginConn = Jsoup.connect(loginHomePage);
    loginConn.cookies(cookies);                                   //login using cookies
    loginConn.data(reqData);									  //login parameters
    Connection.Response loginResponse = loginConn.execute();	
    
  说明：Jsoup库很简单，并且在这里用的不多，通过代码说明很容易理解。        <br/>
  loginHomePage为教务网 __form的action__ 属性值（查看源码可以看到），也就是我们表单要提交的url地址。
  如果我们直接看代码第四行，把而三行注释掉，获得一个Response，我们可以直接打印出loginResponse的内容，全部为HTML代码，根本没有图片。也就是说， __我们得单独请求图片，并且保存请求图片返回的Cookie，并且登陆时使用这个Cookie来登陆。__ 

  __现在思路就是：我们想要登陆，就得先请求并下载验证码图片，获取请求图片时返回的Cookie，并且用这个Cookie加上我们自己填写的账号，密码，和根据下载的图片填写的验证码，把这些参数一并提交给服务器进行登陆__。

  于是我们可以完善我们的代码：
		
	//all static for convenience

	//file request parameters
	static String personType = "0";           //0-我是学生,  1-我是家长。 很多教务网都没有这个字段，可忽略掉
	static String userNumber = "****";        //你的账号
	static String password = "****";          //你的密码
	static String verifyCode = "";            //验证码默认设置为空，后续通过用户的输入来填充这个值
	
	//登陆请求的地址
	final static String loginHomePage = "http://kkk.edu.cn/jww/Logon.do?method=logon";
	//验证码请求地址
	final static String verifyImgSrcURL = "http://kk.edu.cn/jww/verifycode.servlet";
	//验证码保存的桌面位置
	final static String verifyImgDestURL = "C:\\Users\\Liture\\Desktop\\verifyCodeImg.jpg";
	课表的地址
	final static String getTableBaseURL = "http://kkk.edu.cn/jww/tkglAction.do?method=goListKbByXs";
	
	//cookie name
	final static String sessionName = "JSESSIONID"; 

	//cookie value
	static String sessionValue = "";						    
	
	//request parameters [personType, username, password, RANDOMCODE]
	static Map<String, String> reqData = new HashMap<>();  
	
	static Map<String, String> cookies = new HashMap<>();       //save cookies
	
	// 'reqData' initialization
	static { 
		reqData.put("personType", personType); //  0 - students or teachers , 1 - parents
    	reqData.put("USERNAME", userNumber);  
    	reqData.put("PASSWORD", password);
	}

	public static void main(String[] args) throws IOException {

		//download verifyImg and
		//get the cookie we getfrom the img
	    //use the cookie everywhere around the site.
		downloadImgReturnCookie(verifyImgSrcURL, verifyImgDestURL);

	    System.out.println("Download Image Successfully");

		//print cookie
	    System.out.println(cookies);

		//input verifycode
    	System.out.print("Please input your verifyCode: ");
    	verifyCode = new Scanner(System.in).nextLine();
    	reqData.put("RANDOMCODE", verifyCode);

    	//login using cookie prepared.
    	Connection loginConn = Jsoup.connect(loginHomePage);

		//login using cookies
    	loginConn.cookies(cookies);     
             
		//login parameters                 
    	loginConn.data(reqData);

		//try to login
    	Connection.Response loginResponse = loginConn.execute();

		System.out.println( loginResponse.body() );

		//。。。未完，看下面分析
	}

	// download verifyCode image from 'srcUrl' and save to 'dest' at localhost
    private static void downloadImgReturnCookie(String srcUrl, String dest) throws MalformedURLException, IOException{

    	//get image using the cookie
    	HttpURLConnection imgConn = (HttpURLConnection) ( new URL(srcUrl) ).openConnection();

    	//get the cookie
    	String cookie = imgConn.getHeaderField("Set-Cookie");
    	sessionValue = cookie.substring( cookie.indexOf('=') + 1, cookie.indexOf(';') );
    	cookies.put( sessionName,  sessionValue );

    	//new input from network( 'imgConn' )
    	try( BufferedInputStream imgInputStream = new BufferedInputStream( imgConn.getInputStream() ) ){
    		//new output to local file system
    		try( BufferedOutputStream imgOutputStream = new BufferedOutputStream( new FileOutputStream(dest) );  ){
    			byte[] buf = new byte[1024];
    	    	while( -1 != ( imgInputStream.read(buf) ) )   
    	    		imgOutputStream.write(buf);
    		} catch(IOException e) { e.printStackTrace();  }
    	} catch(IOException e) { e.printStackTrace();  }
    }
  
  代码先调用`downloadImgReturnCookie(verifyImgSrcURL, verifyImgDestURL);`请求验证码图片，并且保存到桌面，于此同时把返回的Cookie保存起来。再通过`new Scanner(System.in).nextLine()`获取用户输入的验证码（下载到桌面了自己很容易打开并且肉眼识别了）。
  最后登录，打印登陆请求返回的HTML源码。

  我们发现，当我们输入了错误的验证码（或者账号或密码错误）的时候，打印出来的是登陆界面的HTML源码，当我们登陆成功的时候，返回的内容很简单：`<script language='javascript'>window.location.href='http://kk.edu.cn/jww/framework/main.jsp';</script>`。据此我们很容易判断我们是否登陆成功了。

  以上是网络请求的全部分析过程。

  更多的将数据呈现在Android平台上可以查看源代码。同时可以查看下面的效果图。

  
  
  
