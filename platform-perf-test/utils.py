"""
Utility functions for performance testing
"""

import os
import json
import subprocess
import logging
import time
import psutil
from datetime import datetime
from pathlib import Path

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler("perf_test.log")
    ]
)
logger = logging.getLogger("perf_utils")

def run_command(cmd, cwd=None, env=None, timeout=None):
    """
    Run a shell command and return its output
    
    Args:
        cmd (list): Command to run as a list of arguments
        cwd (str): Working directory
        env (dict): Environment variables
        timeout (int): Timeout in seconds
        
    Returns:
        tuple: (return_code, stdout, stderr)
    """
    logger.info(f"Running command: {' '.join(cmd)}")
    try:
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            cwd=cwd,
            env=env,
            universal_newlines=True
        )
        
        stdout, stderr = process.communicate(timeout=timeout)
        return_code = process.returncode
        
        if return_code != 0:
            logger.warning(f"Command returned non-zero exit code: {return_code}")
            logger.warning(f"stderr: {stderr}")
            
        return return_code, stdout, stderr
    
    except subprocess.TimeoutExpired:
        process.kill()
        logger.error(f"Command timed out after {timeout} seconds")
        return -1, "", "Timeout expired"
    
    except Exception as e:
        logger.error(f"Error running command: {e}")
        return -1, "", str(e)

def monitor_process(pid, interval=1.0, duration=60):
    """
    Monitor a process for the specified duration and collect performance metrics
    
    Args:
        pid (int): Process ID to monitor
        interval (float): Sampling interval in seconds
        duration (int): Total monitoring duration in seconds
        
    Returns:
        dict: Performance metrics
    """
    metrics = {
        'cpu_percent': [],
        'memory_percent': [],
        'memory_rss': [],
        'io_read_bytes': [],
        'io_write_bytes': [],
        'timestamps': []
    }
    
    try:
        process = psutil.Process(pid)
        start_time = time.time()
        end_time = start_time + duration
        
        # Initial IO counters
        io_counters_start = process.io_counters()
        
        while time.time() < end_time:
            # Get CPU and memory usage
            metrics['cpu_percent'].append(process.cpu_percent(interval=0.1))
            mem_info = process.memory_info()
            metrics['memory_rss'].append(mem_info.rss)
            metrics['memory_percent'].append(process.memory_percent())
            
            # Get IO counters
            io_counters = process.io_counters()
            metrics['io_read_bytes'].append(io_counters.read_bytes)
            metrics['io_write_bytes'].append(io_counters.write_bytes)
            
            # Record timestamp
            metrics['timestamps'].append(time.time() - start_time)
            
            # Sleep until next interval
            time.sleep(interval)
            
        return metrics
    
    except psutil.NoSuchProcess:
        logger.error(f"Process with PID {pid} not found")
        return None
    
    except Exception as e:
        logger.error(f"Error monitoring process: {e}")
        return None

def save_results(results, output_file):
    """
    Save test results to a JSON file
    
    Args:
        results (dict): Test results
        output_file (str): Output file path
    """
    try:
        os.makedirs(os.path.dirname(output_file), exist_ok=True)
        
        with open(output_file, 'w') as f:
            json.dump(results, f, indent=2)
            
        logger.info(f"Results saved to {output_file}")
        
    except Exception as e:
        logger.error(f"Error saving results: {e}")

def generate_timestamp():
    """Generate a timestamp string for file naming"""
    return datetime.now().strftime("%Y%m%d_%H%M%S")

def calculate_statistics(values):
    """
    Calculate basic statistics for a list of values
    
    Args:
        values (list): List of numeric values
        
    Returns:
        dict: Statistics including min, max, avg, median, p95, p99
    """
    if not values:
        return {
            'min': 0,
            'max': 0,
            'avg': 0,
            'median': 0,
            'p95': 0,
            'p99': 0
        }
    
    sorted_values = sorted(values)
    n = len(sorted_values)
    
    return {
        'min': min(sorted_values),
        'max': max(sorted_values),
        'avg': sum(sorted_values) / n,
        'median': sorted_values[n // 2],
        'p95': sorted_values[int(n * 0.95)],
        'p99': sorted_values[int(n * 0.99)]
    }
