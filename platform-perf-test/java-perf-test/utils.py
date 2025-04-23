"""
Java Performance Testing Utilities

Provides helper functions for Java performance testing.
"""

import os
import json
import logging
import subprocess
import requests
from pathlib import Path

# Set up logging
logger = logging.getLogger("java_test_utils")

def discover_endpoints(base_url, timeout=30):
    """
    Attempt to discover API endpoints by querying common discovery paths
    
    Args:
        base_url (str): Base URL of the service
        timeout (int): Request timeout in seconds
        
    Returns:
        list: Discovered endpoints
    """
    logger.info(f"Discovering endpoints for {base_url}")
    
    # Common paths that might expose API information
    discovery_paths = [
        '/swagger-ui.html',  # Swagger UI
        '/v3/api-docs',      # OpenAPI 3.0
        '/v2/api-docs',      # Swagger 2.0
        '/actuator/mappings' # Spring Boot Actuator
    ]
    
    endpoints = []
    
    for path in discovery_paths:
        try:
            url = f"{base_url.rstrip('/')}{path}"
            logger.info(f"Trying discovery path: {url}")
            
            response = requests.get(url, timeout=timeout)
            
            if response.status_code == 200:
                logger.info(f"Found API info at {url}")
                
                # Parse response based on path type
                if 'api-docs' in path:
                    # OpenAPI / Swagger format
                    try:
                        api_docs = response.json()
                        
                        if 'paths' in api_docs:
                            # OpenAPI format
                            for path_url, methods in api_docs['paths'].items():
                                for method in methods.keys():
                                    if method.lower() in ['get', 'post', 'put', 'delete']:
                                        endpoints.append({
                                            'path': path_url,
                                            'method': method.upper()
                                        })
                    except json.JSONDecodeError:
                        logger.warning(f"Failed to parse JSON from {url}")
                
                elif 'actuator/mappings' in path:
                    # Spring Boot Actuator format
                    try:
                        mappings = response.json()
                        
                        if 'contexts' in mappings:
                            # Spring Boot 2.x format
                            for context_name, context in mappings['contexts'].items():
                                if 'mappings' in context:
                                    for mapping_type, mapping_details in context['mappings'].items():
                                        if mapping_type == 'dispatcherServlets':
                                            for details in mapping_details.values():
                                                endpoints.append({
                                                    'path': details.get('pattern', ''),
                                                    'method': details.get('method', 'GET')
                                                })
                    except json.JSONDecodeError:
                        logger.warning(f"Failed to parse JSON from {url}")
        
        except requests.RequestException as e:
            logger.warning(f"Error accessing {url}: {e}")
    
    logger.info(f"Discovered {len(endpoints)} endpoints")
    return endpoints

def analyze_logs(log_file, error_patterns=None):
    """
    Analyze service logs for errors and performance issues
    
    Args:
        log_file (str): Path to log file
        error_patterns (list): List of error patterns to look for
        
    Returns:
        dict: Log analysis results
    """
    if not os.path.exists(log_file):
        return {'error': 'Log file not found'}
    
    if error_patterns is None:
        error_patterns = [
            'Exception', 'Error', 'WARNING', 'SEVERE',
            'OutOfMemory', 'timeout', 'Timeout', 'deadlock'
        ]
    
    results = {
        'error_count': 0,
        'errors': [],
        'warning_count': 0,
        'warnings': []
    }
    
    try:
        with open(log_file, 'r') as f:
            lines = f.readlines()
        
        for i, line in enumerate(lines):
            # Check for errors
            if any(pattern in line for pattern in error_patterns):
                results['error_count'] += 1
                
                # Get context (lines before and after)
                start = max(0, i - 2)
                end = min(len(lines), i + 3)
                context = ''.join(lines[start:end])
                
                results['errors'].append({
                    'line': i + 1,
                    'text': line.strip(),
                    'context': context
                })
                
            # Check for warnings
            elif 'WARN' in line:
                results['warning_count'] += 1
                results['warnings'].append({
                    'line': i + 1,
                    'text': line.strip()
                })
        
        return results
    
    except Exception as e:
        logger.error(f"Error analyzing logs: {e}")
        return {'error': f'Failed to analyze logs: {str(e)}'}

def parse_thread_dump(dump_file):
    """
    Parse a Java thread dump to analyze thread states
    
    Args:
        dump_file (str): Path to thread dump file
        
    Returns:
        dict: Thread analysis results
    """
    if not os.path.exists(dump_file):
        return {'error': 'Thread dump file not found'}
    
    results = {
        'total_threads': 0,
        'running': 0,
        'waiting': 0,
        'timed_waiting': 0,
        'blocked': 0,
        'thread_states': {},
        'blocked_threads': [],
        'hot_methods': {}
    }
    
    try:
        with open(dump_file, 'r') as f:
            content = f.read()
        
        # Split dump into threads
        thread_sections = content.split('"')
        
        # Skip first section (header)
        thread_sections = thread_sections[1:]
        
        for i in range(0, len(thread_sections), 2):
            if i + 1 < len(thread_sections):
                thread_name = thread_sections[i]
                thread_details = thread_sections[i + 1]
                
                results['total_threads'] += 1
                
                # Determine thread state
                if 'runnable' in thread_details:
                    state = 'RUNNABLE'
                    results['running'] += 1
                elif 'waiting on' in thread_details:
                    state = 'WAITING'
                    results['waiting'] += 1
                elif 'waiting for' in thread_details:
                    state = 'TIMED_WAITING'
                    results['timed_waiting'] += 1
                elif 'blocked' in thread_details:
                    state = 'BLOCKED'
                    results['blocked'] += 1
                    results['blocked_threads'].append(thread_name)
                else:
                    state = 'UNKNOWN'
                
                # Count thread states
                if state in results['thread_states']:
                    results['thread_states'][state] += 1
                else:
                    results['thread_states'][state] = 1
                
                # Extract stack trace methods
                stack_lines = [line.strip() for line in thread_details.split('\n') if 'at ' in line]
                for line in stack_lines:
                    method = line.split('at ')[1].strip()
                    if method in results['hot_methods']:
                        results['hot_methods'][method] += 1
                    else:
                        results['hot_methods'][method] = 1
        
        # Sort hot methods by frequency
        results['hot_methods'] = dict(sorted(
            results['hot_methods'].items(), 
            key=lambda x: x[1], 
            reverse=True
        )[:20])  # Get top 20
        
        return results
    
    except Exception as e:
        logger.error(f"Error parsing thread dump: {e}")
        return {'error': f'Failed to parse thread dump: {str(e)}'}

def generate_thread_dump(pid, output_file):
    """
    Generate a thread dump from a running Java process
    
    Args:
        pid (int): Process ID
        output_file (str): Output file path
        
    Returns:
        bool: Success status
    """
    try:
        # Create directory if it doesn't exist
        os.makedirs(os.path.dirname(output_file), exist_ok=True)
        
        # Use jstack to generate thread dump
        cmd = ['jstack', str(pid)]
        
        with open(output_file, 'w') as f:
            result = subprocess.run(cmd, stdout=f, stderr=subprocess.PIPE, text=True)
        
        if result.returncode != 0:
            logger.error(f"jstack failed: {result.stderr}")
            return False
        
        logger.info(f"Thread dump saved to {output_file}")
        return True
        
    except Exception as e:
        logger.error(f"Error generating thread dump: {e}")
        return False

def generate_heap_dump(pid, output_file):
    """
    Generate a heap dump from a running Java process
    
    Args:
        pid (int): Process ID
        output_file (str): Output file path
        
    Returns:
        bool: Success status
    """
    try:
        # Create directory if it doesn't exist
        os.makedirs(os.path.dirname(output_file), exist_ok=True)
        
        # Use jmap to generate heap dump
        cmd = ['jmap', '-dump:format=b,file=' + output_file, str(pid)]
        
        result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        
        if result.returncode != 0:
            logger.error(f"jmap failed: {result.stderr}")
            return False
        
        logger.info(f"Heap dump saved to {output_file}")
        return True
        
    except Exception as e:
        logger.error(f"Error generating heap dump: {e}")
        return False

def analyze_gc_logs(gc_log_file):
    """
    Analyze Garbage Collection logs
    
    Args:
        gc_log_file (str): Path to GC log file
        
    Returns:
        dict: GC analysis results
    """
    if not os.path.exists(gc_log_file):
        return {'error': 'GC log file not found'}
    
    results = {
        'gc_events': 0,
        'full_gc_events': 0,
        'gc_pause_times': [],
        'avg_gc_pause': 0,
        'max_gc_pause': 0,
        'total_gc_pause': 0
    }
    
    try:
        with open(gc_log_file, 'r') as f:
            lines = f.readlines()
        
        for line in lines:
            # Parse GC events
            if '[GC' in line:
                results['gc_events'] += 1
                
                # Check for full GC
                if '[Full GC' in line:
                    results['full_gc_events'] += 1
                
                # Extract pause time
                if 'real=' in line:
                    try:
                        pause_str = line.split('real=')[1].split()[0]
                        if pause_str.endswith('s'):
                            pause_time = float(pause_str[:-1])  # Remove trailing 's'
                            results['gc_pause_times'].append(pause_time)
                    except (IndexError, ValueError):
                        pass
        
        # Calculate statistics
        if results['gc_pause_times']:
            results['avg_gc_pause'] = sum(results['gc_pause_times']) / len(results['gc_pause_times'])
            results['max_gc_pause'] = max(results['gc_pause_times'])
            results['total_gc_pause'] = sum(results['gc_pause_times'])
        
        return results
    
    except Exception as e:
        logger.error(f"Error analyzing GC logs: {e}")
        return {'error': f'Failed to analyze GC logs: {str(e)}'}
