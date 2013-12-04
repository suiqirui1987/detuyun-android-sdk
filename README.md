detuyun-android-sdk
===================



得图云Android SDK 基于<a href="http://www.detuyun.com/docs/form.html" target="_blank">得图云表单API</a>构建。在开发者的 Android App 工程项目中使用此 SDK 能够非常方便地将 Android 系统里边的文件快速直传到得图云存储。



- [应用接入](#install)
	- [获取Access Key 和 Secret Key](#acc-appkey)
- [使用说明](#detuyun-api)
	- [1 设置初始参数](#detuyun-init)
	- [2 设置上传参数](#detuyun-upload)
	- [3 获取policy参数](#detuyun-getdir)
	- [4 Signature 签名](#detuyun-down)
	- [5 上传文件](#detuyun-createdir)
	- [6 并发特性](#detuyun-deletedir)
- [异常处理](#detuyun-exception)


<a name="install"></a>
## 应用接入

<a name="acc-appkey"></a>

### 1. 获取Access Key 和 Secret Key

要接入得图云存储，您需要拥有一对有效的 Access Key 和 Secret Key 用来进行签名认证。可以通过如下步骤获得：

1. <a href="http://www.detuyun.com/user/accesskey" target="_blank">登录得图云开发者自助平台，查看 Access Key 和 Secret Key 。</a>

<a name=detuyun-api></a>
## 使用说明

<a name="detuyun-init"></a>
### 1.设置初始参数

	private static final String TEST_API_KEY = "fhx442gh1n1qmeuqyvmtf5nt2uk482";
	private static final String BUCKET = "abcdd";					
	private static final long EXPIRATION = System.currentTimeMillis()/1000 + 1000 * 5 * 10; 	
	private static final String SOURCE_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "sample.jpg"; 

参数`TEST_API_KEY`为测试使用的表单api验证密钥，`BUCKET`为存储空间名称，`EXPIRATION`为过期时间，必须大于当前时间，`SOURCE_FILE`为来源文件。

<a name="detuyun-upload"></a>
### 2. 设置上传参数

	String SAVE_KEY = "faith196";//File.separator + "test" + File.separator + System.currentTimeMillis()+".jpg";
	HashMap<String, Object> map = new HashMap<String, Object>();
	map.put("save_name", "/{year}/{mon}/{random}{.suffix}");
	map.put("content_length_range", "0,1024000");
	map.put("image_width_range", "100,1024000");
	map.put("image_height_range", "100,1024000");
	String policy = DetuYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET,map);

* `SAVE_KEY`表示设置服务器上保存文件的目录和文件名，如果服务器上同目录下已经有同名文件会被自动覆盖的。
* `save_name`表示文件名生成格式，具体请参阅<a href="http://www.detuyun.com/docs/form2.html" target="_blank">表单API文档</a>。
* `content_length_range`表示限制文件大小，为可选参数。
* `image_width_range`表示限制图片宽度，为可选参数。
* `image_height_range`表示限制图片高度，为可选参数。

<a name="detuyun-getdir"></a>
###3.获取policy参数

	String policy = DetuYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET,map);

获取base64编码后的policy。

<a name=detuyun-down></a>
### 4. Signature 签名

	String signature = DetuYunUtils.signature(policy + "&" + TEST_API_KEY);
根据表单api签名密钥对policy进行签名。通常我们建议这一步在用户自己的服务器上进行，并通过http请求取得签名后的结果。

<a name=detuyun-createdir></a>
### 5.上传文件

	HashMap<String, Object> resultMap = Uploader.upload(policy, signature , BUCKET, SOURCE_FILE);
	string = DetuYunUtils.getResultString(resultMap);

将要存储的图片上传到对应的bucket中去。当文件上传成功时，会试图跳转到浏览器访问已经上传的资源。如果失败，会toast提示。

<a name=detuyun-deletedir></a>
###6.并发特性

此 Android SDK 不是线程安全的，请勿在没有保护的情况下跨线程使用。

<a name=detuyun-exception></a>
## 异常处理
通常只有code和message信息。但在上传时候还会传递其他错误信息，供开发者使用，具体请参考<a href="http://www.detuyun.com/docs/form6.html" target="_blank">表单API错误列表</a>。

* **DetuYunException(20, "miss param access_key")** 验证密钥未初始化
* **DetuYunException(20, "miss param expiration")** 过期时间未初始化
* **DetuYunException(20, "miss param bucket")**  空间名称未初始化
* **DetuYunException(21, e.getMessage())**      获取信息错误

未包含在以上异常中的错误，将统一抛出 `DetuYunException` 异常。
