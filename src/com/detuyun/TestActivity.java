package com.detuyun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.detuyun.R;
import com.detuyun.api.Uploader;
import com.detuyun.api.utils.DetuYunException;
import com.detuyun.api.utils.DetuYunUtils;

public class TestActivity extends Activity {
	protected static final int UPLOADERROR = 0x101;   
	private static final String TEST_API_KEY = "j26itmzbb2e2371ew7knzglm3rkyuq"; //测试使用的表单api验证密钥
	private static final String BUCKET = "ljf";	
//	private static final String TEST_API_KEY = "fhx442gh1n1qmeuqyvmtf5nt2uk482";
//	private static final String BUCKET = "abcdd";						//存储空间  abcdd
	private static final long EXPIRATION = System.currentTimeMillis()/1000 + 1000 * 5 * 10; //过期时间，必须大于当前时间
	
	private static final String SOURCE_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator + "sample.jpg"; //来源文件
	
	private static TextView resultShow;
    Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case TestActivity.UPLOADERROR:   
                       //myBounceView.invalidate();  
                	   DetuYunException ex =(DetuYunException)msg.obj;
                	   resultShow.setText(ex.toString());
                       break;   
             }   
             super.handleMessage(msg);   
        }   
   };  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		copyFileToSD();
		resultShow = (TextView)findViewById(R.id.result_show);
		new UploadTask().execute();

	}
	//拷贝sample.jpg到存储卡上
    private void copyFileToSD(){
		AssetManager assetManager = getApplicationContext().getAssets();
		 try {
			 InputStream is=assetManager.open("sample.jpg");

          if (!(new File(SOURCE_FILE)).exists())
          {
          	FileOutputStream fos = new FileOutputStream(SOURCE_FILE);
          	byte[] buffer = new byte[700000];
          	int count = 0;
          	while ((count = is.read(buffer)) > 0) {
              fos.write(buffer, 0, count);
          	}
          	fos.close();    	
          }
          if(is!=null) is.close();
          
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public class UploadTask extends AsyncTask<Void, Void, String> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(Void... params) {
			String string = null;
			
			try {
				//设置服务器上保存文件的目录和文件名，如果服务器上同目录下已经有同名文件会被自动覆盖的。
				//String SAVE_KEY = "faith196";//File.separator + "test" + File.separator + System.currentTimeMillis()+".jpg";
				String SAVE_KEY = "zxcdz653";
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("save_name", "/{year}/{mon}/{random}{.suffix}");/// 文件名生成格式，请参阅 API 文档
				map.put("content_length_range", "0,1024000");/// 限制文件大小，可选
				map.put("image_width_range", "100,1024000");/// 限制图片宽度，可选
				map.put("image_height_range", "100,1024000");/// 限制图片高度，可选
				//取得base64编码后的policy
				String policy = DetuYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET,map);
				
				//根据表单api签名密钥对policy进行签名
				//通常我们建议这一步在用户自己的服务器上进行，并通过http请求取得签名后的结果。
				String signature = DetuYunUtils.signature(policy + "&" + TEST_API_KEY);

				//上传文件到对应的bucket中去。
				HashMap<String, Object> resultMap = Uploader
						.upload(policy, signature , BUCKET, SOURCE_FILE);
				string = DetuYunUtils.getResultString(resultMap);

			} catch (DetuYunException e) {
                Message message = new Message();   
                message.what = TestActivity.UPLOADERROR;   
                message.obj=e;  
                TestActivity.this.myHandler.sendMessage(message);  
				e.printStackTrace();
			}
			
			return string;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_LONG).show();
				
				resultShow.setText(result);
			} else {
				Toast.makeText(getApplicationContext(), "失败", Toast.LENGTH_LONG).show();
				
			}
			
		}

	}
}