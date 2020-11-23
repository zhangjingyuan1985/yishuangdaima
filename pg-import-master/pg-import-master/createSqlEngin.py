import psycopg2 as pg
from sqlalchemy import create_engine
import cx_Oracle
import pandas as pd
#数据库游标
TargetCnnDict = {};
#数据库引擎
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
    keyList = TargetCnnDict.keys();
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

#更新TargetCnnDict
def updateTargetCnn(mapValueList,databaseTypeList):
    for i in range(0,len(mapValueList)):
        mapValue = mapValueList[i];
        cnnKey = "key_"+str(mapValue['host'])+str(mapValue['port'])+mapValue['database'];
        isLive = checkCnn(cnnKey);
        if isLive==True:
            continue;
        (TargetCursor, TargetCnn) = createTargetCnn(mapValue,databaseTypeList[i]);
        TargetCnnDict.setdefault(cnnKey,(TargetCursor, TargetCnn));#添加数据库连接
#更新数据库引擎engine
def updateEngin(mapValueList,databaseTypeList):
    for i in range(0,len(mapValueList)):
        mapValue = mapValueList[i];
        enginKey = "key_"+str(mapValue['host'])+str(mapValue['port'])+mapValue['database'];
        isLive = checkEngin(enginKey);
        if isLive==True:
            continue;
        engin = createEngin(mapValue,databaseTypeList[i]);
        enginDict.setdefault(enginKey,engin);
#从源数据库中读取数据并导入
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
    updateTargetCnn(mapValueList,databaseTypeList);

    data['标记值'] = creatSpIndex(mapValueList,targetTableList);
    mapValueList = [];
    databaseTypeList = [];

    for i in range(0, len(sourceDataConfigList)):
        (tempMapValue, tempDatabaseType) = creatMapValue(sourceDataConfigList[i]);
        mapValueList.append(tempMapValue);
        databaseTypeList.append(tempDatabaseType);
    updateTargetCnn(mapValueList, databaseTypeList);
    return data;

