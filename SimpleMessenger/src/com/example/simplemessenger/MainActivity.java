package com.example.simplemessenger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;

public class MainActivity extends Activity {

	TextView tv;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final EditText nameField = (EditText) findViewById(R.id.et);             

		try{
			ServerSocket serversocket = new ServerSocket(10000);

			new Server().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,serversocket);

		}catch(IOException ie){
			ie.printStackTrace();
		}


		nameField.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
				{

					nameField.clearComposingText();
					
					final String name = nameField.getText().toString();
					if((name.getBytes().length)<=128)
					{
					new Client().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name);
					}
					
					//nameField.setText("");
					//nameField.clearComposingText();
				}
				return false; 

			}

		});
	}

	class Server extends AsyncTask <ServerSocket, String, String>
	{
		public InetAddress byIpAsName ;
		@Override
		protected String doInBackground(ServerSocket... serverport)  {

			ServerSocket  serversocket =serverport[0];
			BufferedReader in=null;
			Socket socket=null;
			String cIn="";
			try {


				while(true)
				{
					//Socket socket=serversocket.accept();


					//Socket socket= new Socket("10.0.2.2",10000);
					socket = serversocket.accept();
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line=in.readLine();
					publishProgress(line);

					socket.close();
				}//while    

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;


		}
		//@SuppressWarnings("null")
		protected void onProgressUpdate(String... cIn)
		{

			TextView tv = (TextView) findViewById(R.id.tv1);

			tv.append(cIn[0]+"\n");
			return;
		}


		

	}
	class Client extends AsyncTask<String, String,Void>
	{


		@Override
		protected Void doInBackground(String... params) {

			Socket socket = null;
			DataOutputStream outToServer = null;
			BufferedReader inFromServer = null;
			String	sIn=params[0];
			try 
			{
			
				TelephonyManager tel = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
				int q;
				if(portStr.equalsIgnoreCase("5554"))
				{
					q=11112;
				}else
				{
					q=11108;
				}


				socket = new Socket(InetAddress.getByName("10.0.2.2"), q);
				outToServer=new DataOutputStream(socket.getOutputStream());  
				outToServer.writeBytes(params[0]+"\n");
				outToServer.flush();
				publishProgress(params[0]);
			}catch(IOException ioe)
			{
				ioe.printStackTrace();
			}




		

			return null;

		}
		protected void onProgressUpdate(String... sIn)
				{
					 TextView tv = (TextView) findViewById(R.id.tv1);
					tv.append(sIn[0]+"\n");
				}

	}


    
}
