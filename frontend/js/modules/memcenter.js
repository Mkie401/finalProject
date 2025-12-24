// 從 JWT Token 解析會員資料
function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (error) {
        console.error('JWT 解析錯誤:', error);
        return null;
    }
}

// 取得當前會員ID
function getCurrentMemberId() {
    const token = localStorage.getItem('authToken');
    if (!token) {
        alert('尚未登入，請先登入會員');
        window.location.href = '#login';
        return null;
    } 
        
    const payload = parseJwt(token);
    return payload ? payload.memberId : null;
}

/*------------------------------------------------------------
  會員頭像
------------------------------------------------------------*/
// 載入會員頭像
async function loadMemAvatar() {
    try {
        const memberId = getCurrentMemberId();
        if(!memberId) return;

        // 加上時間戳記，避免瀏覽器快取
        const timestamp = new Date().getTime();
        const response = await fetch(`/api/mem/avatar?memberId=${memberId}&t=${timestamp}`);
        
        if (response.ok) {
            const blob = await response.blob();
            if(blob.size > 0) {
                // 先釋放舊的 URL（避免記憶體洩漏）
                const memberAvatarImg = document.getElementById('memberAvatar');
                if (memberAvatarImg) {
                    const oldSrc = memberAvatarImg.src;
                    if (oldSrc && oldSrc.startsWith('blob:')) {
                        URL.revokeObjectURL(oldSrc);
                    }
                    
                    // 建立新的 URL
                    const imageUrl = URL.createObjectURL(blob);
                    memberAvatarImg.src = imageUrl;
                    console.log('頭像載入成功');
                }
            } else {
                console.log('頭像資料為空，使用預設頭像');
            }
        } else if (response.status === 404) {
            console.log('尚未上傳頭像，使用預設頭像');
        } else {
            console.log('使用預設頭像');
        }
    } catch (error) {
        console.error('載入頭像時發生錯誤:', error);
    }
}

// 上傳頭像
async function uploadAvatar(base64Data) {
    try {
        const memberId = getCurrentMemberId();
        if (!memberId) return;

        const response = await fetch('/api/mem/avatar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ 
                avatar: base64Data,
                memberId: memberId 
            }),
            credentials: 'include'
        });

        const result = await response.json();
        
        if (!response.ok || !result.success) {
            throw new Error(result.message || '上傳失敗');
        }

        console.log('上傳成功:', result);
        alert('頭像上傳成功！');
        
        // 重新載入頭像
        loadMemAvatar();

    } catch (error) {
        console.error('上傳錯誤:', error);
        alert('頭像上傳失敗：' + error.message);
    }
}

// 初始化頭像
function initAvatarUpload() {
    const avatarContainer = document.querySelector('.avatar-container');
    const avatarInput = document.getElementById('avatarInput');
    const memberAvatar = document.getElementById('memberAvatar');    

    if(!avatarContainer || !avatarInput) {
        console.log('頭像尚未載入');
        return;
    }

    // 點擊頭像選擇檔案
    avatarContainer.addEventListener('click', function() {
        avatarInput.click();
    });

    // 選擇檔案後預覽並上傳
    avatarInput.addEventListener('change', function(e) {
        const file = e.target.files[0];

        if(!file) return;

        // 檢查檔案類型
        if (!file.type.startsWith('image/')) {
            alert('請選擇圖片檔案！');
            return;
        }

        // 檢查檔案大小（2MB）
        if (file.size > 2 * 1024 * 1024) {
            alert('圖片大小不能超過 2MB！');
            return;
        }

        // 預覽頭像
        const reader = new FileReader();
        reader.onload = function(e) {
            memberAvatar.src = e.target.result;  // 顯示預覽
            uploadAvatar(e.target.result);  // 上傳 Base64 字串
        };
        reader.readAsDataURL(file);
    });
}
/*------------------------------------------------------------
  載入會員暱稱
------------------------------------------------------------*/
// 載入會員暱稱到側邊欄
window.loadMemberNickname = async function() {
    try {
        const memberId = getCurrentMemberId();
        if (!memberId) return;

        const response = await fetch(`/api/mem/profile?memberId=${memberId}`);
        const result = await response.json();

        if (result.success) {
            const data = result.data;
            const nickname = data.nickName || data.name; // 如果沒有暱稱就使用姓名
            document.getElementById('nickname').textContent = nickname;
            console.log('暱稱載入成功:', nickname);
        } else {
            console.error('載入暱稱失敗:', result.message);
        }

    } catch (error) {
        console.error('載入暱稱時發生錯誤:', error);
    }
}

/*------------------------------------------------------------
  初始化頁面
------------------------------------------------------------*/
// 初始化頁面
function memcenter_init() {
    // 載入會員暱稱
    window.loadMemberNickname();

    // 載入會員頭像
    loadMemAvatar();

    // 初始化頭像上傳功能
    initAvatarUpload();

    // 預設內容
    $("#memcontent").load("../content/memcentercontent/account.html");

    //  預設active
    $(".sidebarhref[data-file='account.html']").addClass("active");

    //  點擊側邊選單
    $(".sidebarhref").on("click", function (e) {
        e.preventDefault();

        // 移除全部 active
        $(".sidebarhref").removeClass("active");

        // 加上 active
        $(this).addClass("active");

        // 取得要載入的檔案
        const file = $(this).data("file");

        // 載入內容
        if (file) {
            $("#memcontent").load("../content/memcentercontent/" + file, function (){
                // 如果載入的是 account.html，則初始化帳戶頁面
                if(file === 'account.html' && typeof window.initAccountPage === 'function') {
                    window.initAccountPage();
                }
            });
        }
    });

    // 收合按鈕
    $("#toggleSidebarBtn").on("click", function () {
        console.log("clicked");
        $("#sidebar").toggleClass("active");
        $("#toggleSidebarBtn").toggleClass("active")
    })

    // 登出按鈕
    $("#logoutBtn").on("click", function (){
        localStorage.removeItem('authToken');
        localStorage.removeItem('userEmail');
        window.location.href = '#home';
    })

}

memcenter_init();

