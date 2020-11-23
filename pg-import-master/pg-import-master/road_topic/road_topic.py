import pandas as pd
import createSqlEngin
import sendMail
import sys
import os
import threading
from apscheduler.schedulers.blocking import BlockingScheduler
import psycopg2 as pg
import pandas as pd
import configparser
from sqlalchemy import create_engine
import cx_Oracle
import schedule
import time
import datetime
#将每张表最新的标记字段写入配置文件
def writeConfig(tableName,firstTag,firstTagValue,secondTag,secondTagValue):
    #sig = 1表示新增配置文件key sig=0表示修改
    if firstTagValue==-1:
        return ;
    sig = 1;
    (conf, tableNameList) = readLocalConfig();
    if tableName in tableNameList:
        sig = 0;
    if sig==1:
        conf.add_section(tableName);
    conf.set(tableName,firstTag,str(firstTagValue));
    if secondTag!=None:
       conf.set(tableName, secondTag, str(secondTagValue));
    conf.write(open("roadConfig.ini", "w"))
    return
#读取每张表最新的标记字段
def readLocalConfig():
    conf = configparser.ConfigParser();
    conf.read("roadConfig.ini");
    tableNameList = conf.sections();
    return (conf,tableNameList);

#每个月5,15,25结合数据库与配置文件进行校准
def adjustData(data):
    (conf, tableNameList) = readLocalConfig();
    tempDataForDatabase = data[data['存储方式(源)'] == 'database'];
    indexKeyForDatabase = list(set(list(tempDataForDatabase['标记值'])));
    for i in range(0, len(indexKeyForDatabase)):
         tempData = tempDataForDatabase[tempDataForDatabase['标记值'] == indexKeyForDatabase[i]];
         tableName = list(tempData['目标表'])[0];
         if tableName not in tableNameList:
             continue;
         config = list(tempData['配置'])[0];
         (mapValue, databaseType) = creatMapValue(config);
         cnnKey = creatCnnKey(mapValue);
         (TargetCursorDes, TargetCnnDes) = createSqlEngin.TargetCnnDict[cnnKey]
         timingMarkDesList = list(tempData['定时标记']);
         filedTarget = list(tempData['目标字段名称']);
         fileTimeDic = dict(zip(filedTarget, timingMarkDesList));
         temp1 = '';
         temp2 = '';
         tag1 = None;
         tag2 = None;
         tempSql = 'select * from ' + tableName;
         for key in fileTimeDic.keys():
             if fileTimeDic[key] == 1:
                 temp1 = ' order by ' + key + ' desc';
                 tag1 = key;
             elif fileTimeDic[key] == 2:
                 temp2 = ',' + key + ' desc';
                 tag2 = key;
         tempSql = tempSql + temp1 + temp2 + ' limit 20';
         TargetCursorDes.execute(tempSql);
         data = TargetCursorDes.fetchmany(100);
         if data==[]:
             continue;
         data = pd.DataFrame(data);
         cols = TargetCursorDes.description;
         colnum = [];
         for col in cols:
             colnum.append(col[0]);
         data.columns = colnum;
         data.sort_values(by = tag1,ascending=False);
         value1 = list(data[tag1])[0];
         value2 = None;
         if tag2!=None:
             value2 = list(data[tag2])[0];
         #获得配置文件中有关当前表的信息
         dic = dict(conf.items(tableName));
         value1ForConfig1 = dic[tag1];
         value1ForConfig2 = None;
         if tag2!=None:
             value1ForConfig2 = dic[tag2];
         if value1ForConfig1!=value1:
             sendMail.sendMail("校准更新日期出错",tableName);
             writeConfig(tableName, tag1, value1, tag2, value2);
    return
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
#绑定目标数据库与目标table形成唯一字段
def creatSpIndex(mapValueList,tableNameList):
    spIndexList = [];
    for i in range(0,len(mapValueList)):
        tempMapValue = mapValueList[i];
        tempTableName = tableNameList[i];
        spIndex = creatCnnKey(tempMapValue)+tempTableName;
        spIndexList.append(spIndex);
    return spIndexList;

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
            if key!= 'create_time':
               newData[key] = [tempvalue]*lineValue;
            else:
               createTime = int(str(datetime.datetime.now().today()).split(' ')[0].replace('-', ''));
               newData[key] = [createTime]*lineValue;
    newData = newData.drop_duplicates();
    return newData;

def readDataFromOriginDatabase(TargetCursor,sql,rules,engin,tableName,tag1,tag2):
    TargetCursor.execute(sql);
    cols = TargetCursor.description;
    colnum = [];
    tagValue1 = -1;
    tagValue2 = -1;
    for col in cols:
        colnum.append(col[0]);
    while True:
        data = TargetCursor.fetchmany(1000000);
        if data == []:
            break;
        data = pd.DataFrame(data);
        data.columns = colnum;  # 默认以源数据库中的字段作为作为数据列名，并导入新数据库中
        newData = transFormData(data,rules);
        tempValue1 = -1;
        tempValue2 = -1;
        if tag2!=None:
            newData = newData.sort_values(by=[tag1,tag2],ascending=[False,False]);
            tempValue1 = list(newData[tag1])[0];
            tempValue2 = list(newData[tag2])[0];
        else:
            newData = newData.sort_values(by=tag1, ascending=False);
            tempValue1 = list(newData[tag1])[0];
        if tempValue1>tagValue1:
            tagValue1 = tempValue1;
            tagValue2 = tempValue2;
        elif tempValue1==tagValue1:
             if tagValue2<tempValue2:
                 tagValue2 = tempValue2;
        else:
            pass;
        loadData(newData, engin, tableName);
    writeConfig(tableName,tag1,tagValue1,tag2,tagValue2)
    print(tableName+"完成")
def readConfig(configPath):
    data = pd.read_excel(configPath);
    col = ['存储方式(源)','配置(源)','表名(源)','配置','目标表','导入频率','sql语句'];
    data[col] = data[col].ffill();
    return data;

def loadDataDatabase(data):
    day = datetime.datetime.now().day
    #每个月5,10,15,20,25在0点10分前对数据更新做校准
    if day%5==0:
       currentTime = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()));
       currentH = int(currentTime.split(' ')[1].split(":")[0]);
       currentM = int(currentTime.split(' ')[1].split(":")[1]);
       if ((currentH==0)&(currentM<10)):
           pass;
           #adjustData(data);
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

        timingMarkDesList = list(tempData['定时标记']);

        filedSource = list(tempData['字段名称(源)']);
        filedTarget = list(tempData['目标字段名称']);

        fileDic = dict(zip(filedTarget,filedSource));
        fileTimeDic = dict(zip(filedTarget,timingMarkDesList));

        (mapValueDatabase, databaseTypeDatabase) = creatMapValue(databaseConfig);
        (mapValue, databaseType) = creatMapValue(config);
        enginKey = creatCnnKey(mapValue);
        cnnKey = creatCnnKey(mapValueDatabase);
        (TargetCursor, TargetCnn) = createSqlEngin.TargetCnnDict[cnnKey];#得到源数据库游标
        (TargetCursorDes,TargetCnnDes) = createSqlEngin.TargetCnnDict[enginKey]#得到目标数据库游标
        engin = createSqlEngin.enginDict[enginKey];

        #先读取配置文件的定时标记形成预加载，如果配置文件没有该表的定时文件，
        # 直接从目标库中读取最新定时标记信息，然后直接写入配置文件，
        # 每隔十次/天进行配置文件和目标库的标记字段进行校准
        tag1 = None;
        tag2 = None;
        (conf,tableNameList) = readLocalConfig();
        if tableName in tableNameList:
            tempTimeTag = conf.items(tableName);
            sql = 'select * from '+sourceTableName;
            if len(tempTimeTag)==1:
               tempStr = ' where '+ fileDic[tempTimeTag[0][0]] +' > '+str(tempTimeTag[0][1]);
               tag1 = tempTimeTag[0][0];
               sql = sql+tempStr;
            elif len(tempTimeTag)==2:
               # 有问题
               tag1 = tempTimeTag[0][0];
               tag2 = tempTimeTag[1][0];
               tempStr = ' where ' + '( '+fileDic[tempTimeTag[0][0]] + ' = ' + str(tempTimeTag[0][1])+' and '+fileDic[tempTimeTag[1][0]] + ' > ' + str(tempTimeTag[1][1])\
                   + ')'+' or ' +' ('+ fileDic[tempTimeTag[0][0]] + ' > '+ str(tempTimeTag[0][1])+' ) ';
               sql = sql+tempStr;
            else:
                sendMail.sendMail(tableName, '数据表时间标记大于2');
                continue;
        else:
            sql = 'select * from '+sourceTableName;
            #此sql用于读取目标数据标定最新日期等数据用于确定update的数据时间
            tempSql = 'select * from '+tableName;
            temp1 = '';
            temp2 = '';

            keyList = fileTimeDic.keys();
            for key in keyList:
                if fileTimeDic[key]==1:
                    temp1 = ' order by '+ key +' desc';
                    tag1 = key;
                elif fileTimeDic[key]==2:
                    temp2 = ','+key +' desc';
                    tag2 = key;
            tempSql = tempSql+temp1+temp2+' limit 20';
            TargetCursorDes.execute(tempSql);
            data = TargetCursorDes.fetchmany(100);
            if data==[]:
               sql = sql;
            else:
                data = pd.DataFrame(data);
                cols = TargetCursorDes.description;

                colnum = [];
                for col in cols:
                    colnum.append(col[0]);
                data.columns = colnum;
                tagValue2 = None;
                tagValue1 = list(data[tag1])[0];

                if tag2!=None:
                    tagValue2 = list(data[tag2])[0];
                tempSql = sql;
                sql = sql+" where "+fileDic[tag1] +' > ' + str(tagValue1);
                if tagValue2!=None:
                    sql = tempSql+" where "+' ( '+fileDic[tag1] +' = ' + str(tagValue1) + ' and '+fileDic[tag2] +' > ' + str(tagValue2) +' ) '\
                        + ' or '+' ( '+fileDic[tag1] +' > ' + str(tagValue1) +' ) ';
                    #sql = sql + ' and '+fileDic[tag2] +' > ' + str(tagValue2);
                writeConfig(tableName,tag1,tagValue1,tag2,tagValue2);
        try:
           readDataFromOriginDatabase(TargetCursor, sql, rules, engin, tableName,tag1,tag2)
        except Exception as e:
           print(e)
           currentTime = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))
           sendMail.sendMail(tableName,'数据导入出问题:'+str(currentTime));
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
    #dataForDay = data[data.导入频率=='1d'];
    # dataForWeek = data[data.导入频率=='1w'];
    # dataForHour = data[data.导入频率=='1h'];
    dataFor5Min = data[data.导入频率=='5m'];
    #schedule.every().day.at("23:56").do(doDayUpdate,dataForDay);
    # schedule.every().friday.at("05:00").do(doWeekUpdate,dataForWeek);
    schedule.every(5).minutes.do(doFiveMinuteUpdate,dataFor5Min);
    # schedule.every().hour.do(doHourUpdate,dataForHour);
    while True:
        schedule.run_pending();
        time.sleep(1);
#根据
if __name__ == '__main__':
    configPath = r"E:\transPass_test\road_topic\LoadDataForRoad1.xlsx";
    data = createSqlEngin.initInfo(configPath);
    #loadDataDatabase(data);
    run(data)