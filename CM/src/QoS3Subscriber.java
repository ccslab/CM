import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.util.Scanner;

import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class QoS3Subscriber {
	private CMClientStub m_clientStub;
	private QoS3SubEventHandler m_eventHandler;
	private boolean m_bRun;
	private Scanner m_scan = null;
	private QoS3Util util;
	
	public QoS3Subscriber()
	{
		m_clientStub = new CMClientStub();
		m_eventHandler = new QoS3SubEventHandler(m_clientStub);
		m_bRun = true;
		util=new QoS3Util();
	}
	
	public CMClientStub getClientStub()
	{
		return m_clientStub;
	}
	
	public QoS3SubEventHandler getClientEventHandler()
	{
		return m_eventHandler;
	}
	
	///////////////////////////////////////////////////////////////
	// test methods
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QoS3Subscriber client = new QoS3Subscriber();
		CMClientStub cmStub = client.getClientStub();
		cmStub.setAppEventHandler(client.getClientEventHandler());
		client.testStartCM();
		
		System.out.println("Client application is terminated.");
	}
	
	public void testStartCM()
	{
		// get current server info from the server configuration file
		String strCurServerAddress = null;
		int nCurServerPort = -1;
		String strNewServerAddress = null;
		String strNewServerPort = null;
		
		strCurServerAddress = m_clientStub.getServerAddress();
		nCurServerPort = m_clientStub.getServerPort();
		
		// ask the user if he/she would like to change the server info
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("========== start CM");
		System.out.println("current server address: "+strCurServerAddress);
		System.out.println("current server port: "+nCurServerPort);

		boolean bRet = m_clientStub.startCM();
		if(!bRet)
		{
			System.err.println("CM initialization error!");
			return;
		}
		loginDS();
	}
	
	public boolean loginDS()
	{
		String strUserName = null;
		String strPassword = null;
		boolean bRequestResult = false;
		Console console = System.console();
		if(console == null)
		{
			
			System.err.println("Unable to obtain console.");
		}
		
		System.out.println("====== login to default server");
		
		strUserName="sub"+util.randomUserName();
		strPassword="0000";
		
		bRequestResult = m_clientStub.loginCM(strUserName, strPassword);
		if(bRequestResult) {
			System.out.println("successfully sent the login request.");
			System.out.println("======");
			return true;
		}
		else {
			System.err.println("failed the login request!");
			System.out.println("======");
			return false;
		}
	}
	
}