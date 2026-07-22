# ===========================================================================
# AI 커뮤니티 — 전체 실행 스크립트 (Windows PowerShell)
#   백엔드 8개 서비스를 모두 띄우고, 프론트(사용자 웹) 실행 방법을 안내합니다.
#   사용법:  powershell -ExecutionPolicy Bypass -File run-all.ps1
# ===========================================================================
$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$be = Join-Path $root "BE"

Write-Host "1) 백엔드 빌드 (최초 1회 시간이 걸립니다)..." -ForegroundColor Cyan
Push-Location $be
& ".\gradlew.bat" bootJar --console=plain
Pop-Location

# 서비스: 모듈명 = 포트
$services = @{
  "auth-service"          = 8081
  "content-service"       = 8082
  "curation-service"      = 8083
  "user-activity-service" = 8084
  "qna-service"           = 8085
  "project-service"       = 8086
  "ranking-api-service"   = 8087
  "ranking-batch-worker"  = 8088
}

Write-Host "2) 백엔드 서비스 8개 실행..." -ForegroundColor Cyan
foreach ($svc in $services.Keys) {
  $jar = Join-Path $be "$svc\build\libs\$svc-0.0.1.jar"
  Start-Process -FilePath "java" -ArgumentList "-jar", "`"$jar`"" -WindowStyle Minimized
  Write-Host "   - $svc (포트 $($services[$svc])) 시작"
}

Write-Host ""
Write-Host "백엔드가 뜨는 중입니다 (약 10~20초). 그 다음 새 터미널에서 프론트를 실행하세요:" -ForegroundColor Green
Write-Host "   cd FE\mobile-web ; npm install ; npm run dev" -ForegroundColor Yellow
Write-Host "   → 브라우저에서 http://localhost:5173" -ForegroundColor Yellow
Write-Host ""
Write-Host "관리자 콘솔을 쓰려면 (다른 터미널):" -ForegroundColor Green
Write-Host "   cd FE\admin-web ; npm install ; npm run dev  → http://localhost:5174" -ForegroundColor Yellow
Write-Host ""
Write-Host "기본 계정: curator@ai.community / curator1234 (큐레이터),  admin@ai.community / admin1234 (관리자)" -ForegroundColor Green
Write-Host "일반 회원은 로그인 화면의 [회원가입] 탭에서 직접 가입할 수 있어요." -ForegroundColor Green
