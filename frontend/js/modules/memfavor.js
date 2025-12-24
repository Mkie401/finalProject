function adoptionDetail_favoriteListener() {
    $('.memcenter-container').on('click', '.heart-fill', function (e) {
        console.log(123);
        
        e.preventDefault();
        e.stopPropagation();

        const $icon = $(this);
        const $card = $icon.closest('.card'); // 找到對應的卡片

        // 切換收藏圖示
        if ($icon.hasClass('bi-suit-heart-fill')) {
            $icon.removeClass('bi-suit-heart-fill').addClass('bi-suit-heart');
            $icon.removeClass('text-danger');

            // 取消收藏 → 卡片淡出後移除
            $card.fadeOut(300, function() {
                $(this).remove();
            });
        } else {
            $icon.removeClass('bi-suit-heart').addClass('bi-suit-heart-fill');
            $icon.addClass('text-danger');
        }
    });
}

adoptionDetail_favoriteListener();