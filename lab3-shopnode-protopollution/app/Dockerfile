FROM node:18-slim

WORKDIR /app

COPY package.json .
RUN npm install --omit=dev

COPY . .

RUN echo "OSWE_LAB3{nosqli_bypass_pp_to_ejs_rce}" > /flag.txt \
    && chmod 644 /flag.txt

EXPOSE 3000
CMD ["node", "server.js"]
