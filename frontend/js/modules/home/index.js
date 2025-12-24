// js/modules/home/index.js

// 匯入其他小弟
import { loadAdoptionData } from './adoption.js';
import { loadMissingData } from './missing.js'; 
import { loadQnaData } from './qna.js';           

// 定義一個主要的啟動函式
export async function initHome() {
    console.log("首頁模組初始化...");
    
    // 平行執行所有 API 請求 (加快速度)
    await Promise.all([
        loadAdoptionData(),
        loadMissingData(),
        loadQnaData()
    ]);

    // 等資料都塞進 DOM 之後，再啟動跑馬燈 (如果原本是全域函式)
    if (typeof index_initMarquee === 'function') index_initMarquee();
    if (typeof missing_initMarquee === 'function') missing_initMarquee();
    
    console.log("首頁載入完成！");
}

// === 關鍵點 ===
// 因為這是 ES Module，載入時會自動執行
// 但為了確保 HTML 已經載入，通常 Router 會處理好
// 這裡直接執行 initHome()
initHome();