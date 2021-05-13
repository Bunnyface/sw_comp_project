FROM node:latest as build
WORKDIR /app
COPY . .
RUN npm install --force
RUN npm run build --prod
