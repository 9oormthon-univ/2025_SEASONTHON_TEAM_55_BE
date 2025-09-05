from fastapi import FastAPI
from addr_router import router as addr_router
from rank_router import router as rank_router

app = FastAPI(title="청년정책 API")

# 라우터 등록
app.include_router(addr_router, prefix="/addr", tags=["주소 기반 정책"])
app.include_router(rank_router, prefix="/rank", tags=["랭킹 정책"])
