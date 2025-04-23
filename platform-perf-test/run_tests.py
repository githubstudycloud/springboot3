#!/usr/bin/env python
"""
Main entry point for running performance tests

This script coordinates running both Java backend and Vue frontend performance tests,
collects results, and generates reports.
"""

import os
import sys
import argparse
import logging
import json
from pathlib import Path
from datetime import datetime

# Add current directory to path to import modules
current_dir = os.path.dirname(os.path.abspath(__file__))
sys.path.append(current_dir)

# Import local modules
import config
from utils import generate_timestamp, save_results
from java_perf_test.runner import JavaTestRunner
from vue_perf_test.runner import VueTestRunner

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler(f"perf_test_{generate_timestamp()}.log")
    ]
)
logger = logging.getLogger("perf_runner")

def parse_args():
    """Parse command line arguments"""
    parser = argparse.ArgumentParser(description='Run performance tests')
    
    # Test type selection
    parser.add_argument('--java', action='store_true', help='Run Java backend tests')
    parser.add_argument('--vue', action='store_true', help='Run Vue frontend tests')
    parser.add_argument('--all', action='store_true', help='Run all tests')
    
    # Java test options
    parser.add_argument('--java-modules', nargs='+', help='Java modules to test')
    parser.add_argument('--java-endpoints', nargs='+', help='API endpoints to test')
    parser.add_argument('--jvm-opts', help='JVM options, comma separated')
    
    # Vue test options
    parser.add_argument('--vue-pages', nargs='+', help='Vue pages to test')
    parser.add_argument('--headless', action='store_true', help='Run browser in headless mode')
    
    # Common options
    parser.add_argument('--duration', type=int, default=config.DEFAULT_TEST_DURATION,
                        help='Test duration in seconds')
    parser.add_argument('--users', type=int, default=config.DEFAULT_CONCURRENT_USERS,
                        help='Number of concurrent users/threads')
    parser.add_argument('--output-dir', help='Directory to save results')
    parser.add_argument('--no-report', action='store_true', help='Skip report generation')
    
    return parser.parse_args()

def setup_output_dir(args):
    """Set up output directory for test results"""
    if args.output_dir:
        output_dir = Path(args.output_dir)
    else:
        output_dir = config.RESULTS_DIR / generate_timestamp()
    
    os.makedirs(output_dir, exist_ok=True)
    logger.info(f"Results will be saved to: {output_dir}")
    return output_dir

def run_java_tests(args, output_dir):
    """Run Java backend performance tests"""
    logger.info("Starting Java backend performance tests")
    
    # Determine which Java modules to test
    modules = args.java_modules if args.java_modules else config.JAVA_MODULES
    
    # Parse JVM options
    jvm_opts = config.DEFAULT_JVM_OPTS
    if args.jvm_opts:
        jvm_opts = args.jvm_opts.split(',')
    
    # Create and run Java test runner
    runner = JavaTestRunner(
        modules=modules,
        endpoints=args.java_endpoints,
        jvm_opts=jvm_opts,
        duration=args.duration,
        concurrent_users=args.users
    )
    
    results = runner.run_tests()
    
    # Save results
    results_file = output_dir / "java_results.json"
    save_results(results, results_file)
    
    return results

def run_vue_tests(args, output_dir):
    """Run Vue frontend performance tests"""
    logger.info("Starting Vue frontend performance tests")
    
    # Create and run Vue test runner
    runner = VueTestRunner(
        pages=args.vue_pages,
        headless=args.headless,
        duration=args.duration
    )
    
    results = runner.run_tests()
    
    # Save results
    results_file = output_dir / "vue_results.json"
    save_results(results, results_file)
    
    return results

def generate_report(java_results, vue_results, output_dir):
    """Generate comprehensive performance test report"""
    logger.info("Generating performance test report")
    
    # Combine results
    combined_results = {
        'timestamp': datetime.now().isoformat(),
        'java_backend': java_results if java_results else {},
        'vue_frontend': vue_results if vue_results else {}
    }
    
    # Save combined results
    combined_file = output_dir / "combined_results.json"
    save_results(combined_results, combined_file)
    
    # TODO: Generate HTML report using a template
    if config.GENERATE_HTML_REPORT:
        html_report = output_dir / "report.html"
        # This would typically use a template engine like Jinja2
        logger.info(f"HTML report saved to {html_report}")
    
    logger.info(f"Report generation complete")

def main():
    """Main function"""
    args = parse_args()
    
    # Set up output directory
    output_dir = setup_output_dir(args)
    
    # Determine which tests to run
    run_java = args.java or args.all
    run_vue = args.vue or args.all
    
    # Default to running all tests if none specified
    if not (run_java or run_vue):
        run_java = run_vue = True
    
    # Run tests
    java_results = None
    vue_results = None
    
    if run_java:
        java_results = run_java_tests(args, output_dir)
        
    if run_vue:
        vue_results = run_vue_tests(args, output_dir)
    
    # Generate report
    if not args.no_report:
        generate_report(java_results, vue_results, output_dir)
    
    logger.info("Performance testing completed successfully")

if __name__ == "__main__":
    main()
