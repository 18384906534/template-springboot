<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>试卷详情</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/exam.css">
    <style>
        body {
            padding: 15px;
            position: relative;
        }

        .answer-analysis {
            display: block;
        }
    </style>
</head>
<body>
<!-- 左侧信息栏 -->
<div class="exam-info-panel">
    <div class="info-item">
        <span class="info-label">科目:</span>
        <span class="info-value" id="subjectCode">--</span>
    </div>
    <div class="info-item">
        <span class="info-label">测试类型:</span>
        <span class="info-value" id="examCategoryCode">--</span>
    </div>
    <div class="info-item">
        <span class="info-label">试卷名称:</span>
        <span class="info-value" id="examName">--</span>
    </div>
    <div class="info-item">
        <span class="info-label">题目总数:</span>
        <span class="info-value" id="total-questions">0</span>
    </div>
</div>

<!-- 右侧题目导航 -->
<div class="question-nav">
    <div class="nav-header">
        <div class="nav-title">题目导航</div>
        <div class="nav-stats">
            <div>总题数: <span id="nav-total-questions">0</span></div>
        </div>
    </div>
    <div class="nav-question-list" id="nav-question-list">
        <!-- 动态生成导航项 -->
    </div>
</div>

<div class="exam-container">
    <!-- 试卷头部 -->
    <div class="exam-header">
        <h1 class="exam-title" id="pageTitle">试卷详情</h1>
    </div>

    <!-- 统计信息 -->
    <div class="stats-info">
        <h3>答题统计</h3>
        <div class="stats-grid" id="statsGrid">
            <!-- 动态生成统计信息 -->
        </div>
    </div>

    <!-- 题目容器 -->
    <div id="questionsContainer">
        <div class="loading">加载中...</div>
    </div>

    <!-- 返回按钮 -->
    <div style="text-align: center; margin-top: 25px;">
        <button type="button" class="back-btn" onclick="goBack()">
            返回上一页
        </button>
    </div>
</div>

<script>
    // 全局变量
    let currentQuestion = 1;
    let questionStats = {
        total: 0,
        correct: 0,
        wrong: 0,
        highFrequency: 0,
        recentCorrect: 0,
        neverWrong: 0
    };

    // 页面加载完成后执行
    document.addEventListener('DOMContentLoaded', function() {
        loadExamDetail();
    });

    // 加载试卷详情
    function loadExamDetail() {
        // 从URL参数中获取参数
        const urlParams = new URLSearchParams(window.location.search);
        const examId = urlParams.get('examId');
        const subjectCode = urlParams.get('subjectCode');
        const examCategoryCode = urlParams.get('examCategoryCode');

        // 构建查询参数
        const queryData = {};
        if (examId) queryData.examId = examId;
        if (subjectCode) queryData.subjectCode = subjectCode;
        if (examCategoryCode) queryData.examCategoryCode = parseInt(examCategoryCode);

        // 设置页面标题
        setPageTitle(examId, subjectCode, examCategoryCode);

        // 调用后端接口获取数据
        fetch('/cqzk-exam/exam/getExamDetailList', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(queryData)
        })
            .then(function(response) {
                return response.json();
            })
            .then(function(data) {
                // 使用获取到的数据渲染页面
                renderExamDetail(data);
            })
            .catch(function(error) {
                console.error('加载详情失败:', error);
                document.getElementById('questionsContainer').innerHTML = '<div class="loading">加载失败，请重试</div>';
            });
    }

    // 设置页面标题
    function setPageTitle(examId, subjectCode, examCategoryCode) {
        let title = '试卷详情';
        if (examId) {
            title = '单次测试详情';
        } else if (subjectCode && examCategoryCode) {
            title = '科目测试类型记录';
        } else if (subjectCode) {
            title = '科目做题记录';
        }
        document.getElementById('pageTitle').textContent = title;
    }

    // 渲染试卷详情
    function renderExamDetail(examAnswerInfo) {
        // 更新左侧信息栏
        document.getElementById('subjectCode').textContent = examAnswerInfo.subjectCode || '--';
        document.getElementById('examCategoryCode').textContent = examAnswerInfo.examCategoryCode || '--';
        document.getElementById('examName').textContent = examAnswerInfo.examName || '--';
        document.getElementById('total-questions').textContent = examAnswerInfo.questionVOList ? examAnswerInfo.questionVOList.length : 0;

        // 更新导航栏总题数
        document.getElementById('nav-total-questions').textContent = examAnswerInfo.questionVOList ? examAnswerInfo.questionVOList.length : 0;

        // 渲染题目列表
        renderQuestions(examAnswerInfo.questionVOList);

        // 初始化导航栏和统计信息
        initializeQuestionNavigation();
        initializeStats();
        initializeScrollListener();

        // 刷新页面时回到第一题
        setTimeout(function() {
            scrollToQuestion(1);
        }, 300);
    }

    // 渲染题目列表
    function renderQuestions(questions) {
        const container = document.getElementById('questionsContainer');

        if (!questions || questions.length === 0) {
            container.innerHTML = '<div class="loading">暂无题目数据</div>';
            return;
        }

        let html = '';
        let currentType = '';

        questions.forEach(function(question, index) {
            const questionNumber = index + 1;

            // 检查题型变化，添加题型标题
            if (question.questionOptionVOList && question.questionOptionVOList.length > 0) {
                if (currentType !== 'single-choice') {
                    currentType = 'single-choice';
                    html += '<div class="question-type-title">一、单选题</div>';
                }
            }

            // 计算题目状态
            const questionData = calculateQuestionStatus(question, questionNumber);
            html += questionData.html;
        });

        container.innerHTML = html;
    }

    // 计算题目状态并生成HTML
    function calculateQuestionStatus(question, questionNumber) {
        let latestAnswer = '';
        let isLatestCorrect = false;
        let wrongCount = 0;
        let isHighFrequency = false;
        let isRecentCorrect = false;
        let isNeverWrong = false;

        if (question.examAnswerHistoryVOList && question.examAnswerHistoryVOList.length > 0) {
            // 获取最近一次答案
            const latestHistory = question.examAnswerHistoryVOList[0];
            latestAnswer = latestHistory.myAnswer || '';
            isLatestCorrect = latestHistory.right || false;

            // 计算错误次数
            question.examAnswerHistoryVOList.forEach(function(history) {
                if (!history.right) {
                    wrongCount++;
                }
            });

            // 判断是否为高频错题（错误次数>=3）
            if (wrongCount >= 3) {
                isHighFrequency = true;
            }

            // 判断是否为以前常错但最近正确
            if (wrongCount >= 1 && isLatestCorrect) {
                isRecentCorrect = true;
            }

            // 判断是否为从未错过
            if (wrongCount === 0 && question.examAnswerHistoryVOList.length > 0) {
                isNeverWrong = true;
            }
        }

        // 设置题目CSS类
        let questionClass = 'question-item';
        if (isLatestCorrect) questionClass += ' correct';
        if (!isLatestCorrect && latestAnswer) questionClass += ' wrong';
        if (isHighFrequency) questionClass += ' high-frequency';
        if (isRecentCorrect) questionClass += ' recent-correct';
        if (isNeverWrong) questionClass += ' never-wrong';

        // 生成题目HTML
        let html = '<div class="' + questionClass + '" id="question-' + questionNumber + '">';
        html += '<div class="question-header">';
        html += '<div class="question-content">';
        html += '<span class="question-number">' + questionNumber + '.</span>';
        html += question.questionContent;
        html += '</div>';
        html += '<span class="question-score">' + question.fullScore + '分</span>';
        html += '</div>';

        // 题目元信息
        html += '<div class="question-meta">';
        if (wrongCount > 0) {
            html += '<span class="wrong-count">错' + wrongCount + '次</span>';
        }
        if (isHighFrequency) {
            html += '<span class="high-frequency-badge">高频错题</span>';
        }
        if (isRecentCorrect) {
            html += '<span class="recent-correct-badge">进步显著</span>';
        }
        if (isNeverWrong) {
            html += '<span class="never-wrong-badge">从未错过</span>';
        }
        html += '</div>';

        // 单选题选项
        if (question.questionOptionVOList && question.questionOptionVOList.length > 0) {
            html += '<div class="single-choice-options">';
            question.questionOptionVOList.forEach(function(option) {
                let optionClass = 'single-choice-option';
                let optionStatus = '';

                // 判断选项状态
                if (option.optionKey === question.rightAnswer) {
                    optionClass += ' correct-answer';
                }
                if (option.optionKey === latestAnswer) {
                    if (isLatestCorrect) {
                        optionClass += ' user-correct';
                        optionStatus = 'status-correct';
                    } else {
                        optionClass += ' user-wrong';
                        optionStatus = 'status-wrong';
                    }
                }

                html += '<div class="' + optionClass + '">';
                html += '<span class="option-text">';
                html += '<strong>' + option.optionKey + '.</strong> ' + option.optionText;
                html += '</span>';

                if (optionStatus) {
                    const statusSymbol = optionStatus === 'status-correct' ? '✓' : '✗';
                    html += '<span class="option-status ' + optionStatus + '">' + statusSymbol + '</span>';
                }

                html += '</div>';
            });
            html += '</div>';
        }

        // 答案和解析区域
        html += '<div class="answer-analysis">';
        if (question.rightAnswer) {
            html += '<div class="analysis-title">正确答案：' + question.rightAnswer + '</div>';
        }
        if (latestAnswer) {
            const statusText = isLatestCorrect ? '<span style="color: #27ae60; font-weight: bold;">✓ 正确</span>' :
                '<span style="color: #e74c3c; font-weight: bold;">✗ 错误</span>';
            html += '<div class="analysis-title">您的答案：' + latestAnswer + ' ' + statusText + '</div>';
        }
        if (question.analysis) {
            html += '<div style="margin-top: 8px;">';
            html += '<div class="analysis-title">解析：</div>';
            html += '<div>' + question.analysis + '</div>';
            html += '</div>';
        }
        html += '</div></div>';

        // 更新统计信息
        if (isLatestCorrect) questionStats.correct++;
        if (!isLatestCorrect && latestAnswer) questionStats.wrong++;
        if (isHighFrequency) questionStats.highFrequency++;
        if (isRecentCorrect) questionStats.recentCorrect++;
        if (isNeverWrong) questionStats.neverWrong++;
        questionStats.total++;

        return { html: html };
    }

    // 初始化导航栏
    function initializeQuestionNavigation() {
        var navList = document.getElementById('nav-question-list');
        var questions = document.querySelectorAll('.question-item');

        // 清空现有导航项
        navList.innerHTML = '';

        var currentType = '';
        var typeCount = 0;

        for (var i = 0; i < questions.length; i++) {
            var questionNumber = i + 1;
            var questionElement = questions[i];

            // 检测题型变化
            var questionStyle = detectQuestionStyle(questionElement);
            if (questionStyle !== currentType) {
                currentType = questionStyle;
                typeCount++;

                // 添加题型标题（不是第一个题型时添加分割线）
                if (typeCount > 1) {
                    var divider = document.createElement('div');
                    divider.className = 'nav-divider';
                    navList.appendChild(divider);
                }

                var sectionTitle = document.createElement('div');
                sectionTitle.className = 'nav-section-title';
                sectionTitle.textContent = getTypeName(questionStyle);
                navList.appendChild(sectionTitle);
            }

            // 添加题目导航项
            var navItem = document.createElement('div');
            var navClass = 'nav-question-item ' + questionStyle;

            // 根据题目状态添加相应样式
            if (questionElement.classList.contains('correct')) {
                navClass += ' correct';
            } else if (questionElement.classList.contains('wrong')) {
                navClass += ' wrong';
            }

            if (questionElement.classList.contains('high-frequency')) {
                navClass += ' high-frequency';
            }

            if (questionElement.classList.contains('recent-correct')) {
                navClass += ' recent-correct';
            }

            if (questionElement.classList.contains('never-wrong')) {
                navClass += ' never-wrong';
            }

            navItem.className = navClass;
            navItem.setAttribute('data-question', questionNumber);
            navItem.textContent = questionNumber;
            navItem.addEventListener('click', function() {
                var questionNum = this.getAttribute('data-question');
                scrollToQuestion(questionNum);
            });
            navList.appendChild(navItem);
        }
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
        var typeNames = {
            'single-choice': '单选题',
            'short-answer': '简答题',
            'essay': '论述题'
        };
        return typeNames[type] || '未知题型';
    }

    // 初始化统计信息
    function initializeStats() {
        var statsGrid = document.getElementById('statsGrid');
        var correctRate = questionStats.total > 0 ? Math.round((questionStats.correct / questionStats.total) * 100) : 0;

        statsGrid.innerHTML =
            '<div class="stat-item">' +
            '  <div class="stat-value">' + questionStats.total + '</div>' +
            '  <div class="stat-label">总题数</div>' +
            '</div>' +
            '<div class="stat-item">' +
            '  <div class="stat-value">' + questionStats.correct + '</div>' +
            '  <div class="stat-label">做对题数</div>' +
            '</div>' +
            '<div class="stat-item">' +
            '  <div class="stat-value">' + correctRate + '%</div>' +
            '  <div class="stat-label">正确率</div>' +
            '</div>' +
            '<div class="stat-item">' +
            '  <div class="stat-value">' + questionStats.highFrequency + '</div>' +
            '  <div class="stat-label">高频错题</div>' +
            '</div>' +
            '<div class="stat-item">' +
            '  <div class="stat-value">' + questionStats.neverWrong + '</div>' +
            '  <div class="stat-label">从未错过</div>' +
            '</div>';
    }

    // 滚动到指定题目
    function scrollToQuestion(questionNumber) {
        var targetElement = document.getElementById('question-' + questionNumber);
        var navItem = document.querySelector('.nav-question-item[data-question="' + questionNumber + '"]');

        if (targetElement && navItem) {
            // 移除所有当前选中状态
            var navItems = document.querySelectorAll('.nav-question-item');
            for (var i = 0; i < navItems.length; i++) {
                navItems[i].classList.remove('current');
            }

            // 添加当前选中状态
            navItem.classList.add('current');

            // 计算滚动位置，使题目在屏幕顶部
            var elementPosition = targetElement.getBoundingClientRect().top + window.pageYOffset;
            var offsetPosition = elementPosition - 20;

            // 滚动到目标题目
            window.scrollTo({
                top: offsetPosition,
                behavior: 'smooth'
            });

            // 滚动导航栏，使当前导航项在导航栏中可见
            scrollNavToItem(navItem);

            currentQuestion = questionNumber;
        }
    }

    // 滚动导航栏使指定项可见
    function scrollNavToItem(navItem) {
        var navList = document.getElementById('nav-question-list');
        if (!navList) return;

        // 计算导航项在导航列表中的位置
        var navItemTop = navItem.offsetTop;
        var navItemHeight = navItem.offsetHeight;
        var navListHeight = navList.clientHeight;

        // 计算滚动位置，使导航项在导航栏中间
        var scrollTop = navItemTop - (navListHeight / 2) + (navItemHeight / 2);

        // 平滑滚动导航栏
        navList.scrollTo({
            top: scrollTop,
            behavior: 'smooth'
        });
    }

    // 初始化滚动监听
    function initializeScrollListener() {
        var questionElements = document.querySelectorAll('.question-item');
        var navItems = document.querySelectorAll('.nav-question-item');

        var scrollTimeout;
        function updateCurrentQuestion() {
            var scrollPosition = window.scrollY + 100;

            for (var i = 0; i < questionElements.length; i++) {
                var element = questionElements[i];
                var elementTop = element.offsetTop;
                var elementBottom = elementTop + element.offsetHeight;

                if (scrollPosition >= elementTop && scrollPosition < elementBottom) {
                    var newQuestion = (i + 1).toString();
                    if (newQuestion !== currentQuestion) {
                        currentQuestion = newQuestion;

                        // 更新导航选中状态
                        for (var j = 0; j < navItems.length; j++) {
                            navItems[j].classList.remove('current');
                        }

                        var currentNavItem = document.querySelector('.nav-question-item[data-question="' + currentQuestion + '"]');
                        if (currentNavItem) {
                            currentNavItem.classList.add('current');
                            scrollNavToItem(currentNavItem);
                        }
                    }
                    break;
                }
            }
        }

        // 使用防抖的滚动监听
        window.addEventListener('scroll', function() {
            clearTimeout(scrollTimeout);
            scrollTimeout = setTimeout(updateCurrentQuestion, 50);
        });

        // 初始化当前题目
        updateCurrentQuestion();
    }

    // 返回上一页
    function goBack() {
        window.history.back();
    }
</script>
</body>
</html>