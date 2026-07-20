FROM node:22-alpine AS frontend-build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM nginx:1.27-alpine
COPY edge-gateway/nginx.conf /etc/nginx/conf.d/default.conf
COPY edge-gateway/proxy_params /etc/nginx/proxy_params
COPY --from=frontend-build /app/dist /usr/share/nginx/html
EXPOSE 8080
