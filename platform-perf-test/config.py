"""
Configuration settings for performance testing framework
"""

import os
from pathlib import Path

# Base paths
BASE_DIR = Path(os.path.dirname(os.path.abspath(__file__)))
PROJECT_ROOT = BASE_DIR.parent

# Java backend settings
JAVA_MODULES = [
    'platform-gateway',
    'platform-scheduler',
    'platform-common',
    # Add more modules as needed
]

# Default JVM options for Java testing
DEFAULT_JVM_OPTS = [
    '-Xms512m',
    '-Xmx2g',
    '-XX:+UseG1GC',
    '-XX:MaxGCPauseMillis=200'
]

# Vue frontend settings
VUE_APP_DIR = PROJECT_ROOT / 'platform-vue-ui'
VUE_BUILD_CMD = 'npm run build'
VUE_DEV_SERVER_CMD = 'npm run serve'

# Performance test settings
DEFAULT_TEST_DURATION = 60  # seconds
DEFAULT_CONCURRENT_USERS = 10
RESULTS_DIR = BASE_DIR / 'results'

# Reporting settings
GENERATE_HTML_REPORT = True
GENERATE_JSON_REPORT = True
REPORT_TEMPLATE_DIR = BASE_DIR / 'templates'

# Thresholds for performance alerts
RESPONSE_TIME_THRESHOLD = 500  # ms
ERROR_RATE_THRESHOLD = 0.01  # 1%
CPU_USAGE_THRESHOLD = 80  # %
MEMORY_USAGE_THRESHOLD = 85  # %
