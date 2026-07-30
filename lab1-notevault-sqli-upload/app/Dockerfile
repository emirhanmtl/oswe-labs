FROM php:8.1-apache

RUN docker-php-ext-install pdo pdo_mysql

# Legacy app also serves .phtml as PHP (kept for an old widget that used that extension).
RUN printf '<FilesMatch "\\.(php|phtml|php5|pht)$">\n    SetHandler application/x-httpd-php\n</FilesMatch>\n' \
    > /etc/apache2/conf-available/php-extra-handlers.conf \
    && a2enconf php-extra-handlers

COPY src/ /var/www/html/

RUN chown -R www-data:www-data /var/www/html/uploads \
    && chmod -R 775 /var/www/html/uploads

RUN echo "OSWE_LAB1{sqli_auth_bypass_to_webshell_$(date +%s)}" > /flag.txt \
    && chmod 644 /flag.txt

EXPOSE 80
