const { createApp } = Vue;

const CONTEXT_PATH = location.pathname === "/"
    ? ""
    : location.pathname.replace(/\/index\.html$/, "").replace(/\/$/, "");

createApp({
    data() {
        return {
            user: null,
            loading: false,
            tab: "violations",
            message: "",
            messageError: false,
            messageTimer: null,
            loginForm: { account: "", password: "", account_type: "", remember: true },
            filters: { college_id: "", building_no: "", room_no: "" },
            violationForm: {
                college_id: "", building_no: "", room_no: "",
                violation_type: "", description: "", occurred_at: ""
            },
            violations: [],
            appeals: [],
            students: [],
            colleges: [],
            excelFile: null,
            appealTarget: null,
            appealReason: ""
        };
    },
    computed: {
        isTeacher() {
            return this.user?.user_type === "teacher";
        },
        isSchoolTeacher() {
            return this.user?.role === "school_teacher";
        },
        isManager() {
            return this.isTeacher || this.user?.role === "cadre";
        },
        isBasicStudent() {
            return this.user?.user_type === "student" && this.user?.role === "student";
        },
        roleName() {
            return {
                student: "基础学生",
                cadre: "学生干部",
                college_teacher: "学院教师",
                school_teacher: "校级教师"
            }[this.user?.role] || "";
        },
        pageTitle() {
            return {
                violations: "违规记录",
                entry: "违规录入",
                appeals: "申诉审核",
                students: "学生授权"
            }[this.tab] || "";
        },
        pendingAppealCount() {
            if (this.isManager) {
                return this.appeals.filter(item => item.status === "pending").length;
            }
            return this.violations.filter(item => item.appeal_status === "pending").length;
        },
        scopeName() {
            if (this.isSchoolTeacher) return "全校";
            if (this.isBasicStudent) return `${this.user.building_no}-${this.user.room_no}`;
            return this.user.college_id || "本学院";
        }
    },
    async mounted() {
        try {
            const result = await this.request("/auth/current");
            this.user = result.data;
            await this.afterLogin();
        } catch (_) {
            this.user = null;
        }
    },
    methods: {
        async request(url, options = {}) {
            const defaultHeaders = options.body instanceof FormData
                ? {}
                : { "Content-Type": "application/json" };
            const response = await fetch(CONTEXT_PATH + url, {
                credentials: "same-origin",
                ...options,
                headers: { ...defaultHeaders, ...(options.headers || {}) }
            });
            const contentType = response.headers.get("content-type") || "";
            if (!contentType.includes("application/json")) {
                throw new Error(response.ok ? "服务器返回格式错误" : `请求失败（${response.status}）`);
            }
            const result = await response.json();
            if (!response.ok || result.code !== 200) {
                throw new Error(result.msg || "请求失败");
            }
            return result;
        },
        notify(text, error = false) {
            this.message = text;
            this.messageError = error;
            clearTimeout(this.messageTimer);
            this.messageTimer = setTimeout(() => {
                this.message = "";
            }, 3500);
        },
        async login() {
            this.loading = true;
            this.message = "";
            try {
                const result = await this.request("/auth/login", {
                    method: "POST",
                    body: JSON.stringify(this.loginForm)
                });
                this.user = result.data;
                await this.afterLogin();
            } catch (error) {
                this.notify(error.message, true);
            } finally {
                this.loading = false;
            }
        },
        async afterLogin() {
            await this.loadColleges();
            await this.loadViolations();
            if (this.isManager) await this.loadAppeals();
            this.violationForm.college_id =
                this.user.college_id || this.colleges[0]?.college_id || "";
        },
        async logout() {
            try {
                await this.request("/auth/logout");
            } finally {
                this.user = null;
                this.tab = "violations";
                this.loginForm.password = "";
            }
        },
        async openTab(tab) {
            this.tab = tab;
            if (tab === "violations") await this.loadViolations();
            if (tab === "appeals") await this.loadAppeals();
            if (tab === "students") await this.loadStudents();
        },
        async loadColleges() {
            const result = await this.request("/global/colleges");
            this.colleges = result.data || [];
        },
        async loadViolations() {
            try {
                const query = new URLSearchParams();
                Object.entries(this.filters).forEach(([key, value]) => {
                    if (value) query.set(key, value);
                });
                const suffix = query.toString() ? `?${query}` : "";
                const result = await this.request(`/violation/list${suffix}`);
                this.violations = result.data || [];
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async addViolation() {
            try {
                const payload = { ...this.violationForm };
                if (payload.occurred_at) {
                    payload.occurred_at = payload.occurred_at.replace("T", " ");
                }
                await this.request("/violation/add", {
                    method: "POST",
                    body: JSON.stringify(payload)
                });
                this.notify("违规信息已录入");
                this.violationForm.violation_type = "";
                this.violationForm.description = "";
                await this.loadViolations();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        startAppeal(item) {
            this.appealTarget = item;
            this.appealReason = "";
        },
        async submitAppeal() {
            try {
                await this.request("/violation/appeal", {
                    method: "POST",
                    body: JSON.stringify({
                        violation_id: this.appealTarget.violation_id,
                        reason: this.appealReason
                    })
                });
                this.appealTarget = null;
                this.notify("申诉已提交");
                await this.loadViolations();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async loadAppeals() {
            try {
                const result = await this.request("/violation/appealList");
                this.appeals = result.data || [];
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async audit(item, status) {
            const reply = prompt(status === "approved" ? "请输入通过意见" : "请输入驳回理由");
            if (!reply?.trim()) return;
            try {
                await this.request("/violation/audit", {
                    method: "POST",
                    body: JSON.stringify({
                        appeal_id: item.appeal_id,
                        status,
                        reply: reply.trim()
                    })
                });
                this.notify("申诉审核完成");
                await this.loadAppeals();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async loadStudents() {
            try {
                const result = await this.request("/student/list");
                this.students = result.data || [];
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async authorize(item) {
            try {
                await this.request("/student/authorize", {
                    method: "POST",
                    body: JSON.stringify({
                        student_id: item.student_id,
                        authorized: item.role !== "cadre"
                    })
                });
                this.notify("学生权限已更新");
                await this.loadStudents();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async importExcel() {
            if (!this.excelFile) {
                this.notify("请先选择 Excel 文件", true);
                return;
            }
            const form = new FormData();
            form.append("file", this.excelFile);
            try {
                const result = await this.request("/violation/importExcel", {
                    method: "POST",
                    body: form
                });
                this.notify(result.msg);
                this.excelFile = null;
                await this.loadViolations();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        exportWeek() {
            location.href = CONTEXT_PATH + "/violation/exportWeek";
        },
        appealStatus(status) {
            return {
                pending: "待审核",
                approved: "已通过",
                rejected: "已驳回"
            }[status] || "未申诉";
        },
        formatDate(value) {
            if (!value) return "-";
            return String(value).replace("T", " ").slice(0, 16);
        }
    }
}).mount("#app");
