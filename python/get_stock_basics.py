# -*- coding: utf-8 -*-
"""
Created on Wed Jun  8 17:50:43 2016

@author: admin
"""
from sqlalchemy import create_engine
from sqlalchemy.types import String
engine = create_engine('mysql://root:@127.0.0.1/turnover?charset=utf8')
import tushare as ts
df = ts.get_stock_basics()
dtype={'code': String(length=6),'name': String(length=50),'industry': String(length=50),'area': String(length=50)}
df.to_sql('stock',engine,index_label='id',if_exists='replace',dtype=dtype )
