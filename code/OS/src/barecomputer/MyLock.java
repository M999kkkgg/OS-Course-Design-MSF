package barecomputer;

public class MyLock 
{
	// ʱ���ж���
	public static Object ClockIsChanged = new Object();
	// �ڴ�������
	public static Object MemoryLock = new Object();
	// �����߳�������
	public static Object BlockLock = new Object();
	// ��������߳���
	public static Object BlockCheckLock = new Object();
	// GUIͬ����
	public static Object GUILock = new Object();
	
	public MyLock()
	{
		// TODO Auto-generated constructor stub
	}
}
