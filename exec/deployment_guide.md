# 프로젝트 빌드 및 배포 가이드

본 문서는 Gitlab 저장소에서 소스를 클론한 후 빌드 및 배포하는 전체 과정을 안내합니다.

---

## 1. 개발 환경 및 기술 스택

### 1.1 Backend (Spring Boot)

#### IDE 및 개발 도구
- **IDE**: IntelliJ IDEA (Gradle/Java 개발 지원)
- **JDK**: OpenJDK 17
- **빌드 도구**: Gradle (Wrapper 포함)

#### 프레임워크 및 서버
- **프레임워크**: Spring Boot 3.3.5
- **WAS**: 내장형 Apache Tomcat (Spring Boot 기본 탑재)
  - 별도의 외부 WAS 설치 불필요
  - Embedded Servlet Container로 독립 실행 가능

#### 데이터베이스
- **RDBMS**: MySQL
- **Hibernate Dialect**: `com.ssafy.yeobaek.config.CustomMySQLDialect`
- **JDBC Driver**: `com.mysql.cj.jdbc.Driver`

#### 캐시 시스템
- **분산 캐시**: Redis
  - 의존성: `spring-boot-starter-data-redis`
- **로컬 캐시**: Caffeine 3.1.8
  - 의존성: `spring-boot-starter-cache`

#### 외부 서비스 연동
- **AWS S3**: `io.awspring.cloud:spring-cloud-aws-starter-s3:3.1.1`
- **Firebase Admin SDK**: `com.google.firebase:firebase-admin:9.3.0`
- **Google Cloud BigQuery**: `com.google.cloud:google-cloud-bigquery:2.38.2`
- **Vworld API**: 지오코딩 서비스

### 1.2 Mobile (Android)

#### 개발 환경
- **Gradle**: 8.x
- **Android Gradle Plugin (AGP)**: 8.13.0
- **Kotlin**: 2.0.21
- **Java Compatibility**: 11 (sourceCompatibility & targetCompatibility)

#### 핵심 라이브러리
- **Compose BOM**: 2024.09.00
- **Hilt**: 2.51.1 (의존성 주입)
- **Retrofit**: 2.11.0 (네트워킹)
- **Coroutines**: 1.9.0 (비동기 처리)
- **Firebase BOM**: 34.2.0
- **Google Maps**: 19.0.0
- **AR Core**: 1.51.0

### 1.3 Python API Server

#### 실행 환경
- **Python**: 3.12 이상
- **패키지 관리**: Conda (Miniforge/Anaconda)
- **웹 프레임워크**: FastAPI + Uvicorn

#### 주요 라이브러리
- **implicit**: ALS 추천 알고리즘
- **pandas, numpy, scipy**: 데이터 처리
- **pydantic**: 데이터 검증

---

## 2. 환경 변수 설정

### 2.1 Backend 필수 환경 변수

프로젝트는 `application.yaml`에서 환경 변수를 참조합니다. 실행 전에 다음 변수들을 설정해야 합니다.

#### 데이터베이스 (MySQL)
- `DB_HOST`: 데이터베이스 서버 호스트
- `DB_PORT`: 데이터베이스 포트
- `DB_NAME`: 스키마 이름
- `DB_USERNAME`: 접속 사용자명
- `DB_PASSWORD`: 접속 비밀번호

#### Redis
- `REDIS_HOST`: Redis 서버 호스트
- `REDIS_PORT`: Redis 포트

#### AWS S3
- `AWS_ACCESS_KEY`: AWS Access Key ID
- `AWS_SECRET_KEY`: AWS Secret Access Key
- `AWS_S3_BUCKET`: S3 버킷 이름
- `AWS_REGION`: AWS 리전 (예: `ap-northeast-2`)

#### Firebase
- `FIREBASE_PROJECT_ID`: Firebase 프로젝트 ID
- **서비스 계정 파일**: `src/main/resources/firebase/firebase-service-account.json`
  - Firebase 콘솔에서 다운로드 후 해당 경로에 배치 필수

#### Google Cloud BigQuery
- `BIGQUERY_PROJECT_ID`: BigQuery 프로젝트 ID
- `BIGQUERY_DATASET_ID`: BigQuery 데이터셋 ID

#### Python 서버
- `PYTHON_SERVER_URL`: Python 마이크로서비스 URL (예: `http://localhost:5000`)

#### JWT 설정
- `JWT_SECRET`: JWT 서명용 비밀 키 (보안상 강력한 랜덤 문자열 권장)

#### Vworld API
- `GEOCODER_API_KEY`: Vworld API 키

### 2.2 Android 환경 변수

- `BASE_URL`: 백엔드 API 서버 기본 URL
  - 예시: `https://k13d207.p.ssafy.io/api/v1/`
- `MAPS_API_KEY`: Google Maps API 키
  - 예시: `AIzaSyCxaBnmiCvYzRXwbAyPclzwIWfPbX9TwC0`
- `WS_URL`: WebSocket 서버 URL
  - 예시: `wss://k13d207.p.ssafy.io/ws`

### 2.3 환경 변수 주입 방법

#### 방법 1: yeobaek.env 파일 사용 (권장)
`application.yaml`에 `spring.config.import: optional:file:yeobaek.env[.properties]` 설정이 있어 환경 변수 파일을 자동으로 로드합니다.

**yeobaek.env 파일 예시:**
```properties
DB_HOST=서버호스트
DB_PORT=3306
DB_NAME=데이터베이스명
DB_USERNAME=DB계정
DB_PASSWORD=DB비밀번호
REDIS_HOST=Redis서버호스트
REDIS_PORT=6379
AWS_ACCESS_KEY=AWS액세스키
AWS_SECRET_KEY=AWS시크릿키
AWS_S3_BUCKET=S3버킷명
AWS_REGION=ap-northeast-2
FIREBASE_PROJECT_ID=Firebase프로젝트ID
BIGQUERY_PROJECT_ID=BigQuery프로젝트ID
BIGQUERY_DATASET_ID=BigQuery데이터셋ID
PYTHON_SERVER_URL=Python서버URL
JWT_SECRET=JWT비밀키
GEOCODER_API_KEY=Vworld_API키
```

JAR 파일과 같은 디렉토리에 `yeobaek.env` 파일을 배치하면 자동으로 로드됩니다.

#### 방법 2: 시스템 환경 변수
```bash
export DB_HOST=서버호스트
export DB_PASSWORD=DB비밀번호
# ... 필요한 모든 환경 변수 설정
```

#### 방법 3: JVM 파라미터
```bash
java -DDB_HOST=서버호스트 -DDB_PASSWORD=DB비밀번호 -jar yeobaek-0.0.1-SNAPSHOT.jar
```

---

## 3. 빌드 방법

### 3.1 Backend 빌드

프로젝트 루트 디렉토리(`BackEnd/yeobaek/`)에서 실행:

```bash
# 클린 빌드 (기존 빌드 결과 삭제 후 재빌드)
./gradlew clean build

# 테스트 제외 빌드
./gradlew clean build -x test
```

빌드 성공 시 `build/libs/yeobaek-0.0.1-SNAPSHOT.jar` 파일이 생성됩니다.

### 3.2 Android 빌드

Android Studio에서 프로젝트를 열고:

```bash
# Debug APK 빌드
./gradlew assembleDebug

# Release APK 빌드
./gradlew assembleRelease
```

### 3.3 Python API Server 환경 구성

#### Conda 환경 생성 및 활성화
```bash
# Python 3.12 환경 생성
conda create -n yeobaek python=3.12 -y

# 환경 활성화
conda activate yeobaek
```

#### 패키지 설치
```bash
# C++ 컴파일이 필요한 패키지는 conda로 설치
conda install -c conda-forge implicit pandas numpy scipy -y

# 나머지는 pip로 설치
pip install fastapi uvicorn[standard] pydantic
```

---

## 4. 배포 방법

### 4.1 Backend 단독 실행

#### JAR 파일 직접 실행
```bash
# 환경 변수 파일과 함께 실행
java -jar build/libs/yeobaek-0.0.1-SNAPSHOT.jar

# 또는 특정 포트 지정
java -jar -Dserver.port=8080 build/libs/yeobaek-0.0.1-SNAPSHOT.jar
```

### 4.2 Docker 컨테이너 배포

#### Docker 이미지 빌드
프로젝트 루트(`BackEnd/yeobaek/`)에서:

```bash
docker build -t yeobaek-backend .
```

#### Docker Compose로 전체 스택 실행
```bash
# 백그라운드 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 중지
docker-compose down
```

**주의**: `docker-compose.yml`의 `environment` 섹션에서 환경 변수 값을 확인하고 수정해야 합니다.

### 4.3 Python API Server 실행

#### 개발 모드 (Hot Reload)
```bash
conda activate yeobaek
cd yeobaek-python
uvicorn main:app --reload
```

#### 프로덕션 모드
```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --workers 4
```

---

## 5. 배포 시 특이사항

### 5.1 데이터베이스 초기화

프로젝트에는 데이터베이스 초기화를 위한 SQL 스크립트가 포함되어 있습니다:

- **초기 데이터 삽입**: `BackEnd/yeobaek/src/main/resources/insert.sql`
- **테이블 초기화**: `BackEnd/yeobaek/src/main/resources/truncate.sql`

**운영 환경 배포 시 주의사항:**
- 초기 데이터 삽입 스크립트는 개발/테스트 환경에서만 실행
- 운영 DB에서는 신중하게 검토 후 실행 여부 결정

### 5.2 보안 고려사항

#### 민감 정보 관리
- `.gitignore`에 다음 파일들이 포함되어 있는지 확인:
  - `yeobaek.env`
  - `firebase-service-account.json`
  - `application-prod.yaml`

#### 운영 환경 권장사항
- Docker Secret, Kubernetes Secret 등을 활용한 환경 변수 관리
- AWS Secrets Manager, HashiCorp Vault 등의 보안 솔루션 활용
- JWT Secret은 256비트 이상의 강력한 랜덤 문자열 사용

### 5.3 네트워크 및 포트 설정

다음 포트들이 방화벽에서 허용되어야 합니다:

- **Backend API**: 8080 (기본값)
- **MySQL**: 3306
- **Redis**: 6379
- **Python API**: 5000 (기본값) 또는 8000
- **WebSocket**: 443 (WSS)

### 5.4 Firebase 설정

1. Firebase 콘솔에서 서비스 계정 키(JSON) 다운로드
2. `BackEnd/yeobaek/src/main/resources/firebase/firebase-service-account.json`에 배치
3. 파일 권한 확인: `chmod 600 firebase-service-account.json`

### 5.5 Python API 워크플로우

1. **모델 학습**: 백엔드에서 배치 작업으로 `/train` API 호출
   - 전체 평점 데이터 전송
   - ALS 모델 학습 및 저장 (`models/als_model.pkl`)

2. **추천 제공**: 사용자 요청 시 `/recommend` API 호출
   - 학습된 모델로 실시간 추천 반환

---

## 6. 주요 설정 파일 목록

### 6.1 Backend 설정 파일

| 파일 경로 | 설명 | 비고 |
|---------|------|------|
| `BackEnd/yeobaek/src/main/resources/application.yaml` | 메인 설정 파일 | DB, Redis, AWS, Firebase, API 키 등 모든 설정 포함 |
| `BackEnd/yeobaek/yeobaek.env` | 환경 변수 파일 | 민감 정보 관리용 (Git 제외) |
| `BackEnd/yeobaek/src/main/resources/firebase/firebase-service-account.json` | Firebase 서비스 계정 키 | Google Cloud 콘솔에서 다운로드 |
| `BackEnd/yeobaek/docker-compose.yml` | Docker Compose 설정 | 컨테이너 환경 변수 및 서비스 정의 |
| `BackEnd/yeobaek/Dockerfile` | Docker 이미지 빌드 스크립트 | 컨테이너 이미지 생성용 |
| `BackEnd/yeobaek/src/main/resources/insert.sql` | 초기 데이터 삽입 스크립트 | 개발/테스트 환경용 |
| `BackEnd/yeobaek/src/main/resources/truncate.sql` | 테이블 초기화 스크립트 | 테스트 환경용 |

### 6.2 Android 설정 파일

| 파일 | 설명 |
|------|------|
| `build.gradle` (프로젝트 수준) | 전역 빌드 설정, 플러그인 버전 |
| `build.gradle` (앱 수준) | 앱 의존성, compileSdk, minSdk 등 |
| `gradle.properties` | Gradle 설정 및 환경 변수 |
| `local.properties` | 로컬 SDK 경로 (Git 제외) |

### 6.3 Python API 설정 파일

| 파일 | 설명 |
|------|------|
| `yeobaek-python/main.py` | FastAPI 애플리케이션 엔트리포인트 |
| `yeobaek-python/requirements.txt` | Python 패키지 의존성 |
| `yeobaek-python/schemas/models.py` | API 요청/응답 모델 |
| `yeobaek-python/services/als_trainer.py` | ALS 모델 학습 로직 |
| `yeobaek-python/services/als_recommender.py` | 추천 로직 |