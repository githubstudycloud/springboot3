"""
Vue Performance Test Runner

Handles the execution of performance tests for Vue.js frontend applications.
"""

import os
import sys
import time
import json
import logging
import subprocess
from pathlib import Path

# Add parent directory to path
parent_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(parent_dir)

# Import from parent module
import config
from utils import run_command, calculate_statistics, generate_timestamp

# Set up logging
logger = logging.getLogger("vue_test_runner")

class VueTestRunner:
    """Runner for Vue.js frontend performance tests"""
    
    def __init__(self, pages=None, headless=True, duration=60):
        """
        Initialize Vue test runner
        
        Args:
            pages (list): Vue pages to test
            headless (bool): Whether to run browser in headless mode
            duration (int): Test duration in seconds
        """
        self.pages = pages or []
        self.headless = headless
        self.duration = duration
        self.vue_app_dir = config.VUE_APP_DIR
        
        # Create results directory if it doesn't exist
        os.makedirs(config.RESULTS_DIR, exist_ok=True)
    
    def run_tests(self):
        """
        Run performance tests for Vue frontend
        
        Returns:
            dict: Test results
        """
        results = {
            'timestamp': generate_timestamp(),
            'duration': self.duration,
            'pages': {}
        }
        
        # Check if Vue app directory exists
        if not self.vue_app_dir.exists():
            logger.error(f"Vue app directory not found: {self.vue_app_dir}")
            return {'error': 'Vue app directory not found'}
        
        # Build the Vue app
        build_result = self._build_vue_app()
        if build_result.get('error'):
            return build_result
        
        # Start development server
        server_process = self._start_dev_server()
        if not server_process:
            return {'error': 'Failed to start development server'}
        
        try:
            # Wait for server to start
            logger.info("Waiting for development server to start...")
            time.sleep(10)
            
            # Discover pages if not specified
            if not self.pages:
                self.pages = self._discover_pages()
            
            # Test each page
            for page in self.pages:
                logger.info(f"Testing page: {page}")
                page_results = self._test_page(page)
                results['pages'][page] = page_results
            
            # Additional tests for user interactions
            interaction_results = self._test_interactions()
            results['interactions'] = interaction_results
            
            return results
            
        finally:
            # Stop development server
            self._stop_dev_server(server_process)
    
    def _build_vue_app(self):
        """
        Build the Vue app using npm
        
        Returns:
            dict: Build results
        """
        logger.info("Building Vue app")
        
        start_time = time.time()
        
        cmd = config.VUE_BUILD_CMD.split()
        return_code, stdout, stderr = run_command(cmd, cwd=str(self.vue_app_dir))
        
        build_time = time.time() - start_time
        
        if return_code != 0:
            logger.error(f"Failed to build Vue app: {stderr}")
            return {'error': 'Build failed', 'details': stderr}
        
        logger.info(f"Successfully built Vue app in {build_time:.2f} seconds")
        return {'build_time': build_time}
    
    def _start_dev_server(self):
        """
        Start the Vue development server
        
        Returns:
            subprocess.Popen: Server process
        """
        logger.info("Starting Vue development server")
        
        cmd = config.VUE_DEV_SERVER_CMD.split()
        
        try:
            env = os.environ.copy()
            process = subprocess.Popen(
                cmd,
                cwd=str(self.vue_app_dir),
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
                logger.error(f"Vue dev server failed to start: {stderr}")
                return None
            
            logger.info(f"Vue dev server started with PID: {process.pid}")
            return process
            
        except Exception as e:
            logger.error(f"Error starting Vue dev server: {e}")
            return None
    
    def _stop_dev_server(self, process):
        """
        Stop the Vue development server
        
        Args:
            process (subprocess.Popen): Server process
        """
        if not process:
            return
        
        logger.info("Stopping Vue development server")
        
        try:
            # Send SIGTERM
            process.terminate()
            
            # Wait for process to terminate
            try:
                process.wait(timeout=5)
            except subprocess.TimeoutExpired:
                # If still running, kill it
                logger.warning("Vue dev server did not terminate gracefully, killing it")
                process.kill()
                
            logger.info("Vue dev server stopped")
            
        except Exception as e:
            logger.error(f"Error stopping Vue dev server: {e}")
    
    def _discover_pages(self):
        """
        Discover Vue pages by analyzing the router configuration
        
        Returns:
            list: Discovered pages
        """
        logger.info("Discovering Vue pages")
        
        # Look for router configuration file
        router_file = None
        for path in [
            self.vue_app_dir / 'src/router/index.js',
            self.vue_app_dir / 'src/router.js'
        ]:
            if path.exists():
                router_file = path
                break
        
        if not router_file:
            logger.warning("Router configuration file not found")
            return ['/', '/about']  # Default pages
        
        # Parse router file
        try:
            with open(router_file, 'r') as f:
                content = f.read()
            
            # Extract routes
            pages = []
            lines = content.split('\n')
            for line in lines:
                if 'path:' in line:
                    # Extract path
                    path_parts = line.split('path:')
                    if len(path_parts) > 1:
                        path = path_parts[1].split(',')[0].strip().strip('"\'')
                        if path and path not in pages:
                            pages.append(path)
            
            if not pages:
                logger.warning("No pages found in router configuration")
                return ['/', '/about']  # Default pages
            
            logger.info(f"Discovered {len(pages)} pages: {', '.join(pages)}")
            return pages
            
        except Exception as e:
            logger.error(f"Error discovering pages: {e}")
            return ['/', '/about']  # Default pages
    
    def _test_page(self, page):
        """
        Test a specific Vue page using Puppeteer
        
        Args:
            page (str): Page URL path
            
        Returns:
            dict: Test results for this page
        """
        logger.info(f"Testing page: {page}")
        
        # Path to Puppeteer script
        script_path = self._generate_puppeteer_script(page)
        
        if not script_path:
            return {'error': 'Failed to generate Puppeteer script'}
        
        # Run Puppeteer
        cmd = ['node', str(script_path)]
        return_code, stdout, stderr = run_command(cmd)
        
        if return_code != 0:
            logger.error(f"Puppeteer test failed: {stderr}")
            return {'error': 'Puppeteer test failed', 'details': stderr}
        
        # Parse results
        try:
            results = json.loads(stdout)
            logger.info(f"Successfully tested page: {page}")
            return results
            
        except json.JSONDecodeError:
            logger.error(f"Failed to parse Puppeteer results: {stdout}")
            return {'error': 'Failed to parse Puppeteer results'}
    
    def _generate_puppeteer_script(self, page):
        """
        Generate a Puppeteer script for testing a Vue page
        
        Args:
            page (str): Page URL path
            
        Returns:
            Path: Path to generated script
        """
        # Script directory
        script_dir = Path(__file__).parent / 'scripts'
        os.makedirs(script_dir, exist_ok=True)
        
        # Script path
        script_name = f"test_{page.strip('/').replace('/', '_')}.js"
        script_path = script_dir / script_name
        
        # Page URL
        page_url = f"http://localhost:8080{page}"
        
        # Script content
        script_content = f"""
const puppeteer = require('puppeteer');
const fs = require('fs');

(async () => {{
  const results = {{
    page: '{page}',
    url: '{page_url}',
    timestamp: new Date().toISOString(),
    metrics: {{}},
    performance: {{}},
    resourcesLoaded: [],
    errors: []
  }};
  
  try {{
    // Launch browser
    const browser = await puppeteer.launch({{ 
      headless: {str(self.headless).lower()},
      args: ['--no-sandbox', '--disable-setuid-sandbox']
    }});
    
    // Create new page
    const page = await browser.newPage();
    
    // Enable necessary APIs
    await page.setCacheEnabled(false);
    await Promise.all([
      page.coverage.startJSCoverage(),
      page.coverage.startCSSCoverage()
    ]);
    
    // Collect errors
    page.on('error', error => {{
      results.errors.push({{ type: 'error', message: error.message }});
    }});
    
    page.on('pageerror', error => {{
      results.errors.push({{ type: 'pageerror', message: error.message }});
    }});
    
    page.on('console', msg => {{
      if (msg.type() === 'error') {{
        results.errors.push({{ type: 'console', message: msg.text() }});
      }}
    }});
    
    // Collect loaded resources
    page.on('response', response => {{
      const request = response.request();
      results.resourcesLoaded.push({{
        url: request.url(),
        method: request.method(),
        resourceType: request.resourceType(),
        status: response.status(),
        size: response.headers()['content-length'] || 0
      }});
    }});
    
    // Navigate to page and measure load time
    const start = Date.now();
    await page.goto('{page_url}', {{ 
      waitUntil: 'networkidle2',
      timeout: 30000
    }});
    results.loadTime = Date.now() - start;
    
    // Collect performance metrics
    results.metrics = await page.metrics();
    
    // Collect performance timings
    results.performance = await page.evaluate(() => {{
      return JSON.parse(JSON.stringify(window.performance.timing));
    }});
    
    // Calculate render times
    results.renderTime = results.performance.domComplete - results.performance.domLoading;
    results.timeToInteractive = results.performance.domInteractive - results.performance.navigationStart;
    
    // Stop coverage
    const [jsCoverage, cssCoverage] = await Promise.all([
      page.coverage.stopJSCoverage(),
      page.coverage.stopCSSCoverage()
    ]);
    
    // Calculate unused bytes
    let jsUsed = 0;
    let jsTotal = 0;
    let cssUsed = 0;
    let cssTotal = 0;
    
    for (const entry of jsCoverage) {{
      jsTotal += entry.text.length;
      for (const range of entry.ranges) {{
        jsUsed += range.end - range.start;
      }}
    }}
    
    for (const entry of cssCoverage) {{
      cssTotal += entry.text.length;
      for (const range of entry.ranges) {{
        cssUsed += range.end - range.start;
      }}
    }}
    
    results.coverage = {{
      js: {{
        used: jsUsed,
        total: jsTotal,
        percentUsed: jsTotal ? (jsUsed / jsTotal) * 100 : 0
      }},
      css: {{
        used: cssUsed,
        total: cssTotal,
        percentUsed: cssTotal ? (cssUsed / cssTotal) * 100 : 0
      }}
    }};
    
    // Run Lighthouse audit
    // This would normally be done with lighthouse, but for simplicity we'll use puppeteer's metrics
    
    // Close browser
    await browser.close();
    
  }} catch (error) {{
    results.error = error.toString();
  }}
  
  // Output results as JSON
  console.log(JSON.stringify(results, null, 2));
}})();
"""
        
        # Write script to file
        with open(script_path, 'w') as f:
            f.write(script_content)
        
        logger.info(f"Generated Puppeteer script: {script_path}")
        return script_path
    
    def _test_interactions(self):
        """
        Test common user interactions on the Vue app
        
        Returns:
            dict: Interaction test results
        """
        logger.info("Testing user interactions")
        
        # Generate interaction script
        script_path = self._generate_interaction_script()
        
        if not script_path:
            return {'error': 'Failed to generate interaction script'}
        
        # Run Puppeteer
        cmd = ['node', str(script_path)]
        return_code, stdout, stderr = run_command(cmd)
        
        if return_code != 0:
            logger.error(f"Interaction test failed: {stderr}")
            return {'error': 'Interaction test failed', 'details': stderr}
        
        # Parse results
        try:
            results = json.loads(stdout)
            logger.info("Successfully tested interactions")
            return results
            
        except json.JSONDecodeError:
            logger.error(f"Failed to parse interaction results: {stdout}")
            return {'error': 'Failed to parse interaction results'}
    
    def _generate_interaction_script(self):
        """
        Generate a Puppeteer script for testing user interactions
        
        Returns:
            Path: Path to generated script
        """
        # Script directory
        script_dir = Path(__file__).parent / 'scripts'
        os.makedirs(script_dir, exist_ok=True)
        
        # Script path
        script_path = script_dir / "test_interactions.js"
        
        # Script content
        script_content = """
const puppeteer = require('puppeteer');

(async () => {
  const results = {
    timestamp: new Date().toISOString(),
    interactions: {},
    errors: []
  };
  
  try {
    // Launch browser
    const browser = await puppeteer.launch({ 
      headless: true,
      args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
    
    // Create new page
    const page = await browser.newPage();
    
    // Collect errors
    page.on('error', error => {
      results.errors.push({ type: 'error', message: error.message });
    });
    
    page.on('pageerror', error => {
      results.errors.push({ type: 'pageerror', message: error.message });
    });
    
    // Navigate to home page
    await page.goto('http://localhost:8080/', { 
      waitUntil: 'networkidle2',
      timeout: 30000
    });
    
    // Common interactions to test
    const interactions = [
      {
        name: 'button_click',
        selector: 'button',
        action: async (page, selector) => {
          const buttons = await page.$$(selector);
          if (buttons.length > 0) {
            const start = Date.now();
            await buttons[0].click();
            await page.waitForTimeout(500);
            return Date.now() - start;
          }
          return null;
        }
      },
      {
        name: 'input_type',
        selector: 'input[type="text"]',
        action: async (page, selector) => {
          const inputs = await page.$$(selector);
          if (inputs.length > 0) {
            const start = Date.now();
            await inputs[0].type('Test input text');
            return Date.now() - start;
          }
          return null;
        }
      },
      {
        name: 'navigation',
        selector: 'a',
        action: async (page, selector) => {
          const links = await page.$$(selector);
          if (links.length > 0) {
            const start = Date.now();
            await Promise.all([
              page.waitForNavigation({ waitUntil: 'networkidle2' }),
              links[0].click()
            ]);
            return Date.now() - start;
          }
          return null;
        }
      },
      {
        name: 'scroll',
        action: async (page) => {
          const start = Date.now();
          await page.evaluate(() => {
            window.scrollTo(0, document.body.scrollHeight);
          });
          await page.waitForTimeout(500);
          return Date.now() - start;
        }
      }
    ];
    
    // Test each interaction
    for (const interaction of interactions) {
      try {
        let duration;
        if (interaction.selector) {
          duration = await interaction.action(page, interaction.selector);
        } else {
          duration = await interaction.action(page);
        }
        
        results.interactions[interaction.name] = {
          duration,
          success: duration !== null
        };
        
        // Get FPS during interaction
        if (duration !== null) {
          const fps = await page.evaluate(() => {
            return new Promise(resolve => {
              let frameCount = 0;
              const startTime = performance.now();
              
              function countFrame() {
                frameCount++;
                if (performance.now() - startTime < 1000) {
                  requestAnimationFrame(countFrame);
                } else {
                  resolve(frameCount);
                }
              }
              
              requestAnimationFrame(countFrame);
            });
          });
          
          results.interactions[interaction.name].fps = fps;
        }
        
      } catch (error) {
        results.interactions[interaction.name] = {
          error: error.toString(),
          success: false
        };
      }
    }
    
    // Measure memory usage
    results.memoryUsage = await page.evaluate(() => {
      return performance.memory ? {
        totalJSHeapSize: performance.memory.totalJSHeapSize,
        usedJSHeapSize: performance.memory.usedJSHeapSize,
        jsHeapSizeLimit: performance.memory.jsHeapSizeLimit
      } : null;
    });
    
    // Close browser
    await browser.close();
    
  } catch (error) {
    results.error = error.toString();
  }
  
  // Output results as JSON
  console.log(JSON.stringify(results, null, 2));
})();
"""
        
        # Write script to file
        with open(script_path, 'w') as f:
            f.write(script_content)
        
        logger.info(f"Generated interaction script: {script_path}")
        return script_path
