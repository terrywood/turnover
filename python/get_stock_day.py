# -*- coding: utf-8 -*-
"""
Created on Wed Jun  8 17:50:43 2016

@author: admin
"""
from sqlalchemy import create_engine
from sqlalchemy.types import String
import tushare as ts
dc=ts.get_hist_data('000100')
dc['code'] = '000100'
#engine = create_engine('mysql://root:@127.0.0.1/turnover?charset=utf8')
#dc.to_sql('tick_data',engine,if_exists='append')
