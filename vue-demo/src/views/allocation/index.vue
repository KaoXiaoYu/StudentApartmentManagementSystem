<template>
  <div class="allocation-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input 
        v-model="searchForm.keyword" 
        placeholder="请输入学号、姓名或寝室号" 
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
        v-model="searchForm.building" 
        placeholder="楼栋" 
        class="search-select"
      >
        <el-option label="全部" value="" />
        <el-option label="1号楼" value="1号楼" />
        <el-option label="2号楼" value="2号楼" />
        <el-option label="3号楼" value="3号楼" />
        <el-option label="4号楼" value="4号楼" />
      </el-select>
      
      <el-button type="primary" class="add-btn" @click="showAddModal = true">
        <el-icon><Plus /></el-icon>
        安排寝室
      </el-button>
    </div>
    
    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="allocationList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? 'primary' : 'success'">
              {{ scope.row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="building" label="楼栋" width="100" />
        <el-table-column prop="dormNo" label="寝室号" width="100" />
        <el-table-column prop="bedNo" label="床位号" width="80" />
        <el-table-column prop="department" label="院系" />
        <el-table-column prop="major" label="专业" />
        <el-table-column prop="allocationTime" label="入住时间" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '入住' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">调整</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">退宿</el-button>
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
      :title="isEdit ? '调整寝室' : '安排寝室'" 
      :visible.sync="showAddModal"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋" prop="building">
          <el-select v-model="form.building" placeholder="请选择楼栋">
            <el-option label="1号楼" value="1号楼" />
            <el-option label="2号楼" value="2号楼" />
            <el-option label="3号楼" value="3号楼" />
            <el-option label="4号楼" value="4号楼" />
          </el-select>
        </el-form-item>
        <el-form-item label="寝室号" prop="dormNo">
          <el-input v-model="form.dormNo" placeholder="请输入寝室号" />
        </el-form-item>
        <el-form-item label="床位号" prop="bedNo">
          <el-select v-model="form.bedNo" placeholder="请选择床位号">
            <el-option label="1号床" value="1" />
            <el-option label="2号床" value="2" />
            <el-option label="3号床" value="3" />
            <el-option label="4号床" value="4" />
            <el-option label="5号床" value="5" />
            <el-option label="6号床" value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="院系" prop="department">
          <el-input v-model="form.department" placeholder="请输入院系" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入专业" />
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
  building: ''
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
  studentName: '',
  gender: '',
  building: '',
  dormNo: '',
  bedNo: '',
  department: '',
  major: '',
  allocationTime: '',
  status: '入住'
})

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  building: [{ required: true, message: '请选择楼栋', trigger: 'blur' }],
  dormNo: [{ required: true, message: '请输入寝室号', trigger: 'blur' }],
  bedNo: [{ required: true, message: '请选择床位号', trigger: 'blur' }]
}

const allocationList = ref([
  { id: 1, studentNo: '2021001', studentName: '张三', gender: '男', building: '1号楼', dormNo: '101', bedNo: '1', department: '计算机学院', major: '软件工程', allocationTime: '2021-09-01', status: '入住' },
  { id: 2, studentNo: '2021002', studentName: '李四', gender: '女', building: '2号楼', dormNo: '203', bedNo: '2', department: '电子工程学院', major: '通信工程', allocationTime: '2021-09-01', status: '入住' },
  { id: 3, studentNo: '2022001', studentName: '王五', gender: '男', building: '3号楼', dormNo: '105', bedNo: '1', department: '商学院', major: '工商管理', allocationTime: '2022-09-01', status: '入住' },
  { id: 4, studentNo: '2022002', studentName: '赵六', gender: '女', building: '4号楼', dormNo: '302', bedNo: '3', department: '文学院', major: '汉语言文学', allocationTime: '2022-09-01', status: '退宿' },
  { id: 5, studentNo: '2023001', studentName: '钱七', gender: '男', building: '1号楼', dormNo: '201', bedNo: '2', department: '计算机学院', major: '人工智能', allocationTime: '2023-09-01', status: '入住' }
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
  ElMessage.confirm('确定要办理退宿吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    const index = allocationList.value.findIndex(item => item.id === id)
    if (index !== -1) {
      allocationList.value[index].status = '退宿'
    }
    ElMessage.success('退宿成功')
  })
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  if (isEdit.value) {
    const index = allocationList.value.findIndex(item => item.id === form.id)
    if (index !== -1) {
      allocationList.value[index] = { ...form, allocationTime: new Date().toISOString().split('T')[0] }
    }
    ElMessage.success('调整成功')
  } else {
    const newId = allocationList.value.length > 0 ? Math.max(...allocationList.value.map(i => i.id)) + 1 : 1
    allocationList.value.push({ 
      ...form, 
      id: newId, 
      allocationTime: new Date().toISOString().split('T')[0],
      status: '入住'
    })
    ElMessage.success('安排成功')
  }
  
  showAddModal.value = false
  resetForm()
}

const resetForm = () => {
  isEdit.value = false
  Object.assign(form, {
    id: '',
    studentNo: '',
    studentName: '',
    gender: '',
    building: '',
    dormNo: '',
    bedNo: '',
    department: '',
    major: '',
    allocationTime: '',
    status: '入住'
  })
}
</script>

<style lang="scss" scoped>
.allocation-page {
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
