FROM nginx:1.17.1-alpine
COPY /angular_dev/hello-world-angular /usr/share/nginx/html
COPY angular/nginx.conf /etc/nginx/conf.d/default.conf

