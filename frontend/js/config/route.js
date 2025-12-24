// --- 1. 路由對應表 (ROUTE_MAP) ---
// 將 URL hash 對應到 HTML 內容檔案和對應的 JS 模組檔案
const ROUTE_MAP = {
    '': { contentFile: 'content/home.html', moduleJS: 'js/modules/home/index.js' }, // 預設首頁
    '#home': { contentFile: 'content/home.html', moduleJS: 'js/modules/home/index.js' },
    '#pet-adoption-list': { contentFile: 'content/pet-adoption-list.html', moduleJS: 'js/modules/pet-adoption-list.js' },
    '#pet-adoption-detail': { contentFile: 'content/pet-adoption-detail.html', moduleJS: 'js/modules/pet-adoption-detail.js' },
    '#pet-missing-list': { contentFile: 'content/pet-adoption-list.html', moduleJS: 'js/modules/pet-adoption-list.js' },
    '#pet-missing-detail': { contentFile: 'content/pet-adoption-detail.html', moduleJS: 'js/modules/pet-adoption-detail.js' },
    '#missing_publish_1': { contentFile: 'content/missing_publish_1.html', moduleJS: 'js/modules/missing_publish_1_main.js' },
    '#missing_publish_2': { contentFile: 'content/missing_publish_2.html', moduleJS: 'js/modules/missing_publish_2.js' },
    '#missing_publish_result': { contentFile: 'content/missing_publish_result.html', moduleJS: 'js/modules/missing_publish_result.js' },
    '#qna': { contentFile: 'content/qna.html', moduleJS: 'js/modules/qna.js' },
    '#register': { contentFile: 'content/register.html', moduleJS: null },
    '#login': { contentFile: 'content/login_form.html', moduleJS: 'js/modules/login_bundle.js' },
    '#resetpassword': { contentFile: 'content/repasswd.html', moduleJS: 'js/modules/resetpassword.js' },
    '#member': { contentFile: 'content/memcenter.html', moduleJS: 'js/modules/memcenter.js' }, 
    '#adoption-review': { contentFile: 'content/adoption-review.html', moduleJS: 'js/modules/adoption-review.js' },
    '#pet-inf': { contentFile: 'content/pet-Inf.html', moduleJS: 'js/modules/petinf.js' },
    '#pet-inf-con': { contentFile: 'content/pet-inf-con.html', moduleJS: 'js/modules/petinfcon.js' },
    '#review-successful': { contentFile: 'content/review-successful.html', moduleJS: 'js/modules/review-successful.js' },
    '#review-failed': { contentFile: 'content/review-failed.html', moduleJS: 'js/modules/review-failed.js' }   
};
