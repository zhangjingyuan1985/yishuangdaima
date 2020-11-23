import pandas as pd
inputdir1 = r"D:\temp\standard_layer\t_common_space_adcode_dim\t_common_space_adcode_dim.csv";
inputdir2 = r"D:\temp\standard_layer\t_common_space_adcode_dim\t_common_dict_city.csv";
data1 = pd.read_csv(inputdir1);
data2 = pd.read_csv(inputdir2);
data2 = data2.drop_duplicates()
data1 = data1.drop_duplicates()
#data3 = pd.merge(data1,data2,how='left',left_on='city',right_on='city');
data3 = pd.merge(data1,data2,how='left',on='city');
#print(data3)
data4 = pd.DataFrame();
data4['adcode']=data3['adcode_x'];
data4['nation']=data3['country'];
data4['provice']=data3['province_x'];
data4['city']=data3['city'];
data4['district'] = data3['district'];
data4['city_pinyin'] = data3['city_pinyin'];
data4['vehicle_id_head'] = data3["vehicle_id_head"];
data4 = data4.drop_duplicates();


data4.to_csv("D:\\temp\\standard_layer\\t_common_space_adcode_dim\\new_acoddim.csv")
