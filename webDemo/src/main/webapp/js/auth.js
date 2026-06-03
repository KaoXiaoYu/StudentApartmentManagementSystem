class AuthManager {
    constructor() {
        this.init();
    }

    init() {
        this.bindEvents();
    }

    bindEvents() {
        const tabs = document.querySelectorAll('.auth-tab');
        tabs.forEach(tab => {
            tab.addEventListener('click', () => this.switchTab(tab.dataset.tab));
        });

        document.getElementById('loginForm').addEventListener('submit', (e) => this.handleLogin(e));
        document.getElementById('registerForm').addEventListener('submit', (e) => this.handleRegister(e));
    }

    switchTab(tabName) {
        document.querySelectorAll('.auth-tab').forEach(tab => tab.classList.remove('active'));
        document.querySelectorAll('.auth-form').forEach(form => form.classList.remove('active'));

        document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
        document.getElementById(`${tabName}Form`).classList.add('active');
    }

    handleLogin(e) {
        e.preventDefault();

        const email = document.getElementById('loginEmail').value.trim();
        const password = document.getElementById('loginPassword').value;

        if (!email || !password) {
            this.showToast('请填写邮箱和密码', 'error');
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            this.showToast('邮箱格式不正确', 'error');
            return;
        }

        const storedUsers = localStorage.getItem('users');
        if (!storedUsers) {
            this.showToast('暂无用户数据', 'error');
            return;
        }

        const users = JSON.parse(storedUsers);
        const user = users.find(u => u.email === email);

        if (!user) {
            this.showToast('用户不存在', 'error');
            return;
        }

        this.showToast('登录成功', 'success');
        setTimeout(() => {
            window.location.href = 'userManager.html';
        }, 1000);
    }

    handleRegister(e) {
        e.preventDefault();

        const username = document.getElementById('registerUsername').value.trim();
        const email = document.getElementById('registerEmail').value.trim();
        const password = document.getElementById('registerPassword').value;
        const confirmPassword = document.getElementById('registerConfirmPassword').value;

        if (!username || !email || !password || !confirmPassword) {
            this.showToast('请填写所有必填项', 'error');
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            this.showToast('邮箱格式不正确', 'error');
            return;
        }

        if (password.length < 6) {
            this.showToast('密码至少6位', 'error');
            return;
        }

        if (password !== confirmPassword) {
            this.showToast('两次输入的密码不一致', 'error');
            return;
        }

        const storedUsers = localStorage.getItem('users');
        let users = [];
        if (storedUsers) {
            users = JSON.parse(storedUsers);
            if (users.find(u => u.email === email)) {
                this.showToast('邮箱已被注册', 'error');
                return;
            }
        }

        const newId = users.length > 0 ? Math.max(...users.map(u => u.id)) + 1 : 1;
        const newUser = {
            id: newId,
            username: username,
            email: email,
            phone: '',
            createTime: this.formatDate(new Date())
        };

        users.push(newUser);
        localStorage.setItem('users', JSON.stringify(users));

        this.showToast('注册成功', 'success');
        setTimeout(() => {
            this.switchTab('login');
            document.getElementById('loginEmail').value = email;
            document.getElementById('loginPassword').value = '';
        }, 1000);
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

const authManager = new AuthManager();