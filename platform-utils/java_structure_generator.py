#!/usr/bin/env python
"""
Java Structure Generator

This tool generates a directory structure diagram for Java code in a specified directory.
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
        logging.FileHandler("java_structure.log")
    ]
)
logger = logging.getLogger("java_structure")

class JavaStructureGenerator:
    """Generate directory structure diagrams for Java code"""
    
    def __init__(self, root_dir, output_file=None, format='text', 
                 max_depth=None, include_pattern=None, exclude_pattern=None,
                 include_package_info=True, include_class_info=True):
        """
        Initialize the structure generator
        
        Args:
            root_dir (str): Root directory to analyze
            output_file (str, optional): Output file path
            format (str): Output format ('text', 'markdown', 'json')
            max_depth (int, optional): Maximum directory depth to display
            include_pattern (str, optional): Pattern for files/dirs to include
            exclude_pattern (str, optional): Pattern for files/dirs to exclude
            include_package_info (bool): Include package info in the output
            include_class_info (bool): Include class info in the output
        """
        self.root_dir = Path(root_dir)
        self.output_file = output_file
        self.format = format.lower()
        self.max_depth = max_depth
        self.include_pattern = include_pattern
        self.exclude_pattern = exclude_pattern
        self.include_package_info = include_package_info
        self.include_class_info = include_class_info
        
        # Statistics
        self.stats = {
            'total_dirs': 0,
            'total_files': 0,
            'total_java_files': 0,
            'total_packages': 0,
            'total_classes': 0,
            'total_interfaces': 0,
            'total_enums': 0,
        }
        
        # Collected structure
        self.structure = {}
    
    def generate(self):
        """
        Generate the directory structure
        
        Returns:
            str: The generated structure as a string
        """
        logger.info(f"Generating Java structure for: {self.root_dir}")
        
        # Analyze the structure
        self._analyze_structure()
        
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
            'is_package': False,
            'package_name': None,
        }
        
        # Count this directory
        self.stats['total_dirs'] += 1
        
        # Check if this directory is a Java package
        java_files = list(directory.glob('*.java'))
        if java_files and self.include_package_info:
            # Try to extract package name from a Java file
            for java_file in java_files:
                package_name = self._extract_package_name(java_file)
                if package_name:
                    dir_info['is_package'] = True
                    dir_info['package_name'] = package_name
                    self.stats['total_packages'] += 1
                    break
        
        # Process children
        for item in sorted(directory.iterdir(), key=lambda p: (not p.is_dir(), p.name.lower())):
            if not self._should_include(item.name):
                continue
                
            if item.is_dir():
                # Recursively process subdirectory
                subdir_info = self._analyze_directory(item, depth + 1)
                if subdir_info:
                    dir_info['children'].append(subdir_info)
            elif item.suffix.lower() == '.java':
                # Process Java file
                self.stats['total_java_files'] += 1
                file_info = self._analyze_java_file(item)
                if file_info:
                    dir_info['children'].append(file_info)
                    self.stats['total_files'] += 1
            else:
                # Count non-Java file
                self.stats['total_files'] += 1
                # Only include in structure if it's a relevant file
                if item.suffix.lower() in ['.xml', '.properties', '.yml', '.yaml']:
                    dir_info['children'].append({
                        'name': item.name,
                        'type': 'file',
                    })
        
        return dir_info
    
    def _analyze_java_file(self, file_path):
        """
        Analyze a Java file to extract information
        
        Args:
            file_path (Path): Path to the Java file
            
        Returns:
            dict: Java file information
        """
        file_info = {
            'name': file_path.name,
            'type': 'java_file',
            'classes': [],
        }
        
        if not self.include_class_info:
            return file_info
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Extract class definitions
            class_pattern = r'(?:public|protected|private|static|\s)*(?:class|interface|enum)\s+([A-Za-z0-9_]+)'
            classes = re.findall(class_pattern, content)
            
            for class_name in classes:
                # Determine type (class, interface, enum)
                if re.search(rf'interface\s+{class_name}', content):
                    class_type = 'interface'
                    self.stats['total_interfaces'] += 1
                elif re.search(rf'enum\s+{class_name}', content):
                    class_type = 'enum'
                    self.stats['total_enums'] += 1
                else:
                    class_type = 'class'
                    self.stats['total_classes'] += 1
                
                file_info['classes'].append({
                    'name': class_name,
                    'type': class_type,
                })
        
        except Exception as e:
            logger.warning(f"Error analyzing Java file {file_path}: {e}")
        
        return file_info
    
    def _extract_package_name(self, java_file):
        """
        Extract package name from a Java file
        
        Args:
            java_file (Path): Path to the Java file
            
        Returns:
            str: Package name, or None if not found
        """
        try:
            with open(java_file, 'r', encoding='utf-8') as f:
                for line in f:
                    # Look for package declaration
                    match = re.match(r'package\s+([a-zA-Z0-9_.]+);', line.strip())
                    if match:
                        return match.group(1)
        except Exception as e:
            logger.warning(f"Error extracting package from {java_file}: {e}")
        
        return None
    
    def _generate_text(self):
        """
        Generate plain text representation of the structure
        
        Returns:
            str: Text representation
        """
        lines = []
        lines.append(f"Java Structure: {self.root_dir}")
        lines.append("=" * 50)
        
        self._append_text_directory(lines, self.structure, 0)
        
        lines.append("=" * 50)
        lines.append(f"Total directories: {self.stats['total_dirs']}")
        lines.append(f"Total Java files: {self.stats['total_java_files']}")
        lines.append(f"Total packages: {self.stats['total_packages']}")
        lines.append(f"Total classes: {self.stats['total_classes']}")
        lines.append(f"Total interfaces: {self.stats['total_interfaces']}")
        lines.append(f"Total enums: {self.stats['total_enums']}")
        
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
        
        # Directory name
        lines.append(f"{prefix}├── {dir_info['name']}/")
        
        # Package info
        if dir_info.get('is_package') and dir_info.get('package_name'):
            lines.append(f"{prefix}│   (package: {dir_info['package_name']})")
        
        # Append children
        for i, child in enumerate(dir_info.get('children', [])):
            is_last = (i == len(dir_info.get('children', [])) - 1)
            
            if child['type'] == 'directory':
                self._append_text_directory(lines, child, depth + 1)
            else:
                # File
                child_prefix = prefix + '    '
                lines.append(f"{child_prefix}├── {child['name']}")
                
                # Class info
                if child['type'] == 'java_file' and child.get('classes'):
                    for cls in child['classes']:
                        lines.append(f"{child_prefix}│   └── {cls['name']} ({cls['type']})")
    
    def _generate_markdown(self):
        """
        Generate Markdown representation of the structure
        
        Returns:
            str: Markdown representation
        """
        lines = []
        lines.append(f"# Java Structure: {self.root_dir}")
        lines.append("")
        
        self._append_markdown_directory(lines, self.structure, 0)
        
        lines.append("")
        lines.append("## Summary")
        lines.append("")
        lines.append(f"- Total directories: {self.stats['total_dirs']}")
        lines.append(f"- Total Java files: {self.stats['total_java_files']}")
        lines.append(f"- Total packages: {self.stats['total_packages']}")
        lines.append(f"- Total classes: {self.stats['total_classes']}")
        lines.append(f"- Total interfaces: {self.stats['total_interfaces']}")
        lines.append(f"- Total enums: {self.stats['total_enums']}")
        
        return '\n'.join(lines)
    
    def _append_markdown_directory(self, lines, dir_info, depth):
        """
        Append directory information in Markdown format
        
        Args:
            lines (list): List of lines to append to
            dir_info (dict): Directory information
            depth (int): Current depth level
        """
        # Directory name
        if depth == 0:
            lines.append("## Directory Structure")
            lines.append("")
            lines.append("```")
        
        prefix = '  ' * depth
        
        # Directory name
        lines.append(f"{prefix}📂 {dir_info['name']}")
        
        # Package info
        if dir_info.get('is_package') and dir_info.get('package_name'):
            lines.append(f"{prefix}  📦 {dir_info['package_name']}")
        
        # Append children
        for child in dir_info.get('children', []):
            if child['type'] == 'directory':
                self._append_markdown_directory(lines, child, depth + 1)
            else:
                # File
                child_prefix = prefix + '  '
                if child['type'] == 'java_file':
                    lines.append(f"{child_prefix}📄 {child['name']}")
                else:
                    lines.append(f"{child_prefix}📄 {child['name']}")
                
                # Class info
                if child['type'] == 'java_file' and child.get('classes'):
                    for cls in child['classes']:
                        if cls['type'] == 'class':
                            icon = '🔶'
                        elif cls['type'] == 'interface':
                            icon = '🔷'
                        else:  # enum
                            icon = '🔸'
                        lines.append(f"{child_prefix}  {icon} {cls['name']} ({cls['type']})")
        
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
            'stats': self.stats
        }
        
        return json.dumps(output, indent=2)
    
    def _log_summary(self):
        """Log a summary of the analysis"""
        logger.info("====== Java Structure Summary ======")
        logger.info(f"Root directory: {self.root_dir}")
        logger.info(f"Total directories: {self.stats['total_dirs']}")
        logger.info(f"Total Java files: {self.stats['total_java_files']}")
        logger.info(f"Total packages: {self.stats['total_packages']}")
        logger.info(f"Total classes: {self.stats['total_classes']}")
        logger.info(f"Total interfaces: {self.stats['total_interfaces']}")
        logger.info(f"Total enums: {self.stats['total_enums']}")
        logger.info("===================================")

def main():
    """Command line entry point"""
    parser = argparse.ArgumentParser(
        description='Generate Java code structure diagram'
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
        help='Pattern for files/dirs to include (e.g. *.java)'
    )
    parser.add_argument(
        '-e', '--exclude',
        help='Pattern for files/dirs to exclude (e.g. target)'
    )
    parser.add_argument(
        '--no-package-info',
        action='store_true',
        help='Don\'t include package information'
    )
    parser.add_argument(
        '--no-class-info',
        action='store_true',
        help='Don\'t include class information'
    )
    
    args = parser.parse_args()
    
    # Run the structure generator
    generator = JavaStructureGenerator(
        args.root_dir,
        args.output,
        args.format,
        args.depth,
        args.include,
        args.exclude,
        not args.no_package_info,
        not args.no_class_info
    )
    output = generator.generate()
    
    # Print output if no output file specified
    if not args.output:
        print(output)

if __name__ == "__main__":
    main()
