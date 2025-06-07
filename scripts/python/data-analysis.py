#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
数据分析脚本示例
用于分析系统产生的数据，生成报表和图表
"""

import os
import sys
from datetime import datetime, timedelta
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sqlalchemy import create_engine
import pymysql
from loguru import logger
import click
import warnings

warnings.filterwarnings('ignore')

# 配置matplotlib中文显示
plt.rcParams['font.sans-serif'] = ['SimHei', 'Arial Unicode MS', 'DejaVu Sans']
plt.rcParams['axes.unicode_minus'] = False

class DataAnalyzer:
    """数据分析器"""
    
    def __init__(self, db_config):
        """初始化数据分析器"""
        self.db_config = db_config
        self.engine = None
        self.connect_database()
        
    def connect_database(self):
        """连接数据库"""
        try:
            db_url = f"mysql+pymysql://{self.db_config['user']}:{self.db_config['password']}@{self.db_config['host']}:{self.db_config['port']}/{self.db_config['database']}?charset=utf8mb4"
            self.engine = create_engine(db_url)
            logger.info("数据库连接成功")
        except Exception as e:
            logger.error(f"数据库连接失败: {e}")
            sys.exit(1)
    
    def analyze_user_stats(self, days=30):
        """分析用户统计数据"""
        logger.info(f"分析最近 {days} 天的用户统计数据")
        
        # 查询用户数据
        sql = """
        SELECT 
            DATE(create_time) as date,
            COUNT(*) as new_users,
            status
        FROM users 
        WHERE create_time >= DATE_SUB(NOW(), INTERVAL %s DAY)
        GROUP BY DATE(create_time), status
        ORDER BY date
        """
        
        df = pd.read_sql(sql, self.engine, params=[days])
        
        if df.empty:
            logger.warning("没有找到用户数据")
            return
        
        # 数据透视
        pivot_df = df.pivot_table(
            index='date', 
            columns='status', 
            values='new_users', 
            fill_value=0
        )
        
        # 生成图表
        plt.figure(figsize=(12, 6))
        
        # 用户注册趋势
        plt.subplot(1, 2, 1)
        pivot_df.plot(kind='bar', stacked=True, ax=plt.gca())
        plt.title('每日新增用户数')
        plt.xlabel('日期')
        plt.ylabel('用户数')
        plt.xticks(rotation=45)
        plt.legend(title='状态')
        
        # 累计用户数
        plt.subplot(1, 2, 2)
        cumsum_df = pivot_df.cumsum()
        cumsum_df.plot(kind='line', ax=plt.gca())
        plt.title('累计用户数趋势')
        plt.xlabel('日期')
        plt.ylabel('累计用户数')
        plt.xticks(rotation=45)
        plt.legend(title='状态')
        
        plt.tight_layout()
        output_path = 'user_stats.png'
        plt.savefig(output_path, dpi=300, bbox_inches='tight')
        logger.info(f"用户统计图表已保存: {output_path}")
        
        return pivot_df
    
    def analyze_article_stats(self, days=30):
        """分析文章统计数据"""
        logger.info(f"分析最近 {days} 天的文章统计数据")
        
        sql = """
        SELECT 
            DATE(create_time) as date,
            COUNT(*) as articles_count,
            AVG(view_count) as avg_views,
            AVG(like_count) as avg_likes,
            status
        FROM articles 
        WHERE create_time >= DATE_SUB(NOW(), INTERVAL %s DAY)
        GROUP BY DATE(create_time), status
        ORDER BY date
        """
        
        df = pd.read_sql(sql, self.engine, params=[days])
        
        if df.empty:
            logger.warning("没有找到文章数据")
            return
        
        # 生成文章统计图表
        plt.figure(figsize=(15, 10))
        
        # 每日文章数量
        plt.subplot(2, 2, 1)
        daily_articles = df.groupby('date')['articles_count'].sum()
        daily_articles.plot(kind='line', marker='o')
        plt.title('每日文章发布数量')
        plt.xlabel('日期')
        plt.ylabel('文章数')
        plt.xticks(rotation=45)
        
        # 平均阅读量
        plt.subplot(2, 2, 2)
        avg_views = df.groupby('date')['avg_views'].mean()
        avg_views.plot(kind='bar', color='skyblue')
        plt.title('平均文章阅读量')
        plt.xlabel('日期')
        plt.ylabel('平均阅读量')
        plt.xticks(rotation=45)
        
        # 平均点赞数
        plt.subplot(2, 2, 3)
        avg_likes = df.groupby('date')['avg_likes'].mean()
        avg_likes.plot(kind='bar', color='lightcoral')
        plt.title('平均文章点赞数')
        plt.xlabel('日期')
        plt.ylabel('平均点赞数')
        plt.xticks(rotation=45)
        
        # 文章状态分布
        plt.subplot(2, 2, 4)
        status_counts = df['status'].value_counts()
        status_labels = {0: '草稿', 1: '已发布', 2: '已下架'}
        status_counts.index = [status_labels.get(x, f'状态{x}') for x in status_counts.index]
        status_counts.plot(kind='pie', autopct='%1.1f%%')
        plt.title('文章状态分布')
        
        plt.tight_layout()
        output_path = 'article_stats.png'
        plt.savefig(output_path, dpi=300, bbox_inches='tight')
        logger.info(f"文章统计图表已保存: {output_path}")
        
        return df
    
    def analyze_operation_logs(self, days=7):
        """分析操作日志"""
        logger.info(f"分析最近 {days} 天的操作日志")
        
        sql = """
        SELECT 
            operation,
            COUNT(*) as count,
            AVG(time) as avg_time,
            DATE(create_time) as date,
            HOUR(create_time) as hour
        FROM operation_logs 
        WHERE create_time >= DATE_SUB(NOW(), INTERVAL %s DAY)
        GROUP BY operation, DATE(create_time), HOUR(create_time)
        ORDER BY date DESC, hour DESC
        """
        
        df = pd.read_sql(sql, self.engine, params=[days])
        
        if df.empty:
            logger.warning("没有找到操作日志数据")
            return
        
        # 生成操作统计图表
        plt.figure(figsize=(15, 10))
        
        # 操作类型分布
        plt.subplot(2, 2, 1)
        operation_counts = df.groupby('operation')['count'].sum().sort_values(ascending=False)
        operation_counts.head(10).plot(kind='bar')
        plt.title('操作类型分布 (Top 10)')
        plt.xlabel('操作类型')
        plt.ylabel('操作次数')
        plt.xticks(rotation=45)
        
        # 每小时操作量
        plt.subplot(2, 2, 2)
        hourly_ops = df.groupby('hour')['count'].sum()
        hourly_ops.plot(kind='line', marker='o', color='green')
        plt.title('每小时操作量分布')
        plt.xlabel('小时')
        plt.ylabel('操作次数')
        
        # 平均响应时间
        plt.subplot(2, 2, 3)
        avg_response_time = df.groupby('operation')['avg_time'].mean().sort_values(ascending=False)
        avg_response_time.head(10).plot(kind='bar', color='orange')
        plt.title('平均响应时间 (Top 10)')
        plt.xlabel('操作类型')
        plt.ylabel('响应时间(ms)')
        plt.xticks(rotation=45)
        
        # 每日操作趋势
        plt.subplot(2, 2, 4)
        daily_ops = df.groupby('date')['count'].sum()
        daily_ops.plot(kind='line', marker='s', color='purple')
        plt.title('每日操作量趋势')
        plt.xlabel('日期')
        plt.ylabel('操作次数')
        plt.xticks(rotation=45)
        
        plt.tight_layout()
        output_path = 'operation_logs_stats.png'
        plt.savefig(output_path, dpi=300, bbox_inches='tight')
        logger.info(f"操作日志统计图表已保存: {output_path}")
        
        return df
    
    def generate_report(self, days=30):
        """生成综合报表"""
        logger.info(f"生成最近 {days} 天的综合数据报表")
        
        report_data = {}
        
        # 分析各种数据
        user_stats = self.analyze_user_stats(days)
        article_stats = self.analyze_article_stats(days)
        log_stats = self.analyze_operation_logs(min(days, 7))  # 日志分析最多7天
        
        # 生成报表
        report_time = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        report_content = f"""
# 数据分析报表

生成时间: {report_time}
分析周期: 最近 {days} 天

## 用户统计
- 用户数据分析完成，图表已保存为 user_stats.png
- 建议关注用户注册趋势和活跃度

## 文章统计  
- 文章数据分析完成，图表已保存为 article_stats.png
- 建议关注文章质量和用户互动

## 操作日志分析
- 操作日志分析完成，图表已保存为 operation_logs_stats.png
- 建议关注系统性能和用户行为模式

## 建议
1. 根据用户注册趋势调整运营策略
2. 关注文章阅读量和点赞数的相关性
3. 优化响应时间较长的操作
4. 根据用户活跃时间段调整服务器资源
"""
        
        # 保存报表
        report_file = f'data_report_{datetime.now().strftime("%Y%m%d_%H%M%S")}.md'
        with open(report_file, 'w', encoding='utf-8') as f:
            f.write(report_content)
        
        logger.info(f"数据分析报表已生成: {report_file}")
        return report_file

@click.command()
@click.option('--host', default='localhost', help='数据库主机')
@click.option('--port', default=3306, help='数据库端口')
@click.option('--user', default='root', help='数据库用户名')
@click.option('--password', default='rootpass', help='数据库密码')
@click.option('--database', default='mydb', help='数据库名')
@click.option('--days', default=30, help='分析天数')
@click.option('--analysis-type', 
              type=click.Choice(['all', 'users', 'articles', 'logs']), 
              default='all', 
              help='分析类型')
def main(host, port, user, password, database, days, analysis_type):
    """数据分析主函数"""
    logger.info("开始数据分析...")
    
    # 数据库配置
    db_config = {
        'host': host,
        'port': port,
        'user': user,
        'password': password,
        'database': database
    }
    
    # 创建分析器
    analyzer = DataAnalyzer(db_config)
    
    try:
        if analysis_type == 'all':
            analyzer.generate_report(days)
        elif analysis_type == 'users':
            analyzer.analyze_user_stats(days)
        elif analysis_type == 'articles':
            analyzer.analyze_article_stats(days)
        elif analysis_type == 'logs':
            analyzer.analyze_operation_logs(min(days, 7))
        
        logger.info("数据分析完成！")
        
    except Exception as e:
        logger.error(f"数据分析过程中出现错误: {e}")
        sys.exit(1)

if __name__ == '__main__':
    # 配置日志
    logger.add("data_analysis.log", rotation="1 MB", retention="7 days")
    main() 