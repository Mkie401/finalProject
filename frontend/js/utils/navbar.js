
    const toggler = document.querySelector(".custom-toggler");
    const navbarCollapse = document.getElementById("navbarNav"); // 確保這是您的選單 ID
    const navbar = document.getElementById("navbar");
    let lastScrollY = window.scrollY;

    // --- A. 漢堡圖標 X 動畫與 Bootstrap 狀態同步 ---
    toggler.addEventListener("click", () => {
        toggler.classList.toggle("open");
    });

    // 監聽 Bootstrap 的收合事件，確保 X 圖標與選單狀態完全同步
    if (navbarCollapse) {
        navbarCollapse.addEventListener('shown.bs.collapse', function () {
            toggler.classList.add("open");
        });

        navbarCollapse.addEventListener('hidden.bs.collapse', function () {
            toggler.classList.remove("open");
        });
    }

    // --- B. 導航列滾動隱藏/顯示邏輯 (已加入關鍵判斷) ---
    window.addEventListener("scroll", () => {
        const currentScroll = window.scrollY;

        if (!navbarCollapse.classList.contains('show')) {
            if (currentScroll > lastScrollY && currentScroll > 80) {
                // 往下捲 → 隱藏 navbar
                navbar.classList.add("hide");

            } else {
                // 往上捲 → 出現 navbar
                navbar.classList.remove("hide");

            }
        }


        lastScrollY = currentScroll;
    });

    document.getElementById("backToTop").addEventListener("click", () => {
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
        console.log("11");

    });


