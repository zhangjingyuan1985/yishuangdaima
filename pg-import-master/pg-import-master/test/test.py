import configparser
import time
import datetime
conf = configparser.ConfigParser();
conf.read("config.ini")
#print(dict(conf.items('a')));
a = (1,2,3,4,5)
b = a[1];
res = {'aaa':"ddd"};
#print(b);
#conf.add_section("d");
#tablename = 'a'
#conf.set(tablename,'fdate',str(20190901))
#conf.write(open("config.ini","w"))
#currentTime = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))
#print(conf.get('d','name'))
print(int(str(datetime.datetime.now().today()).split(' ')[0].replace('-','')));
#print("数据导入出错"+str(currentTime))
