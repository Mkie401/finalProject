let currentModuleScript = null; // 追蹤當前載入的 JS 模組的路徑

// --- 載入 基本HTML 片段 ---
function loadFragments() {
    // 載入 Header
    $('#header-placeholder').load('fragments/header.html');
    // 載入 Footer
    $('#footer-placeholder').load('fragments/footer.html');
    // 載入 Floating Element
    $('#floating-element').load('fragments/floatingelement.html');

    $('#chatroom-element').load('fragments/ChatroomFinal.html');
    
    renderScript('/js/utils/navbar.js');
}

// --- 路由切換與內容載入 (核心邏輯) ---
function handleRouting() {
    const currentHash = window.location.hash.split('?')[0] || '';
    const route = ROUTE_MAP[currentHash];

    if (!route) {
        console.error('404: 找不到對應的路由:', currentHash);
        // 建議：如果是首頁 ('') 就不要導向，避免無窮迴圈
        if (routeKey !== '') {
            window.location.hash = ''; // 導向首頁
        }
        return;
    }

    // 1. 載入對應的 HTML 內容
    $('#main-content-area').load(route.contentFile, function (response, status, xhr) {
        if (status === "error") {
            $('#main-content-area').html(`<h2>404 - 找不到頁面：${route.contentFile}</h2>`);
            return;
        }

        // 2. 執行模組化 JS 
        loadModuleScript(route.moduleJS);
    });
}

function renderScript(scriptPath) {
    // 動態創建 <script> 標籤並載入
    const script = document.createElement('script');
    script.type = 'module';
    script.src = scriptPath;
    // 使用 .prop('defer', true) 確保與 index.html 的 defer 行為一致
    $(script).prop('defer', true);
    document.body.appendChild(script);

    if (scriptPath.includes('pet-adoption-list')) {        
        setTimeout(() => {
            if (typeof window.adoptionInit === 'function') {
                window.adoptionInit();
            }
        }, 100);
    } else if (scriptPath.includes('pet-adoption-detail')) {
        setTimeout(() => {
            if (typeof window.adoptionDetail_init === 'function') {
                window.adoptionDetail_init();
            }
        }, 100);
    }
}

// --- 載入並執行業務邏輯模組 ---
function loadModuleScript(scriptPath) {
    // 移除前一個模組的 script 元素，防止重複或衝突
    if (currentModuleScript) {
        $(`script[src="${currentModuleScript}"]`).remove();
    }

    if (scriptPath) {
        renderScript(scriptPath);
        currentModuleScript = scriptPath;
    }
}

// --- 5. 初始化與事件綁定 ---
loadFragments();
handleRouting();
$(window).on('hashchange', handleRouting); // 監聽 URL hash 變更事件

