from fastapi import FastAPI
from pydantic import BaseModel
from typing import List

# FastAPI 애플리케이션 인스턴스 생성
app = FastAPI(
    title="Yeobaek Recommender API",
    description="추천 시스템 API",
    version="1.0.0"
)

# ============================================
# 요청/응답 모델 정의 (Pydantic)
# ============================================

class RatingData(BaseModel):
    """평점 데이터 모델 (요청)"""
    userId: int
    itemId: int
    score: float

class RecommendationResponse(BaseModel):
    """추천 결과 응답 모델"""
    items: List[int]


# ============================================
# API 엔드포인트
# ============================================

@app.get("/health")
async def health_check():
    """헬스 체크 엔드포인트"""
    return {"status": "healthy"}

@app.post("/recommend", response_model=RecommendationResponse)
async def get_recommendations(ratings: List[RatingData]):
    """
    평점 데이터를 받아서 추천 아이템 목록을 반환

    Args:
        ratings: 평점 데이터 배열 [{"userId": 1, "itemId": 101, "score": 4.5}, ...]

    Returns:
        추천 아이템 ID 목록 {"items": [101, 102, 103, ...]}
    """
    # TODO: SVD 또는 ALS 모델을 사용한 추천 로직 구현 예정
    # 임시로 더미 데이터 반환
    return RecommendationResponse(items=[101, 102, 103, 104, 105])


# ============================================
# 서버 실행 (개발용)
# ============================================

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
