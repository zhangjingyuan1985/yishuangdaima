import sendMail
import time
import psycopg2 as pg
from sqlalchemy import create_engine
import cx_Oracle
import pandas as pd

def deleteData(tableName,keyWord,TargetCursor,value,TargetCnn):
    sql = "delete from " + tableName+' where ' +keyWord + ' < '+ str(value);
    try:
      TargetCursor.execute(sql)
      TargetCnn.commit()
    except Exception as e:
        print(e)
        currentTime = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))
        #sendMail.sendMail(tableName, '数据删除出问题:' + str(currentTime));
    return