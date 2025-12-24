// ==========================================
// 0. 基礎工具：時間格式化與 LocalStorage (前端模擬結案用)
// ==========================================
function formatTime(isoString) {
    if (!isoString) return "";
    const date = new Date(isoString);
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
}

// 取得前端模擬結案的 ID 列表
function getArchivedIds() {
    return JSON.parse(localStorage.getItem('cs_archived_ids') || "[]");
}

// 用來追蹤未讀狀態的 Set (解決新對話紅點消失問題)
let unreadIds = new Set();

// ==========================================
// 1. 工具函式 (內建 FetchApi)
// ==========================================
const BASE_URL = "http://localhost:8081"; 
const ADMIN_ID = 2; 

// API 路徑
const API_PATH_QNA_LIST = "/api/csf/all"; 
const API_PATH_QNA_REPLY = "/api/csf/reply"; 

async function fetchApi(url, method = 'GET', body = null) {
    const headers = {};
    if (body && !(body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
        body = JSON.stringify(body);
    }

    // ★★★ 修正：先拿掉 credentials，解決列表消失問題 ★★★
    // 等後端 CORS 設定好特定網域後，再加回來
    const opts = { 
        method, 
        headers
    };
    
    if (body) opts.body = body;

    let finalUrl = url.startsWith("http") ? url : (BASE_URL + (url.startsWith("/") ? url : "/" + url));

    try {
        const res = await fetch(finalUrl, opts);
        if (!res.ok) {
            // 嘗試讀取後端錯誤訊息
            const errText = await res.text();
            throw new Error(`API Error ${res.status}: ${errText}`);
        }
        return await res.json();
    } catch (err) {
        console.error(`FetchApi Error (${url}):`, err);
        // 回傳 null 避免前端整頁掛掉
        return null;
    }
}

// ==========================================
// 2. 全域變數
// ==========================================
const urlParams = new URLSearchParams(window.location.search);
let myId = urlParams.get('id') || 999; 

console.log(`🔥 initChatServer 準備啟動 | 我是: ${myId}`);

let currentReceiverId = null; 
let currentQnAId = null; 
let stompClient = null;
let isInitialized = false; 

// DOM
let chatContainer = document.getElementById("floating-chat");
let msgBox = document.getElementById("messages");
let listBox = document.getElementById("chat-list-container");

// 罐頭訊息
const adminCannedMessages = [
    "👋 嗨嗨～ReHome 狗狗小編來啦！有什麼可以為您效勞的嗎？🥰",
    "🙏 稍等我一下下喔，我正努力幫您查資料中～🐾",
    "📄 想跟您確認一下，領養申請表上的資訊都有填寫正確嗎？🌟",
    "📸 麻煩您再補傳一張居家環境的小照片給我們看看可以嗎？謝謝您～💕",
    "✅ 好的沒問題！我已經幫您記錄下來囉，接下來會有專人再和您聯繫～🐶✨"
];
const userCannedMessages = [
    "👋 您好，我想詢問關於領養的詳細資訊 🐾",
    "🐶 請問這隻毛孩目前還在找家嗎？",
    "📝 我已經填寫領養申請表囉，再麻煩確認一下～🙏",
    "🐕 請問這隻狗狗的個性適合跟其他貓狗相處嗎？",
    "🍼 請問這隻毛孩目前的年紀與健康狀況如何？",
    "💉 請問毛孩都已經完成結紮與疫苗施打紀錄了嗎？",
    "🏡 想了解領養後的後續追蹤流程有哪些？",
    "💖 謝謝小編詳細的回覆，我考慮後再聯繫您！"
];

// ==========================================
// 3. 主啟動函式
// ==========================================
function initChatServer() {
    if (isInitialized) return;
    isInitialized = true;
    
    console.log("🚀 initChatServer 執行中...");

    chatContainer = document.getElementById("floating-chat");
    msgBox = document.getElementById("messages");
    listBox = document.getElementById("chat-list-container");

    const listDiv = document.querySelector(".sidebar-left");
    const rightDiv = document.querySelector(".sidebar-right"); 
    const mainContainer = document.querySelector(".container");
    const navBar = document.getElementById("global-navbar");

    if (!chatContainer) {
        console.error("❌ 找不到 #floating-chat");
        return;
    }

    chatContainer.classList.remove("expanded");

    if (String(myId) === String(ADMIN_ID)) {
        console.log("👮‍♂️ 客服模式");
        if(mainContainer) mainContainer.style.gridTemplateColumns = "var(--left-width) 1fr var(--right-width)";
        if(navBar) navBar.style.gridTemplateColumns = "var(--left-width) 1fr var(--right-width)";
        if (listDiv) listDiv.style.display = 'flex';
        if (rightDiv) rightDiv.style.display = 'flex';
        loadChatList(0); 
    } else {
        console.log("🙋‍♂️ 會員模式");
        if (listDiv) listDiv.style.display = 'none';
        if (rightDiv) rightDiv.style.display = 'none';
        if(mainContainer) mainContainer.style.gridTemplateColumns = "1fr";
        if(navBar) navBar.style.gridTemplateColumns = "0 1fr 60px";
        currentReceiverId = ADMIN_ID;
        loadChatHistory(myId, ADMIN_ID);
    }

    bindEvents();
    connectWebSocket();
}

function bindEvents() {
    const minBtn = document.getElementById("minimize-button");
    if (minBtn) minBtn.onclick = toggleChatWindow;
    
    const ballIcon = document.getElementById("minimized-ball-icon");
    if (ballIcon) ballIcon.onclick = toggleChatWindow;

    const msgInput = document.getElementById("message-input");
    if(msgInput) {
        msgInput.addEventListener("keydown", (e) => {
            if(e.key === "Enter") { e.preventDefault(); sendMessage(); }
        });
        msgInput.addEventListener("focus", () => closeAllMenus());
    }

    const plusIcon = document.getElementById("plus-menu-icon");
    if(plusIcon) plusIcon.onclick = togglePlusMenu;

    const emojiIcon = document.getElementById("emoji-keyboard-icon");
    if(emojiIcon) emojiIcon.onclick = toggleStickerMenu;

    const stickerArea = document.getElementById("sticker-area");
    if(stickerArea) {
        stickerArea.querySelectorAll("img").forEach(img => {
            img.onclick = function() {
                sendMessage(this.getAttribute("src")); 
                stickerArea.style.display = "none";
                if(emojiIcon) {
                    emojiIcon.classList.replace("fa-keyboard", "fa-face-smile");
                    emojiIcon.classList.replace("fa-solid", "fa-regular");
                }
            };
        });
    }

    const uploadIcon = document.getElementById("image-upload-icon");
    const fileInput = document.getElementById("file-input");
    if (uploadIcon && fileInput) {
        uploadIcon.onclick = () => { fileInput.accept = "image/*"; fileInput.click(); };
        fileInput.onchange = (e) => {
            const file = e.target.files[0];
            if (!file || !currentReceiverId) return;
            const formData = new FormData();
            formData.append("senderId", myId);
            formData.append("receiverId", currentReceiverId);
            formData.append("content", "傳送了一張圖片");
            formData.append("image", file);
            
            const reader = new FileReader();
            reader.onload = (evt) => renderSingleMessage({ img: evt.target.result, senderId: myId, sentAt: new Date().toISOString() });
            reader.readAsDataURL(file);

            fetchApi("/api/cs/send", 'POST', formData).catch(console.error);
            fileInput.value = '';
        };
    }
    document.addEventListener("click", (e) => closeMenusIfClickedOutside(e));
}

// ==========================================
// 4. WebSocket (核心修正：確保縮小時紅點必亮)
// ==========================================
function connectWebSocket() {
    if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') return;
    const socket = new SockJS(`${BASE_URL}/ws`); 
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
        console.log("✅ WebSocket 連線成功");
        stompClient.subscribe('/queue/messages/' + myId, onMessageReceived);
    }, (err) => console.error(err));
}

function onMessageReceived(payload) {
    let msg;
    try {
        msg = (typeof payload.body === 'string') ? JSON.parse(payload.body) : payload.body;
    } catch (e) { console.error("解析失敗", e); return; }

    let senderId = String(msg.senderId || (msg.sender && msg.sender.id) || "unknown");

    if (senderId === String(myId)) return;

    // 收到新訊息，自動從結案清單移除
    let archived = getArchivedIds();
    if (archived.includes(senderId)) {
        archived = archived.filter(id => id !== senderId);
        localStorage.setItem('cs_archived_ids', JSON.stringify(archived));
    }

    const isWindowExpanded = chatContainer && chatContainer.classList.contains("expanded");
    const isChattingWithSender = (currentReceiverId && senderId === String(currentReceiverId));

    // ★ 關鍵：只要不是正在聊天或視窗沒開，就強制顯示全域紅點
    if (!isWindowExpanded || !isChattingWithSender) {
        const globalDot = document.getElementById("global-red-dot");
        if (globalDot) {
            globalDot.style.setProperty('display', 'block', 'important');
            globalDot.classList.add("show");
        }
    }

    if (isChattingWithSender) {
        renderSingleMessage(msg);
    } else {
        // 標記未讀，解決新列表項紅點不亮問題
        unreadIds.add(senderId);

        if (String(myId) === String(ADMIN_ID)) {
            const item = document.getElementById(`list-item-${senderId}`);
            if (item) {
                 item.classList.add("unread");
                 const preview = item.querySelector(".list-preview");
                 if (preview) preview.innerText = msg.content || "[圖片]";
                 if(listBox) listBox.prepend(item);
            } else {
                 loadChatList(0); // 重新載入列表以顯示新案子
            }
        }
    }
}

function isSticker(content) {
    if (!content) return false;
    return content.includes("/img/") || content.includes("assets/img") || content.match(/\.(png|jpg|jpeg|gif)$/i);
}

// ==========================================
// 5. Tab 切換 (整合 Q&A)
// ==========================================
function switchTab(tab) {
    document.querySelectorAll('.tab-menu button').forEach(b => b.classList.remove('active-tab'));
    const btn = document.getElementById(`tab-${tab}`);
    if(btn) btn.classList.add('active-tab');

    if (tab === 'email') {
        loadQnAList();
    } else if (tab === 'completed') {
        loadChatList(1); 
    } else {
        loadChatList(0); 
    }
}

// ==========================================
// 6. Q&A 列表與回覆 (別動區：QnA 邏輯完全保留)
// ==========================================
function loadQnAList() {
    if (!listBox) return;
    listBox.innerHTML = '<div style="padding:15px; color:gray;">載入表單中...</div>';

    fetchApi(API_PATH_QNA_LIST, 'GET')
        .then(res => {
            let dataList = [];
            if (res && res.data && Array.isArray(res.data)) {
                dataList = res.data;
            } else if (Array.isArray(res)) {
                dataList = res;
            } else {
                listBox.innerHTML = '<div style="padding:15px;">目前沒有待回覆表單</div>';
                return;
            }

            if (dataList.length === 0) {
                listBox.innerHTML = '<div style="padding:15px;">目前沒有待回覆表單</div>';
                return;
            }

            console.log("✅ Q&A 列表讀取成功:", dataList); 
            
            let html = "";
            dataList.forEach(item => {
                const qId = item.id; 
                const name = item.cname || "匿名用戶";
                const title = item.questionTitle || "無標題";
                const content = item.questionInfo || "無內容";
                const type = item.questionTypeName || "一般提問";
                const safeContent = String(content).replace(/'/g, "\\'").replace(/\n/g, " ");

                html += `
                <div class="list-item qna-item" onclick="window.openQnADetail(${qId}, '${name}', '${safeContent}', '${title}')">
                    <div class="avatar" style="background:#fff3e0; display:flex; align-items:center; justify-content:center;">
                        <i class="fa-solid fa-file-circle-question" style="color:#f57c00; font-size:20px;"></i>
                    </div>
                    <div class="list-content">
                        <div class="list-header">
                            <span class="list-title">${name}</span>
                            <small style="color:#999;">${type}</small>
                        </div>
                        <div class="list-preview" style="color:#555; font-weight:bold;">${title}</div>
                        <div class="list-preview" style="color:#888; font-size:12px;">${content.substring(0, 20)}...</div>
                    </div>
                </div>`;
            });
            listBox.innerHTML = html;
        });
}

function openQnADetail(qId, name, content, title) {
    currentQnAId = qId; 
    currentReceiverId = null; 
    const titleEl = document.querySelector(".global-title");
    if (titleEl) titleEl.innerText = `工單處理 - ${name}`;

    if (msgBox) {
        msgBox.innerHTML = `
            <div style="padding: 20px; max-width: 750px; margin: 0 auto; font-family: 'PingFang TC', sans-serif;">
                <div style="background: #fff; border-radius: 10px; padding: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); margin-bottom: 20px; border-left: 5px solid #f57c00;">
                    <h3 style="margin-top:0; color:#333; font-size:18px; margin-bottom:12px;">
                        <i class="fa-solid fa-circle-question" style="color:#f57c00;"></i> ${title}
                    </h3>
                    <div style="color:#555; line-height:1.6; font-size:16px; min-height:80px; white-space: pre-wrap;">${content}</div>
                </div>
                <div style="background: #ffffff; border-radius: 10px; padding: 20px; border: 1px solid #ddd; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">
                    <h3 style="margin-top:0; color:#4a4a4a; font-size:15px; margin-bottom:12px;">
                        <i class="fa-solid fa-reply-all"></i> 客服回覆內容
                    </h3>
                    <textarea id="qna-reply-input" placeholder="請在此輸入回覆，系統將同步寄送 Email..." 
                        style="width:100%; height:200px; padding:12px; border-radius:6px; border:1px solid #ccc; resize:vertical; font-size:15px; line-height:1.5; outline:none; box-sizing: border-box;"></textarea>
                    <button id="qna-submit-btn" onclick="window.submitQnAReply()" 
                        style="margin-top:15px; width:100%; background:#f57c00; color:white; border:none; padding:12px; border-radius:6px; cursor:pointer; font-size:16px; font-weight:bold; transition: 0.3s;">
                        確認送出並寄信通知
                    </button>
                </div>
            </div>
        `;
        const textarea = document.getElementById("qna-reply-input");
        if (textarea) {
            textarea.onfocus = () => textarea.style.borderColor = "#f57c00";
            textarea.onblur = () => textarea.style.borderColor = "#ccc";
        }
    }
    const chatFooter = document.querySelector(".chat-footer");
    if (chatFooter) chatFooter.style.display = "none";
}

function submitQnAReply() {
    const input = document.getElementById("qna-reply-input");
    const content = input ? input.value.trim() : "";
    if (!content || !currentQnAId) return;
    const btn = document.getElementById("qna-submit-btn");
    if (btn) { btn.disabled = true; btn.innerText = "正在寄信並更新狀態..."; }
    fetchApi(`${API_PATH_QNA_REPLY}/${currentQnAId}`, 'POST', { replyContent: content })
        .then(res => {
            alert("✅ 回覆成功！已寄出通知信。");
            loadQnAList();
            if (msgBox) msgBox.innerHTML = '<div style="text-align:center; padding:50px; color:gray;">回覆完成</div>';
        }).catch(err => { if (btn) { btn.disabled = false; btn.innerText = "確認送出並寄信通知"; } });
}

// ==========================================
// 7. 聊天列表渲染 (頭像、未讀標記)
// ==========================================
function loadChatList(status = 0) {
    if (!listBox) return Promise.resolve();
    const chatFooter = document.querySelector(".chat-footer");
    if (chatFooter) chatFooter.style.display = "flex";

    return fetchApi(`/api/cs/list?myId=${myId}`, 'GET')
        .then(data => {
            if (data && data.data) data = data.data;
            if (!data || !Array.isArray(data)) return;

            const archivedIds = getArchivedIds();
            let filteredData = data.filter(item => {
                let isArchived = archivedIds.includes(String(item.otherUserId));
                return status === 1 ? isArchived : !isArchived;
            });

            if (filteredData.length === 0) {
                listBox.innerHTML = `<div style="padding:15px; text-align:center; color:gray;">目前無紀錄</div>`;
                return;
            }

            let html = "";
            filteredData.forEach(item => {
                const sid = String(item.otherUserId);
                const displayName = item.otherUserName || `用戶 ${sid}`;
                
                // ★ 檢查是否在未讀 Set 中
                const unreadClass = unreadIds.has(sid) ? "unread" : "";
                
                // ★ 載入資料庫頭貼 (icon)
                let avatarUrl = "../assets/img/Chatphoto/img/頭.png";
                if (item.otherUserAvatar) {
                    avatarUrl = `data:image/jpeg;base64,${item.otherUserAvatar}`;
                }

                const itemData = encodeURIComponent(JSON.stringify(item));

                html += `
                <div class="list-item ${unreadClass}" id="list-item-${sid}" 
                     data-info="${itemData}"
                     onclick="openChat('${displayName}', ${sid}, ${status})">
                    <div class="avatar"><img src="${avatarUrl}" onerror="this.src='https://placehold.co/40'"/></div>
                    <div class="list-content">
                        <div class="list-header"><span class="list-title">${displayName}</span></div>
                        <div class="list-preview">${item.content || "..." }</div>
                    </div>
                </div>`;
            });
            listBox.innerHTML = html;
        });
}

function openChat(title, id, status = 0) {
    const sid = String(id);
    currentReceiverId = sid;
    currentQnAId = null; 

    // ★ 移除未讀狀態並同步隱藏全域紅點
    unreadIds.delete(sid);
    if (unreadIds.size === 0) {
        const globalDot = document.getElementById("global-red-dot");
        if (globalDot) { globalDot.classList.remove("show"); globalDot.style.display = 'none'; }
    }

    const chatFooter = document.querySelector(".chat-footer");
    if (chatFooter) chatFooter.style.display = "flex";

    document.querySelectorAll('.list-item').forEach(item => {
        item.style.backgroundColor = "";
        item.classList.remove("active-chat");
    });
    const listItem = document.getElementById(`list-item-${sid}`);
    if (listItem) {
        listItem.classList.remove("unread");
        listItem.classList.add("active-chat");
        listItem.style.backgroundColor = "#eef"; 
    }
    const titleEl = document.querySelector(".global-title");
    if (titleEl) titleEl.innerText = (status === 1) ? `[已結案] ${title}` : title;
    
    if (String(myId) === String(ADMIN_ID)) updateRightSidebar(id, title);
    if (msgBox) msgBox.innerHTML = '';
    loadChatHistory(myId, id);
}

// ==========================================
// 8. 訊息顯示 (補上泡泡旁時間)
// ==========================================
function renderSingleMessage(msg) {
    if (!msgBox) return;
    let msgSenderId = msg.senderId || (msg.sender && msg.sender.id) || "unknown";
    const isMe = (String(msgSenderId) === String(myId));
    
    // ★ 處理泡泡旁時間顯示
    const timeStr = formatTime(msg.sentAt || new Date().toISOString());
    
    let contentHtml = "";
    if (msg.img) {
         const imgSrc = msg.img.startsWith('data:') ? msg.img : `data:image/jpeg;base64,${msg.img}`;
         contentHtml = `<img src="${imgSrc}" style="max-width:200px; border-radius:10px;">`;
    } else if (msg.content && isSticker(msg.content)) {
         contentHtml = `<img src="${msg.content}" style="max-width:120px;">`;
    } else {
         contentHtml = `<div>${msg.content || ''}</div>`;
    }

    const html = `
        <div class="message-row ${isMe ? 'sent' : 'received'}">
            <div class="message-wrapper" style="display:flex; align-items:flex-end; gap:5px; ${isMe ? 'flex-direction:row-reverse;' : ''}">
                <div class="message-bubble ${isMe ? 'sent-bubble' : 'received-bubble'}">${contentHtml}</div>
                <span class="message-time" style="font-size:11px; color:#999; margin-bottom:5px; white-space:nowrap;">${timeStr}</span>
            </div>
        </div>`;
    msgBox.insertAdjacentHTML('beforeend', html);
    msgBox.scrollTop = msgBox.scrollHeight;
}

// ==========================================
// 9. 右側資訊欄 (動態填入資料庫欄位)
// ==========================================
function updateRightSidebar(id, name) {
    const listItem = document.getElementById(`list-item-${id}`);
    if (!listItem) return;
    try {
        const info = JSON.parse(decodeURIComponent(listItem.getAttribute("data-info")));
        const nameEl = document.getElementById("info-name");
        const emailEl = document.getElementById("info-email");
        const avatarEl = document.getElementById("info-avatar");
        const phoneEl = document.getElementById("info-phone"); 
        const nickEl = document.getElementById("info-nickname");

        if (nameEl) nameEl.innerText = info.otherUserName || name;
        if (emailEl) emailEl.innerText = info.otherUserEmail || `user${id}@rehome.com`;
        if (phoneEl) phoneEl.innerText = info.otherUserPhone || "未提供手機";
        if (nickEl) nickEl.innerText = info.otherUserNickname || "未填寫暱稱";

        if (avatarEl) {
            let url = "https://placehold.co/40";
            if (info.otherUserAvatar) url = `data:image/jpeg;base64,${info.otherUserAvatar}`;
            avatarEl.src = url;
        }
    } catch (e) { console.error(e); }
}

// ==========================================
// 10. 選單、視窗與輔助邏輯
// ==========================================
function archiveChat() {
    if (!currentReceiverId) return;
    if (!confirm("結束對話？案子將移至「已完成」列表。")) return;
    let archived = getArchivedIds();
    let sid = String(currentReceiverId);
    if (!archived.includes(sid)) { archived.push(sid); localStorage.setItem('cs_archived_ids', JSON.stringify(archived)); }
    if (msgBox) msgBox.innerHTML = '<div style="text-align:center; padding:50px; color:gray;">案件已結案</div>';
    switchTab('chat');
    closeAllMenus();
}

function toggleChatWindow(event) {
    if (event) event.stopPropagation();
    if (!chatContainer) return;
    chatContainer.classList.toggle("expanded");
    
    // ★ 修復：視窗展開時隱藏全域紅點
    const dot = document.getElementById("global-red-dot");
    if (chatContainer.classList.contains("expanded") && dot) {
        dot.classList.remove("show"); dot.style.display = 'none';
    }
}

function loadChatHistory(senderId, receiverId) {
    fetchApi(`/api/cs/history?myId=${senderId}&otherId=${receiverId}`).then(data => {
        if (data && data.data) data = data.data;
        msgBox.innerHTML = '';
        if (data && data.length > 0) data.forEach(msg => renderSingleMessage(msg));
        msgBox.scrollTop = msgBox.scrollHeight;
    });
}

function sendMessage(text = null) {
    const input = document.getElementById("message-input");
    const content = text ? text : (input ? input.value.trim() : "");
    if (!content || !currentReceiverId) return;
    renderSingleMessage({ senderId: myId, content: content, sentAt: new Date().toISOString() });
    if(input) input.value = "";
    closeAllMenus();
    const formData = new FormData();
    formData.append("senderId", myId);
    formData.append("receiverId", currentReceiverId);
    formData.append("content", content);
    fetchApi("/api/cs/send", 'POST', formData).catch(console.error);
}

function togglePlusMenu(e) { 
    if(e) e.stopPropagation(); 
    if (String(myId) !== String(ADMIN_ID)) { toggleQuickReplies(e); return; }
    toggleMenuById("expanded-menu"); 
}

function toggleQuickReplies(e) {
    if(e) e.stopPropagation();
    const menu = document.getElementById("quick-reply-menu");
    if(!menu) return;
    if (menu.style.display === "block") {
        menu.style.display = "none";
    } else {
        closeAllMenus();
        if (String(myId) === String(ADMIN_ID)) { menu.style.right = "15px"; menu.style.left = "auto"; } 
        else { menu.style.left = "20px"; menu.style.right = "auto"; }
        const msgs = (String(myId) === String(ADMIN_ID)) ? adminCannedMessages : userCannedMessages;
        menu.querySelector(".quick-reply-list").innerHTML = msgs.map(m => `<div style="padding:10px; border-bottom:1px solid #eee; cursor:pointer;" onclick="window.sendQuickReply('${m.replace(/'/g, "\\'")}')">${m}</div>`).join("");
        menu.style.display = "block";
    }
}

function toggleMenuById(id, type="flex") {
    const el = document.getElementById(id);
    if(el && el.style.display === type) el.style.display = "none";
    else if(el) { closeAllMenus(); el.style.display = type; }
}
function closeAllMenus() {
    ["expanded-menu", "quick-reply-menu", "sticker-area"].forEach(id => {
        const el = document.getElementById(id);
        if(el) el.style.display = "none";
    });
}
function closeMenusIfClickedOutside(e) {
    if(e.target.closest(".menu-item") || e.target.closest("#plus-menu-icon") || e.target.closest("#emoji-keyboard-icon")) return;
    closeAllMenus();
}
function toggleStickerMenu(e) { if(e) e.stopPropagation(); toggleMenuById("sticker-area"); }
function sendQuickReply(t) { sendMessage(t); closeAllMenus(); }
function openPrivateNote() { alert("開發中"); closeAllMenus(); }

// 綁定全域變數
Object.assign(window, { initChatServer, toggleChatWindow, openChat, switchTab, togglePlusMenu, toggleStickerMenu, sendMessage, onMessageReceived, archiveChat, sendQuickReply, toggleQuickReplies, openPrivateNote, openQnADetail, submitQnAReply });

initChatServer();