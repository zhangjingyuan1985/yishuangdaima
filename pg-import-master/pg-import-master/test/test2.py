import createSqlEngin
import pandas as pd
import time
data = pd.DataFrame();
inputdir = r"D:\transPass_test\loaddata\LoadData3.xlsx";
createSqlEngin.initInfo(inputdir);
engin = createSqlEngin.enginDict
for i in range(1,100):
    data['fdate'] = [20190940+i]*10;
    data['name'] = ['tom']*10;
    data['age'] = [10]*10;
    keys = list(engin.keys());
    tempengin = engin[keys[0]];
    data.to_sql(name = "test1",con = tempengin,if_exists="append", index=False);
    print("完成")
    time.sleep(60);