import sys
import os
from apscheduler.schedulers.blocking import BlockingScheduler
import psycopg2 as pg
import pandas as pd
import configparser
import re
from sqlalchemy import create_engine
import cx_Oracle
import time
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
TargetCnnDict = {};
sqlStringList = [];
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
def readConfig(configPath):
    data = pd.read_excel(configPath);
    col = ['配置', '表名', '表说明'];
    data[col] = data[col].ffill();
    return data;
# 建立数据库连接
def createTargetCnn(mapValue,databaseType):
    tempPort = mapValue['port'];
    mapValue.setdefault('port',tempPort);
    if(databaseType=='oracle'):
        dsn = cx_Oracle.makedsn(mapValue['host'], str(mapValue['port']), mapValue['database'])
        TargetCnn = cx_Oracle.connect(mapValue['user'], mapValue['password'], dsn);
    else:
        TargetCnn = pg.connect(**mapValue);
    TargetCursor = TargetCnn.cursor();
    return (TargetCursor, TargetCnn);
#检查数据库连接是否存在
def checkCnn(cnnKey):
    keyList = TargetCnnDict.keys();
    if cnnKey in keyList:
        return True;
    return False;
#更新TargetCnnDict
def updateTargetCnn(mapValueList,databaseTypeList):
    for i in range(0,len(mapValueList)):
        mapValue = mapValueList[i];
        cnnKey = creatCnnKey(mapValue);
        isLive = checkCnn(cnnKey);
        if isLive==True:
            continue;
        (TargetCursor, TargetCnn) = createTargetCnn(mapValue,databaseTypeList[i]);
        TargetCnnDict.setdefault(cnnKey,(TargetCursor, TargetCnn));#添加数据库连接
#创建单张表的sql
def creatSql(filedName,filedinfo,isLastFiled,tableName):
    tempValue = list(filedinfo['字段默认值'])[0];
    tempType = list(filedinfo['类型'])[0];
    tempLen = list(filedinfo['长度'])[0];
    tempIsNull = list(filedinfo['是否为空'])[0];
    tempMajorKey = list(filedinfo['主键'])[0];
    tempIndex = list(filedinfo['索引'])[0];
    string = filedName+' '+tempType+' ';
    stringIndex = None;
    if str(tempLen)!='nan':
        if type(tempLen)==float:
            tempLen = int(tempLen);
        string = string+'('+str(tempLen)+')';
    if str(tempMajorKey)!='nan':
        string = string+'PRIMARY KEY'
    else:
        if str(tempValue)!='nan':
            string = string+"DEFAULT "+ str(tempValue);
        else:
            if tempIsNull=='是':
                string = string+' NULL';
            elif tempIsNull=='否':
                string = string+' NOT NULL';
    if isLastFiled == False:
        string = string + ',';
    if str(tempIndex)!='nan':
        stringIndex = "create INDEX "+tableName+'_'+filedName+'_index'+' ON '+tableName+'('+filedName+')'+";";
    return (string,stringIndex);
def createTableSignal(infoTable):
    tablename = list(infoTable['表名'])[0];
    tableExplain = list(infoTable['表说明'])[0];
    stringIndex = [];
    string = "CREATE TABLE IF NOT EXISTS public." + tablename + '(';
    filedList = list(infoTable['字段名']);
    filedExplainList = list(infoTable['字段说明']);
    filedSpExplainList = list(infoTable['字段特殊说明']);
    filedinfo = infoTable[['字段名','字段默认值','类型','长度','是否为空','主键','索引']];
    for i in range(0,len(filedList)):
        tempFiledInfo = filedinfo[filedinfo.字段名==filedList[i]];
        if i==len(filedList)-1:
            (tempString,tempStringIndex) = creatSql(filedList[i],tempFiledInfo,True,tablename);
            if tempStringIndex != None:
                stringIndex.append(tempStringIndex);
            string = string+tempString+')';
            break;
        (tempString, tempStringIndex) = creatSql(filedList[i], tempFiledInfo, False, tablename);
        if tempStringIndex!=None:
           stringIndex.append(tempStringIndex);
        string = string+tempString;
    tempstr = "WITH (OIDS=FALSE);ALTER TABLE public." + tablename + " OWNER TO postgres;";
    string = string + tempstr;
    for i in range(0,len(stringIndex)):
        string = string + stringIndex[i];
    tempstr = "COMMENT ON TABLE public." + tablename + " IS '" + tableExplain + "';";
    string = string+tempstr;
    for i in range(0, len(filedList)):
        if str(filedSpExplainList[i]) != 'nan':
            tempstr = "COMMENT ON COLUMN public." + tablename + "." + filedList[i] + " IS '" + filedExplainList[i] + '|' + filedSpExplainList[i] + "';";
        else:
            tempstr = "COMMENT ON COLUMN public." + tablename + "." + filedList[i] + " IS '" + filedExplainList[i] + "';";
        string = string + tempstr;
    return string;
#根据配置文件自动化建表
def autoCreateTableStatement(data):
    dataSource = list(set(list(data['配置'])));
    mapValueList = [];
    databaseTypeList = [];
    for i in range(0,len(dataSource)):
        tempData = data[data.配置==dataSource[i]];
        (mapValue,databaseType)= creatMapValue(dataSource[i]);
        mapValueList.append(mapValue);
        databaseTypeList.append(databaseType);
        tempTableNameList = list(set(list(tempData['表名'])));
        tempCnnKey = creatCnnKey(mapValue);
        for j in range(0,len(tempTableNameList)):
            tempDataNew = tempData[tempData.表名==tempTableNameList[j]];
            sqlString = createTableSignal(tempDataNew);
            sqlStringList.append((tempCnnKey,sqlString));
    updateTargetCnn(mapValueList, databaseTypeList)
    return ;
#建表主函数
if __name__ == '__main__':
    configPath = r"D:\inf\createtable\datacreateTable4.xlsx";
    #configPath = r"D:\inf\createTable.xlsx";
    #configPath = r"D:\tjdata\tjcreatetable\datacreateTable.xlsx";
    data = readConfig(configPath);
    autoCreateTableStatement(data);
    for sqlString in sqlStringList:
         tempSqlString = sqlString[1];
         tempCnnKey = sqlString[0];
         (TargetCursor, TargetCnn) = TargetCnnDict[tempCnnKey];
         TargetCursor.execute(tempSqlString);
         TargetCnn.commit();
