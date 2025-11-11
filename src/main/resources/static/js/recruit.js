// Current filter state
let currentPage = 0;
let currentCategory = '';
let currentStartDate = '';
let currentEndDate = '';
let currentSearchKeyword = '';

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    initializePage();
});

function initializePage() {
    // Set up event listeners for filter tabs
    const tabs = document.querySelectorAll('.tab-btn');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const category = this.getAttribute('data-category');
            filterByCategory(category);
        });
    });
    
    // Show/hide date filter based on selected category
    updateDateFilterVisibility();
}

// Filter by category
function filterByCategory(category) {
    currentCategory = category;
    currentPage = 0;
    
    // Update active tab
    const tabs = document.querySelectorAll('.tab-btn');
    tabs.forEach(tab => {
        if (tab.getAttribute('data-category') === category) {
            tab.classList.add('active');
        } else {
            tab.classList.remove('active');
        }
    });
    
    // Show/hide date filter
    updateDateFilterVisibility();
    
    // Load filtered data
    loadRecruits();
}

// Update date filter visibility
function updateDateFilterVisibility() {
    const dateFilter = document.getElementById('dateFilter');
    if (currentCategory === '날짜') {
        dateFilter.style.display = 'flex';
    } else {
        dateFilter.style.display = 'none';
    }
}

// Apply date filter
function applyDateFilter() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    
    if (startDate && endDate) {
        if (new Date(startDate) > new Date(endDate)) {
            alert('시작 날짜는 종료 날짜보다 이전이어야 합니다.');
            return;
        }
    }
    
    currentStartDate = startDate;
    currentEndDate = endDate;
    currentPage = 0;
    
    loadRecruits();
}

// Search recruit
function searchRecruit() {
    const searchInput = document.getElementById('searchInput');
    currentSearchKeyword = searchInput.value.trim();
    currentPage = 0;
    
    loadRecruits();
}

// Change page
function changePage(page) {
    currentPage = page;
    loadRecruits();
}

// Load recruits from server
function loadRecruits() {
    const params = new URLSearchParams({
        page: currentPage,
        size: 3
    });
    
    if (currentCategory && currentCategory !== '') {
        params.append('category', currentCategory);
    }
    
    if (currentStartDate) {
        params.append('startDate', currentStartDate);
    }
    
    if (currentEndDate) {
        params.append('endDate', currentEndDate);
    }
    
    if (currentSearchKeyword) {
        params.append('search', currentSearchKeyword);
    }
    
    // Redirect to load new data
    window.location.href = `/home?${params.toString()}`;
}

// Handle recruit card click
function viewRecruitDetail(recruitId) {
    window.location.href = `/recruit/${recruitId}`;
}

// Add event listeners to recruit cards
document.addEventListener('DOMContentLoaded', function() {
    const recruitCards = document.querySelectorAll('.recruit-card');
    recruitCards.forEach(card => {
        card.addEventListener('click', function() {
            const recruitId = this.getAttribute('data-recruit-id');
            if (recruitId) {
                viewRecruitDetail(recruitId);
            }
        });
    });
});

// Format date
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Search input enter key handler
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchRecruit();
            }
        });
    }
});
