# Design12306
try to design the structure of 12306.cn

MySQL部分已经完成，云数据库的地址在代码中，说明在pdf文件内。</br>
MongoDB部分已经完成，云数据库的地址在代码中，说明在pdf文件内。</br>
如果您用云数据库测试，希望在网络延迟上多多包涵</br>
再从markdown导出PDF的时候代码可能会无法显示，您可以选择去看markdown文件</br>

## 代码指引

init中为数据库创建数据的操作，initMongoDB用于mongodb，initMySQL和CreateTable用于MySQL的创建。然后MySQL的建表SQL在mysql.sql中。

impl中为方法的具体实现

connector为数据库driver的连接

service中是接口和方便输出的类

test中按照MySQL、MongoDB分为了两个测试文件夹，里面分别是相关方法的测试。
</br>eclipse中运行testng需要安装插件，具体请搜索testng
