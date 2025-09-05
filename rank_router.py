from fastapi import APIRouter
from fastapi.responses import JSONResponse
import requests, asyncio, time
from policy_enrich_async import enrich_policies

BASE = "https://www.youthcenter.go.kr"
URL  = f"{BASE}/wrk/yrm/plcy/RankPlcy"

router = APIRouter()

def get_rank10():
    s = requests.Session()
    s.get(BASE, timeout=8)
    r = s.get(URL, params={"isMaskingYn": "Y"}, timeout=8)
    r.raise_for_status()
    data = r.json()
    return [{"plcyNo": item["plcyNo"]} for item in data.get("result", {}).get("rankPlcyList", [])]

@router.get("/top10")
def rank10():
    start = time.time()
    rank_list = get_rank10()
    enriched = asyncio.run(enrich_policies(rank_list))
    return JSONResponse(status_code=200, content={
        "policies": enriched
    })
