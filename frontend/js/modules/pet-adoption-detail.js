// -------------------------
import { fetchApi, calculateAgeUsingDiff } from '/js/utils/helper.js';
import petCard, {
    renderBanner,
    parseCaseDetail,
    renderDetail,
    updatePreviewMap
} from './petCard/component.js';

// ----------------------------------------------------------------
// 詳細單頁使用變數
let adoptionDetail_imgList = [];

function adoptionDetail_adoptionModalListener() {
    // 監聽 Modal **開始顯示** 的事件 (動畫開始前)
    $('#adopt_question').on('show.bs.modal', function (event) {
        $(this).find('input, select').val('');
    });

    // 監聽 Modal **完全顯示** 的事件 (動畫結束後，Modal 穩定)
    $('#adopt_question').on('shown.bs.modal', function (event) {
        fetchApi('mem/profile?memberId=1').done(result => {
            console.log(result);
            if (result.success) {
                console.log(result.message);

                $('#info_name').val(result.data.nickName || result.data.name);
                $('#info_gender').val(result.data.gender ? '男' : '女');
                $('#info_age').val(calculateAgeUsingDiff(result.data.birthDate));
                $('#info_phone').val(result.data.phone);
                $('#info_address').val('');
            } else {
                alert(result.message);
            }
        });

        fetchApi('survey/adoption').done(result => {
            if (result.success) {
                console.log(result.message);

                adoptionDetail__loadModal(result.data.questions)
            } else {
                alert(result.message);
            }
        });

        // 可以在這裡設定焦點
        // $('#info_marital').focus(); 
    });

    $('#adopt_question').on('click', '.question-btn', function () {
        const marital = $('#info_marital').val();
        const employment = $('#info_employment').val();
        console.log();
        
        let questions = [];

        $('#adopt_question').find('.collapse > textarea').each((index, item) => {
            const text = $(item).val().trim();
            if (text) {
                questions.push({
                    questionId: $(item).data('id'),
                    answer: text
                });
            } else {
                questions = [];
                alert('請填寫完所有問卷');
                $(item).focus();
                return false;
            }
        });
        
        if (marital && employment && questions.length > 0) {
            console.log('都有寫');
            
            fetchApi('members/adoption/applications', 'POST', {
                caseNumber: getCaseNumberFromHash(),
                maritalStatus: $('#info_marital').val(),
                employmentStatus: $('#info_employment').val(),
                questions: questions
            }).done(result => {
                console.log(result);
                if (result.success) {
                    console.log(result.message);
                    $('#adopt_question').modal('hide');
                } else {
                    alert(result.message);
                }
                
            });
        } else if (!marital) {
            $('#info_marital').focus();
        } else if (!employment) {
            $('#info_employment').focus();
        }
    });
}

// 載入問卷
function adoptionDetail__loadModal(questionList) {
    $('#question_wrapper').empty();
    questionList.forEach(item => {
        $('#question_wrapper').append(`
            <div class="mb-2">
                <a href="#question_textarea_${item.id}" data-bs-toggle="collapse"
                    class="d-flex align-items-center justify-content-between p-3 fw-bold question-trigger"
                    aria-expanded="true" aria-controls="question_textarea_${item.id}" role="button">

                    <div class="question-text">${item.question}</div>

                    <i class="icon-toggle bi bi-chevron-up"></i>
                </a>

                <div id="question_textarea_${item.id}" class="collapse show">
                    <textarea class="form-control" rows="5" placeholder="範例：......" name="question_textarea_${item.id}" data-id="${item.id}" required></textarea>
                </div>
            </div>
        `);
    })
}

function adoptionDetail_missingPost() {
    $('#missing_post').on('click', '.missing-post-btn', function (e) {
        if ($('#missing_post_textarea').val().trim()) {
            fetchApi('members/missing/applications', 'POST', {
                caseNumber: getCaseNumberFromHash(),
                message: $('#missing_post_textarea').val().trim()
            }).done(result => {
                console.log(result);
                if (result.success) {
                    $('#missing_post').modal('hide');
                    $('#missing_post_textarea').val('');
                }
            });
        }
    });
}

// 監聽 收藏點擊事件
function adoptionDetail_favoriteListener() {
    $('#detail_content_favorite').on('click', '.favorite-btn', function (e) {
        e.preventDefault();
        e.stopPropagation();

        const icon = $(this).find('i');
        const isFavorites = icon.hasClass('bi-suit-heart-fill');

        // ---- UI 先切換（樂觀更新）----
        icon.toggleClass('bi-suit-heart bi-suit-heart-fill text-danger');

        if (isFavorites) {
            // ⭐ DELETE (取消收藏)
            fetchApi(
                'members/favorites/' + getCaseNumberFromHash(),
                'DELETE'
            ).done(result => {
                if (!result.success) {
                    // ❌ 還原 UI
                    icon.toggleClass('bi-suit-heart-fill text-danger bi-suit-heart');
                    alert(result.message);
                }
            });
        } else {
            // ⭐ POST (新增收藏)
            fetchApi(
                'members/favorites/',
                'POST',
                { caseNumber: getCaseNumberFromHash() }
            ).done(result => {
                if (!result.success) {
                    // ❌ 還原 UI
                    icon.toggleClass('bi-suit-heart-fill text-danger bi-suit-heart');
                    alert(result.message);
                }
            });
        }
    });
}

// 監聽 按鈕切換圖片
function adoptionDetail_carouselImgListener() {
    $('#detail_carousel_img').on('click', '#carousel_img_prev', function (e) {
        e.preventDefault;

        let lastImg = adoptionDetail_imgList.shift();
        adoptionDetail_imgList.push(lastImg);

        $('#detail_main_img').prop('src', 'data:image/png;base64,' + adoptionDetail_imgList[0]);
        $('#detail_carousel_img').find('.img-customer').each(function (index, value) {
            $(value).prop('src', 'data:image/png;base64,' + adoptionDetail_imgList[index + 1]);
        });
    });

    $('#detail_carousel_img').on('click', '#carousel_img_next', function (e) {
        e.preventDefault;

        let lastImg = adoptionDetail_imgList.pop();
        adoptionDetail_imgList.unshift(lastImg);

        $('#detail_main_img').prop('src', 'data:image/png;base64,' + adoptionDetail_imgList[0]);
        $('#detail_carousel_img').find('.img-customer').each(function (index, value) {
            $(value).prop('src', 'data:image/png;base64,' + adoptionDetail_imgList[index + 1]);
        });
    });
}

function adoptionDetail_scrollToMapListener() {
    $('.scroll-to-map-btn').on('click', function (e) {
        e.preventDefault();

        // 獲取目標區塊距離頂部的距離 (offset().top)
        const targetPosition = $('#previewMapContainer').offset().top;

        // 使用 $('html, body').animate() 執行平滑捲動
        // 捲動目標：$('html, body')
        // 捲動參數：scrollTop: targetPosition (捲到目標位置)
        // 捲動時間：1000 毫秒 (1 秒)
        $('html, body').animate({
            scrollTop: targetPosition
        }, 100); // 您可以調整這個數值來控制捲動速度
    });
}

// 解析 hash + query string 拿到案件編號
function getCaseNumberFromHash() {
    // #pet-adoption-detail?casenumber=SY202511010021
    const hash = window.location.hash;
    const parts = hash.split('?');
    if (parts.length < 2) window.location.href = '';

    const queryString = parts[1];
    const params = new URLSearchParams(queryString);
    return params.get('casenumber');
}

function setShareBtnUrl(target) {
    const currentUrl = window.location.href;
    const encodedUrl = encodeURIComponent(currentUrl);    // 進行編碼
    let shareUrl = '';

    switch (target) {
        case 'facebook':
            shareUrl = 'https://www.facebook.com/sharer/sharer.php?u=' + encodedUrl;
            break;
        case 'line':
            shareUrl = 'https://social-plugins.line.me/lineit/share?url=' + encodedUrl;
            break;
        case 'twitter-x':
            shareUrl = 'https://twitter.com/intent/tweet?url=' + encodedUrl;
            break;

        default:
            break;
    }

    const targetBtn = '.' + target + '-btn';
    $('#adoption_detail_main .share-btn-wrapper ' + targetBtn).prop('href', shareUrl);

}

// 初始化葉面
function adoptionDetail_init() {
    const casenumber = getCaseNumberFromHash();
    const currentHash = window.location.hash.split('?')[0] || '';
    const isAdoption = currentHash.includes('adoption');

    fetchApi('cases/' + casenumber).done(result => {
        console.log(result);

        // render banner (shared)
        renderBanner(
            '#adoption_detail .custom-title-wrapper',
            isAdoption,
            true,
            result.data.caseInfo.isMissing
                ? 'missing'
                : (result.data.caseInfo.isPublic ? 'shelter' : 'private')
        );

        // 設定分享網址
        setShareBtnUrl('facebook');
        setShareBtnUrl('line');
        setShareBtnUrl('twitter-x');

        // 使用 petCard 的解析與渲染
        const parsed = parseCaseDetail(result.data);
        console.log(parsed);

        const images = renderDetail(parsed);
        adoptionDetail_imgList = images;

        // 如果回傳有座標，更新預覽地圖（lat,lng）
        if (parsed && parsed.isMissing && parsed.lostDetail) {
            updatePreviewMap(parsed.lostDetail.lat, parsed.lostDetail.lng);
            adoptionDetail_scrollToMapListener();
            adoptionDetail_missingPost();
        }

        if (parsed && !parsed.isMissing && !parsed.isPublic) {
            adoptionDetail_adoptionModalListener();
        }
    });

    adoptionDetail_favoriteListener();
    adoptionDetail_carouselImgListener();
}

// 自動執行初始化
// adoptionDetail_init();

window.adoptionDetail_init = adoptionDetail_init;