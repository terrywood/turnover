# -*- coding: utf-8 -*-
"""
Created on Wed Jun  8 17:50:43 2016

@author: admin
"""
import tushare as ts
import os
filename_ = 'D:/Terry/cloud/OneDrive/data/huanshoulv_raw/day/%s.csv'
df = ts.get_stock_basics()
for code in df.index:
    dc=ts.get_hist_data(code)
    filename= filename_%code
    if os.path.exists(filename):
        dc.to_csv(filename, mode='a', header=None)
    else:
        dc.to_csv(filename)
    
#engine = create_engine('mysql://root:@127.0.0.1/turnover?charset=utf8')
#dc.to_sql('tick_data',engine,if_exists='append')
