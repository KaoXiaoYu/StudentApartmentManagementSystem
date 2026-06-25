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
            loginForm: { account: "", password: "", remember: true },
            bindForm: { phone_number: "", building_no: "", room_no: "" },
            filters: { college_id: "", building_no: "", room_no: "" },
            violationForm: { college_id: "", building_no: "", room_no: "", violation_type: "", description: "", occurred_at: "" },
            importRows: [{ student_id: "", name: "" }],
            violations: [],
            appeals: [],
            students: [],
            studentSearch: "",
            permissionStudents: [],
            permissionSearch: "",
            permissionOptions: [],
            teachers: [],
            teacherSearch: "",
            teacherPermissionOptions: [],
            colleges: [],
            dorms: [],
            excelFile: null,
            studentExcelFile: null,
            orgColleges: [],
            collegeSearch: "",
            orgClasses: [],
            classFilterCollege: "",
            classSearch: "",
            collegeForm: { college_id: "", college_name: "" },
            classForm: { college_id: "", major_code: "", major_name: "", major_short_name: "", class_code: "", class_name: "" },
            appealTarget: null,
            appealReason: ""
        };
    },
    computed: {
        isTeacher() {
            return this.user?.user_type === "teacher";
        },
        isStudent() {
            return this.user?.user_type === "student";
        },
        isSystemAdmin() {
            return this.user?.role === "system_admin";
        },
        permissions() {
            return new Set(this.user?.permissions || []);
        },
        mustBindProfile() {
            return this.isStudent && Number(this.user?.first_login_required) === 1;
        },
        canViewSchool() {
            return this.has("STUDENT_VIEW_SCHOOL") || this.has("STUDENT_AUDIT_SCHOOL")
                || this.has("TEACHER_VIEW_SCHOOL") || this.has("TEACHER_AUDIT_SCHOOL")
                || this.has("TEACHER_GRANT_STUDENT_SCHOOL");
        },
        canSubmit() {
            return !this.isSystemAdmin && (this.has("STUDENT_SUBMIT_COLLEGE") || this.has("STUDENT_SUBMIT_SCHOOL")
                || this.has("TEACHER_VIEW_COLLEGE") || this.has("TEACHER_VIEW_SCHOOL"));
        },
        canAudit() {
            return !this.isSystemAdmin && (this.has("STUDENT_AUDIT_COLLEGE") || this.has("STUDENT_AUDIT_SCHOOL")
                || this.has("TEACHER_AUDIT_COLLEGE") || this.has("TEACHER_AUDIT_SCHOOL"));
        },
        canGrant() {
            return !this.isSystemAdmin && (this.has("TEACHER_GRANT_STUDENT_COLLEGE") || this.has("TEACHER_GRANT_STUDENT_SCHOOL"));
        },
        canManageCollege() {
            return !this.isSystemAdmin && this.has("TEACHER_MANAGE_COLLEGE");
        },
        canManageAllClasses() {
            return !this.isSystemAdmin && this.has("TEACHER_MANAGE_CLASS_SCHOOL");
        },
        canManageClasses() {
            return this.canManageAllClasses || (!this.isSystemAdmin && this.has("TEACHER_MANAGE_CLASS_COLLEGE"));
        },
        canManageOrg() {
            return this.canManageCollege || this.canManageClasses;
        },
        canManageStudents() {
            return this.isTeacher && !this.isSystemAdmin;
        },
        canAdminTeachers() {
            return this.has("SYSTEM_MANAGE_TEACHER_PERMISSION");
        },
        roleName() {
            if (this.isStudent) return this.permissions.size ? "学生扩展权限" : "普通学生";
            if (this.isSystemAdmin) return "系统管理员";
            if (this.isTeacher) return this.permissions.size ? "教师扩展权限" : "班级教师";
            return "";
        },
        pageTitle() {
            return {
                violations: "违规记录",
                entry: "违规录入",
                appeals: "申诉审核",
                students: "学生与宿舍",
                permissions: "学生授权",
                org: "组织管理",
                teacherPermissions: "教师权限"
            }[this.tab] || "";
        },
        pendingAppealCount() {
            if (this.canAudit) return this.appeals.filter(item => item.status === "pending").length;
            return this.violations.filter(item => item.appeal_status === "pending").length;
        },
        scopeName() {
            if (this.isSystemAdmin) return "教师权限维护";
            if (this.canViewSchool) return "全校";
            if (this.has("STUDENT_VIEW_COLLEGE") || this.has("STUDENT_AUDIT_COLLEGE")
                || this.has("TEACHER_VIEW_COLLEGE") || this.has("TEACHER_AUDIT_COLLEGE")) {
                return this.collegeName(this.user.college_id) || "本学院";
            }
            if (this.isTeacher) return `${this.collegeName(this.user.college_id) || "本学院"} ${this.classNumber(this.user.class_code)}班`;
            return `${this.user.building_no || "--"}-${this.user.room_no || "---"}`;
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
        has(code) {
            return this.permissions.has(code);
        },
        collegeName(collegeId) {
            return this.colleges.find(item => item.college_id === collegeId)?.college_name || collegeId || "";
        },
        classNumber(classCode) {
            const parsed = Number.parseInt(classCode, 10);
            return Number.isNaN(parsed) ? (classCode || "-") : String(parsed);
        },
        async request(url, options = {}) {
            const defaultHeaders = options.body instanceof FormData ? {} : { "Content-Type": "application/json" };
            const response = await fetch(CONTEXT_PATH + url, {
                credentials: "same-origin",
                ...options,
                headers: { ...defaultHeaders, ...(options.headers || {}) }
            });
            const contentType = response.headers.get("content-type") || "";
            if (!contentType.includes("application/json")) {
                throw new Error(response.ok ? "服务端返回格式错误" : `请求失败：${response.status}`);
            }
            const result = await response.json();
            if (!response.ok || result.code !== 200) throw new Error(result.msg || "请求失败");
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
            if (this.isSystemAdmin) {
                this.tab = "teacherPermissions";
                await this.loadTeacherPermissionPage();
                return;
            }
            if (this.mustBindProfile) return;
            await this.loadViolations();
            if (this.canAudit) await this.loadAppeals();
            if (this.canManageStudents) await this.loadStudents();
            if (this.canGrant) {
                await this.loadPermissionOptions();
                await this.searchPermissionStudents();
            }
            if (this.canManageOrg) this.resetClassForm();
            this.violationForm.college_id = this.user.college_id || this.colleges[0]?.college_id || "";
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
        async bindProfile() {
            try {
                await this.request("/student/bindProfile", {
                    method: "POST",
                    body: JSON.stringify(this.bindForm)
                });
                const result = await this.request("/auth/current");
                this.user = result.data;
                this.notify("绑定成功，欢迎进入系统");
                await this.afterLogin();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async openTab(tab) {
            this.tab = tab;
            if (tab === "violations") await this.loadViolations();
            if (tab === "appeals") await this.loadAppeals();
            if (tab === "students") await this.loadStudents();
            if (tab === "permissions") {
                await this.loadPermissionOptions();
                await this.searchPermissionStudents();
            }
            if (tab === "org") await this.loadOrg();
            if (tab === "teacherPermissions") await this.loadTeacherPermissionPage();
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
                if (payload.occurred_at) payload.occurred_at = payload.occurred_at.replace("T", " ");
                await this.request("/violation/add", { method: "POST", body: JSON.stringify(payload) });
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
                    body: JSON.stringify({ violation_id: this.appealTarget.violation_id, reason: this.appealReason })
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
                    body: JSON.stringify({ appeal_id: item.appeal_id, status, reply: reply.trim() })
                });
                this.notify("申诉审核完成");
                await this.loadAppeals();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        normalizeStudents(list) {
            return (list || []).map(item => ({
                ...item,
                permissionList: item.permissions ? item.permissions.split(",").filter(Boolean) : [],
                edit_building_no: item.building_no || "",
                edit_room_no: item.room_no || ""
            }));
        },
        async loadStudents() {
            try {
                const query = this.studentSearch ? `?keyword=${encodeURIComponent(this.studentSearch)}` : "";
                const result = await this.request(`/student/list${query}`);
                this.students = this.normalizeStudents(result.data);
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async clearStudentSearch() {
            this.studentSearch = "";
            await this.loadStudents();
        },
        async searchPermissionStudents() {
            try {
                const query = this.permissionSearch ? `?keyword=${encodeURIComponent(this.permissionSearch)}` : "";
                const result = await this.request(`/student/search${query}`);
                this.permissionStudents = this.normalizeStudents(result.data);
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async clearPermissionSearch() {
            this.permissionSearch = "";
            await this.searchPermissionStudents();
        },
        async loadPermissionOptions() {
            if (!this.canGrant) return;
            const result = await this.request("/student/permissionOptions");
            this.permissionOptions = result.data || [];
        },
        async savePermissions(item) {
            try {
                await this.request("/student/updatePermissions", {
                    method: "POST",
                    body: JSON.stringify({ student_id: item.student_id, permissions: item.permissionList })
                });
                this.notify("学生权限已更新");
                await this.searchPermissionStudents();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async resetStudentPassword(item) {
            if (!confirm(`确认将 ${item.name} 的密码重置为 123456？`)) return;
            try {
                const result = await this.request("/student/resetPassword", {
                    method: "POST",
                    body: JSON.stringify({ student_id: item.student_id })
                });
                this.notify(result.msg);
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async loadTeacherPermissionPage() {
            if (!this.canAdminTeachers) return;
            await this.loadTeacherPermissionOptions();
            await this.loadTeachers();
        },
        async loadTeachers() {
            try {
                const query = this.teacherSearch ? `?keyword=${encodeURIComponent(this.teacherSearch)}` : "";
                const result = await this.request(`/admin/teacherList${query}`);
                this.teachers = (result.data || []).map(item => ({
                    ...item,
                    permissionList: item.permissions ? item.permissions.split(",").filter(Boolean) : []
                }));
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async clearTeacherSearch() {
            this.teacherSearch = "";
            await this.loadTeachers();
        },
        async loadTeacherPermissionOptions() {
            const result = await this.request("/admin/teacherPermissionOptions");
            this.teacherPermissionOptions = result.data || [];
        },
        async saveTeacherPermissions(item) {
            try {
                const result = await this.request("/admin/updateTeacherPermissions", {
                    method: "POST",
                    body: JSON.stringify({ teacher_id: item.teacher_id, permissions: item.permissionList })
                });
                this.notify(result.msg);
                await this.loadTeachers();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async loadOrg() {
            if (this.canManageCollege) await this.loadOrgColleges();
            if (this.canManageClasses) await this.loadClasses();
            this.resetClassForm();
        },
        async loadOrgColleges() {
            try {
                const query = this.collegeSearch ? `?keyword=${encodeURIComponent(this.collegeSearch)}` : "";
                const result = await this.request(`/org/collegeList${query}`);
                this.orgColleges = result.data || [];
                await this.loadColleges();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async clearCollegeSearch() {
            this.collegeSearch = "";
            await this.loadOrgColleges();
        },
        editCollege(item) {
            this.collegeForm = { college_id: item.college_id, college_name: item.college_name };
        },
        async saveCollege() {
            try {
                const result = await this.request("/org/saveCollege", {
                    method: "POST",
                    body: JSON.stringify(this.collegeForm)
                });
                this.notify(result.msg);
                this.collegeForm = { college_id: "", college_name: "" };
                await this.loadOrgColleges();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async deleteCollege(item) {
            if (!confirm(`确认删除 ${item.college_name}？`)) return;
            try {
                const result = await this.request("/org/deleteCollege", {
                    method: "POST",
                    body: JSON.stringify({ college_id: item.college_id })
                });
                this.notify(result.msg);
                await this.loadOrgColleges();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async loadClasses() {
            try {
                const query = new URLSearchParams();
                if (this.canManageAllClasses && this.classFilterCollege) query.set("college_id", this.classFilterCollege);
                if (this.classSearch) query.set("keyword", this.classSearch);
                const suffix = query.toString() ? `?${query}` : "";
                const result = await this.request(`/org/classList${suffix}`);
                this.orgClasses = result.data || [];
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async clearClassSearch() {
            this.classSearch = "";
            await this.loadClasses();
        },
        resetClassForm() {
            this.classForm = {
                college_id: this.canManageAllClasses ? (this.classFilterCollege || this.colleges[0]?.college_id || "") : (this.user?.college_id || ""),
                major_code: "",
                major_name: "",
                major_short_name: "",
                class_code: "",
                class_name: ""
            };
        },
        editClass(item) {
            this.classForm = {
                college_id: item.college_id,
                major_code: item.major_code,
                major_name: item.major_name,
                major_short_name: item.major_short_name,
                class_code: item.class_code,
                class_name: item.class_name
            };
        },
        async saveClass() {
            try {
                const result = await this.request("/org/saveClass", {
                    method: "POST",
                    body: JSON.stringify(this.classForm)
                });
                this.notify(result.msg);
                await this.loadClasses();
                this.resetClassForm();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async deleteClass(item) {
            if (!confirm(`确认删除 ${item.class_name}？`)) return;
            try {
                const result = await this.request("/org/deleteClass", {
                    method: "POST",
                    body: JSON.stringify({ college_id: item.college_id, major_code: item.major_code, class_code: item.class_code })
                });
                this.notify(result.msg);
                await this.loadClasses();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async saveDorm(item) {
            try {
                await this.request("/student/updateDorm", {
                    method: "POST",
                    body: JSON.stringify({ student_id: item.student_id, building_no: item.edit_building_no, room_no: item.edit_room_no })
                });
                this.notify("宿舍成员信息已更新");
                await this.loadStudents();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        addImportRow() {
            this.importRows.push({ student_id: "", name: "" });
        },
        removeImportRow(index) {
            if (this.importRows.length === 1) {
                this.importRows = [{ student_id: "", name: "" }];
                return;
            }
            this.importRows.splice(index, 1);
        },
        async importStudents() {
            const students = this.importRows
                .map(row => ({ student_id: row.student_id.trim(), name: row.name.trim() }))
                .filter(row => row.student_id || row.name);
            try {
                const result = await this.request("/student/importStudents", {
                    method: "POST",
                    body: JSON.stringify({ students })
                });
                this.notify(result.msg);
                this.importRows = [{ student_id: "", name: "" }];
                await this.loadStudents();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        async importStudentExcel() {
            if (!this.studentExcelFile) {
                this.notify("请先选择学生 Excel 文件", true);
                return;
            }
            const form = new FormData();
            form.append("file", this.studentExcelFile);
            try {
                const result = await this.request("/student/importExcel", { method: "POST", body: form });
                this.notify(result.msg);
                this.studentExcelFile = null;
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
                const result = await this.request("/violation/importExcel", { method: "POST", body: form });
                this.notify(result.msg);
                this.excelFile = null;
                await this.loadViolations();
            } catch (error) {
                this.notify(error.message, true);
            }
        },
        downloadViolationTemplate() {
            location.href = CONTEXT_PATH + "/violation/importTemplate";
        },
        downloadStudentTemplate() {
            location.href = CONTEXT_PATH + "/student/importTemplate";
        },
        exportWeek() {
            location.href = CONTEXT_PATH + "/violation/exportWeek";
        },
        appealStatus(status) {
            return { pending: "待审核", approved: "已通过", rejected: "已驳回" }[status] || "未申诉";
        },
        formatDate(value) {
            if (!value) return "-";
            return String(value).replace("T", " ").slice(0, 16);
        }
    }
}).mount("#app");
