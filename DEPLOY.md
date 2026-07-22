# 🚀 운영 배포 가이드 (Deployment)

이 문서는 AI 커뮤니티를 **실제 서버에 배포**하는 방법입니다. 전체 스택(백엔드 8개 + PostgreSQL + Redis + 프론트 2개)을
Docker 로 한 번에 띄웁니다.

> ⚠️ **직접 하셔야 하는 부분**: 서버(호스팅) 준비, 클라우드 계정 로그인, 시크릿(비밀번호·키) 입력, 방화벽/도메인 설정은
> 보안상 회원님 계정에서 진행합니다. 이 저장소는 그 실행에 필요한 **모든 설정 파일**을 담고 있습니다.

## 사전 준비물
- **Docker + Docker Compose** 가 설치된 서버 1대 (클라우드 VM: AWS EC2 / GCP / Azure / Vultr / DigitalOcean 등, 또는 로컬 PC)
- (선택) 도메인 + HTTPS 인증서(리버스 프록시 앞단)

## 1. 시크릿 설정 (`.env`)
저장소 루트에서 `.env.example` 를 복사해 `.env` 를 만들고 **강력한 값**으로 채웁니다:
```bash
cp .env.example .env
```
운영에서 반드시 바꿀 값:
- `JWT_SECRET` — 32바이트 이상의 임의 문자열 (로그인 토큰 서명 키)
- `POSTGRES_PASSWORD` — DB 비밀번호
- `AUTH_COOKIE_SECURE=true` — HTTPS 로 서비스할 때
- `CORS_ALLOWED_ORIGINS` — 실제 프론트 도메인 (예: `https://community.example.com`)
- `PUBLIC_AUTH_URL` 등 `PUBLIC_*_URL` — 브라우저에서 접근할 **백엔드 공개 URL** (아래 주의 참고)

## 2. 한 번에 실행
```bash
docker compose -f docker-compose.prod.yml up -d --build
```
- PostgreSQL·Redis 가 뜨고, 백엔드 8개가 자동으로 DB 마이그레이션(Flyway) 후 기동합니다.
- 프론트(사용자 웹 5173, 관리자 웹 5174)가 nginx 로 서빙됩니다.
- 기본 계정(큐레이터/관리자)은 인증 서비스가 자동 생성합니다.

상태 확인:
```bash
docker compose -f docker-compose.prod.yml ps
curl http://localhost:8081/actuator/health
```

## 3. ⚠️ 프론트의 백엔드 URL (중요)
프론트는 브라우저에서 각 백엔드로 직접 호출합니다. 그래서 **빌드 시점**에 백엔드 공개 URL 이 필요합니다.
- **같은 서버에서 테스트**: 기본값(`http://localhost:8081~8087`)이면 그 PC 브라우저에서 동작합니다.
- **실제 도메인/원격 서버**: `.env` 에 `PUBLIC_AUTH_URL=https://api-auth.example.com` 처럼 8개 서비스의 공개 URL 을 넣고 다시 빌드하세요.
  (권장: 리버스 프록시로 `auth.example.com`, `content.example.com` … 또는 경로 라우팅으로 각 서비스를 노출)

## 4. 인터넷에 공개 (Go-live)
1. 서버 방화벽/보안그룹에서 필요한 포트(프론트 80/443, 백엔드 API 포트 또는 프록시 443)를 엽니다.
2. 도메인을 서버 IP 로 연결하고, 앞단에 **HTTPS 리버스 프록시**(Caddy/Nginx/Traefik)를 둡니다.
3. `AUTH_COOKIE_SECURE=true` + `CORS_ALLOWED_ORIGINS`(실도메인)로 다시 빌드/기동합니다.

## 5. 다른 플랫폼(PaaS)로 배포하려면
- **Railway / Render / Fly.io**: 각 백엔드를 `BE/Dockerfile`(`--build-arg SERVICE=<이름>`)로 배포, 관리형 PostgreSQL·Redis 애드온 연결, 프론트는 정적 사이트로 배포.
- **Vercel / Netlify**: 프론트(정적)만 올리기 좋습니다. 백엔드는 별도 컨테이너 호스트가 필요합니다.
- 사용하실 플랫폼을 알려주시면 거기에 맞춘 설정과 단계별 안내를 만들어 드립니다.

## CI (자동 검증)
`.github/workflows/ci.yml` — push/PR 마다 백엔드 빌드+테스트, 프론트 빌드+린트를 자동 실행합니다.

## 참고
- 로컬(개발)은 Docker 없이 H2 로 동작합니다(README 참고). 운영은 위 compose 로 PostgreSQL 을 씁니다.
- Redis 컨테이너는 아키텍처상 포함(세션/캐시용 예약)이며, 현재 앱은 인메모리 방식으로 동작합니다.
