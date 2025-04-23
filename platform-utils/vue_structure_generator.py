#!/usr/bin/env python
"""
Vue Structure Generator

This tool generates a directory structure diagram for Vue code in a specified directory.
It can generate both plain text and Markdown representations of the structure.
"""

import os
import re
import argparse
import logging
from pathlib import Path
import fnmatch
import json

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler("vue_structure.log")
    ]
)
logger = logging.getLogger("vue_structure")

class VueStructureGenerator:
    """Generate directory structure diagrams for Vue code"""
    
    def __init__(self, root_dir, output_file=None, format='text', 
                 max_depth=None, include_pattern=None, exclude_pattern=None,
                 include_component_info=True, include_router_info=True):
        """
        Initialize the structure generator
        
        Args:
            root_dir (str): Root directory to analyze
            output_file (str, optional): Output file path
            format (str): Output format ('text', 'markdown', 'json')
            max_depth (int, optional): Maximum directory depth to display
            include_pattern (str, optional): Pattern for files/dirs to include
            exclude_pattern (str, optional): Pattern for files/dirs to exclude
            include_component_info (bool): Include Vue component info
            include_router_info (bool): Include Vue router info
        """
        self.root_dir = Path(root_dir)
        self.output_file = output_file
        self.format = format.lower()
        self.max_depth = max_depth
        self.include_pattern = include_pattern
        self.exclude_pattern = exclude_pattern
        self.include_component_info = include_component_info
        self.include_router_info = include_router_info
        
        # Statistics
        self.stats = {
            'total_dirs': 0,
            'total_files': 0,
            'total_vue_files': 0,
            'total_components': 0,
            'total_pages': 0,
            'total_js_files': 0,
            'total_routes': 0
        }
        
        # Collected structure
        self.structure = {}
        
        # Router information
        self.router_info = {
            'file': None,
            'routes': []
        }
    
    def generate(self):
        """
        Generate the directory structure
        
        Returns:
            str: The generated structure as a string
        """
        logger.info(f"Generating Vue structure for: {self.root_dir}")
        
        # Analyze the structure
        self._analyze_structure()
        
        # Find router file if needed
        if self.include_router_info:
            self._find_router_file()
        
        # Generate output in the requested format
        if self.format == 'text':
            output = self._generate_text()
        elif self.format == 'markdown':
            output = self._generate_markdown()
        elif self.format == 'json':
            output = self._generate_json()
        else:
            logger.error(f"Unknown format: {self.format}")
            return None
        
        # Save to file if specified
        if self.output_file:
            try:
                with open(self.output_file, 'w', encoding='utf-8') as f:
                    f.write(output)
                logger.info(f"Structure saved to: {self.output_file}")
            except Exception as e:
                logger.error(f"Error saving to {self.output_file}: {e}")
        
        # Log summary
        self._log_summary()
        
        return output
    
    def _should_include(self, path):
        """
        Check if a path should be included based on patterns
        
        Args:
            path (str): Path to check
            
        Returns:
            bool: True if the path should be included
        """
        # First check exclude pattern
        if self.exclude_pattern and fnmatch.fnmatch(path, self.exclude_pattern):
            return False
        
        # Then check include pattern
        if self.include_pattern and not fnmatch.fnmatch(path, self.include_pattern):
            return False
        
        return True
    
    def _analyze_structure(self):
        """Analyze the directory structure and collect information"""
        logger.info("Analyzing directory structure...")
        
        self.structure = self._analyze_directory(self.root_dir, 0)
    
    def _analyze_directory(self, directory, depth):
        """
        Recursively analyze a directory and its contents
        
        Args:
            directory (Path): Directory to analyze
            depth (int): Current depth level
            
        Returns:
            dict: Directory structure information
        """
        if self.max_depth is not None and depth > self.max_depth:
            return None
        
        dir_info = {
            'name': directory.name or directory.as_posix(),
            'type': 'directory',
            'children': [],
            'is_views_dir': 'views' in directory.name.lower() or 'pages' in directory.name.lower(),
            'is_components_dir': 'components' in directory.name.lower()
        }
        
        # Count this directory
        self.stats['total_dirs'] += 1
        
        # Process children
        for item in sorted(directory.iterdir(), key=lambda p: (not p.is_dir(), p.name.lower())):
            if not self._should_include(item.name):
                continue
                
            if item.is_dir():
                # Recursively process subdirectory
                subdir_info = self._analyze_directory(item, depth + 1)
                if subdir_info:
                    dir_info['children'].append(subdir_info)
            elif item.suffix.lower() == '.vue':
                # Process Vue file
                self.stats['total_vue_files'] += 1
                file_info = self._analyze_vue_file(item, is_page=dir_info['is_views_dir'])
                if file_info:
                    dir_info['children'].append(file_info)
                    self.stats['total_files'] += 1
            elif item.suffix.lower() == '.js':
                # Process JS file
                self.stats['total_js_files'] += 1
                self.stats['total_files'] += 1
                file_info = {
                    'name': item.name,
                    'type': 'js_file'
                }
                
                # Check if this is a router file
                if 'router' in item.name.lower() and self.include_router_info:
                    file_info['is_router'] = True
                    self.router_info['file'] = str(item)
                
                dir_info['children'].append(file_info)
            else:
                # Count other files
                self.stats['total_files'] += 1
                # Only include in structure if it's a relevant file
                if item.suffix.lower() in ['.json', '.css', '.scss', '.less', '.sass', '.html']:
                    dir_info['children'].append({
                        'name': item.name,
                        'type': 'file',
                    })
        
        return dir_info
    
    def _analyze_vue_file(self, file_path, is_page=False):
        """
        Analyze a Vue file to extract information
        
        Args:
            file_path (Path): Path to the Vue file
            is_page (bool): Whether this file is in a views/pages directory
            
        Returns:
            dict: Vue file information
        """
        file_info = {
            'name': file_path.name,
            'type': 'vue_file',
            'is_page': is_page,
            'component_name': None,
            'props': [],
            'has_template': False,
            'has_script': False,
            'has_style': False
        }
        
        if is_page:
            self.stats['total_pages'] += 1
        else:
            self.stats['total_components'] += 1
        
        if not self.include_component_info:
            return file_info
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Check sections
            file_info['has_template'] = '<template>' in content
            file_info['has_script'] = '<script>' in content
            file_info['has_style'] = '<style' in content
            
            # Extract component name
            name_match = re.search(r'name:\s*[\'"]([^\'"]+)[\'"]', content)
            if name_match:
                file_info['component_name'] = name_match.group(1)
            
            # Extract props
            props_section = re.search(r'props:\s*({[^}]+}|\[[^\]]+\])', content)
            if props_section:
                props_text = props_section.group(1)
                prop_matches = re.findall(r'[\'"]([\w-]+)[\'"]', props_text)
                file_info['props'] = prop_matches
        
        except Exception as e:
            logger.warning(f"Error analyzing Vue file {file_path}: {e}")
        
        return file_info
    
    def _find_router_file(self):
        """Find and analyze the Vue router file"""
        if not self.router_info['file']:
            # Search for common router file locations
            common_paths = [
                self.root_dir / 'src/router.js',
                self.root_dir / 'src/router/index.js',
                self.root_dir / 'router.js'
            ]
            
            for path in common_paths:
                if path.exists():
                    self.router_info['file'] = str(path)
                    break
        
        # Analyze the router file if found
        if self.router_info['file']:
            self._analyze_router_file(self.router_info['file'])
    
    def _analyze_router_file(self, router_file):
        """
        Analyze the Vue router file to extract routes
        
        Args:
            router_file (str): Path to the router file
        """
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
                    self.stats['total_routes'] += 1
            
            self.router_info['routes'] = routes
            
        except Exception as e:
            logger.warning(f"Error analyzing router file {router_file}: {e}")
    
    def _generate_text(self):
        """
        Generate plain text representation of the structure
        
        Returns:
            str: Text representation
        """
        lines = []
        lines.append(f"Vue Structure: {self.root_dir}")
        lines.append("=" * 50)
        
        self._append_text_directory(lines, self.structure, 0)
        
        # Add router information if available
        if self.include_router_info and self.router_info['routes']:
            lines.append("")
            lines.append("Router Configuration")
            lines.append("-" * 50)
            lines.append(f"Router file: {self.router_info['file']}")
            lines.append("")
            lines.append("Routes:")
            for route in self.router_info['routes']:
                route_str = f"- {route['path']}"
                if 'name' in route:
                    route_str += f" (name: {route['name']})"
                if 'component' in route:
                    route_str += f" => {route['component']}"
                lines.append(route_str)
        
        lines.append("")
        lines.append("=" * 50)
        lines.append(f"Total directories: {self.stats['total_dirs']}")
        lines.append(f"Total files: {self.stats['total_files']}")
        lines.append(f"Total Vue files: {self.stats['total_vue_files']}")
        lines.append(f"  - Components: {self.stats['total_components']}")
        lines.append(f"  - Pages: {self.stats['total_pages']}")
        lines.append(f"Total JS files: {self.stats['total_js_files']}")
        if self.include_router_info:
            lines.append(f"Total routes: {self.stats['total_routes']}")
        
        return '\n'.join(lines)
    
    def _append_text_directory(self, lines, dir_info, depth):
        """
        Append directory information in text format
        
        Args:
            lines (list): List of lines to append to
            dir_info (dict): Directory information
            depth (int): Current depth level
        """
        prefix = '    ' * depth
        
        # Directory name with special indicators for special directories
        dir_name = dir_info['name']
        if dir_info.get('is_views_dir'):
            dir_name += ' (Views)'
        elif dir_info.get('is_components_dir'):
            dir_name += ' (Components)'
        
        lines.append(f"{prefix}├── {dir_name}/")
        
        # Process children
        for i, child in enumerate(dir_info.get('children', [])):
            is_last = (i == len(dir_info.get('children', [])) - 1)
            
            if child['type'] == 'directory':
                self._append_text_directory(lines, child, depth + 1)
            else:
                # File
                child_prefix = prefix + '    '
                
                # File name with additional info
                file_name = child['name']
                
                if child['type'] == 'vue_file':
                    # For Vue files, add component info
                    if child.get('component_name'):
                        file_name += f" ({child['component_name']})"
                    elif child.get('is_page'):
                        file_name += " (Page)"
                    else:
                        file_name += " (Component)"
                
                lines.append(f"{child_prefix}├── {file_name}")
                
                # Additional Vue component info
                if child['type'] == 'vue_file' and self.include_component_info:
                    # Show props
                    if child.get('props'):
                        props_str = ', '.join(child.get('props', []))
                        lines.append(f"{child_prefix}│   └── Props: {props_str}")
                    
                    # Show sections
                    sections = []
                    if child.get('has_template'):
                        sections.append('template')
                    if child.get('has_script'):
                        sections.append('script')
                    if child.get('has_style'):
                        sections.append('style')
                    
                    if sections:
                        sections_str = ', '.join(sections)
                        lines.append(f"{child_prefix}│   └── Sections: {sections_str}")
    
    def _generate_markdown(self):
        """
        Generate Markdown representation of the structure
        
        Returns:
            str: Markdown representation
        """
        lines = []
        lines.append(f"# Vue Structure: {self.root_dir}")
        lines.append("")
        
        self._append_markdown_directory(lines, self.structure, 0)
        
        # Add router information if available
        if self.include_router_info and self.router_info['routes']:
            lines.append("")
            lines.append("## Router Configuration")
            lines.append("")
            lines.append(f"Router file: `{self.router_info['file']}`")
            lines.append("")
            lines.append("### Routes:")
            lines.append("")
            for route in self.router_info['routes']:
                route_str = f"- `{route['path']}`"
                if 'name' in route:
                    route_str += f" (name: `{route['name']}`)"
                if 'component' in route:
                    route_str += f" => `{route['component']}`"
                lines.append(route_str)
        
        lines.append("")
        lines.append("## Summary")
        lines.append("")
        lines.append(f"- Total directories: {self.stats['total_dirs']}")
        lines.append(f"- Total files: {self.stats['total_files']}")
        lines.append(f"- Total Vue files: {self.stats['total_vue_files']}")
        lines.append(f"  - Components: {self.stats['total_components']}")
        lines.append(f"  - Pages: {self.stats['total_pages']}")
        lines.append(f"- Total JS files: {self.stats['total_js_files']}")
        if self.include_router_info:
            lines.append(f"- Total routes: {self.stats['total_routes']}")
        
        return '\n'.join(lines)
    
    def _append_markdown_directory(self, lines, dir_info, depth):
        """
        Append directory information in Markdown format
        
        Args:
            lines (list): List of lines to append to
            dir_info (dict): Directory information
            depth (int): Current depth level
        """
        # Directory name heading
        if depth == 0:
            lines.append("## Directory Structure")
            lines.append("")
            lines.append("```")
        
        prefix = '  ' * depth
        
        # Directory name with special indicators
        dir_name = dir_info['name']
        if dir_info.get('is_views_dir'):
            dir_name += ' (Views)'
        elif dir_info.get('is_components_dir'):
            dir_name += ' (Components)'
        
        lines.append(f"{prefix}📂 {dir_name}")
        
        # Process children
        for child in dir_info.get('children', []):
            if child['type'] == 'directory':
                self._append_markdown_directory(lines, child, depth + 1)
            else:
                # File
                child_prefix = prefix + '  '
                
                # File name with additional info
                file_name = child['name']
                
                if child['type'] == 'vue_file':
                    icon = '🟩'  # Vue file icon
                    
                    # Add component info
                    if child.get('component_name'):
                        file_name += f" ({child['component_name']})"
                    elif child.get('is_page'):
                        file_name += " (Page)"
                        icon = '📄'  # Page icon
                    else:
                        file_name += " (Component)"
                elif child['type'] == 'js_file':
                    icon = '🟨'  # JS file icon
                    if child.get('is_router'):
                        file_name += " (Router)"
                else:
                    icon = '📄'  # Generic file icon
                
                lines.append(f"{child_prefix}{icon} {file_name}")
                
                # Additional Vue component info
                if child['type'] == 'vue_file' and self.include_component_info:
                    # Show props
                    if child.get('props'):
                        props_str = ', '.join(child.get('props', []))
                        lines.append(f"{child_prefix}  ↳ Props: {props_str}")
                    
                    # Show sections
                    sections = []
                    if child.get('has_template'):
                        sections.append('template')
                    if child.get('has_script'):
                        sections.append('script')
                    if child.get('has_style'):
                        sections.append('style')
                    
                    if sections:
                        sections_str = ', '.join(sections)
                        lines.append(f"{child_prefix}  ↳ Sections: {sections_str}")
        
        if depth == 0:
            lines.append("```")
    
    def _generate_json(self):
        """
        Generate JSON representation of the structure
        
        Returns:
            str: JSON representation
        """
        output = {
            'structure': self.structure,
            'router': self.router_info,
            'stats': self.stats
        }
        
        return json.dumps(output, indent=2)
    
    def _log_summary(self):
        """Log a summary of the analysis"""
        logger.info("====== Vue Structure Summary ======")
        logger.info(f"Root directory: {self.root_dir}")
        logger.info(f"Total directories: {self.stats['total_dirs']}")
        logger.info(f"Total files: {self.stats['total_files']}")
        logger.info(f"Total Vue files: {self.stats['total_vue_files']}")
        logger.info(f"  - Components: {self.stats['total_components']}")
        logger.info(f"  - Pages: {self.stats['total_pages']}")
        logger.info(f"Total JS files: {self.stats['total_js_files']}")
        if self.include_router_info:
            logger.info(f"Total routes: {self.stats['total_routes']}")
        logger.info("=================================")

def main():
    """Command line entry point"""
    parser = argparse.ArgumentParser(
        description='Generate Vue code structure diagram'
    )
    parser.add_argument(
        'root_dir',
        help='Root directory to analyze'
    )
    parser.add_argument(
        '-o', '--output',
        help='Output file path'
    )
    parser.add_argument(
        '-f', '--format',
        choices=['text', 'markdown', 'json'],
        default='text',
        help='Output format (default: text)'
    )
    parser.add_argument(
        '-d', '--depth',
        type=int,
        help='Maximum directory depth to display'
    )
    parser.add_argument(
        '-i', '--include',
        help='Pattern for files/dirs to include (e.g. *.vue)'
    )
    parser.add_argument(
        '-e', '--exclude',
        help='Pattern for files/dirs to exclude (e.g. node_modules)'
    )
    parser.add_argument(
        '--no-component-info',
        action='store_true',
        help='Don\'t include Vue component information'
    )
    parser.add_argument(
        '--no-router-info',
        action='store_true',
        help='Don\'t include Vue router information'
    )
    
    args = parser.parse_args()
    
    # Run the structure generator
    generator = VueStructureGenerator(
        args.root_dir,
        args.output,
        args.format,
        args.depth,
        args.include,
        args.exclude,
        not args.no_component_info,
        not args.no_router_info
    )
    output = generator.generate()
    
    # Print output if no output file specified
    if not args.output:
        print(output)

if __name__ == "__main__":
    main()
