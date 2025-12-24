document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('surveyModal');
    const closeBtn = document.querySelector('.close-btn');
    const cancelBtn = document.querySelector('.btn-cancel');
    const saveBtn = document.querySelector('.btn-save');
    const editBtns = document.querySelectorAll('.edit-btn');
    const addQuestionBtn = document.querySelector('.add-question-btn');
    
    let currentSurveyType = ''; // 'adoption' 或 'surrender'

    // 假資料：領養問卷
    const adoptionQuestions = [
        {
            id: 1,
            question: '請問有養寵物的經驗嗎?家中是否還有其他寵物?',
            content: '請詳細說明您的養寵經驗',
            sortOrder: 1
        },
        {
            id: 2,
            question: '新成員的活動空間如何規劃(含防護設施)?是否會關籠?',
            content: '請說明空間規劃細節',
            sortOrder: 2
        },
        {
            id: 3,
            question: '新成員在環境都有一定的適應期，會如何避免以及安排?',
            content: '例如：逐步適應、隔離空間等',
            sortOrder: 3
        },
        {
            id: 4,
            question: '請確認可以配合送養前家訪及領養後不定的寵物生活照追蹤',
            content: null,
            sortOrder: 4
        },
        {
            id: 5,
            question: '家中同住者是否確認過無相關過敏體質，並且全數同意加入新的寵物成員，並對新成員不離不棄?',
            content: null,
            sortOrder: 5
        },
        {
            id: 6,
            question: '未來您的生活上有變動(經濟來源、結婚、懷孕...等)，會如何安排您的新成員?',
            content: '請說明應變計畫',
            sortOrder: 6
        },
        {
            id: 7,
            question: '您已了解領養後代表您會負責新成員的生病過程，您已經完成確認並可以勝任。',
            content: null,
            sortOrder: 7
        }
    ];

    // 假資料：送養問卷
    const surrenderQuestions = [
        {
            id: 8,
            question: '請說明您為什麼要送養這隻寵物？',
            content: '請詳細說明原因',
            sortOrder: 1
        },
        {
            id: 9,
            question: '寵物的基本資訊（品種、年齡、性別、體重等）',
            content: null,
            sortOrder: 2
        },
        {
            id: 10,
            question: '寵物的健康狀況（是否結紮、疫苗接種情況、有無疾病史）',
            content: '請提供完整健康資訊',
            sortOrder: 3
        },
        {
            id: 11,
            question: '寵物的個性與習性描述',
            content: '例如：親人、活潑、怕生等',
            sortOrder: 4
        },
        {
            id: 12,
            question: '送養條件與要求',
            content: '例如：需定期回報、不得棄養等',
            sortOrder: 5
        },
        {
            id: 13,
            question: '是否願意協助新飼主過渡適應期？',
            content: null,
            sortOrder: 6
        }
    ];

    // 儲存最後更新時間
    let lastUpdated = {
        adoption: '2025/12/04 20:15:30',
        surrender: '2025/12/04 20:15:30'
    };

    // 初始化頁面時更新最後更新時間
    updatePageTimestamps();

    // 點擊 Edit 按鈕開啟 Modal
    editBtns.forEach((btn, index) => {
        btn.addEventListener('click', function() {
            currentSurveyType = index === 0 ? 'adoption' : 'surrender';
            const modalTitle = document.querySelector('.modal-title');
            modalTitle.textContent = index === 0 ? '領養問卷編輯' : '送養問卷編輯';
            
            // 載入問卷資料
            loadSurveyData(currentSurveyType);
            
            modal.style.display = 'block';
        });
    });

    // 關閉 Modal
    function closeModal() {
        modal.style.display = 'none';
    }

    closeBtn.addEventListener('click', closeModal);
    cancelBtn.addEventListener('click', closeModal);

    // 點擊 Modal 外部關閉
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            closeModal();
        }
    });

    // 題目展開/收合功能
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('toggle-btn')) {
            const questionItem = e.target.closest('.question-item');
            const answerDiv = questionItem.querySelector('.question-answer');
            const toggleBtn = e.target;
            
            if (answerDiv.style.display === 'none' || !answerDiv.style.display) {
                answerDiv.style.display = 'block';
                toggleBtn.textContent = '×';
                questionItem.classList.add('expanded');
            } else {
                answerDiv.style.display = 'none';
                toggleBtn.textContent = '+';
                questionItem.classList.remove('expanded');
            }
        }
    });

    // 刪除題目
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('delete-btn')) {
            if (confirm('確定要刪除這個題目嗎?')) {
                e.target.closest('.question-item').remove();
                updateQuestionNumbers();
            }
        }
    });

    // 向上移動題目
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('up-btn')) {
            const questionItem = e.target.closest('.question-item');
            const prevItem = questionItem.previousElementSibling;
            if (prevItem) {
                questionItem.parentNode.insertBefore(questionItem, prevItem);
                updateQuestionNumbers();
            }
        }
    });

    // 向下移動題目
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('down-btn')) {
            const questionItem = e.target.closest('.question-item');
            const nextItem = questionItem.nextElementSibling;
            if (nextItem) {
                questionItem.parentNode.insertBefore(nextItem, questionItem);
                updateQuestionNumbers();
            }
        }
    });

    // 更新題目編號和按鈕狀態
    function updateQuestionNumbers() {
        const questionItems = document.querySelectorAll('.question-item');
        questionItems.forEach((item, index) => {
            // 更新題號
            const numberSpan = item.querySelector('.question-number');
            if (numberSpan) {
                numberSpan.textContent = `${index + 1}.`;
            }
            
            // 更新上移按鈕狀態
            const upBtn = item.querySelector('.up-btn');
            if (upBtn) {
                upBtn.disabled = index === 0;
            }
            
            // 更新下移按鈕狀態
            const downBtn = item.querySelector('.down-btn');
            if (downBtn) {
                downBtn.disabled = index === questionItems.length - 1;
            }
        });
    }

    // 初始化拖曳排序
    function initDragAndDrop() {
        const questionItems = document.querySelectorAll('.question-item');
        let draggedItem = null;

        questionItems.forEach(item => {
            item.addEventListener('dragstart', function(e) {
                draggedItem = this;
                this.style.opacity = '0.5';
                e.dataTransfer.effectAllowed = 'move';
            });

            item.addEventListener('dragend', function() {
                this.style.opacity = '1';
                draggedItem = null;
                updateQuestionNumbers();
            });

            item.addEventListener('dragover', function(e) {
                e.preventDefault();
                e.dataTransfer.dropEffect = 'move';
                
                if (this !== draggedItem) {
                    const rect = this.getBoundingClientRect();
                    const midpoint = rect.top + rect.height / 2;
                    
                    if (e.clientY < midpoint) {
                        this.parentNode.insertBefore(draggedItem, this);
                    } else {
                        this.parentNode.insertBefore(draggedItem, this.nextSibling);
                    }
                }
            });
        });
    }

    // 新增題目
    addQuestionBtn.addEventListener('click', function() {
        const questionList = document.getElementById('questionList');
        const currentCount = questionList.children.length;
        const newQuestion = document.createElement('div');
        newQuestion.className = 'question-item';
        newQuestion.setAttribute('draggable', 'true');
        newQuestion.innerHTML = `
            <div class="question-header">
                <div class="sort-buttons">
                    <button class="sort-btn up-btn" title="向上移動" ${currentCount === 0 ? 'disabled' : ''}>↑</button>
                    <button class="sort-btn down-btn" title="向下移動" disabled>↓</button>
                    <span class="drag-handle" title="拖曳排序">⋮⋮</span>
                </div>
                <span class="question-number">${currentCount + 1}.</span>
                <span class="question-text" contenteditable="true">請輸入新問題...</span>
                <button class="toggle-btn collapsed">+</button>
            </div>
            <div class="question-answer" style="display: none;">
                <textarea class="answer-input" placeholder="範例/說明：......" rows="4"></textarea>
                <button class="delete-btn">×</button>
            </div>
        `;
        questionList.appendChild(newQuestion);
        
        // 更新所有題目的按鈕狀態
        updateQuestionNumbers();
        
        // 初始化新題目的拖曳事件
        initDragAndDrop();
        
        // 自動聚焦到新題目
        newQuestion.querySelector('.question-text').focus();
    });

    // 儲存問卷
    saveBtn.addEventListener('click', function() {
        const questions = [];
        const questionItems = document.querySelectorAll('.question-item');
        
        questionItems.forEach((item, index) => {
            const questionText = item.querySelector('.question-text').textContent;
            const contentText = item.querySelector('.answer-input').value;
            
            questions.push({
                question: questionText,
                content: contentText || null,
                sortOrder: index + 1
            });
        });

        // 儲存到後端
        saveSurveyData(currentSurveyType, questions);
        
        // 關閉 Modal
        closeModal();
    });

    // 載入問卷資料
    function loadSurveyData(type) {
        // 從後端 API 載入問卷資料
        const userData = JSON.parse(localStorage.getItem('currentUser'));
        const headers = { 'Content-Type': 'application/json' };
        if (userData && userData.accessToken) {
            headers['Authorization'] = `Bearer ${userData.accessToken}`;
        }
        
        fetch(`/api/survey/${type}`, { 
            method: 'GET',
            headers: headers 
        })
            .then(response => {
                // 先解析 JSON，不管狀態碼
                return response.json().then(data => ({
                    ok: response.ok,
                    status: response.status,
                    data: data
                }));
            })
            .then(result => {
                if (result.ok && result.data.success) {
                    const questions = result.data.data.questions || [];
                    
                    // 更新假資料（用於前端暫存）
                    if (type === 'adoption') {
                        adoptionQuestions.length = 0;
                        adoptionQuestions.push(...questions);
                    } else {
                        surrenderQuestions.length = 0;
                        surrenderQuestions.push(...questions);
                    }
                    
                    // 更新最後更新時間
                    if (result.data.data.lastUpdated) {
                        lastUpdated[type] = formatDateTime(result.data.data.lastUpdated);
                    }
                    
                    renderQuestions(questions);
                } else {
                    // 後端返回失敗訊息
                    const errorMsg = result.data.message || `載入問卷失敗 (HTTP ${result.status})`;
                    console.warn('後端返回:', errorMsg);
                    // 使用預設空問卷
                    const questions = type === 'adoption' ? adoptionQuestions : surrenderQuestions;
                    renderQuestions(questions);
                }
            })
            .catch(error => {
                console.error('載入問卷失敗:', error);
                // 失敗時使用預設問卷
                const questions = type === 'adoption' ? adoptionQuestions : surrenderQuestions;
                renderQuestions(questions);
            });
    }

    // 儲存問卷資料
    function saveSurveyData(type, questions) {
        const userData = JSON.parse(localStorage.getItem('currentUser'));
        const headers = { 'Content-Type': 'application/json' };
        if (userData && userData.accessToken) {
            headers['Authorization'] = `Bearer ${userData.accessToken}`;
        }
        
        // 發送到後端 API
        fetch('/api/survey/save', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify({ 
                type: type,
                questions: questions 
            })
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.message || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                // 更新假資料（用於前端暫存）
                if (type === 'adoption') {
                    adoptionQuestions.length = 0;
                    adoptionQuestions.push(...data.data.questions);
                } else {
                    surrenderQuestions.length = 0;
                    surrenderQuestions.push(...data.data.questions);
                }
                
                // 更新最後更新時間
                if (data.data.lastUpdated) {
                    lastUpdated[type] = formatDateTime(data.data.lastUpdated);
                    updatePageTimestamps();
                }
                
                alert('問卷儲存成功!');
            } else {
                alert('儲存失敗: ' + data.message);
            }
        })
        .catch(error => {
            console.error('儲存問卷失敗:', error);
            alert('儲存問卷失敗: ' + error.message);
        });
    }

    // 更新頁面上的最後更新時間
    function updatePageTimestamps() {
        const sections = document.querySelectorAll('.survey-section');
        sections[0].querySelector('.update-info').textContent = `最近更新：${lastUpdated.adoption}`;
        sections[1].querySelector('.update-info').textContent = `最近更新：${lastUpdated.surrender}`;
    }

    // 更新最近更新時間
    function updateLastModified(type) {
        const now = new Date();
        const formatted = `${now.getFullYear()}/${String(now.getMonth() + 1).padStart(2, '0')}/${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`;
        
        lastUpdated[type] = formatted;
        
        const sections = document.querySelectorAll('.survey-section');
        const index = type === 'adoption' ? 0 : 1;
        sections[index].querySelector('.update-info').textContent = `最近更新：${formatted}`;
    }

    // 格式化日期時間（從後端 API 返回的格式）
    function formatDateTime(dateTimeString) {
        const date = new Date(dateTimeString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        
        return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`;
    }

    // 渲染問卷題目
    function renderQuestions(questions) {
        const questionList = document.getElementById('questionList');
        questionList.innerHTML = '';
        
        questions.forEach((q, index) => {
            const questionItem = document.createElement('div');
            questionItem.className = 'question-item';
            questionItem.setAttribute('data-index', index);
            questionItem.setAttribute('draggable', 'true');
            questionItem.innerHTML = `
                <div class="question-header">
                    <div class="sort-buttons">
                        <button class="sort-btn up-btn" title="向上移動" ${index === 0 ? 'disabled' : ''}>↑</button>
                        <button class="sort-btn down-btn" title="向下移動" ${index === questions.length - 1 ? 'disabled' : ''}>↓</button>
                        <span class="drag-handle" title="拖曳排序">⋮⋮</span>
                    </div>
                    <span class="question-number">${index + 1}.</span>
                    <span class="question-text" contenteditable="true">${q.question}</span>
                    <button class="toggle-btn collapsed">+</button>
                </div>
                <div class="question-answer" style="display: none;">
                    <textarea class="answer-input" placeholder="範例/說明：......" rows="4">${q.content || ''}</textarea>
                    <button class="delete-btn">×</button>
                </div>
            `;
            questionList.appendChild(questionItem);
        });
        
        // 初始化拖曳事件
        initDragAndDrop();
    }
});
