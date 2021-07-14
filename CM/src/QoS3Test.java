
public class QoS3Test {

	public static void main(String[] args) {

		QoS3Broker brk=new QoS3Broker();
		QoS3Subscriber sub=new QoS3Subscriber();
		QoS3Publisher pub=new QoS3Publisher();
		
		brk.main(args);
		sub.main(args);
		pub.main(args);
	}

}
