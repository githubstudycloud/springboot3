/**
 * 日期格式化
 * @param date 日期对象或日期字符串或时间戳
 * @param format 格式化模式，默认 yyyy-MM-dd HH:mm:ss
 * @returns 格式化后的日期字符串
 */
export function formatDate(
  date: Date | string | number,
  format = 'yyyy-MM-dd HH:mm:ss'
): string {
  let _date: Date;
  
  if (typeof date === 'string') {
    _date = new Date(date);
  } else if (typeof date === 'number') {
    _date = new Date(date);
  } else {
    _date = date;
  }

  if (!(_date instanceof Date) || isNaN(_date.getTime())) {
    return '';
  }

  const o: Record<string, any> = {
    'M+': _date.getMonth() + 1, // 月份
    'd+': _date.getDate(), // 日
    'H+': _date.getHours(), // 小时
    'h+': _date.getHours() % 12 === 0 ? 12 : _date.getHours() % 12, // 12小时制
    'm+': _date.getMinutes(), // 分
    's+': _date.getSeconds(), // 秒
    'q+': Math.floor((_date.getMonth() + 3) / 3), // 季度
    S: _date.getMilliseconds(), // 毫秒
  };

  if (/(y+)/.test(format)) {
    format = format.replace(
      RegExp.$1,
      (_date.getFullYear() + '').substr(4 - RegExp.$1.length)
    );
  }

  for (const k in o) {
    if (new RegExp('(' + k + ')').test(format)) {
      format = format.replace(
        RegExp.$1,
        RegExp.$1.length === 1
          ? o[k]
          : ('00' + o[k]).substr(('' + o[k]).length)
      );
    }
  }
  
  return format;
}

/**
 * 获取相对时间
 * @param date 日期对象或日期字符串或时间戳
 * @returns 相对时间字符串，如"刚刚"，"5分钟前"，"2小时前"，"3天前"，"2个月前"，"3年前"
 */
export function getRelativeTime(date: Date | string | number): string {
  let _date: Date;
  
  if (typeof date === 'string') {
    _date = new Date(date);
  } else if (typeof date === 'number') {
    _date = new Date(date);
  } else {
    _date = date;
  }

  if (!(_date instanceof Date) || isNaN(_date.getTime())) {
    return '';
  }

  const now = new Date();
  const diff = now.getTime() - _date.getTime();
  
  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const month = 30 * day;
  const year = 12 * month;
  
  if (diff < minute) {
    return '刚刚';
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前';
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前';
  } else if (diff < month) {
    return Math.floor(diff / day) + '天前';
  } else if (diff < year) {
    return Math.floor(diff / month) + '个月前';
  } else {
    return Math.floor(diff / year) + '年前';
  }
}

/**
 * 获取日期范围
 * @param type 范围类型：today, yesterday, thisWeek, lastWeek, thisMonth, lastMonth, thisYear, lastYear
 * @returns [开始日期, 结束日期]
 */
export function getDateRange(type: string): [Date, Date] {
  const now = new Date();
  let start: Date = new Date();
  let end: Date = new Date();
  
  switch (type) {
    case 'today':
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      end = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59, 999);
      break;
    case 'yesterday':
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1);
      end = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1, 23, 59, 59, 999);
      break;
    case 'thisWeek': {
      const day = now.getDay() || 7;
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - day + 1);
      end = new Date(now.getFullYear(), now.getMonth(), now.getDate() + (7 - day), 23, 59, 59, 999);
      break;
    }
    case 'lastWeek': {
      const day = now.getDay() || 7;
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - day - 6);
      end = new Date(now.getFullYear(), now.getMonth(), now.getDate() - day, 23, 59, 59, 999);
      break;
    }
    case 'thisMonth':
      start = new Date(now.getFullYear(), now.getMonth(), 1);
      end = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59, 999);
      break;
    case 'lastMonth':
      start = new Date(now.getFullYear(), now.getMonth() - 1, 1);
      end = new Date(now.getFullYear(), now.getMonth(), 0, 23, 59, 59, 999);
      break;
    case 'thisYear':
      start = new Date(now.getFullYear(), 0, 1);
      end = new Date(now.getFullYear(), 11, 31, 23, 59, 59, 999);
      break;
    case 'lastYear':
      start = new Date(now.getFullYear() - 1, 0, 1);
      end = new Date(now.getFullYear() - 1, 11, 31, 23, 59, 59, 999);
      break;
    default:
      break;
  }
  
  return [start, end];
}
