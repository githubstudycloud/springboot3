#!/usr/bin/env python
"""
Package Renamer Utility

This tool is designed to rename Java package names throughout a project,
including Java source files, configuration files, annotations and comments.
"""

import os
import re
import argparse
import logging
from pathlib import Path
import fnmatch
import shutil
import xml.etree.ElementTree as ET
from xml.dom import minidom

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler("package_renamer.log")
    ]
)
logger = logging.getLogger("package_renamer")

class PackageRenamer:
    """Utility class for renaming Java packages in a project"""
    
    def __init__(self, source_root, old_package, new_package, dry_run=False):
        """
        Initialize the package renamer
        
        Args:
            source_root (str): Root directory of the project
            old_package (str): Original package name (e.g. 'com.platform')
            new_package (str): New package name (e.g. 'com.aaa.aa.plat')
            dry_run (bool): If True, only report what would change without making actual changes
        """
        self.source_root = Path(source_root)
        self.old_package = old_package
        self.new_package = new_package
        self.dry_run = dry_run
        
        # File extensions to process
        self.java_extensions = ['.java']
        self.xml_extensions = ['.xml', '.xhtml', '.xsd']
        self.properties_extensions = ['.properties']
        self.yaml_extensions = ['.yml', '.yaml']
        self.config_extensions = ['.conf', '.cfg', '.config']
        self.text_extensions = ['.txt', '.md']
        self.js_extensions = ['.js', '.jsx', '.json']
        self.vue_extensions = ['.vue']
        
        # Combined list of all extensions to process
        self.all_extensions = (
            self.java_extensions + 
            self.xml_extensions + 
            self.properties_extensions + 
            self.yaml_extensions + 
            self.config_extensions + 
            self.text_extensions + 
            self.js_extensions + 
            self.vue_extensions
        )
        
        # Generate file patterns for file matching
        self.package_patterns = self._generate_package_patterns()
        
        # Statistics
        self.stats = {
            'directories_moved': 0,
            'files_processed': 0,
            'files_changed': 0,
            'total_replacements': 0,
        }
        
        # Keep track of files to process after directory structure is updated
        self.files_to_process = []
    
    def _generate_package_patterns(self):
        """
        Generate regex patterns for finding package references
        
        Returns:
            dict: Dictionary of patterns for different file types
        """
        # Escape dots in package names for regex
        old_pkg_regex = self.old_package.replace('.', r'\.')
        
        # Generate patterns for different contexts
        patterns = {
            'java_package': re.compile(rf'package\s+{old_pkg_regex}(\.|\s|;|$)'),
            'java_import': re.compile(rf'import\s+{old_pkg_regex}(\.|\s|;|$)'),
            'java_annotation': re.compile(rf'@.+\(\s*"?{old_pkg_regex}(\.|\s|"|,|\)|\})'),
            'java_comment': re.compile(rf'(/\*+|\*|//)\s*.*{old_pkg_regex}(\.|\s|;|$)'),
            'java_fqcn': re.compile(rf'{old_pkg_regex}\.[A-Z][a-zA-Z0-9_]+'),
            'xml_package': re.compile(rf'<.+{old_pkg_regex}(\.|\s|"|\'|>|/)'),
            'properties_package': re.compile(rf'={old_pkg_regex}(\.|\s|;|$)'),
            'yaml_package': re.compile(rf':\s+{old_pkg_regex}(\.|\s|;|$)'),
            'general_text': re.compile(rf'{old_pkg_regex}(\.|\s|;|:|,|$)'),
        }
        
        return patterns
    
    def run(self):
        """
        Run the package renaming process
        
        Returns:
            dict: Statistics about the renaming operation
        """
        logger.info(f"Starting package rename: {self.old_package} -> {self.new_package}")
        logger.info(f"Project root: {self.source_root}")
        logger.info(f"Dry run: {self.dry_run}")
        
        # First, collect files that need to be processed
        self._collect_files()
        
        # Then move directory structure to match new package
        self._move_directories()
        
        # Now process all the collected files
        self._process_files()
        
        # Log summary
        self._log_summary()
        
        return self.stats
    
    def _collect_files(self):
        """Collect all files that need to be processed"""
        logger.info("Collecting files to process...")
        
        for root, dirs, files in os.walk(self.source_root):
            for file in files:
                file_path = os.path.join(root, file)
                file_ext = os.path.splitext(file)[1].lower()
                
                if file_ext in self.all_extensions:
                    self.files_to_process.append(file_path)
        
        logger.info(f"Collected {len(self.files_to_process)} files to process")
    
    def _move_directories(self):
        """
        Move Java package directories to match new package structure
        """
        logger.info("Updating directory structure...")
        
        # Convert package names to directory paths
        old_pkg_path = self.old_package.replace('.', os.sep)
        new_pkg_path = self.new_package.replace('.', os.sep)
        
        # Find all src/main/java and src/test/java directories
        java_dirs = []
        for root, dirs, files in os.walk(self.source_root):
            if 'src' in root and ('main' in root or 'test' in root) and 'java' in root:
                java_dirs.append(root)
        
        for java_dir in java_dirs:
            old_dir = os.path.join(java_dir, old_pkg_path)
            if os.path.exists(old_dir) and os.path.isdir(old_dir):
                new_dir = os.path.join(java_dir, new_pkg_path)
                
                if self.dry_run:
                    logger.info(f"Would move: {old_dir} -> {new_dir}")
                else:
                    # Create parent directories if they don't exist
                    os.makedirs(os.path.dirname(new_dir), exist_ok=True)
                    
                    # Move the directory
                    logger.info(f"Moving: {old_dir} -> {new_dir}")
                    try:
                        # Make sure the destination directory doesn't exist
                        if os.path.exists(new_dir):
                            # If it exists but is empty, we can continue
                            if not os.listdir(new_dir):
                                os.rmdir(new_dir)
                            else:
                                logger.warning(f"Destination directory already exists and is not empty: {new_dir}")
                                continue
                        
                        # Create parent directories
                        os.makedirs(os.path.dirname(new_dir), exist_ok=True)
                        
                        # Move the directory
                        shutil.move(old_dir, new_dir)
                        self.stats['directories_moved'] += 1
                    except Exception as e:
                        logger.error(f"Error moving directory {old_dir} to {new_dir}: {e}")
    
    def _process_files(self):
        """Process all collected files to replace package references"""
        logger.info("Processing files to replace package references...")
        
        for file_path in self.files_to_process:
            try:
                self._process_file(file_path)
                self.stats['files_processed'] += 1
            except Exception as e:
                logger.error(f"Error processing file {file_path}: {e}")
    
    def _process_file(self, file_path):
        """
        Process a single file to replace package references
        
        Args:
            file_path (str): Path to the file to process
        """
        file_ext = os.path.splitext(file_path)[1].lower()
        
        # Determine which processor to use based on file extension
        if file_ext in self.java_extensions:
            processor = self._process_java_file
        elif file_ext in self.xml_extensions:
            processor = self._process_xml_file
        elif file_ext in self.properties_extensions:
            processor = self._process_properties_file
        elif file_ext in self.yaml_extensions:
            processor = self._process_yaml_file
        else:
            # Generic text processor for other types
            processor = self._process_text_file
        
        # Process the file
        changes = processor(file_path)
        
        if changes > 0:
            self.stats['files_changed'] += 1
            self.stats['total_replacements'] += changes
    
    def _process_java_file(self, file_path):
        """
        Process a Java file to replace package references
        
        Args:
            file_path (str): Path to the Java file
            
        Returns:
            int: Number of replacements made
        """
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        changes = 0
        new_content = content
        
        # Replace package declaration
        pattern = self.package_patterns['java_package']
        new_content, count = pattern.subn(
            lambda m: m.group(0).replace(self.old_package, self.new_package), 
            new_content
        )
        changes += count
        
        # Replace import statements
        pattern = self.package_patterns['java_import']
        new_content, count = pattern.subn(
            lambda m: m.group(0).replace(self.old_package, self.new_package), 
            new_content
        )
        changes += count
        
        # Replace annotations
        pattern = self.package_patterns['java_annotation']
        new_content, count = pattern.subn(
            lambda m: m.group(0).replace(self.old_package, self.new_package), 
            new_content
        )
        changes += count
        
        # Replace fully qualified class names
        pattern = self.package_patterns['java_fqcn']
        new_content, count = pattern.subn(
            lambda m: m.group(0).replace(self.old_package, self.new_package), 
            new_content
        )
        changes += count
        
        # Replace in comments
        pattern = self.package_patterns['java_comment']
        new_content, count = pattern.subn(
            lambda m: m.group(0).replace(self.old_package, self.new_package), 
            new_content
        )
        changes += count
        
        # Write changes if any were made and not in dry run mode
        if changes > 0 and not self.dry_run:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            logger.info(f"Updated Java file: {file_path} ({changes} replacements)")
        elif changes > 0:
            logger.info(f"Would update Java file: {file_path} ({changes} replacements)")
        
        return changes
    
    def _process_xml_file(self, file_path):
        """
        Process an XML file to replace package references
        
        Args:
            file_path (str): Path to the XML file
            
        Returns:
            int: Number of replacements made
        """
        try:
            # First try to parse as valid XML
            tree = ET.parse(file_path)
            root = tree.getroot()
            
            # Unfortunately, we can't easily count replacements with ElementTree
            # So we'll just do a text-based replacement
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            pattern = self.package_patterns['xml_package']
            new_content, count = pattern.subn(
                lambda m: m.group(0).replace(self.old_package, self.new_package), 
                content
            )
            
            if count > 0 and not self.dry_run:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                logger.info(f"Updated XML file: {file_path} ({count} replacements)")
            elif count > 0:
                logger.info(f"Would update XML file: {file_path} ({count} replacements)")
            
            return count
            
        except ET.ParseError:
            # If it's not valid XML, fall back to text replacement
            return self._process_text_file(file_path)
    
    def _process_properties_file(self, file_path):
        """
        Process a properties file to replace package references
        
        Args:
            file_path (str): Path to the properties file
            
        Returns:
            int: Number of replacements made
        """
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        pattern = self.package_patterns['properties_package']
        new_content, count = pattern.subn(
            lambda m: m.group(0).replace(self.old_package, self.new_package), 
            content
        )
        
        # Also try general text replacement
        pattern = self.package_patterns['general_text']
        new_content, count2 = pattern.subn(
            lambda m: m.group(0).replace(self.old_package, self.new_package), 
            new_content
        )
        count += count2
        
        if count > 0 and not self.dry_run:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            logger.info(f"Updated properties file: {file_path} ({count} replacements)")
        elif count > 0:
            logger.info(f"Would update properties file: {file_path} ({count} replacements)")
        
        return count
    
    def _process_yaml_file(self, file_path):
        """
        Process a YAML file to replace package references
        
        Args:
            file_path (str): Path to the YAML file
            
        Returns:
            int: Number of replacements made
        """
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        pattern = self.package_patterns['yaml_package']
        new_content, count = pattern.subn(
            lambda m: m.group(0).replace(self.old_package, self.new_package), 
            content
        )
        
        # Also try general text replacement
        pattern = self.package_patterns['general_text']
        new_content, count2 = pattern.subn(
            lambda m: m.group(0).replace(self.old_package, self.new_package), 
            new_content
        )
        count += count2
        
        if count > 0 and not self.dry_run:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            logger.info(f"Updated YAML file: {file_path} ({count} replacements)")
        elif count > 0:
            logger.info(f"Would update YAML file: {file_path} ({count} replacements)")
        
        return count
    
    def _process_text_file(self, file_path):
        """
        Process a generic text file to replace package references
        
        Args:
            file_path (str): Path to the text file
            
        Returns:
            int: Number of replacements made
        """
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        pattern = self.package_patterns['general_text']
        new_content, count = pattern.subn(
            lambda m: m.group(0).replace(self.old_package, self.new_package), 
            content
        )
        
        if count > 0 and not self.dry_run:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            logger.info(f"Updated text file: {file_path} ({count} replacements)")
        elif count > 0:
            logger.info(f"Would update text file: {file_path} ({count} replacements)")
        
        return count
    
    def _log_summary(self):
        """Log a summary of the changes made"""
        logger.info("====== Package Rename Summary ======")
        logger.info(f"Old package: {self.old_package}")
        logger.info(f"New package: {self.new_package}")
        logger.info(f"Directories moved: {self.stats['directories_moved']}")
        logger.info(f"Files processed: {self.stats['files_processed']}")
        logger.info(f"Files changed: {self.stats['files_changed']}")
        logger.info(f"Total replacements: {self.stats['total_replacements']}")
        if self.dry_run:
            logger.info("DRY RUN - No actual changes were made")
        logger.info("====================================")

def main():
    """Command line entry point"""
    parser = argparse.ArgumentParser(
        description='Rename Java package throughout a project'
    )
    parser.add_argument(
        'source_root',
        help='Root directory of the project'
    )
    parser.add_argument(
        'old_package',
        help='Original package name (e.g. com.platform)'
    )
    parser.add_argument(
        'new_package',
        help='New package name (e.g. com.aaa.aa.plat)'
    )
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Only report what would change without making actual changes'
    )
    
    args = parser.parse_args()
    
    # Run the package renamer
    renamer = PackageRenamer(
        args.source_root,
        args.old_package,
        args.new_package,
        args.dry_run
    )
    renamer.run()

if __name__ == "__main__":
    main()
