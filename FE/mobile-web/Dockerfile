# syntax=docker/dockerfile:1
# 프론트(사용자 웹) 이미지: Vite 빌드 → nginx 정적 서빙.
# 백엔드 URL 은 빌드 인자(VITE_*_URL)로 주입 — 운영 도메인으로 설정해서 빌드하세요.
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --no-audit --no-fund
COPY . .
ARG VITE_AUTH_URL
ARG VITE_CONTENT_URL
ARG VITE_CURATION_URL
ARG VITE_ACTIVITY_URL
ARG VITE_QNA_URL
ARG VITE_PROJECT_URL
ARG VITE_RANKING_URL
ENV VITE_AUTH_URL=$VITE_AUTH_URL VITE_CONTENT_URL=$VITE_CONTENT_URL \
    VITE_CURATION_URL=$VITE_CURATION_URL VITE_ACTIVITY_URL=$VITE_ACTIVITY_URL \
    VITE_QNA_URL=$VITE_QNA_URL VITE_PROJECT_URL=$VITE_PROJECT_URL VITE_RANKING_URL=$VITE_RANKING_URL
RUN npm run build

FROM nginx:1.27-alpine AS runtime
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
