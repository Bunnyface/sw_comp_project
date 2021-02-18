FROM node:latest as build
WORKDIR /app
COPY angular/ .
RUN npm install
RUN npm run build --prod

FROM nginx:1.17.1-alpine
COPY --from=build /app/dist/hello-world-angular /usr/share/nginx/html
COPY angular/nginx.conf /etc/nginx/conf.d/default.conf