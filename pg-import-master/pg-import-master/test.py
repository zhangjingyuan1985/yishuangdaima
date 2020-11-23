import time
import threading
import schedule
import pandas as pd
# def job(a):
#     print('yyyy');
#     print(a)
#     time.sleep(3);
#     print('ssss');
# def job_task1(a,b):
#     threading.Thread(target=job(a));
# def job_task2(b,c):
#     threading.Thread(target=job(b));
# def run():
#     schedule.every(2).minutes.do(job_task2, 'b', 'c');
#     schedule.every(1).minutes.do(job_task1,'a','b');
#     while True:
#         schedule.run_pending();
# run();
inputdir = r"D:\inf\m_data\user_stay_day.csv";
data = pd.read_csv(inputdir);
(a,b) = data.shape
#320500
f = lambda x: 1 if x=="RESIDENT"else 2
data["city_type"] = [320500]*a;
data["persiontype"] = data["user_type"].apply(f);
data["usernum"] = range(1,a+1);
data = data[["stay_days","city_type","persiontype","usernum"]]
#print(data)
data.to_csv("D:\\inf\\m_data\\newdata\\user_stay_day.csv")