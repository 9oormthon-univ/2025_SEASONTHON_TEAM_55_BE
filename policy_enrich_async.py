# file: policy_enrich_async.py
import asyncio, httpx, os
from dotenv import load_dotenv

load_dotenv()

BASE_URL = "https://www.youthcenter.go.kr/go/ythip/getPlcy"
API_KEY = os.getenv("YOUTH_API_KEY")

async def get_policy_detail(client, plcyNo):
    """개별 정책 상세조회"""
    params = {"apiKeyNm": API_KEY, "plcyNo": plcyNo}
    try:
        resp = await client.get(BASE_URL, params=params, timeout=10)
        resp.raise_for_status()
        data = resp.json()
    except Exception as e:
        print(f"API 요청 오류 ({plcyNo}): {e}")
        return {}

    if "result" in data and "youthPolicyList" in data["result"]:
        policies = data["result"]["youthPolicyList"]
        if policies:
            p = policies[0]
            url_final = p.get("aplyUrlAddr") or p.get("refUrlAddr1") or p.get("refUrlAddr2") or ""
            return {
                "plcyNo": plcyNo,
                "plcyNm": p.get("plcyNm"),
                "sprvsnInstCdNm": p.get("sprvsnInstCdNm"),
                "inqCnt": int(p.get("inqCnt") or 0),
                "url": url_final,
            }
    return {}

async def enrich_policies(policy_list):
    """병렬조회 + 조회수 정렬"""
    async with httpx.AsyncClient() as client:
        tasks = [get_policy_detail(client, item["plcyNo"]) for item in policy_list]
        results = await asyncio.gather(*tasks)
    results = [r for r in results if r]  # 빈 dict 제거
    results.sort(key=lambda x: x.get("inqCnt", 0), reverse=True)
    return results
