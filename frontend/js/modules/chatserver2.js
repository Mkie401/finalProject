// ==========================================
// 1. 全域變數與設定
// ==========================================
// ★ 設定後端網址
const BASE_URL = "http://localhost:8081";

const urlParams = new URLSearchParams(window.location.search);
// 預設 ID 為 999，方便測試
let myId = urlParams.get('id') || 999; 

let currentReceiverId = null; 
let stompClient = null;

// DOM 元素快取
let chatContainer = null;
let listDiv = null;
let singleDiv = null;
let listChevron = null;
let msgBox = null;

// 罐頭訊息資料
const cannedMessages = [
    "👋 嗨嗨～ReHome 狗狗小編來啦！有什麼可以為您效勞的嗎？🥰",
    "🙏 稍等我一下下喔，我正努力幫您查資料中～🐾",
    "📄 想跟您確認一下，領養申請表上的資訊都有填寫正確嗎？🌟",
    "📸 麻煩您再補傳一張居家環境的小照片給我們看看可以嗎？謝謝您～💕",
    "✅ 好的沒問題！我已經幫您記錄下來囉，接下來會有專人再和您聯繫～🐶✨",
    "👋 感謝您的詢問！祝您今天也被幸福包圍～汪汪陪您度過美好的一天！🌈💛"
];

// ==========================================
// 2. 主啟動函式 (window.initChatServer)
// ==========================================
window.initChatServer = function() {
    console.log("🔥 initChatServer 啟動中... 身分ID:", myId);

    // 1. 抓取 DOM
    chatContainer = document.getElementById("chat-container") || document.getElementById("floating-chat");
    listDiv = document.querySelector(".sidebar-left");
    singleDiv = document.querySelector(".chat-main");
    listChevron = document.getElementById("minimize-arrow");
    msgBox = document.getElementById("messages");

    if (!chatContainer) {
        console.error("❌ 找不到聊天室主體 (ID 應為 chat-container 或 floating-chat)");
        return;
    }

    // 2. 綁定按鈕
    const minBtn = document.getElementById("minimize-button");
    if (minBtn) minBtn.onclick = window.toggleChatWindow;
    
    const ballIcon = document.getElementById("minimized-ball-icon");
    if (ballIcon) ballIcon.onclick = window.toggleChatWindow;

    const msgInput = document.getElementById("message-input");
    if(msgInput) {
        msgInput.addEventListener("keydown", (e) => {
            if(e.key === "Enter") {
                e.preventDefault();
                window.sendMessage();
            }
        });
        msgInput.addEventListener("focus", () => {
            closeAllMenus();
        });
    }

    // 3. 綁定選單
    const plusIcon = document.getElementById("plus-menu-icon");
    if(plusIcon) plusIcon.onclick = window.togglePlusMenu;

    const emojiIcon = document.getElementById("emoji-keyboard-icon");
    if(emojiIcon) emojiIcon.onclick = window.toggleStickerMenu;
    
    const stickerArea = document.getElementById("sticker-area");
    if(stickerArea) {
        stickerArea.querySelectorAll("img").forEach(img => {
            img.onclick = function() {
                window.sendMessage(this.getAttribute("src"));
                stickerArea.style.display = "none";
            };
        });
    }

    // 4. 圖片上傳功能
    const uploadIcon = document.getElementById("image-upload-icon");
    const fileInput = document.getElementById("file-input");

    if (uploadIcon && fileInput) {
        uploadIcon.onclick = function() {
            fileInput.accept = "image/*";
            fileInput.click();
        };

        fileInput.onchange = function(e) {
            const files = e.target.files;
            if (files.length === 0) return;
            
            const file = files[0];
            if (!currentReceiverId) {
                alert("請先選擇聊天對象！");
                fileInput.value = '';
                return;
            }

            const formData = new FormData();
            formData.append("senderId", myId);
            formData.append("receiverId", currentReceiverId);
            formData.append("content", "傳送了一張圖片");
            formData.append("image", file);

            // 前端預覽 (不用等後端回傳，直接顯示)
            const reader = new FileReader();
            reader.onload = function(evt) {
                renderSingleMessage({
                    img: evt.target.result,
                    senderId: myId,
                    sentAt: new Date().toISOString()
                });
            };
            reader.readAsDataURL(file);

            // 後端發送 (改用新 API)
            fetchApi("/api/cs/send", 'POST', formData)
                .catch(err => console.error("圖片上傳失敗", err));

            fileInput.value = '';
        };
    }

    // 5. 載入資料與連線
    loadChatList();
    connectWebSocket();
    
    // 6. 全域點擊關閉選單
    document.addEventListener("click", (e) => {
        closeMenusIfClickedOutside(e);
    });
};

// ==========================================
// 3. 視窗控制與選單
// ==========================================
window.toggleChatWindow = function(event) {
    if (event) event.stopPropagation();
    if (!chatContainer) return;
    if (chatContainer.classList.contains("expanded")) {
        chatContainer.classList.remove("expanded");
        chatContainer.classList.add("minimized");
    } else {
        chatContainer.classList.remove("minimized");
        chatContainer.classList.add("expanded");
    }
};
window.minimizeChat = window.toggleChatWindow;

window.togglePlusMenu = function(e) {
    if(e) e.stopPropagation();
    const menu = document.getElementById("expanded-menu");
    const icon = document.getElementById("plus-menu-icon");
    if(!menu) return;

    if (menu.style.display === "flex") {
        menu.style.display = "none";
        if(icon) { icon.classList.remove("fa-xmark"); icon.classList.add("fa-plus"); }
    } else {
        closeAllMenus(); 
        menu.style.display = "flex";
        if(icon) { icon.classList.remove("fa-plus"); icon.classList.add("fa-xmark"); }
    }
};

window.toggleQuickReplies = function(e) {
    if(e) e.stopPropagation();
    const menu = document.getElementById("quick-reply-menu");
    const list = menu ? menu.querySelector(".quick-reply-list") : null;
    if(!menu || !list) return;

    if (menu.style.display === "block") {
        menu.style.display = "none";
    } else {
        closeAllMenus();
        let html = "";
        cannedMessages.forEach(msg => {
            const safeMsg = msg.replace(/'/g, "\\'");
            html += `<div style="padding:10px; border-bottom:1px solid #eee; cursor:pointer;" 
                          onclick="window.sendQuickReply('${safeMsg}')">
                          ${msg}
                     </div>`;
        });
        list.innerHTML = html;
        menu.style.display = "block";
    }
};

window.sendQuickReply = function(text) {
    window.sendMessage(text);
    closeAllMenus();
};

window.toggleStickerMenu = function(e) {
    if(e) e.stopPropagation();
    const menu = document.getElementById("sticker-area");
    if(!menu) return;
    
    const d = menu.style.display;
    if(d === "grid" || d === "flex" || d === "block") {
        menu.style.display = "none";
    } else {
        closeAllMenus();
        menu.style.display = "grid"; 
    }
};

function closeAllMenus() {
    ["expanded-menu", "quick-reply-menu", "sticker-area"].forEach(id => {
        const el = document.getElementById(id);
        if(el) el.style.display = "none";
    });
    const icon = document.getElementById("plus-menu-icon");
    if(icon) { icon.classList.remove("fa-xmark"); icon.classList.add("fa-plus"); }
}

function closeMenusIfClickedOutside(e) {
    const expandedMenu = document.getElementById("expanded-menu");
    const quickMenu = document.getElementById("quick-reply-menu");
    const stickerArea = document.getElementById("sticker-area");
    
    if(expandedMenu && expandedMenu.style.display === 'flex') {
        if(!expandedMenu.contains(e.target) && e.target.id !== 'plus-menu-icon') {
            expandedMenu.style.display = 'none';
            const icon = document.getElementById("plus-menu-icon");
            if(icon) { icon.classList.remove("fa-xmark"); icon.classList.add("fa-plus"); }
        }
    }
    if(quickMenu && quickMenu.style.display === 'block') {
        if(!quickMenu.contains(e.target) && !e.target.closest('.menu-item')) quickMenu.style.display = 'none';
    }
    if(stickerArea && stickerArea.style.display !== 'none') {
        if(!stickerArea.contains(e.target) && e.target.id !== 'emoji-keyboard-icon') {
             stickerArea.style.display = 'none';
        }
    }
}

// ==========================================
// 4. 聊天核心功能 (已改為新 API)
// ==========================================

function loadChatList() {
    const listBox = document.getElementById("chat-list-container");
    if (!listBox) return;
    listBox.innerHTML = '<div style="padding:15px; color:gray;">載入中...</div>';

    // ★ 改為新 API 路徑
    fetchApi(`/api/cs/list?myId=${myId}`, 'GET')
        .then(data => {
            if (!data || data.length === 0) {
                // 如果沒資料，顯示兩個測試用假人，方便你驗證
                listBox.innerHTML = `
                    <div class="list-item" onclick="openChat('測試員 ID:1', 1)">
                         <div class="list-content"><div>強制連 ID:1</div></div>
                    </div>
                    <div class="list-item" onclick="openChat('測試員 ID:2', 2)">
                         <div class="list-content"><div>強制連 ID:2</div></div>
                    </div>`;
                return;
            }
            let html = "";
            data.forEach(item => {
                const displayName = item.otherUserName || `用戶 ${item.otherUserId}`;
                // 因為是新客服系統，暫時用預設圖
                const avatarUrl = "/img/default.jpg"; 
                let preview = item.content || "...";
                if (preview.includes("data:image") || preview.includes("/img/")) preview = "[圖片]";

                html += `
                <div class="list-item" id="list-item-${item.otherUserId}" onclick="openChat('${displayName}', ${item.otherUserId})">
                    <div class="avatar"><img src="${avatarUrl}" onerror="this.onerror=null; this.src='/img/default.jpg'"/></div>
                    <div class="list-content">
                        <div class="list-header"><span class="list-title">${displayName}</span></div>
                        <div class="list-preview">${preview}</div>
                    </div>
                </div>`;
            });
            listBox.innerHTML = html;
        })
        .catch(err => {
            console.error("列表載入錯誤", err);
            listBox.innerHTML = '<div style="padding:15px;">載入失敗</div>';
        });
}

window.openChat = function(title, id) {
    currentReceiverId = id;
    
    document.querySelectorAll('.list-item').forEach(item => {
        item.style.backgroundColor = "";
        item.classList.remove("active-chat");
    });
    const listItem = document.getElementById(`list-item-${id}`);
    if (listItem) {
        listItem.classList.remove("unread");
        listItem.classList.add("active-chat");
        listItem.style.backgroundColor = "#eef"; 
    }

    const titleEl = document.querySelector(".global-title");
    if (titleEl) titleEl.innerText = title;

    if (msgBox) msgBox.innerHTML = '';
    loadChatHistory(myId, id);
};

function loadChatHistory(senderId, receiverId) {
    if (!msgBox) return;
    msgBox.innerHTML = '<div style="text-align:center; padding:20px;">載入歷史...</div>';
    
    // ★ 改為新 API 路徑
    fetchApi(`/api/cs/history?myId=${senderId}&otherId=${receiverId}`, 'GET')
        .then(data => {
            msgBox.innerHTML = '';
            if (!data || data.length === 0) {
                msgBox.innerHTML = '<div style="text-align:center; margin-top:20px;">開始聊天吧！</div>';
                return;
            }
            data.forEach(msg => renderSingleMessage(msg));
            msgBox.scrollTop = msgBox.scrollHeight;
        })
        .catch(() => {
            msgBox.innerHTML = '<div style="text-align:center;">無歷史訊息</div>';
        });
}

// ==========================================
// 5. WebSocket (重點修改)
// ==========================================
function connectWebSocket() {
    // 確保有引入 SockJS 與 Stomp (HTML 需包含 CDN)
    if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') return;
    
    // ★ 連到 8081 的 WebSocket 端點
    const socket = new SockJS(`${BASE_URL}/ws`); 
    
    stompClient = Stomp.over(socket);
    stompClient.debug = null; 
    
    stompClient.connect({}, () => {
        console.log("✅ WebSocket 連線成功");
        // 訂閱個人訊息佇列
        stompClient.subscribe('/user/queue/messages', onMessageReceived);
    }, (err) => {
        console.error("❌ WebSocket 連線失敗", err);
    });
}

function onMessageReceived(payload) {
    const msg = JSON.parse(payload.body);
    const senderId = String(msg.senderId);
    
    // 如果是我自己發的 (多視窗同步)，或者 對方發給我的
    // 且 剛好是目前打開的視窗，就畫出來
    if (senderId === String(myId) || (currentReceiverId && senderId === String(currentReceiverId))) {
        renderSingleMessage(msg);
    } else {
        // 如果是別人的訊息，可能要亮紅點 (這裡先改背景色)
        const item = document.getElementById(`list-item-${senderId}`);
        if (item) item.style.backgroundColor = "#fff3cd"; 
        
        // 如果我是客服，收到新客人的訊息，應該刷新列表
        loadChatList();
    }
}

window.sendMessage = function(text = null) {
    const input = document.getElementById("message-input");
    const content = text ? text : (input ? input.value.trim() : "");
    if (!content || !currentReceiverId) return;

    const formData = new FormData();
    formData.append("senderId", myId);
    formData.append("receiverId", currentReceiverId);
    formData.append("content", content);

    // ★ 改為新 API 路徑
    fetchApi("/api/cs/send", 'POST', formData).then(msg => {
        renderSingleMessage(msg);
        if(input) input.value = "";
        closeAllMenus();
    }).catch(console.error);
};

function renderSingleMessage(msg) {
    if (!msgBox) return;
    const isMe = (String(msg.senderId) === String(myId));
    let content = "";
    
    if (msg.img) {
         const imgSrc = msg.img.startsWith('data:') ? msg.img : `data:image/jpeg;base64,${msg.img}`;
         content = `<img src="${imgSrc}" style="max-width:200px; border-radius:10px;">`;
    } else if (msg.content && (msg.content.includes("/img/") || msg.content.match(/\.(png|jpg|jpeg|gif)$/i))) {
         // 貼圖或圖片連結
         content = `<img src="${msg.content}" style="max-width:150px;">`;
    } else {
         content = `<div>${msg.content || ''}</div>`;
    }
    
    const html = isMe ? 
        `<div class="message-row sent">
            <div class="message-content"><div class="message-bubble sent-bubble">${content}</div></div>
         </div>` :
        `<div class="message-row received">
            <div class="message-content"><div class="message-bubble received-bubble">${content}</div></div>
         </div>`;
    
    msgBox.insertAdjacentHTML('beforeend', html);
    msgBox.scrollTop = msgBox.scrollHeight;
}

window.switchTab = function(tab) {
    console.log("切換分頁:", tab);
    document.querySelectorAll('.tab-menu button').forEach(b => b.classList.remove('active-tab'));
    const btn = document.getElementById(`tab-${tab}`);
    if(btn) btn.classList.add('active-tab');
};

async function fetchApi(url, method = 'GET', body = null) {
    const opts = { method: method };
    if (body && !(body instanceof FormData)) {
        opts.headers = { 'Content-Type': 'application/json' };
        opts.body = JSON.stringify(body);
    } else if (body) opts.body = body;
    
    // 自動拼接 BASE_URL
    const finalUrl = url.startsWith("http") ? url : BASE_URL + url;

    const res = await fetch(finalUrl, opts);
    if (!res.ok) throw new Error(res.statusText);
    return await res.json();
}

// ==========================================
// 6. 啟動入口 (強制開發模式)
// ==========================================
(function checkLoginAndStart() {
    console.warn("⚠️ 開發模式：強制連線至 " + BASE_URL);

    const devUrlParams = new URLSearchParams(window.location.search);
    if (devUrlParams.get('id')) {
        myId = devUrlParams.get('id');
    } else {
        myId = 999; 
    }

    console.log(`🚀 強制啟動，ID: ${myId}`);

    setTimeout(() => {
        if (typeof window.initChatServer === 'function') {
            window.initChatServer();
        } else {
            console.error("❌ 找不到 initChatServer");
        }
    }, 100);
})();