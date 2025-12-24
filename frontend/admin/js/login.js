// ========== 環境配置 ==========
const CONFIG = {
    USE_MOCK: false, // true: 使用 Mock 資料, false: 使用後端 API
    API_BASE_URL: 'http://localhost:8080/api', // 後端 API 位址
    DEBUG: false, // 是否顯示 console.log
    MIN_PASSWORD_LENGTH: 6, // 最小密碼長度
    NOTIFICATION_DURATION: 3000, // 通知顯示時間(毫秒)
    CAPTCHA_LENGTH: 6, // 驗證碼長度
    REDIRECT_DELAY: 800, // 登入成功後跳轉延遲(毫秒)
    BUTTON_COOLDOWN: 60000, // 按鈕冷卻時間(毫秒) 60000 = 60秒
    INACTIVITY_TIMEOUT: 600, // 閒置逾時時間（秒）- 10 分鐘
    ACTIVITY_CHECK_INTERVAL: 30000, // 每 30 秒檢查一次活動狀態（毫秒）
    ACTIVITY_UPDATE_INTERVAL: 120000, // 每 2 分鐘向後端更新活動時間（毫秒）
    WARNING_BEFORE_TIMEOUT: 60 // 逾時前 60 秒警告（秒）
};

// ========== Mock 使用者資料庫 ==========
// const MOCK_USERS = [
//     {
//         email: 'admin@rehome.com',
//         password: 'admin123',
//         role: 'admin',
//         name: 'ReHome Admin'
//     },
//     {
//         email: 'manager@rehome.com',
//         password: 'admin123',
//         role: 'member',
//         name: 'Manager User'
//     },
//     {
//         email: 'user01@rehome.com',
//         password: 'admin123',
//         role: 'member',
//         name: 'Test User'
//     }
// ];

// ========== Logger 工具 ==========
// const logger = {
//     log: (...args) => CONFIG.DEBUG && console.log(...args),
//     error: (...args) => CONFIG.DEBUG && console.error(...args),
//     warn: (...args) => CONFIG.DEBUG && console.warn(...args)
// };

// ========== 全域變數 ==========
let currentCaptcha = '';
let resetAccountTemp = '';
let lastSubmitTime = 0; // 上次提交時間戳記

// ========== DOM 元素快取 ==========
const DOM = {
    // 表單
    loginForm: null,
    forgotPasswordForm: null,
    resetPasswordForm: null,
    
    // 輸入欄位
    account: null,
    password: null,
    resetAccount: null,
    captchaInput: null,
    newPassword: null,
    confirmPassword: null,
    
    // Modal
    forgotPasswordModal: null,
    resetPasswordModal: null,
    
    // Canvas 與其他元素
    captchaCanvas: null,
    passwordError: null,
    
    // 按鈕
    forgotPasswordLink: null,
    closeModal: null,
    closeResetModal: null
};

// ========== 初始化 DOM 元素 ==========
function cacheDOMElements() {
    // 表單
    DOM.loginForm = document.getElementById('loginForm');
    DOM.forgotPasswordForm = document.getElementById('forgotPasswordForm');
    DOM.resetPasswordForm = document.getElementById('resetPasswordForm');
    
    // 輸入欄位
    DOM.account = document.getElementById('account');
    DOM.password = document.getElementById('password');
    DOM.resetAccount = document.getElementById('resetAccount');
    DOM.captchaInput = document.getElementById('captchaInput');
    DOM.newPassword = document.getElementById('newPassword');
    DOM.confirmPassword = document.getElementById('confirmPassword');
    
    // Modal
    DOM.forgotPasswordModal = document.getElementById('forgotPasswordModal');
    DOM.resetPasswordModal = document.getElementById('resetPasswordModal');
    
    // Canvas 與其他元素
    DOM.captchaCanvas = document.getElementById('captchaCanvas');
    DOM.passwordError = document.getElementById('passwordError');
    
    // 按鈕與連結
    DOM.forgotPasswordLink = document.getElementById('forgotPasswordLink');
    DOM.closeModal = document.getElementById('closeModal');
    DOM.closeResetModal = document.getElementById('closeResetModal');
}

// ========== 初始化 ==========
document.addEventListener('DOMContentLoaded', initLoginPage);

function initLoginPage() {
    cacheDOMElements();
    generateCaptcha();
    bindEventListeners();
    
    if (CONFIG.DEBUG) {
        displayMockUserInfo();
    }
}

// ========== 綁定事件監聽器 ==========
function bindEventListeners() {
    // 驗證碼 Canvas 點擊刷新
    if (DOM.captchaCanvas) {
        DOM.captchaCanvas.style.cursor = 'pointer';
        DOM.captchaCanvas.addEventListener('click', generateCaptcha);
    }

    // 登入表單提交
    if (DOM.loginForm) {
        DOM.loginForm.addEventListener('submit', handleLogin);
    }

    // 忘記密碼連結
    if (DOM.forgotPasswordLink) {
        DOM.forgotPasswordLink.addEventListener('click', (e) => {
            e.preventDefault();
            openForgotPasswordModal();
        });
    }

    // Modal 關閉按鈕
    if (DOM.closeModal) {
        DOM.closeModal.addEventListener('click', closeForgotPasswordModal);
    }
    
    if (DOM.closeResetModal) {
        DOM.closeResetModal.addEventListener('click', closeResetPasswordModal);
    }

    // Modal 外部點擊關閉
    if (DOM.forgotPasswordModal) {
        DOM.forgotPasswordModal.addEventListener('click', (e) => {
            if (e.target === DOM.forgotPasswordModal) {
                closeForgotPasswordModal();
            }
        });
    }
    
    if (DOM.resetPasswordModal) {
        DOM.resetPasswordModal.addEventListener('click', (e) => {
            if (e.target === DOM.resetPasswordModal) {
                closeResetPasswordModal();
            }
        });
    }

    // 忘記密碼表單提交
    if (DOM.forgotPasswordForm) {
        DOM.forgotPasswordForm.addEventListener('submit', handleForgotPassword);
    }

    // 重設密碼表單提交
    if (DOM.resetPasswordForm) {
        DOM.resetPasswordForm.addEventListener('submit', handleResetPassword);
    }

    // 密碼確認即時檢查
    if (DOM.confirmPassword) {
        DOM.confirmPassword.addEventListener('input', checkPasswordMatch);
    }
}

// ========== 生成驗證碼 ==========
/**
 * 生成並顯示新的驗證碼
 */
function generateCaptcha() {
    currentCaptcha = generateCaptchaText(CONFIG.CAPTCHA_LENGTH);
    drawCaptchaToCanvas(currentCaptcha);
}

/**
 * 生成隨機驗證碼字串
 * @param {number} length - 驗證碼長度
 * @returns {string} 驗證碼字串
 */
function generateCaptchaText(length = 6) {
    const chars = '23456789ABCDEFGHJKLMNPQRSTUVWXYZ'; // 排除易混淆字符 0,O,1,I
    return Array.from({ length }, () => 
        chars[Math.floor(Math.random() * chars.length)]
    ).join('');
}

/**
 * 繪製圖形化驗證碼到 Canvas
 * @param {string} captcha - 驗證碼文字
 */
function drawCaptchaToCanvas(captcha) {
    if (!DOM.captchaCanvas) return;
    
    const ctx = DOM.captchaCanvas.getContext('2d');
    const { width, height } = DOM.captchaCanvas;
    
    // 清空並繪製背景
    drawBackground(ctx, width, height);
    
    // 繪製干擾線
    drawInterferenceLines(ctx, width, height, 5);
    
    // 繪製驗證碼文字
    drawCaptchaText(ctx, captcha, width, height);
    
    // 繪製干擾點
    drawInterferencePoints(ctx, width, height, 50);
}

/**
 * 繪製背景與邊框
 */
function drawBackground(ctx, width, height) {
    // 背景漸層
    const gradient = ctx.createLinearGradient(0, 0, width, height);
    gradient.addColorStop(0, '#F5F0E8');
    gradient.addColorStop(1, '#E9E2D8');
    ctx.fillStyle = gradient;
    ctx.fillRect(0, 0, width, height);
    
    // 邊框
    ctx.strokeStyle = '#AB9073';
    ctx.lineWidth = 2;
    ctx.strokeRect(0, 0, width, height);
}

/**
 * 繪製干擾線
 */
function drawInterferenceLines(ctx, width, height, count) {
    for (let i = 0; i < count; i++) {
        ctx.strokeStyle = `rgba(${Math.random() * 255}, ${Math.random() * 255}, ${Math.random() * 255}, 0.3)`;
        ctx.lineWidth = 1 + Math.random() * 2;
        ctx.beginPath();
        ctx.moveTo(Math.random() * width, Math.random() * height);
        ctx.lineTo(Math.random() * width, Math.random() * height);
        ctx.stroke();
    }
}

/**
 * 繪製驗證碼文字
 */
function drawCaptchaText(ctx, captcha, width, height) {
    const charWidth = width / captcha.length;
    const colors = ['#504033', '#AB9073', '#6B5D4F', '#8B7355'];
    
    for (let i = 0; i < captcha.length; i++) {
        const char = captcha[i];
        const fontSize = 28 + Math.random() * 8;
        
        ctx.font = `bold ${fontSize}px Arial, sans-serif`;
        ctx.fillStyle = colors[Math.floor(Math.random() * colors.length)];
        
        const x = charWidth * i + charWidth / 2;
        const y = height / 2 + 5;
        const rotation = (Math.random() - 0.5) * 0.4; // -0.2 ~ 0.2 弧度
        
        ctx.save();
        ctx.translate(x, y);
        ctx.rotate(rotation);
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(char, 0, 0);
        ctx.restore();
    }
}

/**
 * 繪製干擾點
 */
function drawInterferencePoints(ctx, width, height, count) {
    for (let i = 0; i < count; i++) {
        ctx.fillStyle = `rgba(${Math.random() * 255}, ${Math.random() * 255}, ${Math.random() * 255}, 0.5)`;
        ctx.beginPath();
        ctx.arc(Math.random() * width, Math.random() * height, 1, 0, 2 * Math.PI);
        ctx.fill();
    }
}

// ========== API 呼叫函數 ==========

/**
 * 登入 API
 * @param {string} account - 帳號 (Email)
 * @param {string} password - 密碼
 * @returns {Promise<Object>} API 回應
 */
async function loginAPI(account, password) {
    if (CONFIG.USE_MOCK) {
        // Mock 模式: 模擬延遲
        await simulateDelay(500);
        
        const user = MOCK_USERS.find(u => u.email === account && u.password === password);
        
        return user
            ? {
                success: true,
                data: {
                    email: user.email,
                    role: user.role,
                    name: user.name,
                    email: user.email
                }
            }
            : {
                success: false,
                message: '帳號或密碼錯誤'
            };
    } else {
        // 真實 API 呼叫
        const result = await fetchAPI('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ account, password })
        });
        return result;
    }
}

/**
 * 驗證帳號是否存在
 * @param {string} account - 帳號 (Email)
 * @returns {Promise<Object>} API 回應
 */
async function verifyAccountAPI(account) {
    if (CONFIG.USE_MOCK) {
        await simulateDelay(300);
        
        const user = MOCK_USERS.find(u => u.email === account);
        
        return user
            ? {
                success: true,
                message: '驗證碼已發送至您的信箱',
                email: maskEmail(user.email)
            }
            : {
                success: false,
                message: '帳號不存在'
            };
    } else {
        return await fetchAPI('/auth/forgot-password', {
            method: 'POST',
            body: JSON.stringify({ account })
        });
    }
}

/**
 * 重設密碼 API
 * @param {string} account - 帳號 (Email)
 * @param {string} newPassword - 新密碼
 * @returns {Promise<Object>} API 回應
 */
async function resetPasswordAPI(account, newPassword) {
    if (CONFIG.USE_MOCK) {
        await simulateDelay(300);
        
        const userIndex = MOCK_USERS.findIndex(u => u.email === account);
        
        if (userIndex !== -1) {
            MOCK_USERS[userIndex].password = newPassword;
            // logger.log('✅ 密碼已更新:', account);
            
            return {
                success: true,
                message: '密碼重設成功'
            };
        }
        
        return {
            success: false,
            message: '系統錯誤'
        };
    } else {
        return await fetchAPI('/auth/reset-password', {
            method: 'POST',
            body: JSON.stringify({ account, newPassword })
        });
    }
}

/**
 * 統一的 Fetch API 封裝
 * @param {string} endpoint - API 端點
 * @param {Object} options - Fetch 選項
 * @returns {Promise<Object>} JSON 回應
 */
async function fetchAPI(endpoint, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json'
        }
    };
    
    const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`, {
        ...defaultOptions,
        ...options
    });
    
    // 無論狀態碼如何，都嘗試解析 JSON 回應
    const data = await response.json();
    
    // 如果回應不成功，但有 JSON 資料，仍然返回它
    // 這樣可以獲取後端返回的錯誤訊息
    return data;
}

/**
 * 模擬 API 延遲
 * @param {number} ms - 延遲毫秒數
 * @returns {Promise<void>}
 */
function simulateDelay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * 遮蔽信箱地址
 * @param {string} email - 完整信箱
 * @returns {string} 遮蔽後的信箱
 */
function maskEmail(email) {
    return email.replace(/(.{2}).*(@.*)/, '$1***$2');
}

// ========== 表單處理函數 ==========

/**
 * 處理登入表單提交
 * @param {Event} e - 表單提交事件
 */
async function handleLogin(e) {
    e.preventDefault();
    
    const loginBtn = e.target.querySelector('button[type="submit"]');
    const account = DOM.account.value.trim();
    const password = DOM.password.value;
    
    // 停用按鈕,防止重複提交
    setButtonState(loginBtn, true, 'Loading...');
    
    try {
        const result = await loginAPI(account, password);
        
        if (result.success) {
            handleLoginSuccess(result.data);
        } else {
            handleLoginFailure(result.message, loginBtn);
        }
        
    } catch (error) {
        showNotification('系統錯誤,請稍後再試', 'error');
        setButtonState(loginBtn, false, 'Login');
    }
}

/**
 * 處理登入成功
 * @param {Object} userData - 使用者資料
 */
function handleLoginSuccess(userData) {
    // 儲存使用者資訊和 JWT Token 到 localStorage
    const sessionData = {
        memberId: userData.memberId,
        email: userData.email,
        name: userData.name,
        role: userData.role,
        // JWT Token (新版)
        accessToken: userData.accessToken,
        refreshToken: userData.refreshToken,
        expiresIn: userData.expiresIn,
        // 登入時間
        loginTime: new Date().toISOString()
    };
    
    localStorage.setItem('currentUser', JSON.stringify(sessionData));
    
    // 記錄 token 到 console (開發用)
    // if (CONFIG.DEBUG) {
    //     console.log('登入成功,已儲存 JWT Token:');
    //     console.log('- Access Token:', userData.accessToken);
    //     console.log('- Refresh Token:', userData.refreshToken);
    //     console.log('- 有效期(秒):', userData.expiresIn);
    // }
    
    showNotification('登入成功!', 'success');
    
    // 延遲跳轉
    setTimeout(() => {
        window.location.href = './dashborad.html';
    }, CONFIG.REDIRECT_DELAY);
}

/**
 * 處理登入失敗
 * @param {string} message - 錯誤訊息
 * @param {HTMLElement} loginBtn - 登入按鈕
 */
function handleLoginFailure(message, loginBtn) {
    showNotification(message || '帳號或密碼錯誤', 'error');
    
    // 清空密碼並恢復按鈕
    DOM.password.value = '';
    DOM.password.focus();
    setButtonState(loginBtn, false, 'Login');
}

// ========== Modal 管理 ==========

/**
 * 打開忘記密碼 Modal
 */
function openForgotPasswordModal() {
    if (!DOM.forgotPasswordModal) return;
    
    DOM.forgotPasswordModal.removeAttribute('hidden');
    DOM.forgotPasswordModal.classList.add('active');
    
    // 重新生成驗證碼
    generateCaptcha();
    
    // 清空表單
    if (DOM.resetAccount) DOM.resetAccount.value = '';
    if (DOM.captchaInput) DOM.captchaInput.value = '';
    
    // 重置按鈕狀態 (檢查冷卻時間)
    const submitBtn = DOM.forgotPasswordForm?.querySelector('button[type="submit"]');
    if (submitBtn) {
        checkButtonCooldown(submitBtn, '送出驗證碼');
    }
}

/**
 * 關閉忘記密碼 Modal
 */
function closeForgotPasswordModal() {
    if (!DOM.forgotPasswordModal) return;
    
    DOM.forgotPasswordModal.classList.remove('active');
    DOM.forgotPasswordModal.setAttribute('hidden', '');
}

/**
 * 打開重設密碼 Modal
 */
function openResetPasswordModal() {
    if (!DOM.resetPasswordModal) return;
    
    DOM.resetPasswordModal.removeAttribute('hidden');
    DOM.resetPasswordModal.classList.add('active');
    
    // 清空表單
    if (DOM.newPassword) DOM.newPassword.value = '';
    if (DOM.confirmPassword) DOM.confirmPassword.value = '';
    if (DOM.passwordError) DOM.passwordError.textContent = '';
    
    // 重置按鈕狀態
    const submitBtn = DOM.resetPasswordForm?.querySelector('button[type="submit"]');
    if (submitBtn) {
        setButtonState(submitBtn, false, '送出');
    }
}

/**
 * 關閉重設密碼 Modal
 */
function closeResetPasswordModal() {
    if (!DOM.resetPasswordModal) return;
    
    DOM.resetPasswordModal.classList.remove('active');
    DOM.resetPasswordModal.setAttribute('hidden', '');
    resetAccountTemp = '';
}

// ========== 密碼驗證 ==========

/**
 * 驗證密碼格式
 * @param {string} password - 密碼
 * @param {number} minLength - 最小長度
 * @returns {Object} 驗證結果
 */
function validatePassword(password, minLength = CONFIG.MIN_PASSWORD_LENGTH) {
    if (password.length < minLength) {
        return {
            valid: false,
            message: `密碼至少需要 ${minLength} 個字元`
        };
    }
    
    // 可以添加更多驗證規則
    // if (!/[A-Z]/.test(password)) {
    //     return { valid: false, message: '密碼需包含至少一個大寫字母' };
    // }
    // if (!/[a-z]/.test(password)) {
    //     return { valid: false, message: '密碼需包含至少一個小寫字母' };
    // }
    // if (!/[0-9]/.test(password)) {
    //     return { valid: false, message: '密碼需包含至少一個數字' };
    // }
    
    return { valid: true, message: '密碼格式正確' };
}

/**
 * 檢查兩次密碼是否一致
 * @param {string} password1 - 第一次輸入的密碼
 * @param {string} password2 - 第二次輸入的密碼
 * @returns {boolean} 是否一致
 */
function passwordsMatch(password1, password2) {
    return password1 === password2;
}

/**
 * 即時檢查密碼是否一致
 */
function checkPasswordMatch() {
    if (!DOM.newPassword || !DOM.confirmPassword || !DOM.passwordError) return;
    
    const newPassword = DOM.newPassword.value;
    const confirmPassword = DOM.confirmPassword.value;
    
    if (confirmPassword === '') {
        DOM.passwordError.textContent = '';
        return;
    }
    
    if (newPassword !== confirmPassword) {
        DOM.passwordError.textContent = '❌ 密碼不一致';
        DOM.passwordError.style.color = '#EF4444';
    } else {
        DOM.passwordError.textContent = '✓ 密碼一致';
        DOM.passwordError.style.color = '#10B981';
    }
}

/**
 * 處理忘記密碼表單提交(驗證帳號和驗證碼)
 * @param {Event} e - 表單提交事件
 */
async function handleForgotPassword(e) {
    e.preventDefault();
    
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const account = DOM.resetAccount.value.trim();
    const captchaInput = DOM.captchaInput.value.trim().toUpperCase();
    
    // 驗證驗證碼
    if (captchaInput !== currentCaptcha) {
        showNotification('驗證碼錯誤', 'error');
        generateCaptcha();
        DOM.captchaInput.value = '';
        return;
    }
    
    // 停用按鈕
    setButtonState(submitBtn, true, '驗證中...');
    
    try {
        const result = await verifyAccountAPI(account);
        
        if (result.success) {
            resetAccountTemp = account;
            
            // 記錄提交時間 (啟動冷卻)
            lastSubmitTime = Date.now();
            
            showNotification(result.message || '驗證成功', 'success');
            
            // 關閉忘記密碼 Modal,打開重設密碼 Modal
            setTimeout(() => {
                closeForgotPasswordModal();
                openResetPasswordModal();
            }, 500);
            
        } else {
            showNotification(result.message || '帳號不存在', 'error');
            setButtonState(submitBtn, false, '送出驗證碼');
        }
        
    } catch (error) {
        showNotification('系統錯誤,請稍後再試', 'error');
        setButtonState(submitBtn, false, '送出驗證碼');
    }
}

/**
 * 處理重設密碼表單提交
 * @param {Event} e - 表單提交事件
 */
async function handleResetPassword(e) {
    e.preventDefault();
    
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const newPassword = DOM.newPassword.value;
    const confirmPassword = DOM.confirmPassword.value;
    
    // 驗證密碼格式
    const validation = validatePassword(newPassword);
    if (!validation.valid) {
        showNotification(validation.message, 'error');
        return;
    }
    
    // 檢查密碼一致性
    if (!passwordsMatch(newPassword, confirmPassword)) {
        showNotification('兩次密碼輸入不一致', 'error');
        return;
    }
    
    // 停用按鈕
    setButtonState(submitBtn, true, '處理中...');
    
    try {
        const result = await resetPasswordAPI(resetAccountTemp, newPassword);
        
        if (result.success) {
            showNotification('密碼重設成功!請使用新密碼登入', 'success');
            
            // 關閉 Modal
            setTimeout(() => {
                closeResetPasswordModal();
                
                // 自動填入帳號
                DOM.account.value = resetAccountTemp;
                DOM.password.focus();
            }, 1500);
            
        } else {
            showNotification(result.message || '系統錯誤,請重新操作', 'error');
            setButtonState(submitBtn, false, '送出');
        }
        
    } catch (error) {
        showNotification('系統錯誤,請稍後再試', 'error');
        setButtonState(submitBtn, false, '送出');
    }
}


// ========== 工具函數 ==========

/**
 * 設定按鈕狀態
 * @param {HTMLElement} button - 按鈕元素
 * @param {boolean} disabled - 是否停用
 * @param {string} text - 按鈕文字
 */
function setButtonState(button, disabled, text) {
    if (!button) return;
    button.disabled = disabled;
    button.textContent = text;
}

/**
 * 檢查按鈕冷卻時間
 * @param {HTMLElement} button - 按鈕元素
 * @param {string} defaultText - 預設按鈕文字
 */
function checkButtonCooldown(button, defaultText) {
    if (!button) return;
    
    const now = Date.now();
    const timeSinceLastSubmit = now - lastSubmitTime;
    const remainingCooldown = CONFIG.BUTTON_COOLDOWN - timeSinceLastSubmit;
    
    if (remainingCooldown > 0) {
        // 還在冷卻期間
        button.disabled = true;
        startCooldownTimer(button, remainingCooldown, defaultText);
    } else {
        // 冷卻結束，恢復按鈕
        setButtonState(button, false, defaultText);
    }
}

/**
 * 開始冷卻倒數計時
 * @param {HTMLElement} button - 按鈕元素
 * @param {number} remainingTime - 剩餘時間(毫秒)
 * @param {string} defaultText - 預設按鈕文字
 */
function startCooldownTimer(button, remainingTime, defaultText) {
    if (!button) return;
    
    const updateTimer = () => {
        const now = Date.now();
        const remaining = CONFIG.BUTTON_COOLDOWN - (now - lastSubmitTime);
        
        if (remaining > 0) {
            const seconds = Math.ceil(remaining / 1000);
            button.textContent = `請等待 ${seconds} 秒`;
            button.disabled = true;
            
            // 每秒更新一次
            setTimeout(updateTimer, 1000);
        } else {
            // 冷卻結束
            setButtonState(button, false, defaultText);
        }
    };
    
    updateTimer();
}

/**
 * 顯示測試帳號資訊
 */
// function displayMockUserInfo() {
//     console.log('📝 測試帳號列表:');
//     MOCK_USERS.forEach(user => {
//         console.log(`帳號: ${user.email} | 密碼: ${user.password} | 角色: ${user.role} | 信箱: ${user.email}`);
//     });
//     console.log(`\n🔧 當前模式: ${CONFIG.USE_MOCK ? 'Mock 資料' : '真實 API'}`);
//     console.log(`🌐 API 位址: ${CONFIG.API_BASE_URL}`);
// }

// ========== 通知系統 ==========
/**
 * 顯示通知訊息
 * @param {string} message - 訊息內容
 * @param {string} type - 類型 ('success', 'error', 'info')
 */
function showNotification(message, type = 'info') {
    // 移除舊的通知
    removeOldNotification();
    
    // 建立新通知
    const notification = createNotificationElement(message, type);
    document.body.appendChild(notification);
    
    // 自動移除
    scheduleNotificationRemoval(notification);
}

/**
 * 移除舊的通知
 */
function removeOldNotification() {
    const oldNotification = document.querySelector('.notification');
    if (oldNotification) {
        oldNotification.remove();
    }
}

/**
 * 建立通知元素
 * @param {string} message - 訊息內容
 * @param {string} type - 類型
 * @returns {HTMLElement} 通知元素
 */
function createNotificationElement(message, type) {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    
    const backgroundColor = type === 'success' ? '#10B981' : '#EF4444';
    
    Object.assign(notification.style, {
        position: 'fixed',
        top: '20px',
        right: '20px',
        padding: '15px 25px',
        borderRadius: '10px',
        color: '#FFFFFF',
        fontSize: '16px',
        fontWeight: '600',
        zIndex: '99999',
        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.2)',
        animation: 'slideInRight 0.3s ease',
        backgroundColor
    });
    
    return notification;
}

/**
 * 排程通知移除
 * @param {HTMLElement} notification - 通知元素
 */
function scheduleNotificationRemoval(notification) {
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, CONFIG.NOTIFICATION_DURATION);
}

// ========== CSS 動畫 ==========
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// ========== Session 管理功能 ==========
/**
 * Session 管理類別
 */
class SessionManager {
    constructor() {
        this.lastActivityTime = Date.now();
        this.activityCheckIntervalId = null;
        this.activityUpdateIntervalId = null;
        this.warningShown = false;
        this.activityEvents = ['mousedown', 'keydown', 'scroll', 'touchstart', 'click'];
    }

    getSession() {
        try {
            const sessionStr = localStorage.getItem('currentUser');
            if (!sessionStr) return null;
            return JSON.parse(sessionStr);
        } catch (error) {
            return null;
        }
    }

    saveSession(sessionData) {
        try {
            localStorage.setItem('currentUser', JSON.stringify(sessionData));
        } catch (error) {
            // 儲存失敗則忽略
        }
    }

    clearSession() {
        localStorage.removeItem('currentUser');
    }

    hasSession() {
        return this.getSession() !== null;
    }

    getInactiveTime() {
        const now = Date.now();
        const inactiveMs = now - this.lastActivityTime;
        return Math.floor(inactiveMs / 1000);
    }

    recordActivity() {
        this.lastActivityTime = Date.now();
        this.warningShown = false;
    }

    async updateActivityToBackend() {
        const session = this.getSession();
        if (!session || !session.token) return;

        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}/auth/refresh-activity`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    account: session.email,
                    password: session.token
                })
            });

            const result = await response.json();

            if (result.success && result.data) {
                this.saveSession({ ...session, lastActivityTime: result.data.lastActivityTime });
            } else if (response.status === 401) {
                this.autoLogout('Session 已失效');
            }
        } catch (error) {
            // 更新失敗則忽略
        }
    }

    async logout() {
        const session = this.getSession();
        if (!session || !session.token) {
            this.clearSession();
            return { success: true, message: '已登出' };
        }

        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}/auth/logout`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    account: session.email,
                    password: session.token
                })
            });

            const result = await response.json();
            this.clearSession();
            this.stopActivityTracking();
            return result;
        } catch (error) {
            this.clearSession();
            this.stopActivityTracking();
            return { success: false, message: '網路錯誤' };
        }
    }

    showTimeoutWarning() {
        if (this.warningShown) return;
        
        this.warningShown = true;
        const remainingTime = CONFIG.INACTIVITY_TIMEOUT - this.getInactiveTime();
        const seconds = Math.max(0, remainingTime);
        
        showNotification(`閒置時間過長，將在 ${seconds} 秒後自動登出`, 'warning');
    }

    autoLogout(reason = '閒置時間過長') {
        this.clearSession();
        this.stopActivityTracking();
        showNotification(reason + '，請重新登入', 'error');
        
        setTimeout(() => {
            window.location.href = './login.html';
        }, 1000);
    }

    checkActivity() {
        const inactiveTime = this.getInactiveTime();
        const session = this.getSession();
        const timeout = session?.inactivityTimeout || CONFIG.INACTIVITY_TIMEOUT;

        if (inactiveTime >= timeout) {
            this.autoLogout('閒置時間超過 10 分鐘');
            return;
        }

        const timeUntilLogout = timeout - inactiveTime;
        if (timeUntilLogout <= CONFIG.WARNING_BEFORE_TIMEOUT && timeUntilLogout > 0) {
            this.showTimeoutWarning();
        }
    }

    startActivityTracking() {
        this.recordActivity();

        this.activityEvents.forEach(event => {
            document.addEventListener(event, () => this.recordActivity(), { passive: true });
        });

        this.activityCheckIntervalId = setInterval(() => {
            this.checkActivity();
        }, CONFIG.ACTIVITY_CHECK_INTERVAL);

        this.activityUpdateIntervalId = setInterval(() => {
            this.updateActivityToBackend();
        }, CONFIG.ACTIVITY_UPDATE_INTERVAL);
    }

    stopActivityTracking() {
        if (this.activityCheckIntervalId) {
            clearInterval(this.activityCheckIntervalId);
            this.activityCheckIntervalId = null;
        }

        if (this.activityUpdateIntervalId) {
            clearInterval(this.activityUpdateIntervalId);
            this.activityUpdateIntervalId = null;
        }
    }

    requireLogin() {
        if (!this.hasSession()) {
            window.location.href = './login.html';
            return false;
        }

        return true;
    }
}

// ========== 全域 Session 管理器 ==========
window.sessionManager = new SessionManager();

// ========== 自動初始化 Session 追蹤 (非登入頁) ==========
if (typeof window !== 'undefined' && window.location) {
    const isLoginPage = window.location.pathname.includes('login.html');
    
    if (!isLoginPage) {
        document.addEventListener('DOMContentLoaded', () => {
            if (window.sessionManager.requireLogin()) {
                window.sessionManager.startActivityTracking();
            }
        });
    }
}