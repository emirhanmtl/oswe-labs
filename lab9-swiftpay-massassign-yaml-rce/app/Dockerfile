FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

RUN echo "OSWE_LAB9{sqli_recover_massassign_yaml_unsafe_load_rce_$(date +%s)}" > /flag.txt \
    && chmod 644 /flag.txt

EXPOSE 5000
CMD ["python", "app.py"]
