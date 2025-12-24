// 共用元件模組
// -------------- 變數對應表 ----------------
export const sizeMap = {
    small: '小型',
    medium: '中型',
    big: '大型'
};

export const ageMap = {
    child: '幼年',
    adult: '成年',
    old: '老年'
};

export const genderMap = {
    male: '男',
    female: '女'
};

export const dataTypeList = {
    'region': '所在地',
    'source': '來源',
    'shelter': '收容所',
    'species': '物種',
    'gender': '性別',
    'body_size': '體型',
    'age': '年齡',
    'status': '寵物狀態',
    'neutered_status': '結紮狀態',
    'has_chip': '有無晶片',
    'adoption_area': '可送養範圍'
};

// -------------- 通用方法 ----------------
function getAuthToken() {
    return localStorage.getItem('authToken'); 
}

export function fetchApi(url, method = 'GET', data) {
    const token = getAuthToken(); // 取得 Token

    // 檢查是否有 Token，若無則不新增 Authorization Header
    const headers = token ? {
        // 使用 Bearer scheme 是業界最常見的 JWT 傳輸方式
        'Authorization': 'Bearer ' + token 
    } : {};

    return $.ajax({
        url: '/api/' + url,
        method: method,
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify(data)
        // headers: headers    // ** <-- Auth Token 放置處**
    }).fail((xhr, status, error) => {
        console.log(status);
        console.error('載入資料失敗:', error);
    });
}

export function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    if (isNaN(d)) return dateStr;
    return d.toLocaleDateString();
}

export function calculateAgeUsingDiff(birthdateString) {
    const birthDate = new Date(birthdateString);
    const today = new Date();
    
    // 計算兩個時間點之間的毫秒數差異
    const diff_ms = today.getTime() - birthDate.getTime(); 

    // 將毫秒轉換為年。使用 1000 毫秒/秒 * 60 秒/分 * 60 分/時 * 24 時/日 * 365.25 日/年 (考慮閏年)
    const ms_per_year = 1000 * 60 * 60 * 24 * 365.25;

    // 將結果向下取整，得到滿歲的年齡
    return Math.floor(diff_ms / ms_per_year);
}

export default {
    sizeMap,
    ageMap,
    genderMap,
    fetchApi,
    formatDate,
    calculateAgeUsingDiff
};