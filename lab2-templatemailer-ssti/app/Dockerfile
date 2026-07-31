FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .
COPY templates/ templates/

RUN echo "OSWE_LAB2{weak_secret_session_forge_to_ssti_rce}" > /flag.txt \
    && chmod 644 /flag.txt

EXPOSE 5000
CMD ["python", "app.py"]
