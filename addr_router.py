from fastapi import APIRouter, Query
from fastapi.responses import JSONResponse
import requests, os, time
from datetime import datetime
from zip_Cd import get_zip_code
from dotenv import load_dotenv

load_dotenv()
API_KEY = os.getenv("YOUTH_API_KEY")
BASE = "https://www.youthcenter.go.kr/go/ythip/getPlcy"
PAGE_SIZE, MAX_ROWS = 100, 200

router = APIRouter()

def fetch_policies(ziptxt: str, max_rows=MAX_ROWS):
    all_rows, page = [], 1
    zipCd = get_zip_code(ziptxt)

    parts, zipKwd = ziptxt.split(), ""
    if len(parts) > 1:
        second = parts[1]
        if second.endswith(("시", "군")): zipKwd = second[:-1]
        elif second.endswith("구") and len(second) > 2: zipKwd = second[:-1]
        else: zipKwd = second

    while len(all_rows) < max_rows:
        params = {
            "apiKeyNm": API_KEY, "rtnType": "json",
            "pageNum": page, "pageSize": PAGE_SIZE,
            "plcyNm": zipKwd, "zipCd": zipCd
        }
        r = requests.get(BASE, params=params, timeout=30)
        rows = r.json().get("result", {}).get("youthPolicyList", [])
        if not rows: break
        all_rows.extend([row for row in rows if row.get("zipCd", "").startswith(zipCd)])
        page += 1
    return all_rows[:max_rows]

def parse_date_range(date_str: str):
    try:
        return datetime.strptime(date_str.split("~")[1].strip(), "%Y%m%d").date()
    except:
        return None

@router.get("/policies")
def get_policies(address: str = Query(..., description="예: 광주광역시 남구")):
    start = time.time()
    rows = fetch_policies(address)
    if not rows:
        return JSONResponse(status_code=200, content={"message": "No Content"})

    today = datetime.today().date()
    policies = []

    for row in rows:
        end_date = parse_date_range(row.get("aplyYmd", ""))
        if end_date and end_date < today: continue
        if "청년" not in (row.get("plcyNm") or ""): continue

        url = row.get("aplyUrlAddr") or row.get("refUrlAddr1") or row.get("refUrlAddr2") or ""
        policies.append({
            "plcyNm": row.get("plcyNm", ""),
            "sprvsnInstCdNm": row.get("sprvsnInstCdNm", ""),
            "inqCnt": int(row.get("inqCnt") or 0),
            "url": url
        })

    if not policies:
        return JSONResponse(status_code=200, content={"message": "No Content"})

    policies = sorted(policies, key=lambda x: x["inqCnt"], reverse=True)[:20]
    return JSONResponse(status_code=200, content={"policies": policies})
