import sys
import os
import threading
from apscheduler.schedulers.blocking import BlockingScheduler
import psycopg2 as pg
import pandas as pd
import configparser
import re
from sqlalchemy import create_engine
import cx_Oracle
import time
import schedule
class Config(object):
    def __init__(self,conf,path):
        self.conf = conf;
        self.conf.read(path);
    def add_value(self,key,value):
        self.conf.set(key,value[0],value[1]);
        return ;
    def change_value(self,key,value):
        self.conf.set(key, value[0], value[1]);
        return ;
    def remove_value(self,key,value):
        self.conf.remove_option(key,value[0]);
        return ;
    def getmap_value(self,key):
        re_int = '^\d+$';
        re_float = '^\d+\.\d+$';

        map_value = dict(self.conf.items(key));
        value = list(map_value.values());
        key = list(map_value.keys());
        for i in range(0,len(value)):
            if value[i] =='True':
                value[i] = True;
            elif value[i] =='False':
                 value[i] = False;
            elif re.match(re_int,value[i])!=None:
                 value[i] = int(value[i]);
            elif re.match(re_float,value[i])!=None:
                 value[i] = float(value[i]);
            else:
                pass;
        map_value = dict(zip(key,value));
        return map_value;
    def check_key(self,key):
        key_value = self.conf.sections();
        if key not in key_value:
            return False;
        return True;


targetCnnDict = {};
enginDict = {};
def creatCnnKey(mapValue):
    cnnKey = "key_"+str(mapValue['host'])+str(mapValue['port'])+mapValue['database'];
    return cnnKey;
def creatMapValue(cnnString):
    cnnList = cnnString.split(',');
    mapValue = {};
    for ls in cnnList:
        tempKeyValue = ls.split('=');
        if tempKeyValue[0]=='type':
            databaseType = tempKeyValue[1];
            continue;
        mapValue.setdefault(tempKeyValue[0],tempKeyValue[1]);
    return (mapValue,databaseType);
def creatSpIndex(mapValueList,tableNameList):
    spIndexList = [];
    for i in range(0,len(mapValueList)):
        tempMapValue = mapValueList[i];
        tempTableName = tableNameList[i];
        spIndex = creatCnnKey(tempMapValue)+tempTableName;
        spIndexList.append(spIndex);
    return spIndexList;
# 建立数据库连接
def createTargetCnn(mapValue,databaseType):
    if(databaseType=='oracle'):
        dsn = cx_Oracle.makedsn(mapValue['host'], str(mapValue['port']), mapValue['database'])
        TargetCnn = cx_Oracle.connect(mapValue['user'], mapValue['password'], dsn);
    else:
        TargetCnn = pg.connect(**mapValue);
    TargetCursor = TargetCnn.cursor();
    return (TargetCursor, TargetCnn);
#建立数据库引擎(默认只支持orical、postgres)
def createEngin(mapValue,databaseType):
    if databaseType=='oracle':
        string = "oracle+cx_oracle://"+str(mapValue['user'])+":"+str(mapValue["password"])+"@"+str(mapValue['host'])+":"+str(mapValue["port"])+"/"+str(mapValue['database']);
        engin = create_engine(string,echo=False);
    else:
        string = "postgresql://"+str(mapValue['user'])+":"+str(mapValue["password"])+"@"+str(mapValue['host'])+":"+str(mapValue["port"])+"/"+str(mapValue['database']);
        engin = create_engine(string);
    return engin;
# 检查数据库连接是否存在，如果已存在则不用建立新连接
def checkCnn(cnnKey):
    keyList = targetCnnDict.keys();
    if cnnKey in keyList:
        return True;
    return False;
#检查数据库存储引擎是否存在
def checkEngin(enginKey):
    keyList = enginDict.keys();
    if enginKey in keyList:
        return True;
    return False;
# 导入数据库
def loadData(data, engin, tableName):
    data.to_sql(name=tableName, con=engin, if_exists="append", index=False);
# 数据转换
def transFormData(data, rules):
    newData = pd.DataFrame();
    (lineValue,rankvalue) = data.shape;
    sourceFiledList = list(rules['字段名称(源)']);
    filedList = list(rules['目标字段名称']);
    defaultValueList = list(rules['默认值']);
    #isNullList = list(rules['是否为空']);
    tempdict = list(zip(sourceFiledList,defaultValueList));
    filedDict = dict(zip(filedList,tempdict));
    keys = filedDict.keys();
    for key in keys:
        [tempfiled,tempvalue] = filedDict[key];
        if str(tempvalue)=='nan':
            if str(tempfiled)=='nan':
                newData[key] = None;
            else:
                newData[key] = data[tempfiled].fillna(-99);
        else:
            newData[key] = [tempvalue]*lineValue;
    return newData;
#更新TargetCnnDict
def updateTargetCnn(mapValueList,databaseTypeList):
    for i in range(0,len(mapValueList)):
        mapValue = mapValueList[i];
        cnnKey = "key_"+str(mapValue['host'])+str(mapValue['port'])+mapValue['database'];
        isLive = checkCnn(cnnKey);
        if isLive==True:
            continue;
        (TargetCursor, TargetCnn) = createTargetCnn(mapValue,databaseTypeList[i]);
        targetCnnDict.setdefault(cnnKey, (TargetCursor, TargetCnn));#添加数据库连接
#更新数据库引擎engine
def updateEngin(mapValueList,databaseTypeList):
    for i in range(0,len(mapValueList)):
        mapValue = mapValueList[i];
        enginKey = "key_"+str(mapValue['host'])+str(mapValue['port'])+mapValue['database'];
        isLive = checkEngin(enginKey);
        if isLive==True:
            continue;
        engin = createEngin(mapValue,databaseTypeList[i]);
        enginDict.setdefault(enginKey, engin);
# 得到文件大小
def getFileSize(filePath):
    fsize = os.path.getsize(filePath)
    fsize = fsize / float(1024 * 1024)
    return round(fsize, 2)

# 从本地导入文件到数据库
def loadDataFromLocal(dataPath, tableName, rules, engin):
    fileType = dataPath.split('.')[1];
    if fileType == "csv":
        fileSize = getFileSize(dataPath);
        if fileSize < 500:
            data = pd.read_csv(dataPath);
            newData = transFormData(data, rules);
            loadData(newData, engin, tableName);
        else:
            data = pd.read_csv(dataPath, chunksize=1000000);
            for tempData in data:
                tempNewData = transFormData(tempData, rules);
                loadData(tempNewData, engin, tableName);
    else:
       data = pd.read_excel(dataPath);
       newData = transFormData(data,rules);
       loadData(newData,engin,tableName);
    return;

#从源数据库中读取数据并导入
def readDataFromOriginDatabase(TargetCursor,sql,rules,engin,tableName):
    TargetCursor.execute(sql);
    cols = TargetCursor.description;
    colnum = [];
    for col in cols:
        colnum.append(col[0]);
    while True:
        data = TargetCursor.fetchmany(10000);
        if data == []:
            break;
        data = pd.DataFrame(data);
        data.columns = colnum;  # 默认以源数据库中的字段作为作为数据列名，并导入新数据库中
        newData = transFormData(data,rules);
        loadData(newData, engin, tableName);
    print(tableName+"完成")
def readConfig(configPath):
    data = pd.read_excel(configPath);
    col = ['存储方式(源)','配置(源)','表名(源)','配置','目标表','导入频率','sql语句'];
    data[col] = data[col].ffill();
    return data;
def initInfo(configPath):
    data = readConfig(configPath);
    mapValueList = [];
    databaseTypeList = [];
    targetTableList = list(data['目标表']);
    targetDatabaseConfigList = list(data['配置']);
    tempData = data[data['存储方式(源)'] == 'database'];
    sourceDataConfigList = list(tempData['配置(源)']);

    for i in range(0, len(targetDatabaseConfigList)):
        (tempMapValue, tempDatabaseType) = creatMapValue(targetDatabaseConfigList[i]);
        mapValueList.append(tempMapValue);
        databaseTypeList.append(tempDatabaseType);
    updateEngin(mapValueList, databaseTypeList);
    data['标记值'] = creatSpIndex(mapValueList,targetTableList);
    mapValueList = [];
    databaseTypeList = [];
    for i in range(0, len(sourceDataConfigList)):
        (tempMapValue, tempDatabaseType) = creatMapValue(sourceDataConfigList[i]);
        mapValueList.append(tempMapValue);
        databaseTypeList.append(tempDatabaseType);
    updateTargetCnn(mapValueList, databaseTypeList);

    return data;
def loadDataLocal(data):
    tempDataForExcelAndCsv = data[data['存储方式(源)']!='database'];
    indexKeyForExcelAndCsv = list(set(list(tempDataForExcelAndCsv['标记值'])));
    for i in range(0,len(indexKeyForExcelAndCsv)):
        tempData = tempDataForExcelAndCsv[tempDataForExcelAndCsv.标记值==indexKeyForExcelAndCsv[i]];
        dataPath = list(tempData['配置(源)'])[0];
        tableName = list(tempData['目标表'])[0];
        rules = tempData[['字段名称(源)','目标字段名称','默认值']];
        string = list(tempData['配置'])[0];
        (mapValue, databaseType) = creatMapValue(string);
        enginKey = creatCnnKey(mapValue);
        engin = enginDict[enginKey];
        loadDataFromLocal(dataPath,tableName,rules,engin);
def loadDataDatabase(data):
    #先做一次性导入
    tempDataForDatabase = data[data['存储方式(源)'] == 'database'];
    indexKeyForDatabase = list(set(list(tempDataForDatabase['标记值'])));
    for i in range(0,len(indexKeyForDatabase)):
        tempData = tempDataForDatabase[tempDataForDatabase['标记值'] == indexKeyForDatabase[i]];
        databaseConfig = list(tempData['配置(源)'])[0];
        tableName = list(tempData['目标表'])[0];
        sourceTableName = list(tempData['表名(源)'])[0];
        rules = tempData[['字段名称(源)', '目标字段名称', '默认值']];
        config = list(tempData['配置'])[0];
        sql = list(tempData['sql语句'])[0];
        if sql=="*":
            sql = 'select * from '+sourceTableName;
        (mapValueDatabase, databaseTypeDatabase) = creatMapValue(databaseConfig);
        (mapValue, databaseType) = creatMapValue(config);
        enginKey = creatCnnKey(mapValue);
        cnnKey = creatCnnKey(mapValueDatabase);
        (TargetCursor, TargetCnn) = targetCnnDict[cnnKey];
        engin = enginDict[enginKey];
        readDataFromOriginDatabase(TargetCursor, sql, rules, engin, tableName)
    return

#定时任务框架
def doWeekUpdate(data):
    #一周追加一次数据
    threading.Thread(target=loadDataDatabase(data));
    return ;
def doDayUpdate(data):
    threading.Thread(target=loadDataDatabase(data));
    return
def doFiveMinuteUpdate(data):
    threading.Thread(target=loadDataDatabase(data));
    return ;
def doHourUpdate(data):
    threading.Thread(target=loadDataDatabase(data));
    return
def doOnceUpdate(data):
    threading.Thread(target=loadDataDatabase(data));
    return

def run(data):
    dataForDay = data[data.导入频率=='1d'];
    dataForWeek = data[data.导入频率=='1w'];
    dataForHour = data[data.导入频率=='1h'];
    dataFor5Min = data[data.导入频率=='5m'];
    schedule.every().day.at("03:00").do(doDayUpdate,dataForDay);
    schedule.every().friday.at("05:00").do(doWeekUpdate,dataForWeek);
    schedule.every(5).minutes.do(doFiveMinuteUpdate,dataFor5Min);
    schedule.every().hour.do(doHourUpdate,dataForHour);
    while True:
        schedule.run_pending();
        time.sleep(1);
#根据
if __name__ == '__main__':
    configPath = r"D:\inf\m_data\newdata\loaddata\LoadData3.xlsx";
    data = initInfo(configPath);
    loadDataLocal(data);
    dataLoadForOnce = data[data.导入频率=='一次'];
    loadDataDatabase(dataLoadForOnce);
    #run(data);