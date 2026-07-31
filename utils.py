def detect_language(filepath):
    ext = filepath.split(".")[-1].lower()

    backend_extensions = {
        "php": ["php", "phtml"],
        "python": ["py", "pyw"],
        "ruby": ["rb", "rake"],
        "java": ["java", "jsp"],
        "nodejs": ["js", "jsx"],
        "dotnet": ["cs", "aspx", "cshtml"]
    }

    for lang, exts in backend_extensions.items():
        if ext in exts:
            return lang

    if ext in ["html"]:
        return "frontend"

    return None

import os
import datetime

def generate_html_report(findings, output_file="scan_results.html"):
    html_template = """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>OSWE Vuln Scan Report</title>
        <style>
            body {{ font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f9; color: #333; }}
            h1 {{ text-align: center; color: #444; }}
            .finding {{ background: #fff; padding: 20px; margin-bottom: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); border-left: 5px solid #007bff; }}
            .finding.high {{ border-left-color: #dc3545; }}
            .finding.medium {{ border-left-color: #ffc107; }}
            .finding.low {{ border-left-color: #28a745; }}
            .title {{ font-size: 1.2em; font-weight: bold; margin-bottom: 10px; display: flex; justify-content: space-between; align-items: center; }}
            .meta {{ font-size: 0.9em; color: #666; margin-bottom: 10px; }}
            .code-block {{ background: #2d2d2d; color: #f8f8f2; padding: 15px; border-radius: 5px; overflow-x: auto; font-family: 'Consolas', 'Monaco', monospace; display: none; margin-top: 10px; }}
            .line {{ display: block; }}
            .line.highlight {{ background-color: #44475a; color: #ff79c6; font-weight: bold; }}
            .lineno {{ color: #6272a4; margin-right: 10px; user-select: none; }}
            .toggle-btn {{ background: #eee; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 0.9em; color: #555; }}
            .toggle-btn:hover {{ background: #ddd; }}
        </style>
        <script>
            function toggleCode(id) {{
                var x = document.getElementById(id);
                var btn = document.getElementById('btn-' + id);
                if (x.style.display === "none" || x.style.display === "") {{
                    x.style.display = "block";
                    btn.textContent = "Hide Code";
                }} else {{
                    x.style.display = "none";
                    btn.textContent = "Show Code";
                }}
            }}
        </script>
    </head>
    <body>
        <h1>OSWE Vulnerability Scan Report</h1>
        <p style="text-align: center;">Generated on: {date}</p>
        <div id="findings">
            {findings_html}
        </div>
    </body>
    </html>
    """

    findings_html = ""
    for i, f in enumerate(findings):
        severity_class = "medium" # default
        if "Authentication Bypass" in f['vuln_type']: severity_class = "high"
        elif "Remote Code Execution" in f['vuln_type']: severity_class = "high"
        elif "Privilege Escalation" in f['vuln_type']: severity_class = "medium"
        else: severity_class = "low"

        code_snippet_html = ""
        # Handle context lines if available, otherwise fallback to single match
        if 'context' in f and f['context']:
            for line_obj in f['context']:
                ln = line_obj['line']
                content = line_obj['content']
                # Escape HTML special chars loosely
                content = content.replace("<", "&lt;").replace(">", "&gt;")
                
                # Check if this is the matching line
                is_match = (ln == f['line'])
                cls = "highlight" if is_match else ""
                code_snippet_html += f'<div class="line {cls}"><span class="lineno">{ln}</span>{content}</div>'
        else:
            match_content = f['match'].replace("<", "&lt;").replace(">", "&gt;")
            code_snippet_html = f'<div class="line highlight"><span class="lineno">{f["line"]}</span>{match_content}</div>'

        unique_id = f"code-{i}"
        findings_html += f"""
        <div class="finding {severity_class}">
            <div class="title">
                <span>{f['vuln']}</span>
                <div>
                    <span style="font-size: 0.8em; padding: 2px 8px; border-radius: 4px; background: #eee; margin-right: 10px;">{f['vuln_type']}</span>
                    <button id="btn-{unique_id}" class="toggle-btn" onclick="toggleCode('{unique_id}')">Show Code</button>
                </div>
            </div>
            <div class="meta">
                File: <strong>{f['file']}</strong> | Line: <strong>{f['line']}</strong>
            </div>
            <p>{f['description']}</p>
            <div id="{unique_id}" class="code-block">
                {code_snippet_html}
            </div>
        </div>
        """
    
    final_html = html_template.format(
        date=datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        findings_html=findings_html
    )

    with open(output_file, "w", encoding="utf-8") as f:
        f.write(final_html)
    
    return os.path.abspath(output_file)
