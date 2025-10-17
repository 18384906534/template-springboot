<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>考试系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css">
    <style>
        .container {
            display: flex;
            min-height: 600px;
        }

        body {
            padding: 20px;
        }

        #content {
            min-height: 42px;
        }

        .info-content {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .info-content p {
            margin: 0;
            padding: 8px 12px;
            background: rgba(255, 255, 255, 0.7);
            border-radius: 6px;
            border-left: 3px solid #4a90e2;
            transition: all 0.2s ease;
        }

        .info-content p:hover {
            background: rgba(255, 255, 255, 0.9);
            transform: translateX(5px);
        }
    </style>
</head>
<body>
<div class="header">
    <h1>考试系统</h1>
    <p>智能学习平台 - 生成试卷、查看记录、复习错题</p>
</div>

<div class="container">
    <!-- 左侧筛选区域 -->
    <div class="left-sidebar">
        <div class="filter-group">
            <label for="subject">选择科目</label>
            <div class="select-container">
                <select id="subject" name="subjectCode" class="form-control" required>
                    <option value="">-- 请选择科目 --</option>
                </select>
            </div>
        </div>

        <div class="filter-group">
            <label for="testType">选择测试类型</label>
            <div class="select-container">
                <select id="testType" name="examCategoryCode" class="form-control" required>
                    <option value="">-- 请选择测试类型 --</option>
                </select>
            </div>
        </div>

        <div class="filter-group">
            <label id="contentLabel" for="content">选择内容</label>
            <div class="select-container">
                <select id="content" name="examContent" class="form-control">
                    <option value="">-- 请先选择科目和测试类型 --</option>
                </select>
            </div>
        </div>

        <div class="info-box">
            <h3>📚 测试类型说明</h3>
            <div class="info-content">
                <p><strong>章节测试：</strong>针对特定章节内容进行测试，帮助巩固知识点</p>
                <p><strong>课堂测试：</strong>基于课堂讲授内容进行测试，检验学习效果</p>
                <p><strong>模拟试卷：</strong>使用自编试卷进行模拟测试，熟悉考试形式</p>
                <p><strong>历年真题：</strong>使用历年考试真题进行模拟测试，了解命题规律</p>
                <p><strong>自动生成：</strong>系统根据数据权重自动生成个性化试卷，智能推荐</p>
            </div>
        </div>
    </div>

    <!-- 右侧内容区域 -->
    <div class="right-content">
        <div class="content-header">
            <div class="function-tabs">
                <button class="tab-btn active" data-tab="generate">📝 生成试卷</button>
                <button class="tab-btn" data-tab="records">📊 做题记录</button>
                <button class="tab-btn" data-tab="wrong">❌ 做题集</button>
            </div>
        </div>

        <!-- 生成试卷标签页 -->
        <div id="generate-tab" class="tab-content active">
            <form id="examForm" action="/cqzk-exam/exam/generate" method="post">
                <input type="hidden" name="paperId" id="formPaperId">
                <input type="hidden" name="subjectCode" id="formSubjectCode">
                <input type="hidden" name="examCategoryCode" id="formExamCategoryCode">
                <input type="hidden" name="examContent" id="formExamContent">

                <div class="generate-section">
                    <p>根据您选择的科目和测试类型生成个性化试卷</p>
                    <button type="submit" class="btn">
                        🚀 开始考试
                    </button>
                </div>
            </form>
        </div>

        <!-- 做题记录标签页 -->
        <div id="records-tab" class="tab-content">
            <div style="margin-bottom: 20px;">
                <button id="refreshRecords" class="btn btn-secondary">
                    🔄 刷新记录
                </button>
            </div>
            <div class="record-list">
                <table class="record-table">
                    <thead>
                    <tr>
                        <th>试卷名称</th>
                        <th>科目</th>
                        <th>测试类型</th>
                        <th>用时</th>
                        <th>交卷时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody id="records-table-body">
                    <tr>
                        <td colspan="6" class="no-data">请先选择筛选条件，然后点击刷新记录</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 做题集标签页 -->
        <div id="wrong-tab" class="tab-content">
            <div style="margin-bottom: 20px;">
                <button id="viewQuestionSet" class="btn btn-primary">
                    📝 查看做题集
                </button>
            </div>
            <div class="record-list">
                <table class="record-table">
                    <thead>
                    <tr>
                        <th>题目内容</th>
                        <th>科目</th>
                        <th>测试类型</th>
                        <th>错误次数</th>
                        <th>最近状态</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody id="wrong-table-body">
                    <tr>
                        <td colspan="6" class="no-data">请先选择筛选条件，然后点击查看做题集</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script>
    // 页面加载完成后执行
    document.addEventListener('DOMContentLoaded', function() {
        const subjectSelect = document.getElementById('subject');
        const testTypeSelect = document.getElementById('testType');
        const contentSelect = document.getElementById('content');
        const contentLabel = document.getElementById('contentLabel');

        // 表单隐藏域
        const formPaperId = document.getElementById('formPaperId');
        const formSubjectCode = document.getElementById('formSubjectCode');
        const formExamCategoryCode = document.getElementById('formExamCategoryCode');
        const formExamContent = document.getElementById('formExamContent');

        // 初始化数据
        initializeData();

        // 标签页切换
        initializeTabs();

        // 筛选条件变化事件
        subjectSelect.addEventListener('change', function() {
            updateContentOptions();
            updateFormHiddenFields();
        });

        testTypeSelect.addEventListener('change', function() {
            updateContentOptions();
            updateFormHiddenFields();
        });

        contentSelect.addEventListener('change', function() {
            updateFormHiddenFields();
        });

        // 刷新记录按钮事件
        document.getElementById('refreshRecords').addEventListener('click', function() {
            loadExamRecords();
        });

        // 查看做题集按钮事件
        document.getElementById('viewQuestionSet').addEventListener('click', function() {
            viewQuestionSet();
        });

        // 初始化数据
        function initializeData() {
            // 加载科目选项
            fetch('/cqzk-exam/category/getSubject')
                .then(function(response) {
                    return response.json();
                })
                .then(function(data) {
                    if (data.code === 200) {
                        initializeSelect(subjectSelect, data.data, "-- 请选择科目 --");
                    } else {
                        console.error('加载科目失败:', data.message);
                    }
                })
                .catch(function(error) {
                    console.error('加载科目出错:', error);
                    subjectSelect.innerHTML = '<option value="">-- 加载失败 --</option>';
                });

            // 加载测试类型选项
            fetch('/cqzk-exam/category/getExamCategory')
                .then(function(response) {
                    return response.json();
                })
                .then(function(data) {
                    if (data.code === 200) {
                        initializeSelect(testTypeSelect, data.data, "-- 请选择测试类型 --");
                    } else {
                        console.error('加载测试类型失败:', data.message);
                    }
                })
                .catch(function(error) {
                    console.error('加载测试类型出错:', error);
                    testTypeSelect.innerHTML = '<option value="">-- 加载失败 --</option>';
                });
        }

        // 初始化选择框选项
        function initializeSelect(selectElement, options, placeholder) {
            selectElement.innerHTML = '<option value="">' + placeholder + '</option>';
            for (var i = 0; i < options.length; i++) {
                var option = options[i];
                var optionElement = document.createElement('option');
                optionElement.value = option.id;
                optionElement.textContent = option.name;
                selectElement.appendChild(optionElement);
            }
        }

        // 更新内容选项
        function updateContentOptions() {
            var subjectId = subjectSelect.value;
            var testTypeId = testTypeSelect.value;

            // 重置内容选择框
            contentSelect.innerHTML = '<option value="">-- 请选择内容 --</option>';
            contentSelect.disabled = false;

            // 更新标签文本 - 根据测试类型ID调整
            var labelMap = {
                '1': '选择章节',
                '2': '选择课堂',
                '3': '选择模拟试卷',
                '4': '选择真题年月',
                '99': '内容选择'
            };

            contentLabel.textContent = labelMap[testTypeId] || '选择内容';

            if (testTypeId === "99") {
                contentSelect.innerHTML = '<option value="">模拟生成无需选择内容</option>';
                contentSelect.disabled = true;
                updateFormHiddenFields();
                return;
            }

            // 检查是否已选择科目和测试类型
            if (!subjectId || !testTypeId) {
                contentSelect.innerHTML = '<option value="">-- 请先选择科目和测试类型 --</option>';
                updateFormHiddenFields();
                return;
            }

            // 显示加载状态
            contentSelect.innerHTML = '<option value="">加载中...</option>';
            contentSelect.disabled = true;

            // 调用后端API获取内容选项
            fetch('/cqzk-exam/category/getSubjectExamCategory/' + subjectId + '/' + testTypeId)
                .then(function(response) {
                    return response.json();
                })
                .then(function(data) {
                    contentSelect.innerHTML = '<option value="">-- 请选择内容 --</option>';
                    contentSelect.disabled = false;

                    if (data.code === 200 && data.data && data.data.length > 0) {
                        for (var i = 0; i < data.data.length; i++) {
                            var paper = data.data[i];
                            var optionElement = document.createElement('option');
                            optionElement.value = paper.paperId;

                            //var displayText = paper.originPaperName || paper.paperName || '未命名';
                            var displayText = paper.shortPaperName || paper.paperName || '未命名';
                            if (displayText.length > 30) {
                                displayText = displayText.substring(0, 30) + '...';
                            }
                            optionElement.textContent = displayText;

                            //optionElement.title = paper.originPaperName || paper.paperName || '';
                            optionElement.title = paper.shortPaperName || paper.paperName || '';

                            contentSelect.appendChild(optionElement);
                        }
                    } else {
                        contentSelect.innerHTML = '<option value="">-- 暂无内容 --</option>';
                    }
                    updateFormHiddenFields();
                })
                .catch(function(error) {
                    console.error('加载内容出错:', error);
                    contentSelect.innerHTML = '<option value="">-- 加载失败 --</option>';
                    contentSelect.disabled = false;
                    updateFormHiddenFields();
                });
        }

        // 更新表单隐藏域
        function updateFormHiddenFields() {
            formSubjectCode.value = subjectSelect.value;
            formExamCategoryCode.value = testTypeSelect.value;

            formPaperId.value = contentSelect.value;
            formExamContent.value = contentSelect.options[contentSelect.selectedIndex].text;;
        }

        // 初始化标签页
        function initializeTabs() {
            var tabBtns = document.querySelectorAll('.tab-btn');
            var tabContents = document.querySelectorAll('.tab-content');

            for (var i = 0; i < tabBtns.length; i++) {
                tabBtns[i].addEventListener('click', function() {
                    var tabId = this.getAttribute('data-tab');

                    // 更新按钮状态
                    for (var j = 0; j < tabBtns.length; j++) {
                        tabBtns[j].classList.remove('active');
                    }
                    this.classList.add('active');

                    // 更新内容显示
                    for (var k = 0; k < tabContents.length; k++) {
                        tabContents[k].classList.remove('active');
                    }
                    document.getElementById(tabId + '-tab').classList.add('active');

                    // 根据标签页加载数据
                    if (tabId === 'records') {
                        loadExamRecords();
                    } else if (tabId === 'wrong') {
                        loadWrongQuestions();
                    }
                });
            }
        }

        // 加载做题记录
        function loadExamRecords() {
            var subjectCode = subjectSelect.value;
            var examCategoryCode = testTypeSelect.value;
            var examContent = contentSelect.value;

            if (!subjectCode || !examCategoryCode) {
                alert('请先选择科目和测试类型');
                return;
            }

            var tableBody = document.getElementById('records-table-body');
            tableBody.innerHTML = '<tr><td colspan="6" class="loading">加载中...</td></tr>';

            var queryData = {
                subjectCode: subjectCode,
                examCategoryCode: examCategoryCode,
                examContent: examContent || null
            };

            fetch('/cqzk-exam/exam/getExamList', {
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
                    if (data && data.length > 0) {
                        renderRecordsTable(data);
                    } else {
                        tableBody.innerHTML = '<tr><td colspan="6" class="no-data">暂无做题记录</td></tr>';
                    }
                })
                .catch(function(error) {
                    console.error('加载做题记录出错:', error);
                    tableBody.innerHTML = '<tr><td colspan="6" class="no-data">加载失败，请重试</td></tr>';
                });
        }

        // 渲染做题记录表格
        function renderRecordsTable(records) {
            var tableBody = document.getElementById('records-table-body');
            tableBody.innerHTML = '';

            for (var i = 0; i < records.length; i++) {
                var record = records[i];
                var row = document.createElement('tr');

                var subjectName = getSubjectName(record.subjectCode);
                var testTypeName = getTestTypeName(record.examCategoryCode);
                var createTime = formatDate(record.createTime);

                row.innerHTML = '<td>' + (record.examName || '未命名试卷') + '</td>' +
                    '<td>' + subjectName + '</td>' +
                    '<td>' + testTypeName + '</td>' +
                    '<td>' + (record.examTime || '--') + '</td>' +
                    '<td>' + createTime + '</td>' +
                    '<td class="action-cell">' +
                    '<button class="btn btn-primary" onclick="viewRecordDetail(' + record.examId + ')" style="padding: 6px 12px; font-size: 12px;">查看详情</button>' +
                    '</td>';
                tableBody.appendChild(row);
            }
        }

        // 加载做题集
        function loadWrongQuestions() {
            var tableBody = document.getElementById('wrong-table-body');
            tableBody.innerHTML = '<tr><td colspan="6" class="no-data">做题集功能开发中...</td></tr>';
        }

        // 工具函数
        function getSubjectName(subjectCode) {
            return subjectCode;
        }

        function getTestTypeName(typeCode) {
            var typeMap = {
                '1': '章节测试',
                '2': '课堂测试',
                '3': '模拟试卷',
                '4': '历年真题',
                '99': '自动生成'
            };
            return typeMap[typeCode] || '未知类型';
        }

        function formatDate(dateString) {
            if (!dateString) return '--';
            var date = new Date(dateString);
            return date.toLocaleString('zh-CN');
        }
    });

    // 查看记录详情（全局函数）
    function viewRecordDetail(examId) {
        window.location.href = '/cqzk-exam/exam/detail?examId=' + examId;
    }

    // 查看做题集详情
    function viewQuestionSet() {
        const subjectCode = document.getElementById('subject').value;
        const examCategoryCode = document.getElementById('testType').value;

        if (!subjectCode) {
            alert('请先选择科目');
            return;
        }

        let url = '/cqzk-exam/exam/detail?subjectCode=' + subjectCode;
        if (examCategoryCode) {
            url += '&examCategoryCode=' + examCategoryCode;
        }
        window.location.href = url;
    }
</script>
</body>
</html>