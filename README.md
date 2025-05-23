# Ticket 911 - 실시간 티켓팅 서비스

## 📌 프로젝트 소개
대규모 트래픽 환경에서의 동시성 제어에 중점을 둔 실시간 티켓팅 서비스입니다.
다양한 분산 락 구현체를 비교하고 검증하여 안정적인 좌석 예약 시스템을 구현했습니다.


## 🛠 기술 스택

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot
- **ORM**: Spring Data JPA
- **Security**: Spring Security, JWT

### Database
- **RDB**: MySQL
- **Distributed Lock**: Redis
    - Lettuce
    - Redisson

### Monitoring
- **APM**: Prometheus
- **Dashboard**: Grafana

## 🔍 주요 기능
1. **실시간 좌석 예약**
    - 분산 락을 통한 동시성 제어
    - 좌석 선점 시스템

2. **공연 관리**
    - 공연 등록/수정/삭제
    - 좌석 구역 관리

3. **예매 관리**
    - 예매 내역 조회
    - 예매 취소

## 📊 성능 개선
- JMeter를 통한 부하 테스트 수행
- 동시성 문제 해결을 위한 병목 구간 분석
- 분산 락 처리 성능 측정 및 개선

## 📚 프로젝트 문서
- [와이어 프레임](https://www.notion.so/teamsparta/1f52dc3ef514807a8678c57c1d5d7c00?pvs=4)
- [API 명세서 및 ERD](https://www.notion.so/teamsparta/9-1e52dc3ef51480fbb6f5c063e68f9eaf?pvs=4)
- [코드 컨벤션](https://www.notion.so/teamsparta/1f52dc3ef51480569ef2c429cbdd9380?pvs=4)
- [트러블슈팅](https://www.notion.so/teamsparta/1fb2dc3ef51480c8a89af0b1e6c997fc?pvs=4)

## 패키지 구조
````
ticket911/
├── common/        # 공통 유틸리티
├── config/        # 설정
├── domain/        # 도메인 별 패키지
│   ├── auth/
│   ├── booking/
│   ├── concert/
│   └── user/
└── infrastructure/# 인프라 관련
    ├── lettuce/
    └── redisson/

````