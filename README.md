# 操作系统课程设计
马树凡  
南京农业大学18级计算机科学与技术专业  
使用语言-Java  
## 系统设计要求
1. 仿真实现多作业并发运行环境、作业管理、内存管理、设备管理、 目录管理、文件系统调用、文件共享等 OS 内核功能，并提供可视化的人机交互界面
2. 裸机仿真设计基础要求
    - 内存：共 32KB，每个物理块大小 512B，共 64 个物理块
    - 地址线与数据线：物理地址为 16 位。数据存储的单位为双字节. 设计地 址编码长度需要符合要求。
    - 硬盘：10 个柱面，1 个柱面有 32 个磁道，1 个磁道中有 64 个扇区。可 以假设 1 个扇区为 1 个物理块，每个物理块大小 512B。
    - 硬件部件包括： CPU、时钟中断、内存、缺页中断、MMU、缓冲区、磁盘 交换区、磁盘等。
3. 作业管理是本次课设的基本内容
    - 至少用 5 个线程分别仿真作业 请求、进程调度、输入输出处理、缺页中断处理、磁盘文件操作。
4. 其他内容详见Report.pdf文件
## 其他
1. 测试示例在TEST.pdf中
2. jar目录下是可执行程序，需安装java SE 15
3. code目录下是源代码
## 问题
1. 死锁检测问题尚未实现
2. 磁盘模拟还需优化读写速度
3. 待补充