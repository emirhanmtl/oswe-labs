FROM node:18-slim

WORKDIR /app

COPY package.json .
RUN npm install --omit=dev

COPY . .
RUN mkdir -p /app/reports/user_reports /app/reports/plugins

RUN echo "OSWE_LAB5{jwt_weak_secret_path_traversal_rce}" > /flag.txt \
    && chmod 644 /flag.txt

EXPOSE 4000
CMD ["node", "server.js"]
