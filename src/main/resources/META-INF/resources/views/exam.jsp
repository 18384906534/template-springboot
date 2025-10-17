<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>试卷页面</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/exam.css">
    <style>
        body {
            padding: 15px;
            position: relative;
        }
    </style>
</head>
<body>
<!-- 右侧题目导航 -->
<div class="question-nav">
    <div class="nav-header">
        <div class="nav-title">考试信息</div>
        <div class="nav-stats">
            <div>已完成: <span id="nav-answered-count">0</span>/<span id="nav-total-questions">0</span></div>
            <div class="timer" id="nav-timer">00:00:00</div>
        </div>
    </div>

    <div class="nav-question-list" id="nav-question-list">
        <!-- 动态生成导航项 -->
    </div>

    <!-- 导航栏提交按钮区域 -->
    <div class="nav-submit-section">
        <button type="button" id="nav-submit-exam-btn" class="nav-submit-btn">
            📝 提交试卷
        </button>
        <div style="font-size: 11px; color: #666; line-height: 1.3;">
            按 Ctrl+Enter 快速提交
        </div>
    </div>
</div>

<!-- 确认提交对话框 -->
<div class="confirm-dialog" id="confirm-dialog">
    <div class="confirm-content">
        <h3 id="confirm-title">确认提交试卷</h3>
        <p id="confirm-message">您确定要提交试卷吗？</p>
        <div class="confirm-buttons">
            <button class="confirm-btn confirm-yes" id="confirm-yes">确定提交</button>
            <button class="confirm-btn confirm-no" id="confirm-no">继续答题</button>
        </div>
    </div>
</div>

<!-- 加载提示 -->
<div class="loading-overlay" id="loading-overlay">
    <div class="loading-content">
        <div class="loading-spinner"></div>
        <p>正在提交试卷，请稍候...</p>
    </div>
</div>

<div class="exam-container">
    <!-- 隐藏域存储科目代码和测试类型代码 -->
    <input type="hidden" id="subjectCode" value="${exam.subjectCode}" />
    <input type="hidden" id="examCategoryCode" value="${exam.examCategoryCode}" />

    <!-- 试卷头部 -->
    <div class="exam-header">
        <h1 class="exam-title" id="examName">${exam.examName}</h1>
        <div style="color: #666; font-size: 14px; margin-top: 5px;">
            科目: ${exam.subjectCode} | 测试类型:
            <c:choose>
                <c:when test="${exam.examCategoryCode == 1}">章节测试</c:when>
                <c:when test="${exam.examCategoryCode == 2}">课堂测试</c:when>
                <c:when test="${exam.examCategoryCode == 3}">模拟试卷</c:when>
                <c:when test="${exam.examCategoryCode == 4}">历年真题</c:when>
                <c:when test="${exam.examCategoryCode == 99}">自动生成</c:when>
                <c:otherwise>未知类型</c:otherwise>
            </c:choose>
            | 开始时间:
            <c:if test="${not empty exam.startTime}">
                <fmt:formatDate value="${exam.startTime}" pattern="HH:mm:ss" />
            </c:if>
            <c:if test="${empty exam.startTime}">
                <span id="current-start-time"></span>
            </c:if>
        </div>
    </div>

    <!-- 动态渲染所有题目 -->
    <c:set var="currentType" value="" />
    <c:forEach var="question" items="${exam.questionVOList}" varStatus="status">
        <!-- 检查题型变化，添加题型标题 -->
        <c:choose>
            <c:when test="${not empty question.questionOptionVOList}">
                <c:if test="${currentType != 'single-choice'}">
                    <c:set var="currentType" value="single-choice" />
                    <div class="question-type-title">一、单选题</div>
                </c:if>
            </c:when>
            <c:when test="${question.fullScore <= 5}">
                <c:if test="${currentType != 'short-answer'}">
                    <c:set var="currentType" value="short-answer" />
                    <div class="question-type-title">二、简答题</div>
                </c:if>
            </c:when>
            <c:otherwise>
                <c:if test="${currentType != 'essay'}">
                    <c:set var="currentType" value="essay" />
                    <div class="question-type-title">三、论述题</div>
                </c:if>
            </c:otherwise>
        </c:choose>

        <div class="question-item" id="question-${status.count}">
            <div class="question-header">
                <div class="question-content">
                    <span class="question-number">${status.count}.</span>
                        ${question.questionContent}
                </div>
                <span class="question-score">${question.fullScore}分</span>
            </div>

            <!-- 动态渲染输入组件 -->
            <c:choose>
                <%-- 单选题：有选项列表 --%>
                <c:when test="${not empty question.questionOptionVOList}">
                    <div class="single-choice-options">
                        <c:forEach var="option" items="${question.questionOptionVOList}">
                            <label class="single-choice-option">
                                <input type="radio" name="question_${question.questionId}" value="${option.optionKey}">
                                <span class="option-text">
                                    <strong>${option.optionKey}.</strong> ${option.optionText}
                                </span>
                            </label>
                        </c:forEach>
                    </div>
                </c:when>

                <%-- 简答题：分数较小 --%>
                <c:when test="${question.fullScore <= 5}">
                    <textarea class="short-answer-input" placeholder="请在此处作答..." name="question_${question.questionId}"></textarea>
                </c:when>

                <%-- 论述题：分数较大 --%>
                <c:otherwise>
                    <textarea class="essay-input" placeholder="请在此处作答..." name="question_${question.questionId}"></textarea>
                </c:otherwise>
            </c:choose>

            <!-- 显示答案按钮和答案解析 -->
            <button class="show-answer-btn" data-question="${status.count}">查看答案</button>

            <div class="answer-analysis" id="answer-${status.count}" style="display: none;">
                <c:if test="${not empty question.rightAnswer}">
                    <div class="analysis-title">参考答案：${question.rightAnswer}</div>
                </c:if>
                <c:if test="${not empty question.analysis}">
                    <div>解析：${question.analysis}</div>
                </c:if>
            </div>
        </div>
    </c:forEach>
</div>

<script>
    // 全局变量
    let startTime = Date.now();
    let timerInterval;
    let currentQuestion = 1;

    // 页面加载完成后执行
    document.addEventListener('DOMContentLoaded', function() {
        // 设置开始时间
        const now = new Date();
        const startTimeElement = document.getElementById('current-start-time');
        if (startTimeElement) {
            startTimeElement.textContent = now.toLocaleTimeString();
        }

        // 初始化导航栏信息
        initializeNavInfo();

        // 初始化导航栏
        initializeQuestionNavigation();

        // 初始化所有功能
        initializeExamFunctions();

        // 刷新页面时回到第一题
        setTimeout(() => {
            scrollToQuestion(1);
        }, 300);
    });

    // 初始化导航栏信息
    function initializeNavInfo() {
        const totalQuestions = document.querySelectorAll('.question-item').length;
        document.getElementById('nav-total-questions').textContent = totalQuestions;
        updateAnsweredCount();
    }

    // 初始化考试功能
    function initializeExamFunctions() {
        // 计时器功能
        startTimer();

        // 初始化所有事件监听
        initializeEventListeners();

        // 初始化滚动监听
        initializeScrollListener();

        // 初始化题目状态
        initializeQuestionStatus();

        // 添加输入元素自动滚动功能
        addAutoScrollToInputs();

        // 添加快捷键支持
        initializeKeyboardShortcuts();

        // 初始化已选中的单选题状态
        initializeSelectedOptions();
    }

    // 初始化导航栏
    function initializeQuestionNavigation() {
        const navList = document.getElementById('nav-question-list');
        const questions = document.querySelectorAll('.question-item');

        // 清空现有导航项
        navList.innerHTML = '';

        let currentType = '';
        let typeCount = 0;

        questions.forEach((question, index) => {
            const questionNumber = index + 1;

            // 检测题型变化
            const questionStyle = detectQuestionStyle(question);
            if (questionStyle !== currentType) {
                currentType = questionStyle;
                typeCount++;

                // 添加题型标题（不是第一个题型时添加分割线）
                if (typeCount > 1) {
                    const divider = document.createElement('div');
                    divider.className = 'nav-divider';
                    navList.appendChild(divider);
                }

                const sectionTitle = document.createElement('div');
                sectionTitle.className = 'nav-section-title';
                sectionTitle.textContent = getTypeName(questionStyle);
                navList.appendChild(sectionTitle);
            }

            // 添加题目导航项
            const navItem = document.createElement('div');
            navItem.className = 'nav-question-item ' + questionStyle;
            navItem.setAttribute('data-question', questionNumber);
            navItem.textContent = questionNumber;
            navList.appendChild(navItem);
        });
    }

    // 检测题目类型
    function detectQuestionStyle(questionElement) {
        if (questionElement.querySelector('.single-choice-options')) {
            return 'single-choice';
        } else if (questionElement.querySelector('.short-answer-input')) {
            return 'short-answer';
        } else if (questionElement.querySelector('.essay-input')) {
            return 'essay';
        }
        return 'unknown';
    }

    // 获取题型名称
    function getTypeName(type) {
        const typeNames = {
            'single-choice': '单选题',
            'short-answer': '简答题',
            'essay': '论述题'
        };
        return typeNames[type] || '未知题型';
    }

    // 初始化所有事件监听
    function initializeEventListeners() {
        // 查看答案按钮事件
        document.querySelectorAll('.show-answer-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const questionNumber = this.getAttribute('data-question');
                toggleAnswer(questionNumber);
            });
        });

        // 单选按钮变化事件
        document.querySelectorAll('.single-choice-option input[type="radio"]').forEach(radio => {
            radio.addEventListener('change', function() {
                // 移除同组其他选项的选中状态
                const questionName = this.name;
                document.querySelectorAll(`input[name="${questionName}"]`).forEach(otherRadio => {
                    otherRadio.closest('.single-choice-option').classList.remove('selected');
                });

                // 添加当前选项的选中状态
                this.closest('.single-choice-option').classList.add('selected');

                const questionItem = this.closest('.question-item');
                const questionId = questionItem.id;
                const questionNumber = questionId.replace('question-', '');
                updateQuestionStatus(questionNumber);
            });
        });

        // 文本题输入事件
        document.querySelectorAll('textarea').forEach(textarea => {
            textarea.addEventListener('input', function() {
                const questionItem = this.closest('.question-item');
                const questionId = questionItem.id;
                const questionNumber = questionId.replace('question-', '');
                updateQuestionStatus(questionNumber);
            });
        });

        // 题目导航点击事件 - 使用事件委托
        document.getElementById('nav-question-list').addEventListener('click', function(e) {
            if (e.target.classList.contains('nav-question-item')) {
                const questionNumber = e.target.getAttribute('data-question');
                scrollToQuestion(questionNumber);
            }
        });

        // 导航栏提交按钮事件
        document.getElementById('nav-submit-exam-btn').addEventListener('click', function() {
            checkBeforeSubmit();
        });

        // 确认对话框事件
        document.getElementById('confirm-yes').addEventListener('click', function() {
            submitExam();
        });

        document.getElementById('confirm-no').addEventListener('click', function() {
            document.getElementById('confirm-dialog').style.display = 'none';
        });
    }

    // 初始化已选中的单选题状态
    function initializeSelectedOptions() {
        document.querySelectorAll('.single-choice-option input[type="radio"]').forEach(radio => {
            if (radio.checked) {
                radio.closest('.single-choice-option').classList.add('selected');
            }
        });
    }

    // 添加快捷键支持
    function initializeKeyboardShortcuts() {
        document.addEventListener('keydown', function(e) {
            // Ctrl + Enter 提交
            if (e.ctrlKey && e.key === 'Enter') {
                e.preventDefault();
                checkBeforeSubmit();
            }

            // 左右箭头切换题目
            if (e.key === 'ArrowLeft') {
                e.preventDefault();
                navigateToPrevQuestion();
            } else if (e.key === 'ArrowRight') {
                e.preventDefault();
                navigateToNextQuestion();
            }
        });
    }

    function navigateToPrevQuestion() {
        const current = parseInt(currentQuestion);
        if (current > 1) {
            scrollToQuestion(current - 1);
        }
    }

    function navigateToNextQuestion() {
        const current = parseInt(currentQuestion);
        const total = document.querySelectorAll('.question-item').length;
        if (current < total) {
            scrollToQuestion(current + 1);
        }
    }

    // 为所有输入元素添加自动滚动到中央的功能
    function addAutoScrollToInputs() {
        // 为单选题选项添加点击事件
        document.querySelectorAll('.single-choice-option').forEach(option => {
            option.addEventListener('click', function() {
                const questionItem = this.closest('.question-item');
                const questionId = questionItem.id;
                const questionNumber = questionId.replace('question-', '');
                setTimeout(() => {
                    scrollToQuestion(questionNumber);
                }, 50);
            });
        });

        // 为文本输入框添加聚焦事件
        document.querySelectorAll('textarea').forEach(textarea => {
            textarea.addEventListener('focus', function() {
                const questionItem = this.closest('.question-item');
                const questionId = questionItem.id;
                const questionNumber = questionId.replace('question-', '');
                setTimeout(() => {
                    scrollToQuestion(questionNumber);
                }, 50);
            });
        });

        // 为查看答案按钮添加点击事件
        document.querySelectorAll('.show-answer-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const questionNumber = this.getAttribute('data-question');
                setTimeout(() => {
                    scrollToQuestion(questionNumber);
                }, 100);
            });
        });
    }

    // 初始化题目状态
    function initializeQuestionStatus() {
        document.querySelectorAll('.question-item').forEach((item, index) => {
            const questionNumber = (index + 1).toString();
            updateQuestionStatus(questionNumber);
        });
    }

    // 计时器功能 - 已用时间
    function startTimer() {
        function updateTimer() {
            const currentTime = Date.now();
            const elapsedTime = currentTime - startTime;
            const hours = Math.floor(elapsedTime / (1000 * 60 * 60));
            const minutes = Math.floor((elapsedTime % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((elapsedTime % (1000 * 60)) / 1000);

            const timerText = hours.toString().padStart(2, '0') + ':' +
                minutes.toString().padStart(2, '0') + ':' +
                seconds.toString().padStart(2, '0');

            // 更新导航栏计时器
            const navTimer = document.getElementById('nav-timer');
            if (navTimer) {
                navTimer.textContent = timerText;
            }
        }

        timerInterval = setInterval(updateTimer, 1000);
        updateTimer();
    }

    // 显示/隐藏答案功能
    function toggleAnswer(questionNumber) {
        const answerElement = document.getElementById('answer-' + questionNumber);
        const button = document.querySelector('.show-answer-btn[data-question="' + questionNumber + '"]');

        if (!answerElement || !button) return;

        if (answerElement.style.display === 'block') {
            answerElement.style.display = 'none';
            button.textContent = '查看答案';
        } else {
            answerElement.style.display = 'block';
            button.textContent = '隐藏答案';
        }
    }

    // 更新题目状态
    function updateQuestionStatus(questionNumber) {
        const questionElement = document.getElementById('question-' + questionNumber);
        const navItem = document.querySelector('.nav-question-item[data-question="' + questionNumber + '"]');

        if (!questionElement || !navItem) return;

        let isAnswered = false;
        const questionStyle = detectQuestionStyle(questionElement);

        if (questionStyle === 'single-choice') {
            const radioInputs = questionElement.querySelectorAll('input[type="radio"]');
            for (let radio of radioInputs) {
                if (radio.checked) {
                    isAnswered = true;
                    break;
                }
            }
        } else {
            const textInputs = questionElement.querySelectorAll('textarea');
            for (let textarea of textInputs) {
                if (textarea.value.trim() !== '') {
                    isAnswered = true;
                    break;
                }
            }
        }

        if (isAnswered) {
            navItem.classList.add('answered');
        } else {
            navItem.classList.remove('answered');
        }

        updateAnsweredCount();
    }

    // 更新已完成题目计数
    function updateAnsweredCount() {
        const answeredItems = document.querySelectorAll('.nav-question-item.answered');
        const answeredCount = answeredItems.length;

        const navAnsweredElement = document.getElementById('nav-answered-count');
        if (navAnsweredElement) {
            navAnsweredElement.textContent = answeredCount;
        }
    }

    // 滚动到指定题目
    function scrollToQuestion(questionNumber) {
        const targetElement = document.getElementById('question-' + questionNumber);
        const navItem = document.querySelector('.nav-question-item[data-question="' + questionNumber + '"]');

        if (targetElement && navItem) {
            document.querySelectorAll('.nav-question-item').forEach(navItem => {
                navItem.classList.remove('current');
            });

            navItem.classList.add('current');

            const elementPosition = targetElement.getBoundingClientRect().top + window.pageYOffset;
            const offsetPosition = elementPosition;

            window.scrollTo({
                top: offsetPosition,
                behavior: 'smooth'
            });

            scrollNavToItem(navItem);
        }
    }

    // 滚动导航栏使指定项可见
    function scrollNavToItem(navItem) {
        const navList = document.getElementById('nav-question-list');
        if (!navList) return;

        const navItemTop = navItem.offsetTop;
        const navItemHeight = navItem.offsetHeight;
        const navListHeight = navList.clientHeight;

        const scrollTop = navItemTop - (navListHeight / 2) + (navItemHeight / 2);

        navList.scrollTo({
            top: scrollTop,
            behavior: 'smooth'
        });
    }

    // 初始化滚动监听
    function initializeScrollListener() {
        const questionElements = document.querySelectorAll('.question-item');
        const navItems = document.querySelectorAll('.nav-question-item');

        let scrollTimeout;
        function updateCurrentQuestion() {
            const scrollPosition = window.scrollY + 100;

            for (let i = 0; i < questionElements.length; i++) {
                const element = questionElements[i];
                const elementTop = element.offsetTop;
                const elementBottom = elementTop + element.offsetHeight;

                if (scrollPosition >= elementTop && scrollPosition < elementBottom) {
                    const newQuestion = (i + 1).toString();
                    if (newQuestion !== currentQuestion) {
                        currentQuestion = newQuestion;

                        navItems.forEach(navItem => {
                            navItem.classList.remove('current');
                        });

                        const currentNavItem = document.querySelector('.nav-question-item[data-question="' + currentQuestion + '"]');
                        if (currentNavItem) {
                            currentNavItem.classList.add('current');
                            scrollNavToItem(currentNavItem);
                        }
                    }
                    break;
                }
            }
        }

        window.addEventListener('scroll', function() {
            clearTimeout(scrollTimeout);
            scrollTimeout = setTimeout(updateCurrentQuestion, 50);
        });

        updateCurrentQuestion();
    }

    // 提交前检查
    function checkBeforeSubmit() {
        const totalQuestions = document.querySelectorAll('.question-item').length;
        const answeredCount = document.querySelectorAll('.nav-question-item.answered').length;
        const unansweredCount = totalQuestions - answeredCount;

        if (unansweredCount > 0) {
            document.getElementById('confirm-title').textContent = '有未完成的题目';
            document.getElementById('confirm-message').textContent = '您还有 ' + unansweredCount + ' 道题目未完成，确定要提交吗？';
            document.getElementById('confirm-dialog').style.display = 'flex';
        } else {
            submitExam();
        }
    }

    // 提交试卷功能
    function submitExam() {
        const loadingOverlay = document.getElementById('loading-overlay');
        if (loadingOverlay) {
            loadingOverlay.style.display = 'flex';
        }

        // 禁用导航栏提交按钮
        const navSubmitBtn = document.getElementById('nav-submit-exam-btn');
        if (navSubmitBtn) {
            navSubmitBtn.disabled = true;
            navSubmitBtn.textContent = '提交中...';
        }

        const subjectCode = document.getElementById('subjectCode').value;
        const examCategoryCode = document.getElementById('examCategoryCode').value;
        const answers = [];

        document.querySelectorAll('.question-item').forEach(questionItem => {
            const questionId = getQuestionIdFromElement(questionItem);
            const questionStyle = detectQuestionStyle(questionItem);

            if (questionStyle === 'single-choice') {
                const selectedRadio = questionItem.querySelector('input[type="radio"]:checked');
                const answerValue = selectedRadio ? selectedRadio.value : null;
                answers.push({
                    questionStyle: questionStyle,
                    questionId: questionId,
                    answer: answerValue
                });
            } else {
                const textarea = questionItem.querySelector('textarea');
                if (textarea && textarea.value.trim()) {
                    answers.push({
                        questionStyle: questionStyle,
                        questionId: questionId,
                        answer: textarea.value.trim(),
                    });
                } else {
                    answers.push({
                        questionStyle: questionStyle,
                        questionId: questionId,
                        answer: null
                    });
                }
            }
        });

        clearInterval(timerInterval);

        const examTime = document.getElementById('nav-timer').textContent;
        const totalQuestions = document.querySelectorAll('.question-item').length;
        const answeredCount = answers.filter(a => a.answer !== null).length;

        const submitData = {
            subjectCode: subjectCode,
            examCategoryCode: examCategoryCode,
            examName: document.getElementById('examName').textContent,
            answers: answers,
            examTime: examTime
        };

        console.log('提交数据:', submitData);

        fetch('/cqzk-exam/exam/examSubmit', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(submitData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('网络响应不正常');
                }
                return response.json();
            })
            .then(data => {
                console.log('提交成功:', data);

                if (data.success) {
                    alert('试卷提交成功！\n考试用时: ' + examTime + '\n完成题目: ' + answeredCount + '/' + totalQuestions);

                    if (data.redirectUrl) {
                        window.location.href = data.redirectUrl;
                    }
                } else {
                    alert('提交失败: ' + (data.message || '未知错误'));
                }
            })
            .catch(error => {
                console.error('提交错误:', error);
                alert('提交过程中出现错误: ' + error.message);
            })
            .finally(() => {
                document.getElementById('confirm-dialog').style.display = 'none';
                if (loadingOverlay) {
                    loadingOverlay.style.display = 'none';
                }
            });
    }

    // 从题目元素中提取questionId
    function getQuestionIdFromElement(questionElement) {
        const radio = questionElement.querySelector('input[type="radio"]');
        const textarea = questionElement.querySelector('textarea');

        if (radio) {
            return radio.name.replace('question_', '');
        } else if (textarea) {
            return textarea.name.replace('question_', '');
        }

        console.error('无法从题目元素中提取questionId');
        return null;
    }
</script>
</body>
</html>