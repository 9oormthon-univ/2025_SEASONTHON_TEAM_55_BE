import requests, os
from dotenv import load_dotenv

# .env
load_dotenv()

CONF_KEY = os.getenv("JUSO_API_KEY")
BASE_URL = "https://www.juso.go.kr/addrlink/addrLinkApi.do"


def get_zip_code(keyword: str):
    params = {
        "confmKey": CONF_KEY,
        "currentPage": 1,
        "countPerPage": 1,
        "keyword": keyword,
        "resultType": "json"
    }

    r = requests.get(BASE_URL, params=params, timeout=10)
    r.raise_for_status()
    data = r.json()

    juso_list = data.get("results", {}).get("juso")  # 주소 결과 확인
    if not juso_list:
        return ""   # 검색 결과 없음 → 안전하게 빈 문자열 반환

    item = juso_list[0]   # 첫 번째 주소만 사용
    return item.get("admCd", "")[:5]


if __name__ == "__main__":
    keyword = input("주소를 입력하세요: ")
    admCd = get_zip_code(keyword)
    print(f"행정구역코드(앞 5자리): {admCd}")
