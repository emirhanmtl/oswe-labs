import os
import re
import json
from utils import detect_language, generate_html_report
from termcolor import colored

PATTERNS_DIR = "scanners"

# Öncelik haritası
PRIORITY = {
    "Authentication Bypass": 1,
    "Privilege Escalation": 2,
    "Remote Code Execution": 3
}

def load_patterns(lang):
    path = os.path.join(PATTERNS_DIR, f"{lang}_patterns.json")
    if not os.path.exists(path):
        return []
    with open(path, "r") as f:
        return json.load(f)

def extract_vuln_type(vuln_name):
    # "Authentication Bypass – SQL Injection" -> "Authentication Bypass"
    return vuln_name.split("–")[0].strip()

def scan_file(filepath, lang):
    with open(filepath, "r", encoding="utf-8", errors="ignore") as f:
        lines = f.readlines()
    
    content = "".join(lines)
    patterns = load_patterns(lang)
    results = []

    seen_on_line = set()
    for p in patterns:
        for match in re.finditer(p["pattern"], content, re.IGNORECASE):
            vuln_type = extract_vuln_type(p["vuln"])
            # Calculate line number
            match_start = match.start()
            line_num = content.count("\n", 0, match_start) + 1
            
            # Deduplication: If we already found this specific vuln on this line, skip
            # This prevents "File Upload Bypass" triggering 3 times for the same line
            if (line_num, p["vuln"]) in seen_on_line:
                continue
            seen_on_line.add((line_num, p["vuln"]))

            # Context capture (3 before, 3 after)
            start_idx = max(0, line_num - 4) # 0-indexed, so line_num-1 is current, -3 more = -4
            end_idx = min(len(lines), line_num + 3)
            
            context_lines = []
            for i in range(start_idx, end_idx):
                context_lines.append({
                    "line": i + 1,
                    "content": lines[i].rstrip()
                })

            results.append({
                "vuln": p["vuln"],
                "vuln_type": vuln_type,
                "description": p["description"],
                "file": filepath,
                "match": match.group(0).strip(),
                "line": line_num,
                "context": context_lines
            })
    return results

def walk_directory(directory):
    if os.path.isfile(directory):
        lang = detect_language(directory)
        if lang:
            return scan_file(directory, lang)
        return []

    findings = []
    for root, _, files in os.walk(directory):
        for file in files:
            path = os.path.join(root, file)
            lang = detect_language(path)
            if lang:
                findings.extend(scan_file(path, lang))
    return findings

def print_findings(findings):
    seen = set()
    unique_findings = []

    for f in findings:
        key = (f['vuln'], f['file'], f['line'], f['match'])
        if key not in seen:
            seen.add(key)
            unique_findings.append(f)

    # Önceliğe göre sırala
    unique_findings.sort(key=lambda x: PRIORITY.get(x["vuln_type"], 99))

    for f in unique_findings:
        print(colored("="*80, "cyan"))
        print(colored("Vulnerability:", "red", attrs=["bold"]), colored(f"{f['vuln']}", "yellow", attrs=["bold"]))
        print(colored("File        :", "green"), f"{f['file']}")
        print(colored("Line        :", "green"), f"{f['line']}")
        print(colored("Code Snippet:", "magenta"))
        print(colored(f"  {f['match']}", "white"))
        print(colored("Description :", "blue"), f"{f['description']}")
        print()

if __name__ == "__main__":
    import sys
    if len(sys.argv) != 2:
        print("Usage: python vuln_scanner.py <directory>")
        exit(1)

    target_dir = sys.argv[1]
    all_findings = walk_directory(target_dir)

    if not all_findings:
        pass # print(colored("No vulnerabilities found.", "green"))
    else:
        # print_findings(all_findings)
        try:
            report_path = generate_html_report(all_findings)
            print(colored(f"\n[+] HTML Report generated: {report_path}", "blue", attrs=["bold"]))
        except Exception as e:
            print(colored(f"\n[-] Failed to generate HTML report: {e}", "red"))
