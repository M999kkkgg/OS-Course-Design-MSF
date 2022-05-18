package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import barecomputer.*;
import devicemanage.BlockAwakeThread;
import devicemanage.BlockWorkThread;
import filemanage.*;
import gui.DeviceGUI;
import gui.DiskGUI;
import gui.FileGUI;
import gui.FirstGUI;
import gui.JobGUI;
import gui.MemoryGUI;
import gui.OSGUI;
import gui.ProcessGUI;
import memorymanage.MMU;

public class Computer 
{
//	public static final int DISK_NUM = 1;
//	public static final int CYLINDER_NUM = 10;
//	public static final int TRACK_NUM = 32;
//	public static final int SECTOR_NUM = 64;
	public static String DiskStr = "disk";
	public static String Cylinder = "cylinder_";
	public static String Track = "track_";
	public static String Sector = "sector_";
	
	public static int JCB_NUM = 0;		// 开机以来的JCB数目
	public static int FINISH_NUM = 0;	// 完成的进程/作业数目
	
	public static CPU cpu = new CPU();
	public static Memory memory = new Memory();
	public static Disk disk = new Disk();
	public static Clock clock = new Clock();
	public static MMU mmu = new MMU();
	public static RandomGenerator randomGenerator = new RandomGenerator();
	public static Language language = new Language();
	public static BlockWorkThread blockWorkThread = new BlockWorkThread();
	public static BlockAwakeThread blockAwakeThread = new BlockAwakeThread();
	
	// GUI
	public static FirstGUI firstGUI = new FirstGUI();
	public static OSGUI mainGUI = new OSGUI();
	public static MemoryGUI memoryGUI = new MemoryGUI();
	public static DiskGUI diskGUI = new DiskGUI();
	public static DeviceGUI deviceGUI = new DeviceGUI();
	public static JobGUI jobGUI = new JobGUI();
	public static ProcessGUI processGUI = new ProcessGUI();
	public static FileGUI fileGUI = new FileGUI();
	
	// 界面中的字符缓存
	public static String openInfo = "";
	public static String RunInfo;
	public static String DeviceInfo;
	
	// 重定向out到结果输出文件
	public static String Root;
	public static String outPath;
	public static File f;
    public static FileOutputStream fileOutputStream;
    public static PrintStream printStream;
	
	public Computer()
	{
		
	}
	public static void Initlize() throws IOException
	{
		firstGUI.setPriority(9);
		clock.setPriority(8);
		disk.doLoad();
		memory.Initialize(disk);
		
		// 重定向初始化
		Root = System.getProperty("user.dir");
		outPath = Root + File.separator + "output" + File.separator + "ProcessResults.txt";
		File f=new File(outPath);
		f.createNewFile();
		fileOutputStream = new FileOutputStream(f);
		printStream = new PrintStream(fileOutputStream);
	    System.setOut(printStream);
	    
	    RunInfo = "";
	    DeviceInfo = "";
	}
	public static void rebuild(boolean flag)
	{
		if(flag)
		{
			CreateInitFiles();
			System.out.println("创建磁盘映像txt文件");
			openInfo += "创建磁盘映像txt文件\n";
			ClearDisk();
			System.out.println("磁盘格式化");
			openInfo += "磁盘格式化\n";
		}
		ClearFree();
		System.out.println("构建成组区块链");
		openInfo += "构建成组区块链\n";
		ClearSuperBlock();
		System.out.println("超级块初始化");
		openInfo += "超级块初始化\n";
		CreateFirstInode();
		System.out.println("创建测试用文件");
		openInfo += "创建测试用文件\n";
		
		disk.doLoad();
		memory.Initialize(disk);
		
	    RunInfo = "";
	    DeviceInfo = "";
	}
	
	// 创建磁盘文件的函数
 	public static void CreateInitFiles()
	{
		String Root = System.getProperty("user.dir");
		System.out.println("Root: " + Root);
		String diskPath = Root + File.separator + DiskStr;
		File diskFile = new File(diskPath);
		if(!diskFile.exists())
		{
			diskFile.mkdir();
			System.out.println(diskPath + "创建成功");
		}
		for(int i=0;i<barecomputer.Disk.CYLINDER_NUM;++i)
		{
			String cylinderPath = diskPath + File.separator + Cylinder + i;
			File cylinderFile = new File(cylinderPath);
			if(!cylinderFile.exists())
			{
				cylinderFile.mkdir();
				System.out.println(cylinderPath + "创建成功");
			}
			for(int j=0;j<barecomputer.Disk.TRACK_NUM;++j)
			{
				String trackPath = cylinderPath + File.separator + Track + String.format("%2d", j);
				File trackFile = new File(trackPath);
				if(!trackFile.exists())
				{
					trackFile.mkdir();
					System.out.println(trackPath + "创建成功");
				}
				for(int k=0;k<barecomputer.Disk.SECTOR_NUM;++k)
				{
					String sectorPath = trackPath + File.separator + Sector + String.format("%2d", k) + ".txt";
					File sectorFile = new File(sectorPath);
					if(!sectorFile.isFile())
					{
						try {
							sectorFile.createNewFile();
							System.out.println(sectorPath + "创建成功");
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
		}
	}
 	// 磁盘文件内容全部设置为0000
 	public static void ClearDisk()
 	{
		String Root = System.getProperty("user.dir");
		System.out.println("Root: " + Root);
		String diskPath = Root + File.separator + DiskStr;
		File diskFile = new File(diskPath);
		if(!diskFile.exists())
		{
			diskFile.mkdir();
			System.out.println(diskPath + "创建成功");
		}
		for(int i=0;i<barecomputer.Disk.CYLINDER_NUM;++i)
		{
			String cylinderPath = diskPath + File.separator + Cylinder + i;
			File cylinderFile = new File(cylinderPath);
			if(!cylinderFile.exists())
			{
				cylinderFile.mkdir();
				System.out.println(cylinderPath + "创建成功");
			}
			for(int j=0;j<barecomputer.Disk.TRACK_NUM;++j)
			{
				String trackPath = cylinderPath + File.separator + Track + String.format("%2d", j);
				File trackFile = new File(trackPath);
				if(!trackFile.exists())
				{
					trackFile.mkdir();
					System.out.println(trackPath + "创建成功");
				}
				for(int k=0;k<barecomputer.Disk.SECTOR_NUM;++k)
				{
					String sectorPath = trackPath + File.separator + Sector + String.format("%2d", k) + ".txt";
					File sectorFile = new File(sectorPath);
					if(sectorFile.isFile())
					{
						try {
							FileWriter fileWriterA = new FileWriter(sectorFile);
							fileWriterA.write("");
							fileWriterA.close();
							FileWriter fileWriterB = new FileWriter(sectorFile, true);
							for(int r=0; r<Block.MAX_SIZE; ++r)
								fileWriterB.write("0000\n");
							fileWriterB.close();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
			System.out.println((i+1) + "/10");
		}
 	}
 	// 构建格式化后磁盘中文件区的成组链接表（写入磁盘）
 	public static void ClearFree()
 	{
 		String Root = System.getProperty("user.dir");
		System.out.println("Root: " + Root);
		String diskPath = Root + File.separator + DiskStr;
		System.out.println("为格式化后的磁盘实现成组链接表...");
		int groupNum = 0;
		for(int i=1;i<barecomputer.Disk.CYLINDER_NUM;++i)
		{
			// 每个柱面
			String cylinderPath = diskPath + File.separator + Cylinder + i;
			for(int j=0;j<barecomputer.Disk.TRACK_NUM;++j)
			{
				// 跳过头两个物理块
				if(i==0 && j==0)
					continue;
				else if(i==0 && j==1)
					continue;
				else if(i==9 && j==31)
					break;
				// 每个磁道
				try {
					String trackPath = cylinderPath + File.separator + Track + String.format("%2d", j);
					String sectorPath = trackPath + File.separator + Sector + String.format("%2d", 63) + ".txt";
					System.out.println(sectorPath);
					File file = new File(sectorPath);
					FileWriter fWriterA = new FileWriter(file);
					fWriterA.write("");
					fWriterA.flush();
					fWriterA.close();
					FileWriter fWriterB = new FileWriter(file, true);
					int sectorIndex = 63;
					for(int index=0; index<Block.MAX_SIZE; ++index)
					{
						if(index < FreeBlocksGroupsLink.SAVE_START_AT_DISK_BLOCK)
						{
							fWriterB.write("0000\n");
						}
						else if(index == FreeBlocksGroupsLink.SAVE_START_AT_DISK_BLOCK)
						{
							if(i==9 && j==30)
								fWriterB.write(Block.INT_TO_HEX(FreeBlocksGroupsLink.MAX_BLOCK_NUM_IN_GROUP-1) + "\n");
							else
								fWriterB.write(Block.INT_TO_HEX(FreeBlocksGroupsLink.MAX_BLOCK_NUM_IN_GROUP) + "\n");
						}
						else
						{
							int blockNumber = Disk.BlockTransform_Location_To_Index(i, j, sectorIndex) + 64;
							fWriterB.write(Block.INT_TO_HEX(blockNumber) + "\n");
							sectorIndex--;
						}
					}
					fWriterB.flush();
					fWriterB.close();
					groupNum++;
					if(groupNum%32 == 0)
						System.out.println("已创建" + groupNum + "组");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		System.out.println("共创建" + groupNum + "组");
 	}
 	// 构造第一次开机时超级块中的文件内容（本地TXT）
 	public static void ClearSuperBlock()
 	{
 		Block block = new Block(false, 1);
 		// 内存空物理块为48
 		block.setDataAtIndex(0, Block.INT_TO_HEX(36));
 		// 内存物理块位示图
 		block.setDataAtIndex(1, "FFFF");
 		block.setDataAtIndex(2, "0000");
 		block.setDataAtIndex(3, "0000");
 		block.setDataAtIndex(4, "0FFF");
 		// 内存中inode空闲数
 		block.setDataAtIndex(5, "0010");
 		// 内存中Inode位示图
 		block.setDataAtIndex(6, "0000");
 		// 硬盘中inode空闲数
 		block.setDataAtIndex(7, "0EC0");
 		// 硬盘中inode位示图
 		block.setDataAtIndex(8, "0000");
 		for(int i=9;i<118;++i)
 	 		block.setDataAtIndex(i, "0000");
 		// 第一组空闲区块链
 		block.setDataAtIndex(FreeBlocksGroupsLink.SAVE_START_AT_DISK_BLOCK, Block.INT_TO_HEX(60));
 		for(int i=0;i<60;++i)
 	 		block.setDataAtIndex(192+i, Block.INT_TO_HEX(2111-i));
 		block.setDataAtIndex(252, "0000");
 		block.setDataAtIndex(253, "0000");
 		block.setDataAtIndex(254, "0000");
 		block.setDataAtIndex(255, "0000");
 		block.showBlock();
 		block.writeBack();
 		System.out.println("原厂超级块写回本地TXT中");
 	}
 	// 构造开机时引导块中的文件内容（本地TXT）
 	public static void ClearGuideBlock(Disk disk)
 	{
 		Block block = new Block(false, 0);
 		// 作业数目
 		block.setDataAtIndex(0, "0003");
 		// 第一个作业
 		block.setDataAtIndex(1, "000A");
 		block.setDataAtIndex(2, "3C28");
 		block.setDataAtIndex(3, "321E");
 		block.setDataAtIndex(4, "1314");
 		block.setDataAtIndex(5, "0001");
 		block.setDataAtIndex(6, "5210");
 		block.setDataAtIndex(7, "221C");
 		block.setDataAtIndex(8, "008C");
 		block.setDataAtIndex(9, "0010");
 		block.setDataAtIndex(10, "0009");
 		// 第二个作业
 		block.setDataAtIndex(11, "000F");
 		block.setDataAtIndex(12, "3C28");
 		block.setDataAtIndex(13, "321E");
 		block.setDataAtIndex(14, "1314");
 		block.setDataAtIndex(15, "0002");
 		block.setDataAtIndex(16, "5210");
 		block.setDataAtIndex(17, "221C");
 		block.setDataAtIndex(18, "008D");
 		block.setDataAtIndex(19, "0003");
 		block.setDataAtIndex(20, "0004");
 		// 第三个作业
 		block.setDataAtIndex(21, "0022");
 		block.setDataAtIndex(22, "3C28");
 		block.setDataAtIndex(23, "321E");
 		block.setDataAtIndex(24, "1314");
 		block.setDataAtIndex(25, "0003");
 		block.setDataAtIndex(26, "5210");
 		block.setDataAtIndex(27, "221C");
 		block.setDataAtIndex(28, "008E");
 		block.setDataAtIndex(29, "0015");
 		block.setDataAtIndex(30, "0007");
 		block.showBlock();
 		block.writeBack();
 		System.out.println("作业引导块写回本地TXT中");
 	}
 	// 构造初始目录Inode以及其存储块
 	public static void CreateFirstInode()
 	{
 		/**
 		 * 构造第3块（2号块），即第一个inode块内容
 		 * 共4个inode
 		 * 主目录：操作系统课设01
 		 * 操作系统课设01
 		 * 		- 操作系统课设报告
 		 * 			- OSCD.txt
 		 * 		- 马树凡0.txt
 		 * */
 		Block block = new Block(false, 2);
 		// 第一个Inode内容
 		block.setDataAtIndex(0, "0000");
 		block.setDataAtIndex(1, "0001");
 		block.setDataAtIndex(2, "0001");
 		block.setDataAtIndex(3, "0001");
 		block.setDataAtIndex(4, "0001");
 		block.setDataAtIndex(5, "07E4");
 		block.setDataAtIndex(6, "000C");
 		block.setDataAtIndex(7, "000E");
 		block.setDataAtIndex(8, "0006");
 		block.setDataAtIndex(9, "001E");
 		block.setDataAtIndex(10, "07E5");
 		block.setDataAtIndex(11, "0002");
 		block.setDataAtIndex(12, "000E");
 		block.setDataAtIndex(13, "0013");
 		block.setDataAtIndex(14, "0014");
 		block.setDataAtIndex(15, "0000");
 		block.setDataAtIndex(16, "0000");
 		block.setDataAtIndex(17, "0001");
 		block.setDataAtIndex(18, "0804");
 		for(int i=19; i<28; ++i)
 			block.setDataAtIndex(i, "0000");
 		block.setDataAtIndex(28, "7071");
 		block.setDataAtIndex(29, "7273");
 		block.setDataAtIndex(30, "7989");
 		block.setDataAtIndex(31, "7A84");
 		// 第二个Inode内容
 		for(int i=32;i<37;++i)
 			block.setDataAtIndex(i, "0001");
 		block.setDataAtIndex(37, "07E4");
 		block.setDataAtIndex(38, "000C");
 		block.setDataAtIndex(39, "0019");
 		block.setDataAtIndex(40, "0008");
 		block.setDataAtIndex(41, "000A");
 		block.setDataAtIndex(42, "07E5");
 		block.setDataAtIndex(43, "0002");
 		block.setDataAtIndex(44, "0011");
 		block.setDataAtIndex(45, "0015");
 		block.setDataAtIndex(46, "0020");
 		block.setDataAtIndex(47, "0000");
 		block.setDataAtIndex(48, "0000");
 		block.setDataAtIndex(49, "0001");
 		block.setDataAtIndex(50, "0805");
 		for(int i=51;i<60;++i)
 			block.setDataAtIndex(i, "0000");
 		block.setDataAtIndex(60, "7071");
 		block.setDataAtIndex(61, "7273");
 		block.setDataAtIndex(62, "797A");
 		block.setDataAtIndex(63, "7B7C");
 		// 第三个Inode内容
 		block.setDataAtIndex(64, "0002");
 		block.setDataAtIndex(65, "0001");
 		block.setDataAtIndex(66, "0001");
 		block.setDataAtIndex(67, "0001");
 		block.setDataAtIndex(68, "0001");
 		block.setDataAtIndex(69, "07E5");
 		block.setDataAtIndex(70, "0001");
 		block.setDataAtIndex(71, "0001");
 		block.setDataAtIndex(72, "000C");
 		block.setDataAtIndex(73, "002B");
 		block.setDataAtIndex(74, "07E5");
 		block.setDataAtIndex(75, "0003");
 		block.setDataAtIndex(76, "0001");
 		block.setDataAtIndex(77, "0011");
 		block.setDataAtIndex(78, "0026");
 		block.setDataAtIndex(79, "0000");
 		block.setDataAtIndex(80, "0000");
 		block.setDataAtIndex(81, "0001");
 		block.setDataAtIndex(82, "0806");
 		block.setDataAtIndex(83, "0000");
 		for(int i=84;i<92;++i)
 			block.setDataAtIndex(i, "0000");
 		block.setDataAtIndex(92, "8F90");
 		block.setDataAtIndex(93, "7A86");
 		block.setDataAtIndex(94, "8788");
 		block.setDataAtIndex(95, "898A");
 		// 第四个inode内容
 		block.setDataAtIndex(96, "0003");
 		block.setDataAtIndex(97, "0002");
 		block.setDataAtIndex(98, "0001");
 		block.setDataAtIndex(99, "0001");
 		block.setDataAtIndex(100, "0001");
 		block.setDataAtIndex(101, "07E5");
 		block.setDataAtIndex(102, "0002");
 		block.setDataAtIndex(103, "0011");
 		block.setDataAtIndex(104, "0008");
 		block.setDataAtIndex(105, "001E");
 		block.setDataAtIndex(106, "07E5");
 		block.setDataAtIndex(107, "0003");
 		block.setDataAtIndex(108, "0007");
 		block.setDataAtIndex(109, "0016");
 		block.setDataAtIndex(110, "002F");
 		block.setDataAtIndex(111, "0000");
 		block.setDataAtIndex(112, "0000");
 		block.setDataAtIndex(113, "0005");
 		block.setDataAtIndex(114, "0807");
 		block.setDataAtIndex(115, "0808");
 		block.setDataAtIndex(116, "0809");
 		block.setDataAtIndex(117, "080A");
 		block.setDataAtIndex(118, "080B");
 		for(int i=119;i<124;++i)
 			block.setDataAtIndex(i, "0000");
 		block.setDataAtIndex(124, "0109");
 		block.setDataAtIndex(125, "0201");
 		block.setDataAtIndex(126, "0801");
 		block.setDataAtIndex(127, "0001");
 		// 设备驱动程序（4个Inode）
 		// 键盘
 		String[] dev_0 = {"0004", "0003", "0001", "0001", "0001", 
 				"07E5", "0003", "0002", "000E", "001E", "07E5", "0003", "001D", "000A", "000B",
 				"0000", "0000", "0001", "080C",
 				"3314", "2518", "1214", "6B00"};
 		for(int i=0;i<19;++i)
 			block.setDataAtIndex(128+i, dev_0[i]);
 		for(int i=19,j=0;i<23;++i,++j)
 			block.setDataAtIndex(156+j, dev_0[i]);
 		// 屏幕
 		String[] dev_1 = {"0005", "0003", "0001", "0001", "0001", 
 				"07E5", "0003", "0002", "000E", "001E", "07E5", "0003", "001D", "000A", "000B",
 				"0000", "0000", "0001", "080D",
 				"3314", "2518", "1214", "6B01"};
 		for(int i=0;i<19;++i)
 			block.setDataAtIndex(160+i, dev_1[i]);
 		for(int i=19,j=0;i<23;++i,++j)
 			block.setDataAtIndex(188+j, dev_1[i]);
 		// 打印机A
 		String[] dev_2 = {"0006", "0003", "0001", "0001", "0001", 
 				"07E5", "0003", "0002", "000E", "001E", "07E5", "0003", "001D", "000A", "000B",
 				"0000", "0000", "0001", "080E",
 				"3314", "2518", "1214", "6B02"};
 		for(int i=0;i<19;++i)
 			block.setDataAtIndex(192+i, dev_2[i]);
 		for(int i=19,j=0;i<23;++i,++j)
 			block.setDataAtIndex(220+j, dev_2[i]);
 		// 打印机B
 		String[] dev_3 = {"0007", "0003", "0001", "0001", "0001", 
 				"07E5", "0003", "0002", "000E", "001E", "07E5", "0003", "001D", "000A", "000B",
 				"0000", "0000", "0001", "080F",
 				"3314", "2518", "1214", "6B03"};
 		for(int i=0;i<19;++i)
 			block.setDataAtIndex(224+i, dev_3[i]);
 		for(int i=19,j=0;i<23;++i,++j)
 			block.setDataAtIndex(252+j, dev_3[i]);
 		
 		block.showBlock();
 		block.writeBack();
 		
 		// 下一块中写入Inode
 		Block block_3 = new Block(false, 3);
 		// 设备驱动程序（2个Inode）
 		// 打印机C
 		String[] dev_4 = {"0008", "0003", "0001", "0001", "0001", 
 				"07E5", "0003", "0002", "000E", "001E", "07E5", "0003", "001D", "000A", "000B",
 				"0000", "0000", "0001", "0810",
 				"3314", "2518", "1214", "6B04"};
 		for(int i=0;i<19;++i)
 			block_3.setDataAtIndex(0+i, dev_4[i]);
 		for(int i=19,j=0;i<23;++i,++j)
 			block_3.setDataAtIndex(28+j, dev_4[i]);
 		// 摄像机
 		String[] dev_5 = {"0009", "0003", "0001", "0001", "0001", 
 				"07E5", "0003", "0002", "000E", "001E", "07E5", "0003", "001D", "000A", "000B",
 				"0000", "0000", "0001", "0811",
 				"3314", "2518", "1214", "6B05"};
 		for(int i=0;i<19;++i)
 			block_3.setDataAtIndex(32+i, dev_5[i]);
 		for(int i=19,j=0;i<23;++i,++j)
 			block_3.setDataAtIndex(60+j, dev_5[i]);
 		// 操作系统测试文件.dir
 		String[] inode0 = {"000A", "0001", "0001", "0001", "0001", 
 				"07E5", "0003", "0002", "000E", "001E", "07E5", "0003", "001D", "000A", "000B",
 				"0000", "0000", "0001", "0812",
 				"7071", "7273", "8B8C", "8D8E"};
 		for(int i=0;i<19;++i)
 			block_3.setDataAtIndex(64+i, inode0[i]);
 		for(int i=19,j=0;i<23;++i,++j)
 			block_3.setDataAtIndex(92+j, inode0[i]);
 		// OStest_0.txt
 		String[] txt0 = {"000B", "0002", "0001", "0001", "0001", 
 				"07E5", "0003", "0002", "000E", "001E", "07E5", "0003", "001D", "000A", "000B",
 				"0000", "0000", "0001", "0813",
 				"3E42", "2314", "2223", "6B00"};
 		for(int i=0;i<19;++i)
 			block_3.setDataAtIndex(96+i, txt0[i]);
 		for(int i=19,j=0;i<23;++i,++j)
 			block_3.setDataAtIndex(124+j, txt0[i]);
 		// OStest_1.txt
 		String[] txt1 = {"000C", "0002", "0001", "0001", "0001", 
 				"07E5", "0003", "0002", "000E", "001E", "07E5", "0003", "001D", "000A", "000B",
 				"0000", "0000", "0001", "0814",
 				"3E42", "2314", "2223", "6B01"};
 		for(int i=0;i<19;++i)
 			block_3.setDataAtIndex(128+i, txt1[i]);
 		for(int i=19,j=0;i<23;++i,++j)
 			block_3.setDataAtIndex(156+j, txt1[i]);
 		
 		block_3.showBlock();
 		block_3.writeBack();
 		System.out.println("磁盘中写入Inode成功");
 		/**
 		 * 同时修改超级块中的内容
 		 * 这里在ClearSuperBlock函数基础上修改
 		 * */
 		Block sblock = new Block(false, 1);
 		// 内存空物理块为36
 		sblock.setDataAtIndex(0, Block.INT_TO_HEX(36));
 		// 内存物理块位示图
 		sblock.setDataAtIndex(1, "FFFF");
 		sblock.setDataAtIndex(2, "0000");
 		sblock.setDataAtIndex(3, "0000");
 		sblock.setDataAtIndex(4, "0FFF");
 		// 内存中inode空闲数
 		sblock.setDataAtIndex(5, "0010");
 		// 内存中Inode位示图
 		sblock.setDataAtIndex(6, "0000");
 		// 硬盘中inode空闲数
 		sblock.setDataAtIndex(7, "03A3");
 		// 硬盘中inode位示图
 		sblock.setDataAtIndex(8, "FFF8");
 		for(int i=9;i<118;++i)
 	 		sblock.setDataAtIndex(i, "0000");
 		// 第一组空闲区块链
 		sblock.setDataAtIndex(FreeBlocksGroupsLink.SAVE_START_AT_DISK_BLOCK, Block.INT_TO_HEX(43));
 		for(int i=0;i<43;++i)
 	 		sblock.setDataAtIndex(192+i, Block.INT_TO_HEX(2111-i));
 		sblock.showBlock();
 		sblock.writeBack();
 		System.out.println("超级块中相应内容修改成功");
 		/**
 		 * 同时修改对应目录/文件块中的内容
 		 * */
 		// 操作系统课程设计
 		Block block1 = new Block(false, 2052);
 		block1.setDataAtIndex(0, "0003");
 		block1.setDataAtIndex(1, "0001");
 		block1.setDataAtIndex(2, "0002");
 		block1.setDataAtIndex(3, "000A");
 		block1.writeBack();
 		// 操作系统课程报告
 		Block block2 = new Block(false, 2053);
 		block2.setDataAtIndex(0, "0001");
 		block2.setDataAtIndex(1, "0003");
 		block2.writeBack();
 		// 外部设备驱动程序
 		Block block3 = new Block(false, 2054);
 		block3.setDataAtIndex(0, "0006");
 		block3.setDataAtIndex(1, "0004");
 		block3.setDataAtIndex(2, "0005");
 		block3.setDataAtIndex(3, "0006");
 		block3.setDataAtIndex(4, "0007");
 		block3.setDataAtIndex(5, "0008");
 		block3.setDataAtIndex(6, "0009");
 		block3.writeBack();
 		// 操作系统测试文件
 		Block block4 = new Block(false, 2066);
 		block4.setDataAtIndex(0, "0002");
 		block4.setDataAtIndex(1, "000B");
 		block4.setDataAtIndex(2, "000C");
 		block4.writeBack();
 		System.out.println("目录及文件详细内容创建成功");
 	}
 	public void NULL()
 	{
 		
 	}
 	
 	// main
 	public static void main(String[] args) throws IOException
	{
 		// 设置信息输出到指定文件
 		String Root = System.getProperty("user.dir");
 		String outPath = Root + File.separator + "output" + File.separator + "ProcessResults.txt";
 		File f=new File(outPath);
        f.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);
 		
        Computer computer = new Computer();
        computer.NULL();
//        Computer.rebuild(false);
        Computer.firstGUI.start();
        Computer.mainGUI.start();
        Computer.Initlize();
        Computer.firstGUI.yes.setEnabled(true);
        // GUI
        Computer.memoryGUI.start();
        Computer.diskGUI.start();
        Computer.deviceGUI.start();
        Computer.jobGUI.start();
        Computer.processGUI.start();
        Computer.fileGUI.start();
        // computer
        Computer.clock.start();
        Computer.cpu.start();
        Computer.blockWorkThread.start();
        Computer.blockAwakeThread.start();
	}
}


