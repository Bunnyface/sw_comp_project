FROM node:latest as build
WORKDIR /app
COPY angular/ .
RUN npm install
RUN npm run build --prod
