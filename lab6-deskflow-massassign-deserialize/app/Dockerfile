FROM php:8.1-apache

RUN apt-get update && apt-get install -y libpq-dev && rm -rf /var/lib/apt/lists/*
RUN docker-php-ext-install pdo pdo_pgsql

COPY src/ /var/www/html/

RUN chown -R www-data:www-data /var/www/html/reports \
    && chmod -R 775 /var/www/html/reports

RUN echo "OSWE_LAB6{massassign_role_to_deserialize_rce_$(date +%s)}" > /flag.txt \
    && chmod 644 /flag.txt

EXPOSE 80
