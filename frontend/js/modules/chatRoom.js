// import {
//     fetchApi
// } from '/js/utils/helper.js';

// ==========================================
// 全域變數定義
// ==========================================
// ==========================================
// 1. 全域設定 & 工具函式 (修正連線位址)
// ==========================================
const BASE_URL = "http://localhost:8081"; // ★★★ 補上這行：後端伺服器位址

async function fetchApi(url, method = 'GET', body = null, isAuth = false) {
    const headers = {};
    if (body && !(body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
        body = JSON.stringify(body);
    }
    const opts = { method, headers };
    if (body) opts.body = body;

    // ★★★ 關鍵修正：自動補上 BASE_URL ★★★
    // 如果 url 不是以 http 開頭，就自動加上 http://localhost:8081
    let finalUrl = url;
    if (!url.startsWith("http")) {
        // 確保開頭有斜線 (防呆)
        if (!url.startsWith("/")) {
            finalUrl = BASE_URL + "/" + url;
        } else {
            finalUrl = BASE_URL + url;
        }
    }

    try {
        console.log(`📡 發送請求: ${finalUrl}`); // 方便除錯
        const res = await fetch(finalUrl, opts);
        if (!res.ok) throw new Error(`API Error: ${res.status} ${res.statusText}`);
        return await res.json();
    } catch (err) {
        console.error("FetchApi Error:", err);
        throw err;
    }
}
const urlParams = new URLSearchParams(window.location.search);

// 🛑 修正：改用 let，因為下面登入檢查後會修改它
// const myId = urlParams.get('id') || 2; 
let myId = urlParams.get('id') || 2; 

console.log("目前登入身分:", myId);
let currentReceiverId = null; // 當前聊天對象 ID
let stompClient = null;

// DOM 元素快取
const chatContainer = document.getElementById("chat-container");
const listDiv = document.getElementById("conversation-list-view");
const singleDiv = document.getElementById("single-chat-view");
const listChevron = document.getElementById("list-chevron");
const msgBox = document.getElementById("messages"); // 訊息顯示區

// ==========================================
// 1. 初始化與列表載入 (window.onload)
// ==========================================
// function init() {
//     minimizeChat(); // 預設最小化
//     
//     const listBox = document.querySelector(".conversation-list");
//     listBox.innerHTML = '<div style="padding:15px; color:gray;">載入中...</div>';

//     // 呼叫後端 API 取得聊天列表
// fetch(`/api/chatroom/list?myId=${myId}`)
//             .then(response => {
//             if (!response.ok) { throw new Error("API 錯誤"); }
//             return response.json();
//         })
//         .then(data => {
//             console.log("列表資料:", data);

//             if (data.length === 0) {
//                 listBox.innerHTML = '<div style="padding:15px;">目前沒有聊天紀錄</div>';
//                 return;
//             }

//             let html = "";
//             for (let item of data) {
//                 const displayName = item.otherUserName ? item.otherUserName : `用戶 ${item.otherUserId}`;
                
//                 // 設定頭像網址 (指向後端 API)
//                 const avatarUrl = `/api/member/icon/${item.otherUserId}`;
//                 const timeDisplay = item.time ? item.time : "";
                
//                 // 預覽內容處理
//                 let previewContent = item.content || item.lastMessage || "...";
//                 if (previewContent === "..." && item.hasImage) { 
//                       previewContent = "[圖片]";
//                 }

//                 html += `
//                 <div class="list-item" onclick="openChat('${displayName}', ${item.otherUserId})">
//                     <div class="avatar">
//                         <img src="${avatarUrl}" onerror="this.onerror=null; this.src='/img/default.jpg';" />
//                     </div>
//                     <div class="list-content">
//                         <div class="list-header">
//                             <span class="list-title">${displayName}</span>
//                             <span class="list-time">${timeDisplay}</span>
//                         </div>
//                         <div class="list-preview">${previewContent}</div>
//                     </div>
//                 </div>`;
//             }
//             listBox.innerHTML = html;
//         })
//         .catch(error => {
//             console.error("無法載入列表:", error);
//             listBox.innerHTML = '<div style="padding:15px; color:red;">載入失敗</div>';
//         });
// connectWebSocket(); 
// };

// ==========================================
// 1. 初始化與列表載入 (正式版)
// ==========================================
function init() {
    minimizeChat(); // 預設最小化
    
    const listBox = document.querySelector(".conversation-list");
    listBox.innerHTML = '<div style="padding:15px; color:gray;">載入中...</div>';

    // ★★★ 修改處：補上 null 和 false，與你同事的寫法保持一致 ★★★
    fetchApi(`/api/chatroom/list?myId=${myId}`, 'GET', null, false)
        .then(data => {
            console.log("列表資料:", data);

            if (data.length === 0) {
                listBox.innerHTML = '<div style="padding:15px;">目前沒有聊天紀錄</div>';
                return;
            }

            let html = "";
            for (let item of data) {
                const displayName = item.otherUserName ? item.otherUserName : `用戶 ${item.otherUserId}`;
                
                // 設定頭像網址 (指向後端 API)
                const avatarUrl = `/api/member/icon/${item.otherUserId}`;
                const timeDisplay = item.time ? item.time : "";
                
                // 預覽內容處理
                let previewContent = item.content || item.lastMessage || "...";
                if (previewContent === "..." && item.hasImage) { 
                      previewContent = "[圖片]";
                }

                html += `
                <div class="list-item" onclick="openChat('${displayName}', ${item.otherUserId})">
                    <div class="avatar">
                        <img src="${avatarUrl}" onerror="this.onerror=null; this.src='/img/default.jpg';" />
                    </div>
                    <div class="list-content">
                        <div class="list-header">
                            <span class="list-title">${displayName}</span>
                            <span class="list-time">${timeDisplay}</span>
                        </div>
                        <div class="list-preview">${previewContent}</div>
                    </div>
                </div>`;
            }
            listBox.innerHTML = html;
        })
        .catch(error => {
            console.error("無法載入列表:", error);
            listBox.innerHTML = '<div style="padding:15px; color:red;">載入失敗</div>';
        });
        
    connectWebSocket(); 
};

// ==========================================
// 2. 視窗控制功能
// ==========================================
function minimizeChat(event) {
    if (event) event.stopPropagation();
    if (!chatContainer.classList.contains("minimized")) {
        chatContainer.classList.add("minimized");
        listChevron.classList.remove("fa-chevron-down");
        listChevron.classList.add("fa-chevron-up");
        listDiv.style.display = "flex";
        singleDiv.style.display = "none";
    }
}

function toggleChatWindow(event) {
    if (event) event.stopPropagation();
    if (chatContainer.classList.contains("minimized")) {
        chatContainer.classList.remove("minimized");
        listChevron.classList.remove("fa-chevron-up");
        listChevron.classList.add("fa-chevron-down");
        listDiv.style.display = "flex";
    } else {
        minimizeChat();
    }
}

function goBack(event) {
    if (event) event.stopPropagation();
    listDiv.style.display = "flex";
    singleDiv.style.display = "none";
    currentReceiverId = null; // 清空當前對象
}

// ==========================================
// 3. 進入聊天室與載入歷史訊息
// ==========================================
function openChat(title, id) {
    currentReceiverId = id;
    console.log("進入聊天室，對方 ID:", currentReceiverId);

    // UI 切換
    listDiv.style.display = "none";
    singleDiv.style.display = "flex";
    document.getElementById("current-chat-title").innerText = title;

    // 清空舊訊息
    msgBox.innerHTML = '';

    // 載入歷史訊息
    loadChatHistory(myId, currentReceiverId);
}

function loadChatHistory(senderId, receiverId) {
    msgBox.innerHTML = '<div style="text-align:center; color:gray; padding:20px;">載入歷史訊息...</div>';

    // 預先定義好兩人的頭像網址
    const myAvatarUrl = `/api/member/icon/${senderId}`;
    const otherAvatarUrl = `/api/member/icon/${receiverId}`;

    // fetch(`/api/chatroom/history?myId=${senderId}&otherId=${receiverId}`)
    //     .then(res => res.json())
    //     .then(data => {
    fetchApi(`/api/chatroom/history?myId=${senderId}&otherId=${receiverId}`, 'GET', null, false)
        .then(data => {
            msgBox.innerHTML = '';
            if (data.length === 0) {
                msgBox.innerHTML = '<div style="text-align:center; color:gray; margin-top:20px;">開始聊天吧！</div>';
                return;
            }

            data.forEach(msg => {
                const isMe = (msg.sender.id == senderId);
                const currentAvatar = isMe ? myAvatarUrl : otherAvatarUrl;

                let contentHtml = "";

                // A. 處理圖片
                if (msg.hasImage) {
                    const msgImgUrl = `/api/chatroom/message/${msg.id}/img`;
                    contentHtml += `<img src="${msgImgUrl}" style="max-width: 200px; border-radius: 10px; display:block; margin-bottom: 5px;">`;
                }

                // B. 處理文字
                if (msg.content && msg.content !== "傳送了一張圖片") {
                    contentHtml += `<div style="word-break: break-all;">${msg.content}</div>`;
                }

                // C. 貼圖判斷 (如果 content 是貼圖路徑)
                if (msg.content && (
                    msg.content.startsWith("/img/") || 
                    msg.content.startsWith("http") || 
                    msg.content.startsWith("../") || 
                    msg.content.match(/\.(jpeg|jpg|gif|png)$/) != null
                )) {
                    if (!msg.hasImage) {
                        contentHtml = `<img src="${msg.content}" style="max-width: 150px;">`;
                    }
                }

                // 時間處理
                let timeStr = "";
                if (msg.sentAt) {
                    const date = new Date(msg.sentAt);
                    const hours = String(date.getHours()).padStart(2, '0');
                    const minutes = String(date.getMinutes()).padStart(2, '0');
                    timeStr = `${hours}:${minutes}`;
                }

                const html = createMessageHtml(contentHtml, isMe, timeStr, currentAvatar);
                msgBox.innerHTML += html;
            });
            msgBox.scrollTop = msgBox.scrollHeight;
            // })
            // .catch(err => {
            //     console.error(err);
            //     msgBox.innerHTML = '<div style="text-align:center; color:red;">無法載入訊息</div>';
            // });
        })
        .catch(err => {
            console.error(err);
            msgBox.innerHTML = '<div style="text-align:center; color:red;">無法載入訊息</div>';
        });
}

// ==========================================
// WebSocket 連線與接收
// ==========================================
function connectWebSocket() {
    // 1. 建立 SockJS 連線
    const socket = new SockJS('http://localhost:8081/ws'); 
    stompClient = Stomp.over(socket);

    // 2. 啟動連線
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    console.log("WebSocket 連線成功！");
    stompClient.subscribe('/user/queue/messages/' + myId, onMessageReceived);
// console.log("📡 正在訂閱頻道: /topic/messages." + myId);
//     stompClient.subscribe('/topic/messages.' + myId, onMessageReceived);
}

function onError(error) {
    console.log("WebSocket 連線失敗: " + error);
}

function onMessageReceived(payload) {
    console.log(123);
    
    console.log("收到新訊息: ", payload.body);
    const message = JSON.parse(payload.body); 

    // 🛑 修正 1：資料結構變了，要從 sender 物件裡面拿 id
    const msgSenderId = message.sender ? message.sender.id : message.senderId; 

    // 如果收到的訊息發送者是「我自己」，就不要再顯示一次
    if (msgSenderId == myId) {
        return; 
    }

    // 判斷這則訊息是不是給當前聊天視窗的
    // 🛑 修正 2：使用修正後的 msgSenderId 來比對
    if (currentReceiverId && msgSenderId == currentReceiverId) {
        let contentHtml = "";
        
        // ==========================================
        // 🛑配合 DTO，改用 message.img 判斷
        // ==========================================
        if (message.img) { 
            // DTO 裡的 img 已經包含 "data:image/jpeg;base64..."，直接塞進 src
            contentHtml = `<img src="${message.img}" style="max-width: 200px; border-radius: 10px;">`;
        }
        // B. 貼圖判斷
        else if (message.content && (
            message.content.startsWith("/img/") || 
            message.content.startsWith("http") || 
            message.content.startsWith("../") || 
            message.content.match(/\.(jpeg|jpg|gif|png)$/) != null
        )) {
                contentHtml = `<img src="${message.content}" style="max-width: 150px;">`;
        } 
        // C. 普通文字
        else {
                contentHtml = `<div>${message.content}</div>`;
        }

        const timeString = gettime(); 
        // 🛑 修正 3：頭像也要改用 msgSenderId
        const otherAvatarUrl = `/api/member/icon/${msgSenderId}`;

        // 產生 HTML
        const html = createMessageHtml(contentHtml, false, timeString, otherAvatarUrl);
        
        msgBox.innerHTML += html;
        msgBox.scrollTop = msgBox.scrollHeight; 
    } else {
        console.log(`收到非當前視窗的訊息 (當前: ${currentReceiverId}, 訊息來自: ${msgSenderId})`);
    }
}

// ==========================================
// 4. 產生訊息 HTML (核心 UI)
// ==========================================
function createMessageHtml(contentHtml, isSent, timeString, avatarUrl) {
    const messageClass = isSent ? "message-sent" : "message-received";
    const timeDisplay = timeString ? timeString : '..:..';
    const timestampHtml = `<div class="message-timestamp">${timeDisplay}</div>`;

    const containerClass = isSent ? "message-row sent" : "message-row received";
    const bubbleClass = isSent ? "sent-bubble" : "received-bubble";
    
    const bubbleContent = `<div class="message-bubble ${bubbleClass}">${contentHtml}</div>`;

    // 防呆處理
    const defaultAvatar = "/img/default.jpg"; 
    const errorHandling = `this.onerror=null; this.src='${defaultAvatar}'`;

    let html = "";
    if (isSent) {
        html = `
        <div class="${containerClass}">
            ${timestampHtml}
            <div class="message-content">${bubbleContent}</div>
            <div class="avatar-area">
                <img src="${avatarUrl}" class="avatar-img" onerror="${errorHandling}" />
            </div>
        </div>`;
    } else {
        html = `
        <div class="${containerClass}">
            <div class="avatar-area">
                <img src="${avatarUrl}" class="avatar-img" onerror="${errorHandling}" />
            </div>
            <div class="message-content">${bubbleContent}</div>
            ${timestampHtml}
        </div>`;
    }
    return html;
}

// ==========================================
// 5. 發送訊息 (文字)
// ==========================================
function sendMessage() {
    const input = document.getElementById("message-input");
    const text = input.value.trim();

    if (!text) return;
    if (!currentReceiverId) {
        alert("請先選擇聊天對象！");
        return;
    }

    const formData = new FormData();
    formData.append("senderId", myId);
    formData.append("receiverId", currentReceiverId);
    formData.append("content", text);

    // 位置：function sendMessage 裡面
    // 格式：fetchApi(網址, 方法, 資料, 是否跳轉)
    fetchApi("/api/chatroom/mesg", 'POST', formData, false)
        .then(result => {
            // 發送成功的邏輯保持不變
            const timeString = gettime();
            const myAvatarUrl = `/api/member/icon/${myId}`;
            const contentHtml = `<div>${text}</div>`;
            msgBox.innerHTML += createMessageHtml(contentHtml, true, timeString, myAvatarUrl);
        
            input.value = "";
            msgBox.scrollTop = msgBox.scrollHeight;
        })
        .catch(error => {
            console.error("發送失敗:", error);
            appendSystemMessage("❌ 發送失敗", true);
        });

    //     fetch("/api/chatroom/mesg", {
    //         method: "POST",
    //         body: formData
    //     })
    //     .then(response => {
    //         if (response.ok) return response.json();
    //         throw new Error(response.status);
    //     })

    //     .then(result => {
    //         const timeString = gettime();
    //         const myAvatarUrl = `/api/member/icon/${myId}`;
    //         
    //         const contentHtml = `<div>${text}</div>`;
    //         msgBox.innerHTML += createMessageHtml(contentHtml, true, timeString, myAvatarUrl);
    //         
    //         input.value = "";
    //         msgBox.scrollTop = msgBox.scrollHeight;
    //     })
    //     .catch(error => {
    //         console.error("發送失敗:", error);
    //         appendSystemMessage("❌ 發送失敗", true);
    //     });
}
// 綁定到 window
window.sendMessage = sendMessage;

// ==========================================
// 6. DOM 載入後事件綁定 (核心修復區)
// ==========================================
function initLister() {
    console.log("DOM 載入完成，開始綁定事件...");

    const messageInput = document.getElementById("message-input");
    const imageUploadIcon = document.getElementById("image-upload-icon");
    const fileInput = document.getElementById("file-input");
    
    // 貼圖與選單 DOM
    const plusMenuIcon = document.getElementById("plus-menu-icon");
    const expandedMenu = document.getElementById("expanded-menu");
    const emojiKeyboardIcon = document.getElementById("emoji-keyboard-icon");
    const stickerArea = document.getElementById("sticker-area");

    // 1. Enter 發送
    if (messageInput) {
        messageInput.addEventListener("keydown", function (e) {
            if (e.key === "Enter") {
                e.preventDefault();
                sendMessage();
            }
        });
    }

    // 2. 圖片上傳 (相機按鈕)
    if (imageUploadIcon && fileInput) {
        imageUploadIcon.addEventListener("click", () => {
            fileInput.accept = "image/*";
            fileInput.click();
        });

        fileInput.addEventListener("change", async function (event) {
            const files = event.target.files;
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

            try {
                // 🛑 修正：將 await fetch 改為 await fetchApi
                // 注意：fetchApi 通常會 reject 如果失敗，成功則回傳 data
                const result = await fetchApi('/api/chatroom/mesg', 'POST', formData, false);

                // 因為 fetchApi 幫你處理了 error check，跑到這裡代表成功
                // 預覽圖片
                const reader = new FileReader();
                reader.onload = function(e) {
                    const imgHtml = `<img src="${e.target.result}" style="max-width: 200px; border-radius: 10px;">`;
                    const timeString = gettime();
                    const myAvatarUrl = `/api/member/icon/${myId}`;
                    
                    msgBox.innerHTML += createMessageHtml(imgHtml, true, timeString, myAvatarUrl);
                    msgBox.scrollTop = msgBox.scrollHeight;
                };
                reader.readAsDataURL(file);

            } catch (error) {
                console.error("上傳錯誤:", error);
                alert("圖片上傳失敗"); // 或是顯示 error
            }
            fileInput.value = ''; 
        });
    }

    // 3. ★★★ 貼圖選單開關 (已修正為 grid) ★★★
    if (emojiKeyboardIcon && stickerArea && messageInput) {
        emojiKeyboardIcon.addEventListener("click", function (e) {
            e.stopPropagation(); 
            
            // 判斷是否隱藏 (none 或 空值)
            const isHidden = stickerArea.style.display === "none" || stickerArea.style.display === "";

            if (isHidden) {
                // ★ 修正重點：打開貼圖時設定為 grid，而不是 flex
                stickerArea.style.display = "grid"; 
                
                emojiKeyboardIcon.classList.replace("fa-face-smile", "fa-keyboard");
                emojiKeyboardIcon.classList.replace("fa-regular", "fa-solid");
                if (window.toggleMenu) window.toggleMenu(false); // 關閉左側選單
            } else {
                // 關閉時設定為 none
                stickerArea.style.display = "none";
                
                emojiKeyboardIcon.classList.replace("fa-keyboard", "fa-face-smile");
                emojiKeyboardIcon.classList.replace("fa-solid", "fa-regular");
                messageInput.focus();
            }
        });

        // 點擊輸入框時自動關閉貼圖
        messageInput.addEventListener("focus", () => {
            // ★ 修正：檢查是否為 grid
            if (stickerArea.style.display === "grid") {
                emojiKeyboardIcon.click();
            }
        });
    }

    // 4. 貼圖點擊發送 (修正版：真的會傳送給後端)
    if (stickerArea) {
        const stickerItems = stickerArea.querySelectorAll(".sticker-item");
        stickerItems.forEach((item) => {
            item.addEventListener("click", () => {
                if (!currentReceiverId) {
                    alert("請先選擇聊天對象！");
                    return;
                }

                const stickerPath = item.getAttribute("src");
                
                // --- 開始發送給後端 ---
                const formData = new FormData();
                formData.append("senderId", myId);
                formData.append("receiverId", currentReceiverId);
                formData.append("content", stickerPath); // 將貼圖路徑當作文字訊息傳送

                fetchApi("/api/chatroom/mesg", 'POST', formData, false)
                    .then(result => {
                        // 成功的邏輯...
                        const timeString = gettime();
                        const myAvatarUrl = `/api/member/icon/${myId}`;
                        const stickerHtmlContent = `<img src="${stickerPath}" style="max-width: 100px;">`;
                        
                        msgBox.innerHTML += createMessageHtml(stickerHtmlContent, true, timeString, myAvatarUrl);
                        msgBox.scrollTop = msgBox.scrollHeight;
                    })
                    .catch(error => {
                        console.error("貼圖發送失敗:", error);
                        appendSystemMessage("❌ 貼圖發送失敗", true);
                    });
                
                // 關閉貼圖區
                if (emojiKeyboardIcon) emojiKeyboardIcon.click();
            });
            
            //     fetch("/api/chatroom/mesg", {
            //         method: "POST",
            //         body: formData
            //     })
            //     .then(response => {
            //         if (response.ok) return response.json();
            //         throw new Error(response.status);
            //     })
            //     .then(result => {
            //         // 發送成功後，才在自己畫面顯示
            //         const timeString = gettime();
            //         const myAvatarUrl = `/api/member/icon/${myId}`;
            //         const stickerHtmlContent = `<img src="${stickerPath}" style="max-width: 100px;">`;
            //         
            //         msgBox.innerHTML += createMessageHtml(stickerHtmlContent, true, timeString, myAvatarUrl);
            //         msgBox.scrollTop = msgBox.scrollHeight;
            //     })
            //     .catch(error => {
            //         console.error("貼圖發送失敗:", error);
            //         appendSystemMessage("❌ 貼圖發送失敗", true);
            //     });
            
            //     // 關閉貼圖區
            //     if (emojiKeyboardIcon) emojiKeyboardIcon.click();
            // });
        });
    }

    // 5. 選單與全域點擊關閉
    document.addEventListener("click", (event) => {
        const isMenuOpen = expandedMenu && expandedMenu.style.display === "flex";
        
        // ★ 修正：檢查是否為 grid
        const isStickerOpen = stickerArea && stickerArea.style.display === "grid"; 

        const clickInMenu = expandedMenu && expandedMenu.contains(event.target);
        const clickInPlus = plusMenuIcon && plusMenuIcon.contains(event.target);
        const clickInSticker = stickerArea && stickerArea.contains(event.target);
        const clickInEmoji = emojiKeyboardIcon && emojiKeyboardIcon.contains(event.target);
        const clickInInput = messageInput && messageInput.contains(event.target);

        // 關閉選單
        if (isMenuOpen && !clickInMenu && !clickInPlus) {
            toggleMenu(false);
        }
        // 關閉貼圖
        if (isStickerOpen && !clickInSticker && !clickInEmoji && !clickInInput) {
            if (document.activeElement !== messageInput) {
                 stickerArea.style.display = "none";
                 if (emojiKeyboardIcon) {
                    emojiKeyboardIcon.classList.replace("fa-keyboard", "fa-face-smile");
                    emojiKeyboardIcon.classList.replace("fa-solid", "fa-regular");
                 }
            }
        }
    });
}

// ==========================================
// 輔助工具函式
// ==========================================
function gettime() {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
}

function appendSystemMessage(text, isError = false) {
    const cssClass = isError ? "system-message error" : "system-message";
    msgBox.innerHTML += `<div class="${cssClass}">${text}</div>`;
    msgBox.scrollTop = msgBox.scrollHeight;
}

function toggleMenu(showOrHide) {
    const plusMenuIcon = document.getElementById("plus-menu-icon");
    const expandedMenu = document.getElementById("expanded-menu");
    const stickerArea = document.getElementById("sticker-area");
    const emojiKeyboardIcon = document.getElementById("emoji-keyboard-icon");

    if (!expandedMenu || !plusMenuIcon) return;

    const isHidden = expandedMenu.style.display === "none" || expandedMenu.style.display === "";
    let shouldShow = typeof showOrHide === "boolean" ? showOrHide : isHidden;

    expandedMenu.style.display = shouldShow ? "flex" : "none";
    plusMenuIcon.classList.toggle("fa-plus", !shouldShow);
    plusMenuIcon.classList.toggle("fa-xmark", shouldShow);

    if (shouldShow && stickerArea && stickerArea.style.display === "grid") {
        if (emojiKeyboardIcon) emojiKeyboardIcon.click();
    }
}

function handleMenuClick(feature) {
    alert(`點擊了 [${feature}] 功能`);
    toggleMenu(false);
}

// ==========================================
// 7. 啟動流程 (檢查登入 -> 啟動聊天室)
// ==========================================



const urlId = new URLSearchParams(window.location.search).get('id');

if (urlId) {
    myId = urlId;
    console.log("🚀 測試模式：使用網址指定的 ID =", myId);
    init();      // 直接啟動
    initLister();
} else {
    // 如果網址沒帶 id，才嘗試去抓 API (原本的邏輯)
    fetchApi('cases/options', 'GET', null, false)
        .then(userData => {
            if (userData && userData.id) myId = userData.id;
            init();
            initLister();
        })
        .catch(err => {
            console.error("無法取得資料，請在網址加上 ?id=1 測試", err);
            // 強制啟動預設值以便測試
            init();
            initLister();
        });
}
// fetchApi('cases/options', 'GET', null, false)
//     .then(userData => {
//         console.log("✅ 登入確認成功", userData);

        
//         if (userData && userData.id) {
//             myId = userData.id;
//             console.log("已更新當前使用者 ID 為:", myId);
//         }

        
//         init();       // 載入聊天列表、連線 WebSocket
//         initLister(); // 綁定按鈕事件
//     })
//     .catch(err => {
//         console.warn("⛔ 未登入或無法取得使用者資料，聊天室不啟動", err);
       
//     });
//====================================
// console.log("🔧 開發模式：強制設定身分 ID = 2");
// myId = 1; // 假裝你是 user 2 (你可以改成資料庫裡有的任何 ID)

// // 綁定全域函式 (確保 onclick 有效)
// window.init = init;
// window.toggleChatWindow = toggleChatWindow;
// window.minimizeChat = minimizeChat;
// window.goBack = goBack;
// window.openChat = openChat;
// window.sendMessage = sendMessage;
// window.toggleMenu = toggleMenu;
// window.handleMenuClick = handleMenuClick;

// // 立刻啟動！
// init();       // 載入列表 & WebSocket
// initLister(); // 綁定按鈕事件

// ==========================================
// 8. 全域綁定 (解決 HTML onclick 找不到函式問題)
// ==========================================

window.toggleChatWindow = toggleChatWindow;
window.minimizeChat = minimizeChat;
window.goBack = goBack;

// ★★★ 聊天操作 ★★★
window.openChat = openChat;      
window.sendMessage = sendMessage; 

// 選單控制
window.toggleMenu = toggleMenu;
window.handleMenuClick = handleMenuClick;

// 初始化
window.init = init;