
public class QoS3Util {
	String strUserName;
	int usernum;
	
//	public QoS3Util(){
//		usernum=0;
//	}
	
	public String randomUserName() {
		strUserName="user"+usernum;
		usernum++;
		return strUserName;
	}

}
