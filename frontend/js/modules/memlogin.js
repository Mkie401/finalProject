/*------------------------------------------------------------
    登入表單送出
------------------------------------------------------------*/
function initLoginFormSubmit() {
    const loginForm = document.getElementById('login_form');
    
    if (!loginForm) return;
    
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault(); // 阻止表單預設送出行為
        
        // 收集登入表單資料
        const loginData = collectLoginFormData();
        
        // 發送登入請求
        await sendLoginRequest(loginData);
    });
}

// 初始化登入表單
initLoginFormSubmit();

/*------------------------------------------------------------
    收集登入表單資料
------------------------------------------------------------*/
function collectLoginFormData() {
    const email = document.getElementById("login_Account").value;
    const password = document.getElementById("login_Password").value;
    const captcha = document.querySelector('input[name="confirmcode"]').value;
    
    return {
        email: email,
        password: password,
        captcha: captcha,  // 暫時先送，後端目前跳過驗證
        sessionId: window.getCaptchaSessionId()  // 取得驗證碼 Session ID
    };
}

/*------------------------------------------------------------
    發送登入請求到後端
------------------------------------------------------------*/
async function sendLoginRequest(loginData) {
    try {
        // 發送 POST 請求到後端
        const response = await fetch('/api/mem/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });
        
        // 處理回應
        const result = await response.json();
        
        if (result.success) {
            // 登入成功
            alert('登入成功！');
            console.log('登入成功', result);
            
            // 儲存 Token 到 localStorage
            localStorage.setItem('authToken', result.token);  // JWT Token
            localStorage.setItem('userEmail', loginData.email); // 使用者 Email
            
            // 記住帳號功能
            const rememberAccount = document.getElementById('login_rememberAccount').checked;
            if (rememberAccount) {
                localStorage.setItem('rememberedEmail', loginData.email);
            } else {
                localStorage.removeItem('rememberedEmail');
            }
            
            // 導向會員中心
            window.location.href = '#member';
            
        } else {
            // 登入失敗
            alert('登入失敗：' + result.message);
            console.log('登入失敗', result);
        }
        
    } catch (error) {
        // 錯誤處理
        console.error('登入錯誤:', error);
        alert('連線失敗，請稍後再試');
    }
}

/*------------------------------------------------------------
    頁面載入時，自動填入記住的帳號
------------------------------------------------------------*/
function initRememberAccount() {
    const loginAccountInput = document.getElementById('login_Account');
    const rememberCheckbox = document.getElementById('login_rememberAccount');
    
    if (!loginAccountInput) return;
    
    // 檢查是否有記住的帳號
    const rememberedEmail = localStorage.getItem('rememberedEmail');
    if (rememberedEmail) {
        loginAccountInput.value = rememberedEmail;
        if (rememberCheckbox) {
            rememberCheckbox.checked = true;
        }
    }
}

// 初始化記住帳號功能
initRememberAccount();