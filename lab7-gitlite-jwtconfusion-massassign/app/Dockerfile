FROM node:18-slim

WORKDIR /app

COPY package.json .
RUN npm install --omit=dev

COPY . .
RUN mkdir -p /app/data/snippets /app/data/exports

RUN echo "OSWE_LAB7{jwt_algconfusion_massassign_cmdi_$(date +%s)}" > /flag.txt \
    && chmod 644 /flag.txt

EXPOSE 3000
CMD ["node", "server.js"]
