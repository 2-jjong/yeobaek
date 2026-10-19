<div align="center">

<img src="./docs/assets/yeobaek_logo.svg" width="140" alt="Yeobaek Logo" />

# 여백 (Yeobaek)

### 공간과 예술이 만나는 일상 속 전시 플랫폼
카페의 유휴 벽면 공간과 신인 작가의 작품을 연결하는 양방향 매칭 및 전시 관리 서비스

<br/>

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Python](https://img.shields.io/badge/Python-3.12-3776AB?style=flat-square&logo=python&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=flat-square&logo=fastapi&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
<br/>
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-Jetpack_Compose-3DDC84?style=flat-square&logo=android&logoColor=white)
![ARCore](https://img.shields.io/badge/Google_ARCore-1.51-4285F4?style=flat-square&logo=google&logoColor=white)

</div>

## 개요

여백은 오프라인 전시 기회를 찾기 어려운 신인 작가와 인테리어 개선 및 문화 공간 조성을 희망하는 카페 운영자를 연결하는 양방향 매칭 플랫폼입니다.

- **AR 기반 공간 스캔 및 도면 규격화**: Google ARCore를 활용해 카페 벽면의 실제 면적을 정밀 측정하고, 2D 도면 및 전시 슬롯을 생성하여 작품 크기와의 적합성을 사전에 검증합니다.
- **하이브리드 추천 엔진**: 협업 필터링(CF)과 콘텐츠 기반 필터링(CBF)을 결합하여 작품의 분위기·규격과 카페의 공간 특성에 최적화된 매칭 후보를 제공합니다.
- **원스톱 전시 및 계약 라이프사이클**: 지도 기반 공간 탐색부터 1:1 실시간 협의, 계약 조건(일정, 슬롯, 수수료) 확정, 전시 종료 후 상호 평판 관리까지 플랫폼 내에서 일원화하여 관리합니다.

## 시스템 아키텍처

![시스템 아키텍처](./docs/assets/architecture.jpg)

### 아키텍처 구성 요소

1. **클라이언트 (Android)**
   - Jetpack Compose 기반 선언형 UI 구성
   - Nginx 리버스 프록시를 통해 REST API 및 WebSocket(STOMP) 프로토콜로 실시간 통신
   - Spring Boot로부터 발급받은 Presigned URL을 활용하여 AWS S3와 고용량 미디어 직접 업로드/다운로드 수행
   - Firebase Cloud Messaging(FCM)을 통한 채팅 푸시 알림 수신

2. **AWS EC2 (Docker 컨테이너 환경)**
   - **Nginx**: `/api/v1/*` REST API 및 `/ws` 웹소켓 트래픽을 메인 서버로 라우팅하는 리버스 프록시
   - **Spring Boot (메인 API 서버)**:
     - 비즈니스 로직 처리, Spring Security 및 JWT 인증/인가 제어
     - AWS S3 업로드용 Presigned URL 발급 및 FCM 푸시 알림 발송 요청
     - FastAPI 추천 서버와 연동하여 배치 모델 학습 데이터 전송 및 Top-K 추천 결과 서빙
     - Redis를 활용한 Refresh Token 관리 및 URL 캐싱
     - Amazon RDS(MySQL)와의 영속성 DB I/O 처리
   - **FastAPI (추천 서버)**: Implicit ALS 및 콘텐츠 기반 필터링 모델을 통한 Top-K 맞춤 카페/작품 추천 서빙
   - **Redis**: 세션 토큰 관리 및 고성능 인메모리 캐시 계층

3. **클라우드 스토리지 & 서버리스 파이프라인**
   - **AWS S3 & AWS Lambda**: 클라이언트에서 업로드된 원본 이미지를 감지(S3 Event)하여 AWS Lambda 기반으로 썸네일을 자동 리사이징 및 S3 재저장
   - **Amazon RDS (MySQL)**: 회원, 카페, 전시 슬롯, 작품, 매칭 계약 및 평가 데이터 영속화

## 주요 기능 및 화면

### 1. AR 기반 카페 공간 스캔 및 도면 생성
카페 사장은 AR 카메라를 통해 벽면의 크기와 높이를 실측하고 자동으로 2D 평면 도면과 전시 슬롯을 생성할 수 있습니다.

| 공간 등록 시작 | AR 벽면 길이 측정 | 2D 평면 도면 생성 |
| :---: | :---: | :---: |
| <img src="./exec/images/scenario_1.jpg" width="100%" alt="공간 등록 시작" /> | <img src="./exec/images/scenario_2.jpg" width="100%" alt="AR 벽면 길이 측정" /> | <img src="./exec/images/scenario_3.jpg" width="100%" alt="2D 평면 도면 생성" /> |
| 카페 관리 화면에서 공간 등록 진입 | AR 카메라로 실제 매장 벽면 실측 | 측정 데이터 기반 도면 자동 생성 |

| 벽면 높이 설정 | AR 벽면 높이 측정 | 완성된 도면 및 슬롯 확인 |
| :---: | :---: | :---: |
| <img src="./exec/images/scenario_4.jpg" width="100%" alt="벽면 높이 설정" /> | <img src="./exec/images/scenario_5.jpg" width="100%" alt="AR 벽면 높이 측정" /> | <img src="./exec/images/scenario_6.jpg" width="100%" alt="완성된 도면 및 슬롯 확인" /> |
| 생성된 도면의 특정 벽면 선택 | AR 카메라로 전시 가능 높이 실측 | 가로·세로 규격이 반영된 도면 확정 |

### 2. 맞춤형 카페-작품 매칭 추천
작가는 등록된 자신의 작품 중 전시를 원하는 항목을 선택하여 공간 분위기와 조건이 부합하는 카페를 추천받습니다.

| 추천 서비스 진입 | 전시 작품 선택 | 선택 작품 확인 | 맞춤 추천 카페 목록 |
| :---: | :---: | :---: | :---: |
| <img src="./exec/images/scenario_8.jpg" width="100%" alt="추천 서비스 진입" /> | <img src="./exec/images/scenario_9.jpg" width="100%" alt="전시 작품 선택" /> | <img src="./exec/images/scenario_10.jpg" width="100%" alt="선택 작품 확인" /> | <img src="./exec/images/scenario_11.jpg" width="100%" alt="맞춤 추천 카페 목록" /> |
| 작가 홈에서 매칭 추천 시작 | 보유 작품 중 전시 대상 선택 | 선택 작품 사양 및 규격 검토 | 알고리즘 기반 적합 카페 추천 카드 확인 |

### 3. 지도 기반 카페 및 전시 슬롯 탐색
지도를 통해 주변 카페 위치와 전시 슬롯 현황을 확인하고 상세 정보를 탐색합니다.

| 카페 상세 정보 | 지도 기반 카페 탐색 | 카페 검색 결과 | 전시 슬롯 현황 및 문의 |
| :---: | :---: | :---: | :---: |
| <img src="./exec/images/scenario_12.jpg" width="100%" alt="카페 상세 정보" /> | <img src="./exec/images/scenario_13.jpg" width="100%" alt="지도 기반 카페 탐색" /> | <img src="./exec/images/scenario_14.jpg" width="100%" alt="카페 검색 결과" /> | <img src="./exec/images/scenario_15.jpg" width="100%" alt="전시 슬롯 현황 및 문의" /> |
| 공간 소개 및 슬롯 규격 확인 | Google Maps 마커 기반 위치 탐색 | 키워드 검색을 통한 타깃 카페 조회 | 도면 내 슬롯 위치 확인 후 문의 시작 |

### 4. 1:1 실시간 채팅 및 조건 협의·매칭 요청
카페 사장과 작가는 실시간 채팅을 통해 세부 조건을 조율하고 플랫폼 내에서 정형화된 매칭 요청서를 작성합니다.

| 채팅방 진입 | 매칭 요청 선택 | 전시 일정 선택 | 전시 슬롯 선택 |
| :---: | :---: | :---: | :---: |
| <img src="./exec/images/scenario_16.jpg" width="100%" alt="채팅방 진입" /> | <img src="./exec/images/scenario_17.jpg" width="100%" alt="매칭 요청 선택" /> | <img src="./exec/images/scenario_18.jpg" width="100%" alt="전시 일정 선택" /> | <img src="./exec/images/scenario_19.jpg" width="100%" alt="전시 슬롯 선택" /> |
| 실시간 1:1 대화 인터페이스 | 하단 메뉴에서 매칭 요청서 호출 | 전시 시작일 및 종료일 지정 | 희망 전시 벽면(슬롯) 선택 |

| 전시 작품 선택 | 수수료율 협의 | 추가 요청사항 입력 | 매칭 요청 전송 완료 |
| :---: | :---: | :---: | :---: |
| <img src="./exec/images/scenario_20.jpg" width="100%" alt="전시 작품 선택" /> | <img src="./exec/images/scenario_21.jpg" width="100%" alt="수수료율 협의" /> | <img src="./exec/images/scenario_22.jpg" width="100%" alt="추가 요청사항 입력" /> | <img src="./exec/images/scenario_23.jpg" width="100%" alt="매칭 요청 전송 완료" /> |
| 전시할 작품 정보 지정 | 판매 수수료 비율 설정 | 배송 및 디스플레이 특이사항 기재 | 정형화된 매칭 요청 카드 발송 |

### 5. 매칭 확정 및 전시 종료 후 상호 평가
제안된 조건을 수락하면 전시 일정이 확정되며, 전시 종료 후에는 상대방에 대한 신뢰도 리뷰를 등록할 수 있습니다.

| 매칭 수락 및 확정 | 전시 종료 리뷰 진입 | 별점 및 리뷰 작성 | 리뷰 등록 완료 |
| :---: | :---: | :---: | :---: |
| <img src="./exec/images/scenario_24.jpg" width="100%" alt="매칭 수락 및 확정" /> | <img src="./exec/images/scenario_25.jpg" width="100%" alt="전시 종료 리뷰 진입" /> | <img src="./exec/images/scenario_26.jpg" width="100%" alt="별점 및 리뷰 작성" /> | <img src="./exec/images/scenario_27.jpg" width="100%" alt="리뷰 등록 완료" /> |
| 카페 사장의 조건 승인 및 전시 확정 | 전시 종료에 따른 리뷰 작성 유도 | 별점 및 파트너 평가 작성 | 등록된 리뷰의 프로필 평판 반영 |