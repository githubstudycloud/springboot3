"""
Vue Performance Testing Utilities

Provides helper functions for Vue frontend performance testing.
"""

import os
import json
import logging
import subprocess
import re
from pathlib import Path

# Set up logging
logger = logging.getLogger("vue_test_utils")

def parse_webpack_stats(stats_file):
    """
    Parse webpack stats to analyze bundle size and composition
    
    Args:
        stats_file (str): Path to webpack stats JSON file
        
    Returns:
        dict: Webpack stats analysis
    """
    if not os.path.exists(stats_file):
        return {'error': 'Stats file not found'}
    
    try:
        with open(stats_file, 'r') as f:
            stats = json.load(f)
        
        # Extract assets information
        assets = {}
        total_size = 0
        
        if 'assets' in stats:
            for asset in stats['assets']:
                if 'name' in asset and 'size' in asset:
                    assets[asset['name']] = asset['size']
                    total_size += asset['size']
        
        # Extract module information
        modules = {}
        if 'modules' in stats:
            for module in stats['modules']:
                if 'name' in module and 'size' in module:
                    # Clean module name
                    name = module['name']
                    if name.startswith('./'):
                        name = name[2:]
                    
                    # Group by main categories
                    if 'node_modules' in name:
                        category = name.split('node_modules/')[1].split('/')[0]
                        key = f"npm:{category}"
                    else:
                        key = 'app'
                    
                    if key in modules:
                        modules[key] += module['size']
                    else:
                        modules[key] = module['size']
        
        # Calculate percentages
        module_percentages = {}
        for module, size in modules.items():
            module_percentages[module] = (size / total_size) * 100 if total_size > 0 else 0
        
        # Sort by size
        sorted_modules = dict(sorted(
            module_percentages.items(), 
            key=lambda x: x[1], 
            reverse=True
        ))
        
        return {
            'total_size': total_size,
            'assets': assets,
            'modules': sorted_modules
        }
        
    except Exception as e:
        logger.error(f"Error parsing webpack stats: {e}")
        return {'error': f'Failed to parse webpack stats: {str(e)}'}

def analyze_bundle_size(dist_dir):
    """
    Analyze production bundle size
    
    Args:
        dist_dir (str): Path to production build directory
        
    Returns:
        dict: Bundle size analysis
    """
    if not os.path.exists(dist_dir):
        return {'error': 'Build directory not found'}
    
    try:
        results = {
            'total_size': 0,
            'js_size': 0,
            'css_size': 0,
            'assets_size': 0,
            'files': {}
        }
        
        # Walk through all files in dist directory
        for root, dirs, files in os.walk(dist_dir):
            for file in files:
                file_path = os.path.join(root, file)
                rel_path = os.path.relpath(file_path, dist_dir)
                
                # Get file size
                size = os.path.getsize(file_path)
                results['total_size'] += size
                
                # Categorize by file type
                if file.endswith('.js'):
                    results['js_size'] += size
                elif file.endswith('.css'):
                    results['css_size'] += size
                elif file.endswith(('.png', '.jpg', '.jpeg', '.gif', '.svg', '.webp')):
                    results['assets_size'] += size
                
                # Store individual file info
                results['files'][rel_path] = size
        
        # Sort files by size
        results['files'] = dict(sorted(
            results['files'].items(), 
            key=lambda x: x[1], 
            reverse=True
        ))
        
        return results
        
    except Exception as e:
        logger.error(f"Error analyzing bundle size: {e}")
        return {'error': f'Failed to analyze bundle size: {str(e)}'}

def extract_vue_components(src_dir):
    """
    Extract Vue components information
    
    Args:
        src_dir (str): Path to Vue src directory
        
    Returns:
        dict: Vue components information
    """
    if not os.path.exists(src_dir):
        return {'error': 'Source directory not found'}
    
    try:
        components = []
        
        # Find all .vue files
        for root, dirs, files in os.walk(src_dir):
            for file in files:
                if file.endswith('.vue'):
                    file_path = os.path.join(root, file)
                    rel_path = os.path.relpath(file_path, src_dir)
                    
                    # Read file content
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    # Check if it's a single file component
                    has_template = '<template>' in content
                    has_script = '<script>' in content
                    has_style = '<style' in content
                    
                    # Extract component name
                    name = None
                    name_match = re.search(r'name:\s*[\'"]([^\'"]+)[\'"]', content)
                    if name_match:
                        name = name_match.group(1)
                    
                    # Extract props
                    props = []
                    props_section = re.search(r'props:\s*({[^}]+}|\[[^\]]+\])', content)
                    if props_section:
                        props_text = props_section.group(1)
                        prop_matches = re.findall(r'[\'"]([\w-]+)[\'"]', props_text)
                        props = prop_matches
                    
                    # Add component info
                    components.append({
                        'path': rel_path,
                        'name': name,
                        'has_template': has_template,
                        'has_script': has_script,
                        'has_style': has_style,
                        'props': props,
                        'size': os.path.getsize(file_path)
                    })
        
        return {
            'component_count': len(components),
            'components': components
        }
        
    except Exception as e:
        logger.error(f"Error extracting Vue components: {e}")
        return {'error': f'Failed to extract Vue components: {str(e)}'}

def analyze_lighthouse_report(report_file):
    """
    Analyze Lighthouse performance report
    
    Args:
        report_file (str): Path to Lighthouse JSON report
        
    Returns:
        dict: Lighthouse analysis results
    """
    if not os.path.exists(report_file):
        return {'error': 'Lighthouse report file not found'}
    
    try:
        with open(report_file, 'r') as f:
            report = json.load(f)
        
        # Extract key metrics
        performance = {}
        if 'categories' in report and 'performance' in report['categories']:
            performance['score'] = report['categories']['performance']['score'] * 100
        
        # Extract audits
        metrics = {}
        key_metrics = [
            'first-contentful-paint',
            'speed-index',
            'largest-contentful-paint',
            'interactive',
            'total-blocking-time',
            'cumulative-layout-shift'
        ]
        
        if 'audits' in report:
            for metric in key_metrics:
                if metric in report['audits']:
                    metrics[metric] = {
                        'score': report['audits'][metric]['score'],
                        'value': report['audits'][metric]['numericValue']
                    }
        
        # Extract opportunities
        opportunities = []
        if 'audits' in report:
            for audit_id, audit in report['audits'].items():
                if audit.get('details', {}).get('type') == 'opportunity':
                    opportunities.append({
                        'id': audit_id,
                        'title': audit.get('title', ''),
                        'description': audit.get('description', ''),
                        'score': audit.get('score', 0),
                        'saving_ms': audit.get('details', {}).get('overallSavingsMs', 0)
                    })
        
        # Sort opportunities by potential savings
        opportunities.sort(key=lambda x: x['saving_ms'], reverse=True)
        
        return {
            'performance_score': performance.get('score', 0),
            'metrics': metrics,
            'opportunities': opportunities
        }
        
    except Exception as e:
        logger.error(f"Error analyzing Lighthouse report: {e}")
        return {'error': f'Failed to analyze Lighthouse report: {str(e)}'}

def run_lighthouse(url, output_file, opts=None):
    """
    Run Lighthouse performance audit
    
    Args:
        url (str): URL to test
        output_file (str): Path to save report
        opts (dict): Additional Lighthouse options
        
    Returns:
        bool: Success status
    """
    try:
        # Create directory if it doesn't exist
        os.makedirs(os.path.dirname(output_file), exist_ok=True)
        
        # Lighthouse command
        cmd = [
            'lighthouse',
            url,
            '--output=json',
            f'--output-path={output_file}',
            '--chrome-flags="--headless --no-sandbox --disable-gpu"'
        ]
        
        # Add additional options
        if opts:
            for k, v in opts.items():
                cmd.append(f'--{k}={v}')
        
        result = subprocess.run(
            cmd, 
            stdout=subprocess.PIPE, 
            stderr=subprocess.PIPE, 
            text=True,
            shell=True
        )
        
        if result.returncode != 0:
            logger.error(f"Lighthouse failed: {result.stderr}")
            return False
        
        logger.info(f"Lighthouse report saved to {output_file}")
        return True
        
    except Exception as e:
        logger.error(f"Error running Lighthouse: {e}")
        return False

def measure_component_render_time(component_name, test_script):
    """
    Measure render time for a specific Vue component
    
    Args:
        component_name (str): Component name
        test_script (str): Path to test script
        
    Returns:
        dict: Render time metrics
    """
    try:
        cmd = ['node', test_script, component_name]
        result = subprocess.run(
            cmd, 
            stdout=subprocess.PIPE, 
            stderr=subprocess.PIPE, 
            text=True
        )
        
        if result.returncode != 0:
            logger.error(f"Component render test failed: {result.stderr}")
            return {'error': 'Component render test failed'}
        
        # Parse results
        try:
            metrics = json.loads(result.stdout)
            logger.info(f"Successfully measured render time for component: {component_name}")
            return metrics
            
        except json.JSONDecodeError:
            logger.error(f"Failed to parse render metrics: {result.stdout}")
            return {'error': 'Failed to parse render metrics'}
        
    except Exception as e:
        logger.error(f"Error measuring component render time: {e}")
        return {'error': f'Failed to measure component render time: {str(e)}'}

def analyze_vue_router(router_file):
    """
    Analyze Vue Router configuration
    
    Args:
        router_file (str): Path to Vue Router configuration file
        
    Returns:
        dict: Router analysis results
    """
    if not os.path.exists(router_file):
        return {'error': 'Router file not found'}
    
    try:
        with open(router_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Extract routes
        routes = []
        route_blocks = re.findall(r'{[^{]*path:[^{]*}', content)
        
        for block in route_blocks:
            path_match = re.search(r'path:\s*[\'"]([^\'"]+)[\'"]', block)
            name_match = re.search(r'name:\s*[\'"]([^\'"]+)[\'"]', block)
            component_match = re.search(r'component:\s*([A-Za-z0-9_]+)', block)
            
            if path_match:
                route = {
                    'path': path_match.group(1)
                }
                
                if name_match:
                    route['name'] = name_match.group(1)
                    
                if component_match:
                    route['component'] = component_match.group(1)
                
                routes.append(route)
        
        # Check for lazy loading
        lazy_loading = False
        if re.search(r'import\s*\([\'"]', content) or 'webpackChunkName' in content:
            lazy_loading = True
        
        return {
            'route_count': len(routes),
            'routes': routes,
            'lazy_loading': lazy_loading
        }
        
    except Exception as e:
        logger.error(f"Error analyzing Vue Router: {e}")
        return {'error': f'Failed to analyze Vue Router: {str(e)}'}

def analyze_vuex_store(store_file):
    """
    Analyze Vuex store configuration
    
    Args:
        store_file (str): Path to Vuex store file
        
    Returns:
        dict: Vuex store analysis
    """
    if not os.path.exists(store_file):
        return {'error': 'Store file not found'}
    
    try:
        with open(store_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Extract state properties
        state_props = []
        state_block = re.search(r'state:\s*{([^}]*)}', content)
        if state_block:
            state_text = state_block.group(1)
            prop_matches = re.findall(r'([A-Za-z0-9_]+):', state_text)
            state_props = prop_matches
        
        # Extract mutations
        mutations = []
        mutations_block = re.search(r'mutations:\s*{([^}]*)}', content)
        if mutations_block:
            mutations_text = mutations_block.group(1)
            mutation_matches = re.findall(r'([A-Za-z0-9_]+):', mutations_text)
            mutations = mutation_matches
        
        # Extract actions
        actions = []
        actions_block = re.search(r'actions:\s*{([^}]*)}', content)
        if actions_block:
            actions_text = actions_block.group(1)
            action_matches = re.findall(r'([A-Za-z0-9_]+):', actions_text)
            actions = action_matches
        
        # Extract getters
        getters = []
        getters_block = re.search(r'getters:\s*{([^}]*)}', content)
        if getters_block:
            getters_text = getters_block.group(1)
            getter_matches = re.findall(r'([A-Za-z0-9_]+):', getters_text)
            getters = getter_matches
        
        # Check for modules
        has_modules = 'modules:' in content
        
        return {
            'state_props_count': len(state_props),
            'state_props': state_props,
            'mutations_count': len(mutations),
            'mutations': mutations,
            'actions_count': len(actions),
            'actions': actions,
            'getters_count': len(getters),
            'getters': getters,
            'has_modules': has_modules
        }
        
    except Exception as e:
        logger.error(f"Error analyzing Vuex store: {e}")
        return {'error': f'Failed to analyze Vuex store: {str(e)}'}

def generate_component_test(component_name, output_file):
    """
    Generate test script for measuring Vue component render performance
    
    Args:
        component_name (str): Component name
        output_file (str): Path to save test script
        
    Returns:
        bool: Success status
    """
    try:
        # Create directory if it doesn't exist
        os.makedirs(os.path.dirname(output_file), exist_ok=True)
        
        # Script content
        script_content = f"""
const { performance } = require('perf_hooks');
const Vue = require('vue');
const { createRenderer } = require('vue-server-renderer');

// Import the component
// This assumes the component is available as a global when running in Node
// In a real implementation, you would need to properly import the component
const Component = Vue.component('{component_name}');

// Create a Vue instance with the component
const app = new Vue({{
  render: h => h(Component, {{
    // Add props here if needed
    props: {{}}
  }})
}});

// Create a renderer
const renderer = createRenderer();

// Measure render time
const metrics = {{
  component: '{component_name}',
  timestamp: new Date().toISOString(),
  measurements: []
}};

// Run multiple measurements
const ITERATIONS = 10;

(async () => {{
  for (let i = 0; i < ITERATIONS; i++) {{
    const start = performance.now();
    
    // Render component to string
    await renderer.renderToString(app);
    
    const end = performance.now();
    metrics.measurements.push(end - start);
  }}
  
  // Calculate statistics
  metrics.min = Math.min(...metrics.measurements);
  metrics.max = Math.max(...metrics.measurements);
  metrics.avg = metrics.measurements.reduce((sum, val) => sum + val, 0) / ITERATIONS;
  
  // Output as JSON
  console.log(JSON.stringify(metrics, null, 2));
}})();
"""
        
        # Write script to file
        with open(output_file, 'w') as f:
            f.write(script_content)
        
        logger.info(f"Generated component test script: {output_file}")
        return True
        
    except Exception as e:
        logger.error(f"Error generating component test: {e}")
        return False
