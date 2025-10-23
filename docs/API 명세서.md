# 1. 회원 관리 API

## 1.1 이메일 중복 검증

- 회원가입 전 이메일 중복 여부를 확인합니다.
- 사용 가능한 이메일인 경우 true를 반환합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/auth/email/check` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **email** | String | O | 확인할 이메일 주소 |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **email** | String | 확인한 이메일 주소 |
| **available** | Boolean | 사용 가능 여부 (true: 사용 가능, false: 중복) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AU4004** | 이메일 형식이 올바르지 않습니다 |
| **AU4005** | 필수 입력값이 누락되었습니다 |

## 1.2 회원가입

- 사용자 계정을 생성하고 기본 정보를 등록합니다.
- userType에 따라 카페 사장, 작가, 일반 사용자로 구분됩니다.
- 이메일 중복 검증 및 비밀번호 암호화 처리를 수행합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/auth/signup` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **userType** | String | O | 회원 유형 (`CAFE_OWNER`, `ARTIST`, `GENERAL`) |
| **email** | String | O | 로그인용 이메일 주소 |
| **password** | String | O | 비밀번호 (8자 이상, 영문+숫자+특수문자) |
| **passwordConfirm** | String | O | 비밀번호 확인 (password와 동일) |
| **nickname** | String | O | 닉네임 <br> - 카페 사장: 카페 상호명 <br> - 작가: 작가명 <br> - 일반 사용자: 닉네임 (2~15자) |
| **birthDate** | String | O | 생년월일 (YYYY-MM-DD 형식) |
| **gender** | String | O | 성별 (`MALE`, `FEMALE`) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **userId** | Long | 생성된 회원의 ID(PK) |
| **email** | String | 회원 이메일 |
| **nickname** | String | 닉네임 |
| **userType** | String | 회원 유형 (`CAFE_OWNER`, `ARTIST`, `GENERAL`) |
| **createdAt** | String |  YYYY-MM-DD 형식 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AU4001** | 이미 사용 중인 이메일입니다 |
| **AU4002** | 비밀번호는 8자 이상, 영문+숫자+특수문자 조합이어야 합니다 |
| **AU4003** | 비밀번호가 일치하지 않습니다 |
| **AU4004** | 이메일 형식이 올바르지 않습니다 |
| **AU4005** | 필수 입력값이 누락되었습니다 |
| **AU4006** | 유효하지 않은 회원 유형입니다 |
| **AU4007** | 닉네임은 2~15자 이내여야 합니다 |

## 1.3 로그인

- 이메일과 비밀번호로 인증하고 JWT 토큰을 발급합니다.
- accessToken과 refreshToken을 함께 발급합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/auth/login` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **email** | String | O | 로그인 이메일 주소 |
| **password** | String | O | 비밀번호 |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **accessToken** | String | JWT Access Token (유효기간: 1시간) |
| **refreshToken** | String | JWT Refresh Token (유효기간: 7일) |
| **userId** | Long | 회원 ID |
| **email** | String | 회원 이메일 |
| **nickname** | String | 닉네임 |
| **userType** | String | 회원 유형 (`CAFE_OWNER`, `ARTIST`, `GENERAL`) |
| **profileImageUrl** | String | 프로필 이미지 URL |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AU4011** | 이메일 또는 비밀번호가 일치하지 않습니다 |
| **AU4012** | 존재하지 않는 회원입니다 |
| **AU4005** | 필수 입력값이 누락되었습니다 |

## 1.4 로그아웃

- 현재 로그인된 사용자를 로그아웃 처리합니다.
- 서버에서 해당 사용자의 refreshToken을 무효화합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/auth/logout` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 본문

없음

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | null | 응답 데이터 없음 |
| **message** | String | 응답 메시지 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AU4013** | 유효하지 않은 토큰입니다 |
| **AU4014** | 만료된 토큰입니다 |

## 1.5 Access Token 재발급

- refreshToken을 사용하여 새로운 accessToken과 refreshToken을 발급합니다.
- 토큰 갱신(rotate) 방식으로 refreshToken도 함께 갱신됩니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/auth/refresh` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {refreshToken}` |
| **Content-Type** | `application/json` |

### 요청 본문

없음

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **accessToken** | String | 새로 발급된 JWT Access Token |
| **refreshToken** | String | 새로 발급된 JWT Refresh Token |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AU4013** | 유효하지 않은 토큰입니다 |
| **AU4014** | 만료된 토큰입니다 |
| **AU4015** | 재로그인이 필요합니다 |

## 1.6 패스워드 재설정 요청

- 이메일 주소를 입력하여 패스워드 재설정 인증 코드를 요청합니다.
- 인증 코드는 이메일로 발송됩니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/auth/password/reset-request` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **email** | String | O | 가입된 이메일 주소 |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **email** | String | 인증 코드가 발송된 이메일 주소 |
| **expiresIn** | Integer | 인증 코드 유효 시간 (초, 300초 = 5분) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AU4012** | 존재하지 않는 회원입니다 |
| **AU4004** | 이메일 형식이 올바르지 않습니다 |
| **AU5001** | 이메일 발송에 실패했습니다 |

## 1.7 패스워드 재설정 확인

- 인증 코드를 확인하고 새로운 패스워드로 변경합니다.
- 인증 코드는 5분간 유효합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/auth/password/reset-confirm` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **email** | String | O | 이메일 주소 |
| **verificationCode** | String | O | 이메일로 받은 인증 코드 (6자리) |
| **newPassword** | String | O | 새로운 비밀번호 (8자 이상, 영문+숫자+특수문자) |
| **newPasswordConfirm** | String | O | 새로운 비밀번호 확인 |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | null | 응답 데이터 없음 |
| **message** | String | 응답 메시지 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AU4016** | 인증 코드가 일치하지 않습니다 |
| **AU4017** | 인증 코드가 만료되었습니다 |
| **AU4002** | 비밀번호는 8자 이상, 영문+숫자+특수문자 조합이어야 합니다 |
| **AU4003** | 비밀번호가 일치하지 않습니다 |
| **AU4012** | 존재하지 않는 회원입니다 |

# 2. Users (회원/프로필) API

## 2.1 프로필 정보 조회

- 사용자의 프로필 정보를 조회합니다.
- 본인의 프로필 조회 시 상세 정보가 표시되고, 타인의 프로필 조회 시 공개 정보만 표시됩니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/users/{userId}` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **userId** | Long | O | 조회할 회원 ID |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **userId** | Long | 회원 ID |
| **email** | String | 이메일 (본인만 조회 가능) |
| **nickname** | String | 닉네임 |
| **userType** | String | 회원 유형 (`CAFE_OWNER`, `ARTIST`, `GENERAL`) |
| **profileImageUrl** | String | 프로필 이미지 URL |
| **birthDate** | String | 생년월일 (본인만 조회 가능, YYYY-MM-DD) |
| **gender** | String | 성별 (본인만 조회 가능, `MALE`, `FEMALE`) |
| **introduction** | String | 소개 (작가만, 최대 250자) |
| **createdAt** | String | YYYY-MM-DD HH:mm:ss |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **US4001** | 존재하지 않는 회원입니다 |

## 2.2 프로필 정보 수정

- 사용자의 프로필 정보를 수정합니다.
- 본인의 프로필만 수정 가능합니다.
- 회원 유형에 따라 수정 가능한 필드가 다릅니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/users/{userId}` |
| **HTTP Method** | PUT |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **userId** | Long | O | 수정할 회원 ID |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **nickname** | String | X | 닉네임
- 카페 사장: 카페 상호명
- 작가: 작가명
- 일반 사용자: 닉네임 |
| **profileImageUrl** | String | X | 프로필 이미지 URL |
| **introduction** | String | X | 소개 (작가만, 최대 250자) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **userId** | Long | 회원 ID |
| **nickname** | String | 수정된 닉네임 |
| **updatedAt** | String |  YYYY-MM-DD HH:mm:ss |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **US4001** | 존재하지 않는 회원입니다 |
| **US4002** | 본인의 프로필만 수정할 수 있습니다 |
| **US4003** | 닉네임은 2~15자 이내여야 합니다 |
| **US4004** | 소개는 최대 250자까지 입력 가능합니다 |

## 2.3 패스워드 변경

- 현재 패스워드를 확인한 후 새로운 패스워드로 변경합니다.
- 본인의 패스워드만 변경 가능합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/users/{userId}/password` |
| **HTTP Method** | PUT |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **userId** | Long | O | 회원 ID |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **currentPassword** | String | O | 현재 패스워드 |
| **newPassword** | String | O | 새로운 패스워드 (8자 이상, 영문+숫자+특수문자) |
| **newPasswordConfirm** | String | O | 새로운 패스워드 확인 |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | null | 응답 데이터 없음 |
| **message** | String | 응답 메시지 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **US4001** | 존재하지 않는 회원입니다 |
| **US4002** | 본인의 패스워드만 변경할 수 있습니다 |
| **US4005** | 현재 패스워드가 일치하지 않습니다 |
| **US4006** | 새로운 패스워드는 8자 이상, 영문+숫자+특수문자 조합이어야 합니다 |
| **US4007** | 새로운 패스워드가 일치하지 않습니다 |
| **US4008** | 현재 패스워드와 동일한 패스워드로 변경할 수 없습니다 |

## 2.4 북마크 목록 조회

- 사용자가 북마크한 카페와 작품 목록을 조회합니다.
- 본인의 북마크 목록만 조회 가능합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/users/{userId}/bookmarks` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **userId** | Long | O | 회원 ID |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **type** | String | X | 북마크 타입 (`CAFE`, `ARTWORK`, `ALL`), 기본값: `ALL` |
| **page** | Integer | X | 페이지 번호 (기본값: 0) |
| **size** | Integer | X | 페이지 크기 (기본값: 20) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **bookmarks** | List<Bookmark> | 북마크 목록 |
| **totalElements** | Integer | 전체 북마크 수 |
| **totalPages** | Integer | 전체 페이지 수 |
| **currentPage** | Integer | 현재 페이지 번호 |

### Bookmark 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **bookmarkId** | Long | 북마크 ID |
| **type** | String | 북마크 타입 (`CAFE`, `ARTWORK`) |
| **targetId** | Long | 대상 ID (카페 ID 또는 작품 ID) |
| **targetName** | String | 대상 이름 (카페명 또는 작품명) |
| **thumbnailUrl** | String | 썸네일 이미지 URL |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **US4001** | 존재하지 않는 회원입니다 |
| **US4002** | 본인의 북마크 목록만 조회할 수 있습니다 |

## 2.5 회원 탈퇴

- 회원 계정 및 관련 데이터를 삭제합니다.
- 본인의 계정만 탈퇴 가능합니다.
- 전시 중이거나 전시 예정인 매칭이 있는 경우 탈퇴할 수 없습니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/users/{userId}` |
| **HTTP Method** | DELETE |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **userId** | Long | O | 회원 ID |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **password** | String | O | 본인 확인을 위한 패스워드 |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | null | 응답 데이터 없음 |
| **message** | String | 응답 메시지 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **US4001** | 존재하지 않는 회원입니다 |
| **US4002** | 본인의 계정만 탈퇴할 수 있습니다 |
| **US4005** | 패스워드가 일치하지 않습니다 |
| **US4009** | 진행 중인 전시가 있어 탈퇴할 수 없습니다 |

## 2.6 작가 프로필 조회

- 작가의 프로필과 작품 피드를 조회합니다.
- 인스타그램 스타일의 프로필 레이아웃으로 표시됩니다.
- 모든 사용자가 조회 가능합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/users/artists/{userId}` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **userId** | Long | O | 작가 회원 ID |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **page** | Integer | X | 페이지 번호 (기본값: 0) |
| **size** | Integer | X | 페이지 크기 (기본값: 12) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **userId** | Long | 작가 ID |
| **nickname** | String | 작가명 |
| **profileImageUrl** | String | 프로필 이미지 URL |
| **introduction** | String | 작가 소개 (최대 250자) |
| **ratings** | RatingStats | 평가 통계 |
| **artworks** | List<ArtworkSummary> | 작품 목록 |
| **totalArtworks** | Integer | 전체 작품 수 |
| **totalPages** | Integer | 전체 페이지 수 |

### RatingStats 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **totalReviews** | Integer | 전체 평가 수 |
| **likeCount** | Integer | 좋아요 수 |
| **okayCount** | Integer | 괜찮아요 수 |
| **dislikeCount** | Integer | 별로에요 수 |
| **likePercentage** | Decimal | 좋아요 비율 (%) |

### ArtworkSummary 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artworkId** | Long | 작품 ID |
| **title** | String | 작품명 |
| **thumbnailUrl** | String | 썸네일 이미지 URL |
| **status** | String | 작품 상태 (`AVAILABLE`, `EXHIBITING`) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **US4001** | 존재하지 않는 회원입니다 |
| **US4010** | 작가 회원이 아닙니다 |

## 2.7 카페 프로필 조회

- 카페 사장의 프로필과 카페 정보를 조회합니다.
- 카페 사장의 평가 통계와 과거 전시 이력을 확인할 수 있습니다.
- 모든 사용자가 조회 가능합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/users/cafe-owners/{userId}` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **userId** | Long | O | 카페 사장 회원 ID |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **userId** | Long | 카페 사장 ID |
| **nickname** | String | 카페 상호명 |
| **profileImageUrl** | String | 프로필 이미지 URL |
| **cafeInfo** | CafeInfo | 카페 기본 정보 |
| **ratings** | RatingStats | 평가 통계 |
| **exhibitionHistory** | List<ExhibitionHistory> | 과거 전시 이력 (최근 10개) |

### CafeInfo 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **cafeId** | Long | 카페 ID |
| **cafeName** | String | 카페명 |
| **address** | String | 카페 주소 |
| **description** | String | 카페 소개 |
| **imageUrls** | List<String> | 카페 이미지 URL 목록 |

### ExhibitionHistory 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **matchingId** | Long | 매칭 ID |
| **artworkId** | Long | 작품 ID |
| **artworkTitle** | String | 작품명 |
| **artistName** | String | 작가명 |
| **thumbnailUrl** | String | 작품 썸네일 URL |
| **exhibitionStartDate** | String | 전시 시작일 (YYYY-MM-DD) |
| **exhibitionEndDate** | String | 전시 종료일 (YYYY-MM-DD) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **US4001** | 존재하지 않는 회원입니다 |
| **US4011** | 카페 사장이 아닙니다 |
| **US4012** | 등록된 카페 정보가 없습니다 |

# 3. Cafes (카페) API

## 3.1 카페 매장 정보 등록

- 카페 사장이 매장의 기본 정보를 등록합니다.
- 매장명, 주소, 소개, 이미지 등 기본 정보만 등록하며, 전시 슬롯은 별도로 등록합니다.
- 인증된 카페 사장만 이 API를 사용할 수 있습니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/cafes` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **cafeName** | String | O | 매장명 (1~20자) |
| **address** | String | O | 카페 주소 |
| **description** | String | X | 카페 소개 (최대 500자) |
| **contact** | String | X | 연락처 |
| **businessHours** | String | X | 영업시간 |
| **imageUrls** | List<String> | O | 매장 이미지 URL 목록 (최소 3장, 최대 10장) |
| **categories** | List<String> | O | 인테리어 스타일 (다중 선택: `모던`, `빈티지`, `미니멀`, `내추럴` 등) |
| **dailyVisitors** | Integer | X | 일일 평균 방문객 수 |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **cafeId** | Long | 생성된 카페 ID(PK) |
| **cafeName** | String | 매장명 |
| **address** | String | 카페 주소 |
| **createdAt** | String | 등록 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CF4001** | 카페 사장만 매장 정보를 등록할 수 있습니다 |
| **CF4002** | 이미 등록된 매장이 있습니다 |
| **CF4003** | 매장 이미지는 최소 3장, 최대 10장까지 등록 가능합니다 |
| **CF4004** | 필수 입력값이 누락되었습니다 |
| **CF4005** | 매장명은 1~20자 이내여야 합니다 |

## 3.2 카페 매장 정보 수정

- 등록된 카페 매장의 기본 정보를 수정합니다.
- 본인의 카페만 수정 가능합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/cafes/{cafeId}` |
| **HTTP Method** | PUT |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **cafeId** | Long | O | 수정할 카페 ID |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **cafeName** | String | X | 매장명 (1~20자) |
| **address** | String | X | 카페 주소 |
| **description** | String | X | 카페 소개 (최대 500자) |
| **contact** | String | X | 연락처 |
| **businessHours** | String | X | 영업시간 |
| **imageUrls** | List<String> | X | 매장 이미지 URL 목록 (최소 3장, 최대 10장) |
| **categories** | List<String> | X | 인테리어 스타일 |
| **dailyVisitors** | Integer | X | 일일 평균 방문객 수 |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **cafeId** | Long | 카페 ID |
| **cafeName** | String | 매장명 |
| **updatedAt** | String | 수정 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CF4006** | 존재하지 않는 카페입니다 |
| **CF4007** | 본인의 카페만 수정할 수 있습니다 |
| **CF4003** | 매장 이미지는 최소 3장, 최대 10장까지 등록 가능합니다 |
| **CF4005** | 매장명은 1~20자 이내여야 합니다 |

## 3.3 카페 목록 조회

- 카페 목록을 필터링, 정렬하여 조회합니다.
- 지도 뷰와 리스트 뷰를 함께 제공합니다.
- 역할에 따라 필터링 및 정렬 옵션이 다릅니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/cafes` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **region** | String | X | 지역 필터 (시/구/동 단위) |
| **minWidth** | Integer | X | 최소 슬롯 폭 (cm, 작가만) |
| **maxWidth** | Integer | X | 최대 슬롯 폭 (cm, 작가만) |
| **minHeight** | Integer | X | 최소 슬롯 높이 (cm, 작가만) |
| **maxHeight** | Integer | X | 최대 슬롯 높이 (cm, 작가만) |
| **minCommission** | Decimal | X | 최소 수수료율 (%, 작가만) |
| **maxCommission** | Decimal | X | 최대 수수료율 (%, 작가만) |
| **sortBy** | String | X | 정렬 기준 (`RECOMMENDED`, `DISTANCE`, `POPULAR`, `COMMISSION_LOW`, `COMMISSION_HIGH`), 기본값: `RECOMMENDED` |
| **latitude** | Decimal | X | 현재 위치 위도 (거리순 정렬 시 필수) |
| **longitude** | Decimal | X | 현재 위치 경도 (거리순 정렬 시 필수) |
| **page** | Integer | X | 페이지 번호 (기본값: 0) |
| **size** | Integer | X | 페이지 크기 (기본값: 20) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **cafes** | List<CafeSummary> | 카페 목록 |
| **totalElements** | Integer | 전체 카페 수 |
| **totalPages** | Integer | 전체 페이지 수 |
| **currentPage** | Integer | 현재 페이지 번호 |

### CafeSummary 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **cafeId** | Long | 카페 ID |
| **cafeName** | String | 매장명 |
| **address** | String | 카페 주소 |
| **thumbnailUrl** | String | 대표 이미지 URL |
| **categories** | List<String> | 인테리어 스타일 |
| **viewCount** | Integer | 조회수 |
| **bookmarkCount** | Integer | 북마크 수 |
| **availableSlots** | Integer | 전시 가능한 슬롯 수 |
| **commissionRate** | Decimal | 기본 수수료율 (%) |
| **distance** | Decimal | 거리 (km, 거리순 정렬 시에만 제공) |
| **latitude** | Decimal | 위도 |
| **longitude** | Decimal | 경도 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CF4008** | 거리순 정렬은 위치 정보가 필요합니다 |

## 3.4 카페 상세 조회

- 카페의 상세 정보를 조회합니다.
- 기본 정보, 매장 특성, 전시 슬롯 정보, 전시 조건을 확인할 수 있습니다.
- 각 슬롯의 현재 상태(전시 가능/전시 중/전시 예정)를 표시합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/cafes/{cafeId}` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **cafeId** | Long | O | 조회할 카페 ID |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **cafeId** | Long | 카페 ID |
| **cafeName** | String | 매장명 |
| **address** | String | 카페 주소 |
| **description** | String | 카페 소개 |
| **contact** | String | 연락처 |
| **businessHours** | String | 영업시간 |
| **imageUrls** | List<String> | 매장 이미지 URL 목록 |
| **categories** | List<String> | 인테리어 스타일 |
| **dailyVisitors** | Integer | 일일 평균 방문객 수 |
| **viewCount** | Integer | 조회수 |
| **bookmarkCount** | Integer | 북마크 수 |
| **isBookmarked** | Boolean | 북마크 여부 (로그인 시) |
| **ownerId** | Long | 카페 사장 회원 ID |
| **ownerNickname** | String | 카페 사장 닉네임 |
| **slots** | List<SlotDetail> | 전시 슬롯 목록 |
| **latitude** | Decimal | 위도 |
| **longitude** | Decimal | 경도 |

### SlotDetail 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **slotId** | Long | 슬롯 ID |
| **slotName** | String | 슬롯 이름 |
| **widthCm** | Integer | 벽면 폭 (cm) |
| **heightCm** | Integer | 벽면 높이 (cm) |
| **installationMethod** | String | 설치 방식 (`RAIL`, `HOOK`, `OTHER`) |
| **locationDescription** | String | 전시 위치 설명 |
| **arScanData** | String | AR 공간 스캔 데이터 |
| **commissionRate** | Decimal | 수수료율 (%) |
| **additionalRequests** | String | 추가 요청사항 |
| **status** | String | 슬롯 상태 (`AVAILABLE`, `EXHIBITING`, `SCHEDULED`) |
| **currentExhibition** | ExhibitionInfo | 현재 전시 정보 (전시 중일 때만) |

### ExhibitionInfo 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artworkId** | Long | 작품 ID |
| **artworkTitle** | String | 작품명 |
| **artistName** | String | 작가명 |
| **thumbnailUrl** | String | 작품 썸네일 URL |
| **exhibitionStartDate** | String | 전시 시작일 (YYYY-MM-DD) |
| **exhibitionEndDate** | String | 전시 종료일 (YYYY-MM-DD) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CF4006** | 존재하지 않는 카페입니다 |

## 3.5 카페 검색

- 키워드로 카페명 또는 지역명을 검색합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/cafes/search` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **keyword** | String | O | 검색 키워드 (카페명 또는 지역명) |
| **page** | Integer | X | 페이지 번호 (기본값: 0) |
| **size** | Integer | X | 페이지 크기 (기본값: 20) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **cafes** | List<CafeSummary> | 검색된 카페 목록 |
| **totalElements** | Integer | 전체 검색 결과 수 |
| **totalPages** | Integer | 전체 페이지 수 |
| **currentPage** | Integer | 현재 페이지 번호 |
| **keyword** | String | 검색 키워드 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CF4009** | 검색 키워드를 입력해주세요 |

## 3.6 근처 카페 목록 조회

- 사용자의 현재 위치 기반으로 근처 카페 목록을 조회합니다.
- 거리순으로 정렬되어 제공됩니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/cafes/nearby` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **latitude** | Decimal | O | 현재 위치 위도 |
| **longitude** | Decimal | O | 현재 위치 경도 |
| **radius** | Integer | X | 검색 반경 (km, 기본값: 2km) |
| **page** | Integer | X | 페이지 번호 (기본값: 0) |
| **size** | Integer | X | 페이지 크기 (기본값: 20) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **cafes** | List<CafeSummary> | 근처 카페 목록 (거리순 정렬) |
| **totalElements** | Integer | 전체 카페 수 |
| **totalPages** | Integer | 전체 페이지 수 |
| **currentPage** | Integer | 현재 페이지 번호 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CF4010** | 위치 정보가 필요합니다 |

## 3.7 전시 슬롯 등록

- 카페에 전시 슬롯을 추가로 등록합니다.
- 슬롯 정보와 전시 조건을 함께 입력합니다.
- 본인의 카페에만 슬롯을 등록할 수 있습니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/cafes/{cafeId}/slots` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **cafeId** | Long | O | 카페 ID |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **slotName** | String | X | 슬롯 이름 |
| **widthCm** | Integer | O | 벽면 폭 (cm) |
| **heightCm** | Integer | O | 벽면 높이 (cm) |
| **installationMethod** | String | X | 설치 방식 (`RAIL`, `HOOK`, `OTHER`) |
| **locationDescription** | String | X | 전시 위치 설명 |
| **arScanData** | String | X | AR 공간 스캔 데이터 (JSON 문자열) |
| **commissionRate** | Decimal | X | 수수료율 (0~100, 소수점 2자리) |
| **additionalRequests** | String | X | 추가 요청사항 (최대 500자) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **slotId** | Long | 생성된 슬롯 ID |
| **slotName** | String | 슬롯 이름 |
| **widthCm** | Integer | 벽면 폭 (cm) |
| **heightCm** | Integer | 벽면 높이 (cm) |
| **status** | String | 슬롯 상태 (`AVAILABLE`) |
| **createdAt** | String | 등록 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CF4006** | 존재하지 않는 카페입니다 |
| **CF4007** | 본인의 카페에만 슬롯을 등록할 수 있습니다 |
| **CF4011** | 벽면 규격은 필수 입력값입니다 |
| **CF4012** | 수수료율은 0~100 사이의 값이어야 합니다 |

## 3.8 전시 슬롯 수정

- 등록된 전시 슬롯의 정보를 수정합니다.
- 전시 중인 슬롯은 일부 정보만 수정 가능합니다.
- 본인의 카페 슬롯만 수정 가능합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/cafes/{cafeId}/slots/{slotId}` |
| **HTTP Method** | PUT |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **cafeId** | Long | O | 카페 ID |
| **slotId** | Long | O | 슬롯 ID |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **slotName** | String | X | 슬롯 이름 |
| **widthCm** | Integer | X | 벽면 폭 (cm, 전시 중일 때는 수정 불가) |
| **heightCm** | Integer | X | 벽면 높이 (cm, 전시 중일 때는 수정 불가) |
| **installationMethod** | String | X | 설치 방식 |
| **locationDescription** | String | X | 전시 위치 설명 |
| **arScanData** | String | X | AR 공간 스캔 데이터 |
| **commissionRate** | Decimal | X | 수수료율 |
| **additionalRequests** | String | X | 추가 요청사항 |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **slotId** | Long | 슬롯 ID |
| **slotName** | String | 슬롯 이름 |
| **updatedAt** | String | 수정 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CF4006** | 존재하지 않는 카페입니다 |
| **CF4007** | 본인의 카페 슬롯만 수정할 수 있습니다 |
| **CF4013** | 존재하지 않는 슬롯입니다 |
| **CF4014** | 전시 중인 슬롯은 규격을 수정할 수 없습니다 |
| **CF4012** | 수수료율은 0~100 사이의 값이어야 합니다 |

## 3.9 전시 슬롯 삭제

- 등록된 전시 슬롯을 삭제합니다.
- 전시 중이거나 전시 예정인 슬롯은 삭제할 수 없습니다.
- 본인의 카페 슬롯만 삭제 가능합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/cafes/{cafeId}/slots/{slotId}` |
| **HTTP Method** | DELETE |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **cafeId** | Long | O | 카페 ID |
| **slotId** | Long | O | 슬롯 ID |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | null | 응답 데이터 없음 |
| **message** | String | 응답 메시지 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CF4006** | 존재하지 않는 카페입니다 |
| **CF4007** | 본인의 카페 슬롯만 삭제할 수 있습니다 |
| **CF4013** | 존재하지 않는 슬롯입니다 |
| **CF4015** | 전시 중이거나 전시 예정인 슬롯은 삭제할 수 없습니다 |

# 4. Artworks (작품) API

## 4.1 작품 등록

- 작가가 피드 형식으로 작품을 업로드하고 정보를 등록합니다.
- 인스타그램 스타일의 작품 피드로 게시됩니다.
- 인증된 작가만 이 API를 사용할 수 있습니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/artworks` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **title** | String | O | 작품명 |
| **description** | String | X | 작품 설명 (최대 1000자) |
| **price** | Integer | X | 작품 가격 (0 이상) |
| **imageUrls** | List<String> | O | 작품 이미지 URL 목록 (최소 1장, 최대 10장) |
| **categoryName** | String | O | 카테고리 (단일 선택: `회화`, `사진`, `조각`, `설치미술`, `공예` 등) |
| **widthCm** | Integer | X | 가로 크기 (cm) |
| **heightCm** | Integer | X | 세로 크기 (cm) |
| **depthCm** | Integer | X | 깊이 (cm) |
| **weightKg** | Decimal | X | 무게 (kg, 소수점 2자리) |
| **material** | String | X | 재료/기법 |
| **productionYear** | String | X | 제작 연도 (YYYY 형식) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artworkId** | Long | 생성된 작품 ID(PK) |
| **title** | String | 작품명 |
| **status** | String | 작품 상태 (`AVAILABLE`) |
| **createdAt** | String | 등록 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AW4001** | 작가만 작품을 등록할 수 있습니다 |
| **AW4002** | 작품 이미지는 최소 1장, 최대 10장까지 등록 가능합니다 |
| **AW4003** | 필수 입력값이 누락되었습니다 |
| **AW4004** | 작품 가격은 0 이상이어야 합니다 |

## 4.2 작품 수정

- 등록된 작품의 정보를 수정합니다.
- 작품 이미지, 설명, 가격 등을 수정할 수 있습니다.
- 전시 중인 작품은 수정할 수 없습니다.
- 본인의 작품만 수정 가능합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/artworks/{artworkId}` |
| **HTTP Method** | PUT |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **artworkId** | Long | O | 수정할 작품 ID |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **title** | String | X | 작품명 |
| **description** | String | X | 작품 설명 (최대 1000자) |
| **price** | Integer | X | 작품 가격 |
| **imageUrls** | List<String> | X | 작품 이미지 URL 목록 (최소 1장, 최대 10장) |
| **categoryName** | String | X | 카테고리 |
| **widthCm** | Integer | X | 가로 크기 (cm) |
| **heightCm** | Integer | X | 세로 크기 (cm) |
| **depthCm** | Integer | X | 깊이 (cm) |
| **weightKg** | Decimal | X | 무게 (kg) |
| **material** | String | X | 재료/기법 |
| **productionYear** | String | X | 제작 연도 (YYYY 형식) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artworkId** | Long | 작품 ID |
| **title** | String | 작품명 |
| **updatedAt** | String | 수정 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AW4005** | 존재하지 않는 작품입니다 |
| **AW4006** | 본인의 작품만 수정할 수 있습니다 |
| **AW4007** | 전시 중인 작품은 수정할 수 없습니다 |
| **AW4002** | 작품 이미지는 최소 1장, 최대 10장까지 등록 가능합니다 |
| **AW4004** | 작품 가격은 0 이상이어야 합니다 |

## 4.3 작품 삭제

- 등록된 작품을 삭제합니다.
- 전시 중이거나 전시 예정인 작품은 삭제할 수 없습니다.
- 본인의 작품만 삭제 가능합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/artworks/{artworkId}` |
| **HTTP Method** | DELETE |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **artworkId** | Long | O | 삭제할 작품 ID |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | null | 응답 데이터 없음 |
| **message** | String | 응답 메시지 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AW4005** | 존재하지 않는 작품입니다 |
| **AW4006** | 본인의 작품만 삭제할 수 있습니다 |
| **AW4008** | 전시 중이거나 전시 예정인 작품은 삭제할 수 없습니다 |

## 4.4 작품 목록 조회

- 작품 목록을 필터링, 정렬하여 조회합니다.
- 그리드 뷰로 작품을 표시하며 무한 스크롤을 지원합니다.
- 역할에 따라 필터링 옵션이 다릅니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/artworks` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **category** | String | X | 카테고리 필터 (다중 선택 가능, 쉼표로 구분) |
| **minWidth** | Integer | X | 최소 가로 크기 (cm, 카페 사장만) |
| **maxWidth** | Integer | X | 최대 가로 크기 (cm, 카페 사장만) |
| **minHeight** | Integer | X | 최소 세로 크기 (cm, 카페 사장만) |
| **maxHeight** | Integer | X | 최대 세로 크기 (cm, 카페 사장만) |
| **sortBy** | String | X | 정렬 기준 (`RECOMMENDED`, `LATEST`, `POPULAR`), 기본값: `RECOMMENDED` |
| **page** | Integer | X | 페이지 번호 (기본값: 0) |
| **size** | Integer | X | 페이지 크기 (기본값: 20) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artworks** | List<ArtworkSummary> | 작품 목록 |
| **totalElements** | Integer | 전체 작품 수 |
| **totalPages** | Integer | 전체 페이지 수 |
| **currentPage** | Integer | 현재 페이지 번호 |

### ArtworkSummary 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artworkId** | Long | 작품 ID |
| **title** | String | 작품명 |
| **thumbnailUrl** | String | 썸네일 이미지 URL |
| **artistId** | Long | 작가 ID |
| **artistName** | String | 작가명 |
| **price** | Integer | 작품 가격 |
| **categoryName** | String | 카테고리명 |
| **viewCount** | Integer | 조회수 |
| **bookmarkCount** | Integer | 북마크 수 |
| **status** | String | 작품 상태 (`AVAILABLE`, `EXHIBITING`) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |

## 4.5 작품 상세 조회

- 작품의 상세 정보를 조회합니다.
- 작품 정보, 작가 정보, 전시 상태를 확인할 수 있습니다.
- 전시 중인 경우 전시 장소 정보를 표시합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/artworks/{artworkId}` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **artworkId** | Long | O | 조회할 작품 ID |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artworkId** | Long | 작품 ID |
| **title** | String | 작품명 |
| **description** | String | 작품 설명 |
| **price** | Integer | 작품 가격 |
| **imageUrls** | List<String> | 작품 이미지 URL 목록 (슬라이드) |
| **categoryName** | String | 카테고리명 |
| **widthCm** | Integer | 가로 크기 (cm) |
| **heightCm** | Integer | 세로 크기 (cm) |
| **depthCm** | Integer | 깊이 (cm) |
| **weightKg** | Decimal | 무게 (kg) |
| **material** | String | 재료/기법 |
| **productionYear** | String | 제작 연도 |
| **viewCount** | Integer | 조회수 |
| **bookmarkCount** | Integer | 북마크 수 |
| **isBookmarked** | Boolean | 북마크 여부 (로그인 시) |
| **status** | String | 작품 상태 (`AVAILABLE`, `EXHIBITING`) |
| **artistInfo** | ArtistInfo | 작가 정보 |
| **exhibitionInfo** | ExhibitionLocation | 전시 정보 (전시 중일 때만) |
| **createdAt** | String | 등록 일시 (YYYY-MM-DD HH:mm:ss) |

### ArtistInfo 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artistId** | Long | 작가 ID |
| **artistName** | String | 작가명 |
| **profileImageUrl** | String | 프로필 이미지 URL |

### ExhibitionLocation 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **cafeId** | Long | 카페 ID |
| **cafeName** | String | 카페명 |
| **cafeAddress** | String | 카페 주소 |
| **exhibitionStartDate** | String | 전시 시작일 (YYYY-MM-DD) |
| **exhibitionEndDate** | String | 전시 종료일 (YYYY-MM-DD) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AW4005** | 존재하지 않는 작품입니다 |

## 4.6 작품 검색

- 키워드로 작품명 또는 작가명을 검색합니다.
- 작가와 작품이 중복된 경우 상단에 작가 목록, 하단에 작품 그리드를 표시합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/artworks/search` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **keyword** | String | O | 검색 키워드 (작품명 또는 작가명) |
| **page** | Integer | X | 페이지 번호 (기본값: 0) |
| **size** | Integer | X | 페이지 크기 (기본값: 20) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artists** | List<ArtistSummary> | 검색된 작가 목록 (상단 표시) |
| **artworks** | List<ArtworkSummary> | 검색된 작품 목록 (하단 그리드) |
| **totalElements** | Integer | 전체 검색 결과 수 (작품 기준) |
| **totalPages** | Integer | 전체 페이지 수 |
| **currentPage** | Integer | 현재 페이지 번호 |
| **keyword** | String | 검색 키워드 |

### ArtistSummary 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artistId** | Long | 작가 ID |
| **artistName** | String | 작가명 |
| **profileImageUrl** | String | 프로필 이미지 URL |
| **introduction** | String | 작가 소개 |
| **artworkCount** | Integer | 작품 수 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **AW4009** | 검색 키워드를 입력해주세요 |

## 4.7 인기 작품 목록 조회

- 조회수, 북마크, 평가를 기반으로 인기 작품 목록을 조회합니다.
- 홈 화면이나 탐색 페이지에서 사용됩니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/artworks/popular` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **period** | String | X | 집계 기간 (`WEEK`, `MONTH`, `ALL`), 기본값: `WEEK` |
| **size** | Integer | X | 조회할 작품 수 (기본값: 10, 최대 50) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **artworks** | List<ArtworkSummary> | 인기 작품 목록 |
| **period** | String | 집계 기간 |
| **totalCount** | Integer | 조회된 작품 수 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |

# 5. Bookmarks (북마크) API

## 5.1 북마크 토글

- 카페 또는 작품을 북마크에 추가하거나 제거합니다.
- 이미 북마크된 항목이면 제거하고, 북마크되지 않은 항목이면 추가합니다.
- 모든 사용자가 카페와 작품 모두 북마크할 수 있습니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/bookmarks` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **targetType** | String | O | 북마크 대상 타입 (`CAFE`, `ARTWORK`) |
| **targetId** | Long | O | 북마크 대상 ID (카페 ID 또는 작품 ID) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **isBookmarked** | Boolean | 북마크 상태 (true: 추가됨, false: 제거됨) |
| **bookmarkId** | Long | 북마크 ID (추가된 경우에만) |
| **targetType** | String | 북마크 대상 타입 (`CAFE`, `ARTWORK`) |
| **targetId** | Long | 북마크 대상 ID |
| **bookmarkCount** | Integer | 해당 대상의 총 북마크 수 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **BM4001** | 유효하지 않은 대상 타입입니다 |
| **BM4002** | 존재하지 않는 대상입니다 |
| **BM4003** | 필수 입력값이 누락되었습니다 |

# 6. Matchings (매칭) API

## 6.1 매칭 동의 요청

- 채팅을 통해 협의된 전시 조건으로 매칭 동의를 요청합니다.
- 양측 모두 동의하면 매칭이 자동으로 확정됩니다.
- 매칭 확정 시 슬롯 상태가 '전시 예정'으로 변경됩니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/matchings` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **cafeId** | Long | O | 카페 ID |
| **artworkId** | Long | O | 작품 ID |
| **slotId** | Long | O | 전시 슬롯 ID |
| **exhibitionStartDate** | String | O | 전시 시작일 (YYYY-MM-DD) |
| **exhibitionEndDate** | String | O | 전시 종료일 (YYYY-MM-DD) |
| **agreedCommissionRate** | Decimal | X | 합의된 수수료율 (%, 소수점 2자리) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **matchingId** | Long | 생성된 매칭 ID |
| **status** | String | 매칭 상태 (`PENDING`: 대기 중, `CONFIRMED`: 확정) |
| **cafeAgreed** | Boolean | 카페 사장 동의 여부 |
| **artistAgreed** | Boolean | 작가 동의 여부 |
| **exhibitionStartDate** | String | 전시 시작일 (YYYY-MM-DD) |
| **exhibitionEndDate** | String | 전시 종료일 (YYYY-MM-DD) |
| **agreedCommissionRate** | Decimal | 합의된 수수료율 (%) |
| **createdAt** | String | 매칭 요청 일시 (YYYY-MM-DD HH:mm:ss) |
| **agreedAt** | String | 매칭 확정 일시 (확정된 경우에만, YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **MT4001** | 카페 사장 또는 작가만 매칭을 요청할 수 있습니다 |
| **MT4002** | 존재하지 않는 카페입니다 |
| **MT4003** | 존재하지 않는 작품입니다 |
| **MT4004** | 존재하지 않는 슬롯입니다 |
| **MT4005** | 해당 슬롯은 이미 전시 중이거나 예정되어 있습니다 |
| **MT4006** | 해당 작품은 이미 전시 중이거나 예정되어 있습니다 |
| **MT4007** | 전시 종료일은 시작일보다 이후여야 합니다 |
| **MT4008** | 수수료율은 0~100 사이의 값이어야 합니다 |
| **MT4009** | 필수 입력값이 누락되었습니다 |
| **MT4010** | 이미 진행 중인 매칭이 있습니다 |

## 6.2 매칭 취소

- 확정된 매칭을 취소합니다.
- 매칭 취소 시 슬롯 상태가 '전시 가능'으로, 작품 상태가 '전시 가능'으로 변경됩니다.
- 카페 사장 또는 작가가 취소할 수 있습니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/matchings/{matchingId}` |
| **HTTP Method** | DELETE |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **matchingId** | Long | O | 취소할 매칭 ID |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **cancelReason** | String | X | 취소 사유 (최대 500자) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | null | 응답 데이터 없음 |
| **message** | String | 응답 메시지 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **MT4011** | 존재하지 않는 매칭입니다 |
| **MT4012** | 해당 매칭을 취소할 권한이 없습니다 |
| **MT4013** | 이미 취소된 매칭입니다 |
| **MT4014** | 전시가 이미 시작되어 취소할 수 없습니다 |

## 6.3 전시 일정 조회

- 사용자의 전시 일정을 조회합니다.
- 카페 사장: 카페의 슬롯별 전시 일정 확인
- 작가: 작품별 전시 일정 확인
- 본인의 전시 일정만 조회 가능합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/matchings` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **status** | String | X | 매칭 상태 필터 (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`, `ALL`), 기본값: `ALL` |
| **startDate** | String | X | 전시 시작일 필터 시작 (YYYY-MM-DD) |
| **endDate** | String | X | 전시 시작일 필터 종료 (YYYY-MM-DD) |
| **page** | Integer | X | 페이지 번호 (기본값: 0) |
| **size** | Integer | X | 페이지 크기 (기본값: 20) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **matchings** | List<MatchingDetail> | 매칭 목록 |
| **totalElements** | Integer | 전체 매칭 수 |
| **totalPages** | Integer | 전체 페이지 수 |
| **currentPage** | Integer | 현재 페이지 번호 |

### MatchingDetail 객체 (카페 사장용)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **matchingId** | Long | 매칭 ID |
| **status** | String | 매칭 상태 (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`) |
| **slotId** | Long | 슬롯 ID |
| **slotName** | String | 슬롯 이름 |
| **artworkId** | Long | 작품 ID |
| **artworkTitle** | String | 작품명 |
| **artworkThumbnail** | String | 작품 썸네일 URL |
| **artistId** | Long | 작가 ID |
| **artistName** | String | 작가명 |
| **exhibitionStartDate** | String | 전시 시작일 (YYYY-MM-DD) |
| **exhibitionEndDate** | String | 전시 종료일 (YYYY-MM-DD) |
| **agreedCommissionRate** | Decimal | 합의된 수수료율 (%) |
| **cafeAgreed** | Boolean | 카페 사장 동의 여부 |
| **artistAgreed** | Boolean | 작가 동의 여부 |
| **createdAt** | String | 매칭 요청 일시 (YYYY-MM-DD HH:mm:ss) |
| **agreedAt** | String | 매칭 확정 일시 (YYYY-MM-DD HH:mm:ss) |

### MatchingDetail 객체 (작가용)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **matchingId** | Long | 매칭 ID |
| **status** | String | 매칭 상태 (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`) |
| **artworkId** | Long | 작품 ID |
| **artworkTitle** | String | 작품명 |
| **artworkThumbnail** | String | 작품 썸네일 URL |
| **cafeId** | Long | 카페 ID |
| **cafeName** | String | 카페명 |
| **cafeAddress** | String | 카페 주소 |
| **slotId** | Long | 슬롯 ID |
| **slotName** | String | 슬롯 이름 |
| **exhibitionStartDate** | String | 전시 시작일 (YYYY-MM-DD) |
| **exhibitionEndDate** | String | 전시 종료일 (YYYY-MM-DD) |
| **agreedCommissionRate** | Decimal | 합의된 수수료율 (%) |
| **cafeAgreed** | Boolean | 카페 사장 동의 여부 |
| **artistAgreed** | Boolean | 작가 동의 여부 |
| **createdAt** | String | 매칭 요청 일시 (YYYY-MM-DD HH:mm:ss) |
| **agreedAt** | String | 매칭 확정 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **MT4001** | 카페 사장 또는 작가만 조회할 수 있습니다 |

# 7. Chats (채팅) API

## 7.1 채팅방 생성

- 카페 사장과 작가 간의 1:1 채팅방을 생성합니다.
- 작품 또는 카페 상세 페이지에서 채팅을 시작할 때 호출됩니다.
- 이미 채팅방이 존재하는 경우 기존 채팅방 정보를 반환합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/chats/rooms` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **targetUserId** | Long | O | 대화 상대방 회원 ID |
| **cafeId** | Long | X | 카페 ID (카페 관련 채팅인 경우) |
| **artworkId** | Long | X | 작품 ID (작품 관련 채팅인 경우) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **chatRoomId** | Long | 채팅방 ID |
| **targetUserId** | Long | 대화 상대방 회원 ID |
| **targetNickname** | String | 대화 상대방 닉네임 |
| **targetProfileImage** | String | 대화 상대방 프로필 이미지 URL |
| **cafeId** | Long | 카페 ID |
| **artworkId** | Long | 작품 ID |
| **createdAt** | String | 채팅방 생성 일시 (YYYY-MM-DD HH:mm:ss) |
| **isNew** | Boolean | 새로 생성된 채팅방 여부 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CH4001** | 존재하지 않는 회원입니다 |
| **CH4002** | 본인과는 채팅할 수 없습니다 |
| **CH4003** | 필수 입력값이 누락되었습니다 |

## 7.2 채팅방 목록 조회

- 사용자가 참여 중인 채팅방 목록을 조회합니다.
- 최신 메시지 정보와 읽지 않은 메시지 수를 함께 제공합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/chats/rooms` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **page** | Integer | X | 페이지 번호 (기본값: 0) |
| **size** | Integer | X | 페이지 크기 (기본값: 20) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **chatRooms** | List<ChatRoomSummary> | 채팅방 목록 |
| **totalElements** | Integer | 전체 채팅방 수 |
| **totalPages** | Integer | 전체 페이지 수 |
| **currentPage** | Integer | 현재 페이지 번호 |

### ChatRoomSummary 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **chatRoomId** | Long | 채팅방 ID |
| **targetUserId** | Long | 대화 상대방 회원 ID |
| **targetNickname** | String | 대화 상대방 닉네임 |
| **targetProfileImage** | String | 대화 상대방 프로필 이미지 URL |
| **lastMessage** | String | 마지막 메시지 내용 |
| **lastMessageTime** | String | 마지막 메시지 시간 (YYYY-MM-DD HH:mm:ss) |
| **unreadCount** | Integer | 읽지 않은 메시지 수 |
| **cafeId** | Long | 카페 ID |
| **artworkId** | Long | 작품 ID |
| **updatedAt** | String | 채팅방 업데이트 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |

## 7.3 채팅방 나가기

- 채팅방에서 나갑니다.
- 나간 후에도 채팅 기록은 유지되며, 다시 들어올 수 있습니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/chats/rooms/{roomId}` |
| **HTTP Method** | DELETE |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **roomId** | Long | O | 나갈 채팅방 ID |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | null | 응답 데이터 없음 |
| **message** | String | 응답 메시지 |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CH4004** | 존재하지 않는 채팅방입니다 |
| **CH4005** | 채팅방 참여자가 아닙니다 |

## 7.4 채팅 메시지 전송 (WebSocket)

- 채팅 메시지 전송은 **WebSocket 통신**으로 처리됩니다.
- REST API가 아닌 실시간 양방향 통신으로 메시지를 주고받습니다.
- 메시지 전송 시 상대방에게 실시간으로 전달되며, 푸시 알림이 발송됩니다.

### **WebSocket 연결**

```
ws://[domain]/ws/chat?token={accessToken}

```

### **메시지 전송 포맷 (JSON)**

```json
{
  "type": "SEND",
  "chatRoomId": 123,
  "messageType": "TEXT",
  "content": "안녕하세요",
  "imageUrl": null
}
```

### **메시지 수신 포맷 (JSON)**

```json
{
  "type": "MESSAGE",
  "messageId": 456,
  "chatRoomId": 123,
  "senderId": 789,
  "senderNickname": "홍길동",
  "messageType": "TEXT",
  "content": "안녕하세요",
  "imageUrl": null,
  "isRead": false,
  "createdAt": "2025-10-23 14:30:00"
}
```

### **메시지 타입**

- `TEXT`: 텍스트 메시지 (content 필수, 최대 1000자)
- `IMAGE`: 이미지 메시지 (imageUrl 필수)
- `SYSTEM`: 시스템 메시지 (매칭 동의, 취소 등)

## 7.5 채팅 메시지 목록 조회

- 채팅방의 메시지 목록을 조회합니다.
- 메시지 조회 시 자동으로 읽음 처리됩니다.
- 무한 스크롤 방식으로 페이징 처리됩니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/chats/rooms/{roomId}/messages` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **roomId** | Long | O | 채팅방 ID |

### 요청 쿼리 파라미터

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **lastMessageId** | Long | X | 마지막으로 조회한 메시지 ID (페이징 커서) |
| **size** | Integer | X | 페이지 크기 (기본값: 50) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **messages** | List<MessageDetail> | 메시지 목록 |
| **hasNext** | Boolean | 다음 페이지 존재 여부 |
| **nextCursor** | Long | 다음 페이지 커서 (마지막 메시지 ID) |

### MessageDetail 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **messageId** | Long | 메시지 ID |
| **senderId** | Long | 발신자 회원 ID |
| **senderNickname** | String | 발신자 닉네임 |
| **senderProfileImage** | String | 발신자 프로필 이미지 URL |
| **messageType** | String | 메시지 유형 (`TEXT`, `IMAGE`, `SYSTEM`) |
| **content** | String | 메시지 내용 |
| **imageUrl** | String | 이미지 URL |
| **isRead** | Boolean | 읽음 여부 |
| **createdAt** | String | 메시지 전송 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **CH4004** | 존재하지 않는 채팅방입니다 |
| **CH4005** | 채팅방 참여자가 아닙니다 |

# 8. Reviews (평가) API

## 8.1 전시 평가 작성

- 전시 종료 후 상대방에 대한 평가를 작성합니다.
- 카페 사장은 작가를, 작가는 카페 사장을 평가합니다.
- 평가는 전시 종료 후 14일 이내에 작성 가능하며, 수정할 수 없습니다.
- 작성된 평가는 채팅방에 시스템 메시지로 전송됩니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/reviews` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `application/json` |

### 요청 본문

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **matchingId** | Long | O | 매칭 ID |
| **rating** | String | O | 평가 (`LIKE`: 👍 좋아요, `OKAY`: 😐 괜찮아요, `DISLIKE`: 👎 별로에요) |
| **comment** | String | X | 코멘트 (최대 100자, 선택) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **reviewId** | Long | 평가 ID |
| **matchingId** | Long | 매칭 ID |
| **reviewerId** | Long | 평가자 회원 ID |
| **revieweeId** | Long | 피평가자 회원 ID |
| **rating** | String | 평가 (`LIKE`, `OKAY`, `DISLIKE`) |
| **comment** | String | 코멘트 |
| **createdAt** | String | 평가 작성 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **RV4001** | 존재하지 않는 매칭입니다 |
| **RV4002** | 해당 매칭에 대한 평가 권한이 없습니다 |
| **RV4003** | 전시가 종료되지 않아 평가할 수 없습니다 |
| **RV4004** | 이미 평가를 작성했습니다 |
| **RV4005** | 평가 작성 기한이 만료되었습니다 (전시 종료 후 14일) |
| **RV4006** | 유효하지 않은 평가입니다 |
| **RV4007** | 코멘트는 최대 100자까지 입력 가능합니다 |
| **RV4008** | 필수 입력값이 누락되었습니다 |

## 8.2 받은 평가 조회

- 사용자가 받은 평가 통계를 조회합니다.
- 본인 또는 타인의 평가 통계를 확인할 수 있습니다.
- 평가 비율과 총 평가 수를 제공합니다.

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/reviews/received/{userId}` |
| **HTTP Method** | GET |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` (선택) |

### 요청 경로 변수

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **userId** | Long | O | 조회할 회원 ID |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **userId** | Long | 회원 ID |
| **totalReviews** | Integer | 전체 평가 수 |
| **likeCount** | Integer | 👍 좋아요 수 |
| **okayCount** | Integer | 😐 괜찮아요 수 |
| **dislikeCount** | Integer | 👎 별로에요 수 |
| **likePercentage** | Decimal | 좋아요 비율 (%, 소수점 1자리) |
| **okayPercentage** | Decimal | 괜찮아요 비율 (%, 소수점 1자리) |
| **dislikePercentage** | Decimal | 별로에요 비율 (%, 소수점 1자리) |
| **recentReviews** | List<ReviewDetail> | 최근 평가 목록 (최대 5개, 코멘트가 있는 것만) |

### ReviewDetail 객체

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **reviewId** | Long | 평가 ID |
| **reviewerNickname** | String | 평가자 닉네임 |
| **rating** | String | 평가 (`LIKE`, `OKAY`, `DISLIKE`) |
| **comment** | String | 코멘트 |
| **createdAt** | String | 평가 작성 일시 (YYYY-MM-DD) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **RV4009** | 존재하지 않는 회원입니다 |

# 9. Images (이미지) API

## 9.1 이미지 업로드

- 카페 이미지, 작품 이미지, 프로필 이미지를 서버에 업로드합니다.
- 업로드된 이미지 URL을 반환하여 카페/작품 등록/수정 또는 프로필 수정 시 사용합니다.
- 지원 형식: JPG, JPEG, PNG, WEBP
- 최대 파일 크기: 10MB

### **요청 URL**

| **구분** | **상세값** |
| --- | --- |
| **HTTP URL** | `/api/v1/images/upload` |
| **HTTP Method** | POST |

### 요청 헤더

| **구분** | **상세값** |
| --- | --- |
| **Authorization** | `Bearer {accessToken}` |
| **Content-Type** | `multipart/form-data` |

### 요청 본문 (Form Data)

| **이름** | **타입** | **필수** | **설명** |
| --- | --- | --- | --- |
| **image** | File | O | 업로드할 이미지 파일 |
| **type** | String | O | 이미지 타입 (`CAFE`, `ARTWORK`, `PROFILE`) |

### 응답 본문

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **code** | String | 응답 코드 |
| **data** | 응답 데이터 객체 | 응답 데이터를 담은 객체 |
| **message** | String | 응답 메시지 |

### 응답 데이터 객체(data)

| **이름** | **타입** | **설명** |
| --- | --- | --- |
| **imageUrl** | String | 업로드된 이미지 URL |
| **imageId** | Long | 이미지 ID |
| **imageType** | String | 이미지 타입 (`CAFE`, `ARTWORK`, `PROFILE`) |
| **uploadedAt** | String | 업로드 일시 (YYYY-MM-DD HH:mm:ss) |

### 응답 코드

| **응답 코드** | **메시지** |
| --- | --- |
| **S0000** | 정상 처리 |
| **IM4001** | 지원하지 않는 이미지 형식입니다 |
| **IM4002** | 이미지 파일이 비어있습니다 |
| **IM4003** | 이미지 크기가 제한을 초과했습니다 (최대 10MB) |
| **IM4004** | 유효하지 않은 이미지 타입입니다 |
| **IM4005** | 필수 입력값이 누락되었습니다 |
| **IM5001** | 이미지 업로드에 실패했습니다 |