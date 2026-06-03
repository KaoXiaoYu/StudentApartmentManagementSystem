class UserManager {
    constructor() {
        this.users = [];
        this.currentPage = 1;
        this.pageSize = 10;
        this.filteredUsers = [];
        this.deleteUserId = null;

        this.init();
    }

    init() {
        this.loadUsers();
        this.bindEvents();
        this.renderTable();
        this.renderPagination();
    }

    loadUsers() {
        const storedUsers = localStorage.getItem('users');
        if (storedUsers) {
            this.users = JSON.parse(storedUsers);
        } else {
            this.users = this.getMockData();
            this.saveUsers();
        }
        this.filteredUsers = [...this.users];
    }

    saveUsers() {
        localStorage.setItem('users', JSON.stringify(this.users));
    }

    getMockData() {
        return [
            { id: 1, username: '张三', email: 'zhangsan@example.com', phone: '13800138001', createTime: '2025-01-15 10:30:00' },
            { id: 2, username: '李四', email: 'lisi@example.com', phone: '13800138002', createTime: '2025-01-16 14:20:00' },
            { id: 3, username: '王五', email: 'wangwu@example.com', phone: '13800138003', createTime: '2025-01-17 09:15:00' },
            { id: 4, username: '赵六', email: 'zhaoliu@example.com', phone: '13800138004', createTime: '2025-01-18 16:45:00' },
            { id: 5, username: '钱七', email: 'qianqi@example.com', phone: '13800138005', createTime: '2025-01-19 11:00:00' },
            { id: 6, username: '孙八', email: 'sunba@example.com', phone: '13800138006', createTime: '2025-01-20 13:30:00' },
            { id: 7, username: '周九', email: 'zhoujiu@example.com', phone: '13800138007', createTime: '2025-01-21 08:50:00' },
            { id: 8, username: '吴十', email: 'wushi@example.com', phone: '13800138008', createTime: '2025-01-22 15:10:00' },
            { id: 9, username: '郑十一', email: 'zhengshiyi@example.com', phone: '13800138009', createTime: '2025-01-23 10:05:00' },
            { id: 10, username: '冯十二', email: 'fengshier@example.com', phone: '13800138010', createTime: '2025-01-24 17:25:00' },
            { id: 11, username: '陈十三', email: 'chenshisan@example.com', phone: '13800138011', createTime: '2025-01-25 12:40:00' },
            { id: 12, username: '褚十四', email: 'chushisi@example.com', phone: '13800138012', createTime: '2025-01-26 09:55:00' }
        ];
    }

    bindEvents() {
        document.getElementById('btnAddUser').addEventListener('click', () => this.openAddModal());
        document.getElementById('btnCloseModal').addEventListener('click', () => this.closeModal());
        document.getElementById('btnCancel').addEventListener('click', () => this.closeModal());
        document.getElementById('userForm').addEventListener('submit', (e) => this.handleFormSubmit(e));
        
        document.getElementById('btnSearch').addEventListener('click', () => this.searchUsers());
        document.getElementById('btnClearSearch').addEventListener('click', () => this.clearSearch());
        document.getElementById('searchInput').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.searchUsers();
        });

        document.getElementById('btnCloseDeleteModal').addEventListener('click', () => this.closeDeleteModal());
        document.getElementById('btnCancelDelete').addEventListener('click', () => this.closeDeleteModal());
        document.getElementById('btnConfirmDelete').addEventListener('click', () => this.confirmDelete());

        document.getElementById('userModal').addEventListener('click', (e) => {
            if (e.target.id === 'userModal') this.closeModal();
        });

        document.getElementById('deleteConfirmModal').addEventListener('click', (e) => {
            if (e.target.id === 'deleteConfirmModal') this.closeDeleteModal();
        });
    }

    renderTable() {
        const tbody = document.getElementById('userTableBody');
        const start = (this.currentPage - 1) * this.pageSize;
        const end = start + this.pageSize;
        const pageData = this.filteredUsers.slice(start, end);

        if (pageData.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6">
                        <div class="empty-state">
                            <div class="empty-state-icon">📭</div>
                            <div class="empty-state-text">暂无用户数据</div>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = pageData.map(user => `
            <tr data-id="${user.id}">
                <td>${user.id}</td>
                <td>${this.escapeHtml(user.username)}</td>
                <td>${this.escapeHtml(user.email)}</td>
                <td>${this.escapeHtml(user.phone)}</td>
                <td>${user.createTime}</td>
                <td>
                    <div class="actions">
                        <button class="btn btn-sm btn-edit" onclick="userManager.openEditModal(${user.id})">编辑</button>
                        <button class="btn btn-sm btn-delete" onclick="userManager.openDeleteConfirm(${user.id})">删除</button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    renderPagination() {
        const pagination = document.getElementById('pagination');
        const totalPages = Math.ceil(this.filteredUsers.length / this.pageSize);

        if (totalPages <= 1) {
            pagination.innerHTML = '';
            return;
        }

        pagination.innerHTML = `
            <button onclick="userManager.changePage(1)" ${this.currentPage === 1 ? 'disabled' : ''}>首页</button>
            <button onclick="userManager.changePage(${this.currentPage - 1})" ${this.currentPage === 1 ? 'disabled' : ''}>上一页</button>
            <span class="page-info">第 ${this.currentPage} / ${totalPages} 页</span>
            <button onclick="userManager.changePage(${this.currentPage + 1})" ${this.currentPage === totalPages ? 'disabled' : ''}>下一页</button>
            <button onclick="userManager.changePage(${totalPages})" ${this.currentPage === totalPages ? 'disabled' : ''}>末页</button>
        `;
    }

    changePage(page) {
        const totalPages = Math.ceil(this.filteredUsers.length / this.pageSize);
        if (page < 1 || page > totalPages) return;
        
        this.currentPage = page;
        this.renderTable();
        this.renderPagination();
    }

    searchUsers() {
        const keyword = document.getElementById('searchInput').value.trim().toLowerCase();
        
        if (!keyword) {
            this.filteredUsers = [...this.users];
        } else {
            this.filteredUsers = this.users.filter(user => 
                user.username.toLowerCase().includes(keyword) ||
                user.email.toLowerCase().includes(keyword) ||
                user.phone.includes(keyword)
            );
        }

        this.currentPage = 1;
        this.renderTable();
        this.renderPagination();
        
        if (keyword) {
            this.showToast(`找到 ${this.filteredUsers.length} 条记录`, 'info');
        }
    }

    clearSearch() {
        document.getElementById('searchInput').value = '';
        this.filteredUsers = [...this.users];
        this.currentPage = 1;
        this.renderTable();
        this.renderPagination();
    }

    openAddModal() {
        document.getElementById('modalTitle').textContent = '添加用户';
        document.getElementById('userForm').reset();
        document.getElementById('userId').value = '';
        document.getElementById('userModal').classList.add('show');
    }

    openEditModal(userId) {
        const user = this.users.find(u => u.id === userId);
        if (!user) return;

        document.getElementById('modalTitle').textContent = '编辑用户';
        document.getElementById('userId').value = user.id;
        document.getElementById('username').value = user.username;
        document.getElementById('email').value = user.email;
        document.getElementById('phone').value = user.phone;
        document.getElementById('userModal').classList.add('show');
    }

    closeModal() {
        document.getElementById('userModal').classList.remove('show');
        document.getElementById('userForm').reset();
    }

    handleFormSubmit(e) {
        e.preventDefault();

        const userId = document.getElementById('userId').value;
        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const phone = document.getElementById('phone').value.trim();

        if (!username || !email) {
            this.showToast('请填写必填项', 'error');
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            this.showToast('邮箱格式不正确', 'error');
            return;
        }

        if (phone && !/^\d{11}$/.test(phone)) {
            this.showToast('手机号格式不正确（11 位数字）', 'error');
            return;
        }

        if (userId) {
            this.updateUser(parseInt(userId), { username, email, phone });
        } else {
            this.addUser({ username, email, phone });
        }

        this.closeModal();
    }

    addUser(userData) {
        const newId = this.users.length > 0 ? Math.max(...this.users.map(u => u.id)) + 1 : 1;
        const newUser = {
            id: newId,
            username: userData.username,
            email: userData.email,
            phone: userData.phone,
            createTime: this.formatDate(new Date())
        };

        this.users.push(newUser);
        this.saveUsers();
        this.filteredUsers = [...this.users];
        this.renderTable();
        this.renderPagination();
        this.showToast('用户添加成功', 'success');
    }

    updateUser(userId, userData) {
        const index = this.users.findIndex(u => u.id === userId);
        if (index === -1) return;

        this.users[index] = {
            ...this.users[index],
            username: userData.username,
            email: userData.email,
            phone: userData.phone
        };

        this.saveUsers();
        this.filteredUsers = [...this.users];
        this.renderTable();
        this.showToast('用户更新成功', 'success');
    }

    openDeleteConfirm(userId) {
        this.deleteUserId = userId;
        const user = this.users.find(u => u.id === userId);
        if (user) {
            document.getElementById('deleteConfirmMessage').textContent = 
                `确定要删除用户 "${user.username}" 吗？`;
            document.getElementById('deleteConfirmModal').classList.add('show');
        }
    }

    closeDeleteModal() {
        document.getElementById('deleteConfirmModal').classList.remove('show');
        this.deleteUserId = null;
    }

    confirmDelete() {
        if (!this.deleteUserId) return;

        this.users = this.users.filter(u => u.id !== this.deleteUserId);
        this.saveUsers();
        this.filteredUsers = [...this.users];
        
        const totalPages = Math.ceil(this.filteredUsers.length / this.pageSize);
        if (this.currentPage > totalPages) {
            this.currentPage = Math.max(1, totalPages);
        }

        this.renderTable();
        this.renderPagination();
        this.closeDeleteModal();
        this.showToast('用户删除成功', 'success');
    }

    showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;

        const container = document.getElementById('toastContainer');
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.animation = 'slideInRight 0.3s ease reverse';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }
}

const userManager = new UserManager();
