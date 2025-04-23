"""
Java Performance Test Runner

Handles the execution of performance tests for Java backend services.
"""

import os
import sys
import time
import json
import logging
import subprocess
import multiprocessing
from pathlib import Path

# Add parent directory to path
parent_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(parent_dir)

# Import from parent module
import config
from utils import run_command, monitor_process, calculate_statistics, generate_timestamp

# Set up logging
logger = logging.getLogger("java_test_runner")

class JavaTestRunner:
    """Runner for Java backend performance tests"""
    
    def __init__(self, modules=None, endpoints=None, jvm_opts=None, 
                 duration=60, concurrent_users=10):
        """
        Initialize Java test runner
        
        Args:
            modules (list): Java modules to test
            endpoints (list): API endpoints to test
            jvm_opts (list): JVM options
            duration (int): Test duration in seconds
            concurrent_users (int): Number of concurrent users
        """
        self.modules = modules or config.JAVA_MODULES
        self.endpoints = endpoints or []
        self.jvm_opts = jvm_opts or config.DEFAULT_JVM_OPTS
        self.duration = duration
        self.concurrent_users = concurrent_users
        self.project_root = config.PROJECT_ROOT
        
        # Create results directory if it doesn't exist
        os.makedirs(config.RESULTS_DIR, exist_ok=True)
    
    def run_tests(self):
        """
        Run performance tests for all configured modules
        
        Returns:
            dict: Test results for all modules
        """
        results = {
            'timestamp': generate_timestamp(),
            'duration': self.duration,
            'concurrent_users': self.concurrent_users,
            'modules': {}
        }
        
        for module in self.modules:
            logger.info(f"Testing module: {module}")
            module_results = self._test_module(module)
            results['modules'][module] = module_results
        
        return results
    
    def _test_module(self, module):
        """
        Run performance tests for a specific module
        
        Args:
            module (str): Module name
            
        Returns:
            dict: Test results for this module
        """
        module_dir = self.project_root / module
        
        if not module_dir.exists():
            logger.warning(f"Module directory not found: {module_dir}")
            return {'error': 'Module directory not found'}
        
        # Build the module
        build_result = self._build_module(module, module_dir)
        if build_result.get('error'):
            return build_result
        
        # Start the service
        service_pid = self._start_service(module, module_dir)
        if not service_pid:
            return {'error': 'Failed to start service'}
        
        try:
            # Let the service initialize
            logger.info(f"Waiting for service to initialize...")
            time.sleep(10)
            
            # Run JMeter tests
            jmeter_results = self._run_jmeter_tests(module)
            
            # Monitor service during tests
            monitoring_results = self._monitor_service(service_pid)
            
            # Combine results
            return {
                'build_time': build_result.get('build_time', 0),
                'jmeter': jmeter_results,
                'monitoring': monitoring_results
            }
            
        finally:
            # Stop the service
            self._stop_service(service_pid)
    
    def _build_module(self, module, module_dir):
        """
        Build a Java module using Maven
        
        Args:
            module (str): Module name
            module_dir (Path): Module directory
            
        Returns:
            dict: Build results
        """
        logger.info(f"Building module: {module}")
        
        start_time = time.time()
        
        cmd = ['mvn', 'clean', 'package', '-DskipTests']
        return_code, stdout, stderr = run_command(cmd, cwd=str(module_dir))
        
        build_time = time.time() - start_time
        
        if return_code != 0:
            logger.error(f"Failed to build module {module}: {stderr}")
            return {'error': 'Build failed', 'details': stderr}
        
        logger.info(f"Successfully built module {module} in {build_time:.2f} seconds")
        return {'build_time': build_time}
    
    def _start_service(self, module, module_dir):
        """
        Start the Java service for testing
        
        Args:
            module (str): Module name
            module_dir (Path): Module directory
            
        Returns:
            int: Process ID of the started service, or None if failed
        """
        logger.info(f"Starting service: {module}")
        
        # Find the JAR file
        target_dir = module_dir / 'target'
        jar_files = list(target_dir.glob('*.jar'))
        jar_files = [f for f in jar_files if not f.name.endswith('-sources.jar') 
                    and not f.name.endswith('-javadoc.jar')]
        
        if not jar_files:
            logger.error(f"No JAR file found in {target_dir}")
            return None
        
        jar_file = jar_files[0]
        logger.info(f"Using JAR file: {jar_file.name}")
        
        # Prepare Java command
        cmd = ['java']
        cmd.extend(self.jvm_opts)
        cmd.extend(['-jar', str(jar_file)])
        
        # Start the process
        try:
            env = os.environ.copy()
            process = subprocess.Popen(
                cmd,
                cwd=str(target_dir),
                env=env,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                universal_newlines=True
            )
            
            # Wait a moment to check if process starts successfully
            time.sleep(2)
            if process.poll() is not None:
                # Process already terminated
                stdout, stderr = process.communicate()
                logger.error(f"Service failed to start: {stderr}")
                return None
            
            logger.info(f"Service started with PID: {process.pid}")
            return process.pid
            
        except Exception as e:
            logger.error(f"Error starting service: {e}")
            return None
    
    def _stop_service(self, pid):
        """Stop the Java service"""
        if not pid:
            return
        
        logger.info(f"Stopping service with PID: {pid}")
        
        try:
            import psutil
            process = psutil.Process(pid)
            
            # Send SIGTERM
            process.terminate()
            
            # Wait for process to terminate
            gone, alive = psutil.wait_procs([process], timeout=5)
            
            # If still running, kill it
            if process in alive:
                logger.warning(f"Service did not terminate gracefully, killing it")
                process.kill()
                
            logger.info(f"Service stopped")
            
        except Exception as e:
            logger.error(f"Error stopping service: {e}")
    
    def _run_jmeter_tests(self, module):
        """
        Run JMeter performance tests
        
        Args:
            module (str): Module name
            
        Returns:
            dict: JMeter test results
        """
        logger.info(f"Running JMeter tests for module: {module}")
        
        # Check if JMeter is installed
        jmeter_cmd = 'jmeter'
        if os.name == 'nt':  # Windows
            jmeter_cmd = 'jmeter.bat'
        
        # Path to JMeter test plan
        test_plan = Path(__file__).parent / 'jmeter' / f"{module}_test_plan.jmx"
        
        # Create default test plan if it doesn't exist
        if not test_plan.exists():
            logger.info(f"Creating default test plan for {module}")
            self._create_default_test_plan(module, test_plan)
        
        # Results file
        results_file = config.RESULTS_DIR / f"{module}_jmeter_{generate_timestamp()}.csv"
        
        # Run JMeter
        cmd = [
            jmeter_cmd,
            '-n',  # non-GUI mode
            '-t', str(test_plan),  # test plan
            '-l', str(results_file),  # results file
            '-Jthreads=' + str(self.concurrent_users),  # number of threads
            '-Jduration=' + str(self.duration)  # test duration
        ]
        
        return_code, stdout, stderr = run_command(cmd)
        
        if return_code != 0:
            logger.error(f"JMeter test failed: {stderr}")
            return {'error': 'JMeter test failed', 'details': stderr}
        
        # Parse results
        try:
            jmeter_results = self._parse_jmeter_results(results_file)
            logger.info(f"JMeter test completed successfully")
            return jmeter_results
            
        except Exception as e:
            logger.error(f"Error parsing JMeter results: {e}")
            return {'error': 'Failed to parse JMeter results', 'details': str(e)}
    
    def _create_default_test_plan(self, module, test_plan_path):
        """
        Create a default JMeter test plan for a module
        
        Args:
            module (str): Module name
            test_plan_path (Path): Path to save the test plan
        """
        # Create jmeter directory if it doesn't exist
        os.makedirs(test_plan_path.parent, exist_ok=True)
        
        # Default test plan template
        # This is a basic JMeter test plan XML
        # In a real implementation, this would be more sophisticated
        test_plan_content = f"""<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="{module} Performance Test">
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
      <stringProp name="TestPlan.comments"></stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Users">
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <intProp name="LoopController.loops">-1</intProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">${{__P(threads,10)}}</stringProp>
        <stringProp name="ThreadGroup.ramp_time">5</stringProp>
        <longProp name="ThreadGroup.start_time">1458490353000</longProp>
        <longProp name="ThreadGroup.end_time">1458490353000</longProp>
        <boolProp name="ThreadGroup.scheduler">true</boolProp>
        <stringProp name="ThreadGroup.duration">${{__P(duration,60)}}</stringProp>
        <stringProp name="ThreadGroup.delay">0</stringProp>
      </ThreadGroup>
      <hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="HTTP Request">
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
            <collectionProp name="Arguments.arguments"/>
          </elementProp>
          <stringProp name="HTTPSampler.domain">localhost</stringProp>
          <stringProp name="HTTPSampler.port">8080</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.path">/api/health</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
          <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
          <boolProp name="HTTPSampler.auto_redirects">false</boolProp>
          <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
          <boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>
          <boolProp name="HTTPSampler.monitor">false</boolProp>
          <stringProp name="HTTPSampler.embedded_url_re"></stringProp>
        </HTTPSamplerProxy>
        <hashTree/>
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector" testname="Summary Report">
          <boolProp name="ResultCollector.error_logging">false</boolProp>
          <objProp>
            <name>saveConfig</name>
            <value class="SampleSaveConfiguration">
              <time>true</time>
              <latency>true</latency>
              <timestamp>true</timestamp>
              <success>true</success>
              <label>true</label>
              <code>true</code>
              <message>true</message>
              <threadName>true</threadName>
              <dataType>true</dataType>
              <encoding>false</encoding>
              <assertions>false</assertions>
              <subresults>false</subresults>
              <responseData>false</responseData>
              <samplerData>false</samplerData>
              <xml>false</xml>
              <fieldNames>true</fieldNames>
              <responseHeaders>false</responseHeaders>
              <requestHeaders>false</requestHeaders>
              <responseDataOnError>false</responseDataOnError>
              <saveAssertionResultsFailureMessage>false</saveAssertionResultsFailureMessage>
              <assertionsResultsToSave>0</assertionsResultsToSave>
              <bytes>true</bytes>
              <threadCounts>true</threadCounts>
              <sampleCount>true</sampleCount>
            </value>
          </objProp>
          <stringProp name="filename"></stringProp>
        </ResultCollector>
        <hashTree/>
      </hashTree>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
"""
        with open(test_plan_path, 'w') as f:
            f.write(test_plan_content)
            
        logger.info(f"Created default test plan: {test_plan_path}")
    
    def _parse_jmeter_results(self, results_file):
        """
        Parse JMeter CSV results file
        
        Args:
            results_file (Path): Path to JMeter results CSV file
            
        Returns:
            dict: Parsed results with statistics
        """
        # Check if file exists
        if not results_file.exists():
            return {'error': 'Results file not found'}
        
        # Read CSV file
        with open(results_file, 'r') as f:
            lines = f.readlines()
        
        # Check if file is empty
        if len(lines) < 2:
            return {'error': 'Results file is empty'}
        
        # Parse header
        header = lines[0].strip().split(',')
        
        # Parse data rows
        data = []
        for i in range(1, len(lines)):
            row = lines[i].strip().split(',')
            if len(row) == len(header):
                data_row = {}
                for j in range(len(header)):
                    try:
                        data_row[header[j]] = float(row[j])
                    except ValueError:
                        data_row[header[j]] = row[j]
                data.append(data_row)
        
        # Extract metrics
        elapsed_times = [row.get('elapsed', 0) for row in data]
        latencies = [row.get('Latency', 0) for row in data]
        bytes_vals = [row.get('bytes', 0) for row in data]
        
        # Success rate
        success_count = sum(1 for row in data if row.get('success') == 'true')
        total_count = len(data)
        success_rate = success_count / total_count if total_count > 0 else 0
        
        # Calculate statistics
        return {
            'samples': total_count,
            'success_rate': success_rate,
            'elapsed_time': calculate_statistics(elapsed_times),
            'latency': calculate_statistics(latencies),
            'bytes': calculate_statistics(bytes_vals),
            'throughput': total_count / (self.duration if self.duration > 0 else 1)
        }
    
    def _monitor_service(self, pid):
        """
        Monitor service resource usage during test
        
        Args:
            pid (int): Process ID of the service
            
        Returns:
            dict: Monitoring results with statistics
        """
        logger.info(f"Monitoring service with PID: {pid}")
        
        try:
            # Monitor process
            metrics = monitor_process(pid, interval=1.0, duration=self.duration)
            
            if not metrics:
                return {'error': 'Failed to monitor process'}
            
            # Calculate statistics for each metric
            results = {
                'cpu_percent': calculate_statistics(metrics.get('cpu_percent', [])),
                'memory_percent': calculate_statistics(metrics.get('memory_percent', [])),
                'memory_rss': calculate_statistics(metrics.get('memory_rss', [])),
                'io_read_bytes': calculate_statistics(
                    [b - metrics.get('io_read_bytes', [0])[0] for b in metrics.get('io_read_bytes', [0])][1:]
                ),
                'io_write_bytes': calculate_statistics(
                    [b - metrics.get('io_write_bytes', [0])[0] for b in metrics.get('io_write_bytes', [0])][1:]
                )
            }
            
            return results
            
        except Exception as e:
            logger.error(f"Error monitoring service: {e}")
            return {'error': 'Failed to monitor service', 'details': str(e)}
