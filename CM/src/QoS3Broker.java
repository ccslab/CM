import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.sns.CMSNSUserAccessSimulator;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class QoS3Broker {
	private CMServerStub m_serverStub;
	private QoS3BrkEventHandler m_eventHandler;
	private boolean m_bRun;
	private CMSNSUserAccessSimulator m_uaSim;
	private Scanner m_scan = null;
	
	public QoS3Broker()
	{
		m_serverStub = new CMServerStub();
		m_eventHandler = new QoS3BrkEventHandler(m_serverStub);
		m_bRun = true;
		m_uaSim = new CMSNSUserAccessSimulator();
	}
	
	public CMServerStub getServerStub()
	{
		return m_serverStub;
	}
	
	public QoS3BrkEventHandler getServerEventHandler()
	{
		return m_eventHandler;
	}
	
	///////////////////////////////////////////////////////////////
	// test methods

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QoS3Broker server = new QoS3Broker();
		CMServerStub cmStub = server.getServerStub();
		cmStub.setAppEventHandler(server.getServerEventHandler());
		server.startCM();
		
		System.out.println("Server application is terminated.");
	}
	
	public void startCM()
	{
		// get current server info from the server configuration file
		String strSavedServerAddress = null;
		String strCurServerAddress = null;
		int nSavedServerPort = -1;
		String strNewServerAddress = null;
		String strNewServerPort = null;
		int nNewServerPort = -1;
		
		strSavedServerAddress = m_serverStub.getServerAddress();
		strCurServerAddress = CMCommManager.getLocalIP();
		nSavedServerPort = m_serverStub.getServerPort();
		
		// ask the user if he/she would like to change the server info
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("========== start CM");
		System.out.println("detected server address: "+strCurServerAddress);
		System.out.println("saved server port: "+nSavedServerPort);
		
		boolean bRet = m_serverStub.startCM();
		if(!bRet)
		{
			System.err.println("CM initialization error!");
			return;
		}
		startTest();
	}
	
	public void startTest()
	{
		System.out.println("Server application starts.");
	}

}
