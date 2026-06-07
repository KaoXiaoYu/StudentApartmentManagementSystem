<template>
  <div class="student-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input 
        v-model="searchForm.keyword" 
        placeholder="请输入学号或姓名" 
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
      
      <el-select 
        v-model="searchForm.gender" 
        placeholder="性别" 
        class="search-select"
      >
        <el-option label="全部" value="" />
        <el-option label="男" value="男" />
        <el-option label="女" value="女" />
      </el-select>
      
      <el-select 
        v-model="searchForm.department" 
        placeholder="院系" 
        class="search-select"
      >
        <el-option label="全部" value="" />
        <el-option label="计算机学院" value="计算机学院" />
        <el-option label="电子工程学院" value="电子工程学院" />
        <el-option label="商学院" value="商学院" />
        <el-option label="文学院" value="文学院" />
      </el-select>
      
      <el-button type="primary" class="add-btn" @click="showAddModal = true">
        <el-icon><Plus /></el-icon>
        添加学生
      </el-button>
    </div>
    
    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="studentList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? 'primary' : 'success'">
              {{ scope.row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="院系" />
        <el-table-column prop="major" label="专业" />
        <el-table-column prop="grade" label="年级" width="80" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="dormNo" label="寝室号" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '在校' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-bar">
      <el-pagination
        :current-page="pagination.pageNum"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog 
      :title="isEdit ? '编辑学生' : '添加学生'" 
      :visible.sync="showAddModal"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="院系" prop="department">
          <el-select v-model="form.department" placeholder="请选择院系">
            <el-option label="计算机学院" value="计算机学院" />
            <el-option label="电子工程学院" value="电子工程学院" />
            <el-option label="商学院" value="商学院" />
            <el-option label="文学院" value="文学院" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="form.grade" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="在校" value="在校" />
            <el-option label="离校" value="离校" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'

const searchForm = reactive({
  keyword: '',
  gender: '',
  department: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 50
})

const showAddModal = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: '',
  studentNo: '',
  name: '',
  gender: '',
  department: '',
  major: '',
  grade: '',
  phone: '',
  dormNo: '',
  status: '在校'
})

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'blur' }],
  department: [{ required: true, message: '请选择院系', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }]
}

const studentList = ref([
  { id: 1, studentNo: '2021001', name: '张三', gender: '男', department: '计算机学院', major: '软件工程', grade: '2021', phone: '13800138001', dormNo: '1号楼101', status: '在校' },
  { id: 2, studentNo: '2021002', name: '李四', gender: '女', department: '电子工程学院', major: '通信工程', grade: '2021', phone: '13800138002', dormNo: '2号楼203', status: '在校' },
  { id: 3, studentNo: '2022001', name: '王五', gender: '男', department: '商学院', major: '工商管理', grade: '2022', phone: '13800138003', dormNo: '3号楼105', status: '在校' },
  { id: 4, studentNo: '2022002', name: '赵六', gender: '女', department: '文学院', major: '汉语言文学', grade: '2022', phone: '13800138004', dormNo: '4号楼302', status: '离校' },
  { id: 5, studentNo: '2023001', name: '钱七', gender: '男', department: '计算机学院', major: '人工智能', grade: '2023', phone: '13800138005', dormNo: '1号楼201', status: '在校' }
])

const handleSearch = () => {
  pagination.pageNum = 1
  ElMessage.info('搜索功能已触发')
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
}

const handleCurrentChange = (page) => {
  pagination.pageNum = page
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  showAddModal.value = true
}

const handleDelete = (id) => {
  ElMessage.confirm('确定要删除该学生吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    studentList.value = studentList.value.filter(item => item.id !== id)
    ElMessage.success('删除成功')
  })
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  if (isEdit.value) {
    const index = studentList.value.findIndex(item => item.id === form.id)
    if (index !== -1) {
      studentList.value[index] = { ...form }
    }
    ElMessage.success('编辑成功')
  } else {
    const newId = studentList.value.length > 0 ? Math.max(...studentList.value.map(i => i.id)) + 1 : 1
    studentList.value.push({ ...form, id: newId })
    ElMessage.success('添加成功')
  }
  
  showAddModal.value = false
  resetForm()
}

const resetForm = () => {
  isEdit.value = false
  Object.assign(form, {
    id: '',
    studentNo: '',
    name: '',
    gender: '',
    department: '',
    major: '',
    grade: '',
    phone: '',
    dormNo: '',
    status: '在校'
  })
}
</script>

<style lang="scss" scoped>
.student-page {
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input {
  width: 250px;
}

.search-select {
  width: 150px;
}

.add-btn {
  margin-left: auto;
}

.table-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
